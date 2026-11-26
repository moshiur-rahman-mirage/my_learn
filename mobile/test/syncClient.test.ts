import { describe, expect, it } from 'vitest'
import { setCompletion } from '../src/data/items'
import { normalizeUrl, pair, parsePairingCode, pendingCount, readConfig, requestRebuild, syncNow, type Transport } from '../src/sync/client'
import type { Change, SyncRequest, SyncResponse } from '../src/sync/protocol'
import { phoneName } from '../src/sync/phoneName'
import { insertItem, insertPlan, T0 } from './fixtures'
import { migratedDb } from './nodeDb'

const ping = { app: 'learning-tracker', protocol: 1, epoch: 'e1' }

function response(changes: Change[], extra: Partial<SyncResponse> = {}): SyncResponse {
  return { protocol: 1, epoch: 'e1', reset: false, cursor: 10, more: false, serverNow: T0, applied: 0, skipped: [], conflicts: [], changes, ...extra }
}

/** A scripted desktop: answers each POST from the list and records the requests. */
function fakeServer(answers: ((req: SyncRequest) => Promise<SyncResponse> | SyncResponse)[]) {
  const requests: SyncRequest[] = []
  const transport: Transport = async <T,>(path: string, init: { method: string; body?: unknown }) => {
    if (path.endsWith('/ping')) return ping as T
    const req = init.body as SyncRequest
    requests.push(JSON.parse(JSON.stringify(req)))
    return (await answers[requests.length - 1](req)) as T
  }
  return { transport, requests }
}

describe('pairing input', () => {
  it('normalizes typed addresses', () => {
    expect(normalizeUrl(' 192.168.1.20:38427/ ')).toBe('http://192.168.1.20:38427')
    expect(normalizeUrl('https://pc.local:8080')).toBe('https://pc.local:8080')
  })

  it('reads the desktop QR code', () => {
    expect(parsePairingCode('{"url":"http://10.0.0.5:38427","token":"abc"}')).toEqual({ url: 'http://10.0.0.5:38427', token: 'abc' })
    expect(parsePairingCode('not json')).toBeNull()
    expect(parsePairingCode('{"url":"x"}')).toBeNull()
  })

  it('pairing with a new computer starts a fresh history', async () => {
    const db = await migratedDb()
    const { transport } = fakeServer([])
    await pair(db, transport, '10.0.0.5:1', 't')
    await db.run("INSERT OR REPLACE INTO app_meta (key, value) VALUES ('sync_since', '42')")
    await pair(db, transport, '10.0.0.5:1', 't2') // same computer: history kept
    expect((await readConfig(db)).since).toBe(42)
    await pair(db, transport, '10.0.0.9:1', 't') // another computer
    expect((await readConfig(db)).since).toBe(0)
  })
})

describe('syncNow', () => {
  it('refuses to sync before pairing', async () => {
    const db = await migratedDb()
    await expect(syncNow(db, fakeServer([]).transport)).rejects.toThrow(/Pair/)
  })

  it('coalesces overlapping sync requests for the same database', async () => {
    const db = await migratedDb()
    let start!: () => void
    let finish!: (value: SyncResponse) => void
    const started = new Promise<void>((resolve) => { start = resolve })
    const answer = new Promise<SyncResponse>((resolve) => { finish = resolve })
    const server = fakeServer([() => {
      start()
      return answer
    }])
    await pair(db, server.transport, 'pc:1', 't')

    const first = syncNow(db, server.transport)
    const second = syncNow(db, server.transport)
    await started
    finish(response([], { cursor: 11 }))
    const [firstResult, secondResult] = await Promise.all([first, second])

    expect(secondResult).toEqual(firstResult)
    expect(server.requests).toHaveLength(1)
  })

  it('pushes local changes once, then only new ones', async () => {
    const db = await migratedDb()
    const planId = await insertPlan(db, 'P')
    const t = await insertItem(db, planId, 'T', 'TOPIC')
    const server = fakeServer([() => response([]), () => response([], { cursor: 11 })])
    await pair(db, server.transport, 'pc:1', 't')

    await syncNow(db, server.transport)
    expect(server.requests[0].changes.map((c) => c.entity).sort()).toEqual(['item', 'plan'])
    expect(await pendingCount(db)).toBe(0)

    await setCompletion(db, t, true)
    await syncNow(db, server.transport)
    expect(server.requests[1].since).toBe(10)
    expect(server.requests[1].epoch).toBe('e1')
    expect(server.requests[1].changes.map((c) => c.entity).sort()).toEqual(['item', 'plan', 'review'])
  })

  it('keeps a row the user changed while the sync was under way, and pushes it next time', async () => {
    const db = await migratedDb()
    const planId = await insertPlan(db, 'P')
    const t = await insertItem(db, planId, 'T', 'TOPIC')
    const key = (await db.first<{ external_id: string }>('SELECT external_id FROM learning_item WHERE id = ?', [t]))!.external_id
    const planKey = (await db.first<{ external_id: string }>('SELECT external_id FROM learning_plan'))!.external_id
    const serverVersion: Change = {
      entity: 'item', key, changedAt: T0, deleted: false,
      data: { planKey, parentKey: null, type: 'TOPIC', name: 'Server name', completed: false, displayOrder: 0 },
    }
    const server = fakeServer([
      async () => {
        await setCompletion(db, t, true) // the user ticks the topic while the request is in flight
        return response([serverVersion])
      },
      () => response([]),
    ])
    await pair(db, server.transport, 'pc:1', 't')

    await syncNow(db, server.transport)
    expect(await db.first('SELECT name, is_completed FROM learning_item WHERE id = ?', [t])).toEqual({ name: 'T', is_completed: 1 })
    await syncNow(db, server.transport)
    expect(server.requests[1].changes.find((c) => c.key === key)?.data).toMatchObject({ completed: true })
  })

  it('follows pages and saves the cursor only after the last one', async () => {
    const db = await migratedDb()
    const planKey = 'p-1'
    const server = fakeServer([
      () => response([{ entity: 'plan', key: planKey, changedAt: T0, deleted: false, data: { name: 'Paged', status: 'ACTIVE' } }], { more: true, cursor: 5 }),
      (req) => {
        expect(req.since).toBe(5)
        expect(req.changes).toEqual([])
        return response([{ entity: 'item', key: 'i-1', changedAt: T0, deleted: false,
          data: { planKey, parentKey: null, type: 'TOPIC', name: 'Leaf', completed: false, displayOrder: 0 } }], { cursor: 9 })
      },
    ])
    await pair(db, server.transport, 'pc:1', 't')
    const seen: { pages: number; received: number; more: boolean }[] = []
    const r = await syncNow(db, server.transport, null, (p) => seen.push(p))
    expect(seen).toEqual([{ pages: 1, received: 1, more: true }, { pages: 2, received: 2, more: false }])
    expect(r.pages).toBe(2)
    expect((await readConfig(db)).since).toBe(9)
    expect(await db.first('SELECT name FROM learning_item')).toEqual({ name: 'Leaf' })
    expect(await pendingCount(db)).toBe(0) // what came from the server isn't pushed back
  })

  it('a failed page leaves the cursor alone so the next sync repeats safely', async () => {
    const db = await migratedDb()
    const server = fakeServer([() => response([], { more: true, cursor: 5 }), () => Promise.reject(new Error('Wi-Fi dropped'))])
    await pair(db, server.transport, 'pc:1', 't')
    await expect(syncNow(db, server.transport)).rejects.toThrow('Wi-Fi dropped')
    expect((await readConfig(db)).since).toBe(0)
  })

  it('wipes and rebuilds when the desktop’s history was replaced', async () => {
    const db = await migratedDb()
    await insertPlan(db, 'Stale')
    const server = fakeServer([
      () => response([{ entity: 'plan', key: 'fresh', changedAt: T0, deleted: false, data: { name: 'Fresh', status: 'ACTIVE' } }], { reset: true, epoch: 'e2' }),
    ])
    await pair(db, server.transport, 'pc:1', 't')
    const r = await syncNow(db, server.transport)
    expect(r.reset).toBe(true)
    expect(await db.all('SELECT name FROM learning_plan')).toEqual([{ name: 'Fresh' }])
    expect((await readConfig(db)).serverEpoch).toBe('e2')
    expect(await pendingCount(db)).toBe(0)
  })
})

describe('phoneName', () => {
  it('does not repeat the brand when the model already starts with it', () => {
    expect(phoneName('motorola', 'motorola edge 60 fusion')).toBe('Motorola edge 60 fusion')
    expect(phoneName('Google', 'Pixel 8')).toBe('Google Pixel 8')
    expect(phoneName('samsung', 'SM-S918B')).toBe('Samsung SM-S918B')
    expect(phoneName(undefined, undefined)).toBe('')
  })
})

describe('switching computers never duplicates plans', () => {
  // The same plan has different sync keys in two databases (V7 gave each copy its own UUIDs);
  // items keep their keys. Merging would create "Java (2)" and strand the items.
  const javaOn = (planKey: string): Change[] => [
    { entity: 'plan', key: planKey, changedAt: T0, deleted: false, data: { name: 'Java', status: 'ACTIVE' } },
    { entity: 'item', key: 'item-1', changedAt: T0, deleted: false,
      data: { planKey, parentKey: null, type: 'TOPIC', name: 'Streams', completed: false, displayOrder: 0 } },
  ]

  async function phoneSyncedWith(url: string) {
    const db = await migratedDb()
    const first = fakeServer([() => response(javaOn('plan-key-A'))])
    await pair(db, first.transport, url, 't')
    await syncNow(db, first.transport)
    return db
  }

  it("pairing with another computer replaces the phone's data instead of merging it", async () => {
    const db = await phoneSyncedWith('test-server:1')
    const second = fakeServer([() => response(javaOn('plan-key-B'), { epoch: 'real' })])
    await pair(db, second.transport, 'real-desktop:2', 't')
    expect((await readConfig(db)).rebuild).toBe(true)

    const r = await syncNow(db, second.transport)
    expect(r.rebuilt).toBe(true)
    expect(second.requests[0]).toMatchObject({ since: 0, epoch: null, changes: [] }) // pushes nothing to the new computer
    expect(await db.all('SELECT name, external_id FROM learning_plan')).toEqual([{ name: 'Java', external_id: 'plan-key-B' }])
    expect(await db.all('SELECT name FROM learning_item')).toEqual([{ name: 'Streams' }])
    expect((await readConfig(db)).rebuild).toBe(false)
  })

  it('syncing many times with the same computer keeps one plan', async () => {
    const db = await phoneSyncedWith('pc:1')
    const again = fakeServer(Array.from({ length: 5 }, () => () => response(javaOn('plan-key-A'))))
    for (let i = 0; i < 5; i++) await syncNow(db, again.transport)
    expect(await db.all('SELECT name FROM learning_plan')).toEqual([{ name: 'Java' }])
    expect(await db.first('SELECT COUNT(*) AS n FROM learning_item')).toEqual({ n: 1 })
  })

  it('re-pairing the same computer keeps the data (no rebuild)', async () => {
    const db = await phoneSyncedWith('pc:1')
    await pair(db, fakeServer([]).transport, 'pc:1', 'new-token')
    expect((await readConfig(db)).rebuild).toBe(false)
  })

  it('a phone that never synced uploads its own plans when first paired', async () => {
    const db = await migratedDb()
    await insertPlan(db, 'Made offline')
    const server = fakeServer([() => response([])])
    await pair(db, server.transport, 'pc:1', 't')
    expect((await readConfig(db)).rebuild).toBe(false)
    await syncNow(db, server.transport)
    expect(server.requests[0].changes.map((c) => c.entity)).toContain('plan')
  })

  it('refuses to drop unsent changes when switching, unless told to', async () => {
    const db = await phoneSyncedWith('test-server:1')
    await insertPlan(db, 'Unsent')
    await expect(pair(db, fakeServer([]).transport, 'real-desktop:2', 't')).rejects.toMatchObject({ kind: 'unsent-changes' })
    expect((await readConfig(db)).url).toBe('http://test-server:1') // unchanged
    await pair(db, fakeServer([]).transport, 'real-desktop:2', 't', true)
    expect((await readConfig(db)).rebuild).toBe(true)
  })

  it('"Download everything again" rebuilds from the same computer', async () => {
    const db = await phoneSyncedWith('pc:1')
    await db.run("INSERT INTO learning_plan (name, external_id, created_at, updated_at) VALUES ('Java (2)', 'stray', ?, ?)", [T0, T0])
    await db.run("UPDATE sync_change SET origin = 'server'") // pretend it came from a bad merge, not a local edit
    await requestRebuild(db)
    await syncNow(db, fakeServer([() => response(javaOn('plan-key-A'))]).transport)
    expect(await db.all('SELECT name FROM learning_plan')).toEqual([{ name: 'Java' }])
  })
})
