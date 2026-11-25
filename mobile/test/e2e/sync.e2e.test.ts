import { spawn, type ChildProcess } from 'node:child_process'
import { existsSync, mkdtempSync } from 'node:fs'
import { createServer } from 'node:net'
import { tmpdir } from 'node:os'
import { join } from 'node:path'
import { afterAll, beforeAll, describe, expect, it } from 'vitest'
import type { Db } from '../../src/db/types'
import { reviewCard } from '../../src/data/flashcards'
import { setCompletion } from '../../src/data/items'
import { getNote, saveNote } from '../../src/data/notes'
import { createItem, createPlan, deleteItem, moveItem, reorderItem, updatePlan } from '../../src/data/structure'
import { fetchTransport, pair, pendingCount, syncNow } from '../../src/sync/client'
import { readSnapshot } from '../../src/sync/snapshots'
import type { Entity } from '../../src/sync/protocol'
import { migratedDb, nodeDb } from '../nodeDb'

/**
 * End to end: the real desktop server (backend jar) on a throwaway database, the phone's sync code
 * on Node's SQLite, talking real HTTP. After every sync both databases must hold the same data.
 *
 *   cd backend && mvn -q package -DskipTests      # once, builds target/learning-tracker.jar
 *   cd mobile && npm run test:e2e
 */
const JAR = join(__dirname, '..', '..', '..', 'backend', 'target', 'learning-tracker.jar')
const enabled = process.env.LT_E2E === '1'

let server: ChildProcess
let base = ''
let serverDbPath = ''
let token = ''
const transport = fetchTransport(30_000)

async function freePort(): Promise<number> {
  return new Promise((resolve) => {
    const s = createServer().listen(0, '127.0.0.1', () => {
      const port = (s.address() as { port: number }).port
      s.close(() => resolve(port))
    })
  })
}

async function api<T = unknown>(method: string, path: string, body?: unknown): Promise<T> {
  const res = await fetch(base + path, {
    method,
    headers: body === undefined ? {} : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  if (!res.ok) throw new Error(`${method} ${path}: ${res.status} ${await res.text()}`)
  return (res.status === 204 ? undefined : await res.json()) as T
}

/** Everything sync carries, keyed by cross-device keys, plus the derived flashcards. */
async function dump(db: Db) {
  const keys: Record<Entity, string> = {
    plan: 'SELECT external_id AS k FROM learning_plan',
    item: 'SELECT external_id AS k FROM learning_item',
    note: 'SELECT i.external_id AS k FROM item_note n JOIN learning_item i ON i.id = n.item_id',
    link: 'SELECT external_id AS k FROM item_link',
    timeLog: 'SELECT external_id AS k FROM time_log',
    itemTag: "SELECT i.external_id || '|' || lower(t.name) AS k FROM item_tag x JOIN learning_item i ON i.id = x.item_id JOIN tag t ON t.id = x.tag_id",
    review: 'SELECT i.external_id AS k FROM item_review r JOIN learning_item i ON i.id = r.item_id',
    confidence: 'SELECT i.external_id AS k FROM item_confidence c JOIN learning_item i ON i.id = c.item_id',
    flashcard: "SELECT i.external_id || '|' || f.card_key AS k FROM flashcard f JOIN learning_item i ON i.id = f.item_id",
  }
  const out: Record<string, unknown> = {}
  for (const [entity, sql] of Object.entries(keys) as [Entity, string][]) {
    const ks = (await db.all<{ k: string }>(sql)).map((r) => r.k).sort()
    out[entity] = Object.fromEntries(await Promise.all(ks.map(async (k) => [k, await readSnapshot(db, entity, k)] as const)))
  }
  out.cards = await db.all(
    `SELECT i.external_id AS item, f.card_key, f.question, f.answer, f.position FROM flashcard f
     JOIN learning_item i ON i.id = f.item_id ORDER BY i.external_id, f.position`)
  out.noteRefs = await db.all(
    'SELECT i.external_id AS item, r.target_name FROM note_ref r JOIN learning_item i ON i.id = r.source_item_id ORDER BY 1, 2')
  return out
}

async function serverDump() {
  const db = nodeDb(serverDbPath, { readOnly: true })
  try {
    return await dump(db)
  } finally {
    db.raw.close()
  }
}

async function expectInStep(phone: Db) {
  expect(await dump(phone)).toEqual(await serverDump())
}

async function deskPlanId(name: string): Promise<number> {
  const plans = await api<{ id: number; name: string }[]>('GET', '/api/plans')
  return plans.find((p) => p.name === name)!.id
}

async function phoneId(phone: Db, name: string): Promise<number> {
  return (await phone.first<{ id: number }>('SELECT id FROM learning_item WHERE name = ?', [name]))!.id
}

describe.skipIf(!enabled)('phone ⇄ desktop sync, end to end', () => {
  let phone: Db
  const desk: Record<string, number> = {}

  beforeAll(async () => {
    if (!existsSync(JAR)) throw new Error(`Build the backend first: ${JAR} is missing`)
    const dataDir = mkdtempSync(join(tmpdir(), 'lt-e2e-'))
    serverDbPath = join(dataDir, 'learning-tracker.db')
    const port = await freePort()
    base = `http://127.0.0.1:${port}`
    server = spawn('java', ['-jar', JAR, `--server.port=${port}`, `--app.data-dir=${dataDir}`, '--app.backup.automatic=false',
      '--spring.main.banner-mode=off'], { stdio: 'ignore' })
    for (let i = 0; i < 120; i++) {
      try {
        if ((await fetch(`${base}/actuator/health`)).ok) break
      } catch {
        // not up yet
      }
      await new Promise((r) => setTimeout(r, 500))
    }
    token = (await api<{ token: string }>('GET', '/api/sync/pairing')).token

    // the desktop has a plan before the phone ever syncs
    const plan = await api<{ id: number }>('POST', '/api/plans', { name: 'Java', description: 'Core Java' })
    desk.plan = plan.id
    const item = (name: string, type: string, parentId: number | null) =>
      api<{ id: number }>('POST', `/api/plans/${plan.id}/items`, { name, type, parentId, description: null, priority: 'MUST' })
    desk.module = (await item('Collections', 'MODULE', null)).id
    desk.list = (await item('ArrayList', 'TOPIC', desk.module)).id
    desk.map = (await item('HashMap', 'TOPIC', desk.module)).id
    await api('PUT', `/api/items/${desk.list}/notes`, {
      format: 'MARKDOWN',
      content: 'Backed by an array. See [[HashMap]].\n\nQ: Growth factor?\nA: About 1.5x.\n\nQ: Random access cost? \nA: O(1)',
    })
    await api('PUT', `/api/items/${desk.map}/notes`, { format: 'HTML', content: '<p>Q: Load factor?</p><p>A: 0.75 &amp; resize</p>' })
    await api('PUT', `/api/items/${desk.list}/tags`, { tags: ['interview', 'Core'] })
    await api('POST', `/api/items/${desk.list}/links`, { url: 'docs.oracle.com/javase', title: 'Docs' })
    await api('POST', `/api/items/${desk.list}/time`, { minutes: 25 })
    await api('PATCH', `/api/items/${desk.map}/completion`, { completed: true })

    phone = await migratedDb()
  }, 120_000)

  afterAll(() => {
    server?.kill()
  })

  it('pairs and pulls everything on the first sync', async () => {
    await pair(phone, transport, base.replace('http://', ''), token)
    const r = await syncNow(phone, transport, 'Test phone')
    expect(r.pushed).toBe(0)
    expect(r.skipped).toEqual([])
    await expectInStep(phone)
    // flashcards derived on the phone have the desktop's keys, so their schedules can sync
    expect(await phone.first<{ n: number }>('SELECT COUNT(*) AS n FROM flashcard')).toEqual({ n: 3 })
  })

  it('a second sync has nothing to do', async () => {
    const r = await syncNow(phone, transport)
    expect([r.pushed, r.pulled]).toEqual([0, 0])
  })

  it('pushes the phone’s ticks, note edits and flashcard reviews', async () => {
    await setCompletion(phone, await phoneId(phone, 'ArrayList'), true)
    await saveNote(phone, await phoneId(phone, 'HashMap'), '# HashMap\n\nQ: Load factor?\nA: 0.75 (default)\n\nQ: Null keys?\nA: One.')
    const card = (await phone.first<{ id: number }>("SELECT id FROM flashcard WHERE question = 'Growth factor?'"))!
    await reviewCard(phone, card.id, true)
    expect(await pendingCount(phone)).toBeGreaterThan(0)

    const r = await syncNow(phone, transport)
    expect(r.skipped).toEqual([])
    expect(await pendingCount(phone)).toBe(0)
    await expectInStep(phone)
    const onDesk = await api<{ item: { completed: boolean } }>('GET', `/api/items/${desk.list}`)
    expect(onDesk.item.completed).toBe(true)
  })

  it('pulls the desktop’s renames, new items and deletions', async () => {
    const detail = (await api<{ item: { version: number } }>('GET', `/api/items/${desk.map}`)).item
    await api('PUT', `/api/items/${desk.map}`, { name: 'HashMap internals', type: 'TOPIC', description: 'buckets', priority: 'SHOULD', version: detail.version })
    desk.set = (await api<{ id: number }>('POST', `/api/plans/${desk.plan}/items`, { name: 'TreeSet', type: 'TOPIC', parentId: desk.module })).id
    const links = await api<{ id: number }[]>('GET', `/api/items/${desk.list}/links`)
    await api('DELETE', `/api/links/${links[0].id}`)

    await syncNow(phone, transport)
    await expectInStep(phone)
    expect(await phone.first('SELECT name FROM learning_item WHERE name = ?', ['TreeSet'])).toEqual({ name: 'TreeSet' })
  })

  it('a note edited on both sides keeps both texts everywhere', async () => {
    await api('PUT', `/api/items/${desk.set}/notes`, { format: 'MARKDOWN', content: 'Desk: sorted set.' })
    await new Promise((r) => setTimeout(r, 20))
    await saveNote(phone, await phoneId(phone, 'TreeSet'), 'Phone: red-black tree.')

    const r = await syncNow(phone, transport)
    expect(r.conflicts).toEqual([expect.objectContaining({ entity: 'note', resolution: 'MERGED' })])
    const note = (await getNote(phone, await phoneId(phone, 'TreeSet'))).content!
    expect(note).toContain('Phone: red-black tree.')
    expect(note).toContain('Desk: sorted set.')
    await expectInStep(phone)
  })

  it('pushes a deletion made on the phone', async () => {
    await phone.run('DELETE FROM learning_item WHERE name = ?', ['TreeSet'])
    await syncNow(phone, transport)
    await expectInStep(phone)
    await expect(api('GET', `/api/items/${desk.set}`)).rejects.toThrow(/404/)
  })

  it('pushes structure edits made on the phone: new plan, items, move, reorder, delete, archive', async () => {
    const planId = await createPlan(phone, { name: 'Phone plan', description: 'made offline', targetDate: '2027-06-30' })
    const m = await createItem(phone, planId, { name: 'Module', type: 'MODULE', parentId: null, priority: 'MUST' })
    const t1 = await createItem(phone, planId, { name: 'T1', type: 'TOPIC', parentId: m })
    const t2 = await createItem(phone, planId, { name: 'T2', type: 'TOPIC', parentId: m })
    await createItem(phone, planId, { name: 'T0', type: 'TOPIC', parentId: m, position: 0 })
    const gone = await createItem(phone, planId, { name: 'Gone', type: 'TOPIC', parentId: null })
    await moveItem(phone, t2, null, 0)
    await reorderItem(phone, t1, 0)
    await deleteItem(phone, gone)
    // moving a desktop item from another plan into this one is refused, as on the desktop
    await expect(moveItem(phone, await phoneId(phone, 'ArrayList'), m)).rejects.toThrow(/different plan/)

    const r = await syncNow(phone, transport)
    expect(r.skipped).toEqual([])
    await expectInStep(phone)
    const tree = await api<{ name: string; children: { name: string }[] }[]>('GET', `/api/plans/${(await deskPlanId('Phone plan'))}/items`)
    expect(tree.map((n) => n.name)).toEqual(['T2', 'Module'])
    expect(tree[1].children.map((n) => n.name)).toEqual(['T1', 'T0'])

    const v = (await phone.first<{ version: number }>('SELECT version FROM learning_plan WHERE id = ?', [planId]))!.version
    await updatePlan(phone, planId, { name: 'Phone plan', status: 'ARCHIVED', targetDate: '2027-06-30', version: v })
    await syncNow(phone, transport)
    await expectInStep(phone)
    const plans = await api<{ name: string; status: string; targetDate: string }[]>('GET', '/api/plans')
    expect(plans.find((p) => p.name === 'Phone plan')).toMatchObject({ status: 'ARCHIVED', targetDate: '2027-06-30' })
  })

  it('after a desktop restore the phone keeps its pending change and rebuilds from the desktop', async () => {
    const backup = await api<{ fileName: string }>('POST', '/api/admin/backup')
    await api('POST', `/api/plans/${desk.plan}/items`, { name: 'Lost by restore', type: 'TOPIC', parentId: null })
    await syncNow(phone, transport)
    expect(await phone.first('SELECT 1 AS x FROM learning_item WHERE name = ?', ['Lost by restore'])).toEqual({ x: 1 })

    await api('POST', `/api/admin/backups/${encodeURIComponent(backup.fileName)}/restore`)
    await setCompletion(phone, await phoneId(phone, 'ArrayList'), false) // a change the phone makes meanwhile

    const r = await syncNow(phone, transport)
    expect(r.reset).toBe(true)
    await expectInStep(phone)
    expect(await phone.first('SELECT 1 FROM learning_item WHERE name = ?', ['Lost by restore'])).toBeNull()
    expect((await api<{ item: { completed: boolean } }>('GET', `/api/items/${desk.list}`)).item.completed).toBe(false)
  })
})
