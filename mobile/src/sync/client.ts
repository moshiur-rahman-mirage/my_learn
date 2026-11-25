import type { Db } from '@/db/types'
import { nowIso } from '@/domain/dates'
import { applyServerChanges } from './apply'
import { readSnapshot } from './snapshots'
import { PROTOCOL, type Change, type Entity, type Ping, type SyncRequest, type SyncResponse } from './protocol'

/**
 * Phone side of two-way sync (docs/SYNC_PROTOCOL.md §4).
 *
 * Analogy: the phone keeps a logbook of rows it changed. Syncing hands the desktop the pages
 * written since the last visit and takes back the desktop's pages; the desktop decides any
 * argument about the same row.
 */

// app_meta keys (V7 creates the phone's OWN `sync_epoch`; the server's is stored separately)
const K = {
  deviceId: 'device_id',
  url: 'sync_server_url',
  token: 'sync_token',
  serverEpoch: 'sync_server_epoch',
  since: 'sync_since',
  pushedSeq: 'sync_pushed_seq',
  lastSyncAt: 'sync_last_at',
  /** the computer whose data this phone holds (set by a completed sync; kept when unpairing) */
  historyUrl: 'sync_history_url',
  /** '1': the next sync discards the phone's synced data and downloads everything again */
  rebuild: 'sync_rebuild',
} as const

export interface SyncConfig {
  deviceId: string
  url: string | null
  token: string | null
  serverEpoch: string | null
  since: number
  pushedSeq: number
  lastSyncAt: string | null
  /** the next sync will download everything again */
  rebuild: boolean
}

export interface SyncReport {
  pushed: number
  pulled: number
  appliedOnServer: number
  skipped: { entity: string; key: string; reason: string }[]
  conflicts: { entity: string; key: string; resolution: string }[]
  reset: boolean
  /** this phone's data was replaced by the computer's (new computer, or "Download everything again") */
  rebuilt: boolean
  pages: number
}

/** Sends one request; throws {@link SyncError} for HTTP and network failures. */
export type Transport = <T>(path: string, init: { method: 'GET' | 'POST'; token: string; body?: unknown }) => Promise<T>

export class SyncError extends Error {
  constructor(
    message: string,
    readonly kind: 'not-paired' | 'unreachable' | 'server' | 'protocol' | 'unsent-changes',
  ) {
    super(message)
  }
}

async function meta(db: Db, key: string): Promise<string | null> {
  return (await db.first<{ value: string }>('SELECT value FROM app_meta WHERE key = ?', [key]))?.value ?? null
}

async function setMeta(db: Db, key: string, value: string | null): Promise<void> {
  if (value === null) await db.run('DELETE FROM app_meta WHERE key = ?', [key])
  else await db.run('INSERT OR REPLACE INTO app_meta (key, value) VALUES (?, ?)', [key, value])
}

export async function readConfig(db: Db): Promise<SyncConfig> {
  let deviceId = await meta(db, K.deviceId)
  if (!deviceId) {
    deviceId = (await db.first<{ id: string }>('SELECT lower(hex(randomblob(16))) AS id'))!.id
    await setMeta(db, K.deviceId, deviceId)
  }
  return {
    deviceId,
    url: await meta(db, K.url),
    token: await meta(db, K.token),
    serverEpoch: await meta(db, K.serverEpoch),
    since: Number((await meta(db, K.since)) ?? 0),
    pushedSeq: Number((await meta(db, K.pushedSeq)) ?? 0),
    lastSyncAt: await meta(db, K.lastSyncAt),
    rebuild: (await meta(db, K.rebuild)) === '1',
  }
}

/** Normalizes what the user typed or scanned: "192.168.1.20:38427" → "http://192.168.1.20:38427". */
export function normalizeUrl(raw: string): string {
  let url = raw.trim().replace(/\/+$/, '')
  if (!/^https?:\/\//i.test(url)) url = 'http://' + url
  return url
}

/** Parses the desktop's pairing QR code: {"url":"http://…","token":"…"}. */
export function parsePairingCode(text: string): { url: string; token: string } | null {
  try {
    const v = JSON.parse(text) as { url?: unknown; token?: unknown }
    return typeof v.url === 'string' && typeof v.token === 'string' && v.token.length > 0 ? { url: normalizeUrl(v.url), token: v.token } : null
  } catch {
    return null
  }
}

/**
 * Checks the address and token with the desktop, then stores them.
 *
 * Pairing with a DIFFERENT computer than the one this phone's data came from makes the next sync
 * start fresh: the phone's synced data is replaced by that computer's. (Two databases give the same
 * plan different sync keys, so merging them would duplicate plans.) Changes not yet sent to the
 * previous computer would be lost, so that needs `discardUnsent`; without it a SyncError of kind
 * 'unsent-changes' is thrown. A phone that never synced keeps its data and uploads it.
 */
export async function pair(db: Db, transport: Transport, rawUrl: string, token: string, discardUnsent = false): Promise<Ping> {
  const url = normalizeUrl(rawUrl)
  const ping = await transport<Ping>(`${url}/api/sync/ping`, { method: 'GET', token })
  if (ping.app !== 'learning-tracker') throw new SyncError('That address is not a Learning Tracker.', 'protocol')
  if (ping.protocol !== PROTOCOL) {
    throw new SyncError(`The computer speaks sync protocol ${ping.protocol}, this app ${PROTOCOL}. Update the older one.`, 'protocol')
  }
  const old = await readConfig(db)
  const history = await meta(db, K.historyUrl)
  const otherComputer = history !== null && history !== url
  if (otherComputer && !discardUnsent) {
    const unsent = await pendingCount(db)
    if (unsent > 0) {
      throw new SyncError(
        `This phone has ${unsent} change(s) not yet sent to ${history}. Pairing with another computer replaces this phone's data with that computer's, so they would be lost.`,
        'unsent-changes',
      )
    }
  }
  await db.transaction(async () => {
    if (old.url !== url) {
      await setMeta(db, K.since, '0')
      await setMeta(db, K.serverEpoch, null)
      await setMeta(db, K.lastSyncAt, null)
    }
    if (otherComputer) await setMeta(db, K.rebuild, '1')
    await setMeta(db, K.url, url)
    await setMeta(db, K.token, token)
  })
  return ping
}

/**
 * Makes the next sync replace this phone's synced data with the computer's (Sync tab →
 * "Download everything again"). Refuses while changes are unsent, unless `discardUnsent`.
 */
export async function requestRebuild(db: Db, discardUnsent = false): Promise<void> {
  if (!discardUnsent) {
    const unsent = await pendingCount(db)
    if (unsent > 0) throw new SyncError(`${unsent} change(s) on this phone haven't been sent yet. Sync first, or discard them.`, 'unsent-changes')
  }
  await setMeta(db, K.rebuild, '1')
}

export async function unpair(db: Db): Promise<void> {
  await db.transaction(async () => {
    for (const k of [K.url, K.token, K.serverEpoch, K.since]) await setMeta(db, k, null)
  })
}

/** Local changes not yet sent: lines changed on this phone after the last successful push. */
async function localChanges(db: Db, pushedSeq: number): Promise<Change[]> {
  const lines = await db.all<{ entity: Entity; key: string; changed_at: string; deleted: number }>(
    'SELECT entity, key, changed_at, deleted FROM sync_change WHERE origin IS NULL AND seq > ? ORDER BY seq',
    [pushedSeq],
  )
  const changes: Change[] = []
  for (const l of lines) {
    const data = l.deleted ? null : await readSnapshot(db, l.entity, l.key)
    changes.push({ entity: l.entity, key: l.key, changedAt: l.changed_at, deleted: data === null, data })
  }
  return changes
}

/** Number of local changes waiting to be synced (shown on the Sync button). */
export async function pendingCount(db: Db): Promise<number> {
  const cfg = await readConfig(db)
  return (await db.first<{ n: number }>('SELECT COUNT(*) AS n FROM sync_change WHERE origin IS NULL AND seq > ?', [cfg.pushedSeq]))?.n ?? 0
}

async function maxSeq(db: Db): Promise<number> {
  return (await db.first<{ s: number }>('SELECT COALESCE(MAX(seq), 0) AS s FROM sync_change'))!.s
}

/** Removes every synced row before rebuilding from the server (its history was replaced). */
async function wipe(db: Db): Promise<void> {
  await db.run('DELETE FROM learning_plan') // cascades items and everything hanging off them
  await db.run('DELETE FROM tag')
  await db.run('DELETE FROM sync_change')
}

export interface SyncProgress {
  /** pages received so far */
  pages: number
  /** changes applied on this phone so far */
  received: number
  /** more pages are coming */
  more: boolean
}

const activeSyncs = new WeakMap<Db, Promise<SyncReport>>()

/** Pushes local changes and pulls the desktop's, page by page; `onProgress` runs after each page. */
export async function syncNow(
  db: Db,
  transport: Transport,
  deviceName: string | null = null,
  onProgress?: (p: SyncProgress) => void,
): Promise<SyncReport> {
  const active = activeSyncs.get(db)
  if (active) return active

  const run = performSync(db, transport, deviceName, onProgress)
  activeSyncs.set(db, run)
  try {
    return await run
  } finally {
    if (activeSyncs.get(db) === run) activeSyncs.delete(db)
  }
}

async function performSync(
  db: Db,
  transport: Transport,
  deviceName: string | null,
  onProgress?: (p: SyncProgress) => void,
): Promise<SyncReport> {
  const cfg = await readConfig(db)
  if (!cfg.url || !cfg.token) throw new SyncError('Pair with your computer first (Sync → Pair).', 'not-paired')

  // starting fresh: push nothing, pull everything, and replace the local data with it
  const rebuild = cfg.rebuild
  const pushedUpTo = await maxSeq(db)
  const changes = rebuild ? [] : await localChanges(db, cfg.pushedSeq)
  const report: SyncReport = { pushed: changes.length, pulled: 0, appliedOnServer: 0, skipped: [], conflicts: [], reset: false, rebuilt: false, pages: 0 }

  let request: SyncRequest = {
    protocol: PROTOCOL, deviceId: cfg.deviceId, deviceName,
    epoch: rebuild ? null : cfg.serverEpoch, since: rebuild ? 0 : cfg.since, clientNow: nowIso(), changes,
  }
  for (;;) {
    const res = await transport<SyncResponse>(`${cfg.url}/api/sync`, { method: 'POST', token: cfg.token, body: request })
    if (res.protocol !== PROTOCOL) throw new SyncError('The computer answered with another sync protocol version.', 'protocol')
    const first = report.pages === 0
    report.pages++
    await db.transaction(async () => {
      if (first && (res.reset || rebuild)) {
        report.reset = res.reset
        report.rebuilt = rebuild
        await wipe(db)
      }
      const before = await maxSeq(db)
      const outcome = await applyServerChanges(db, res.changes, first && (res.reset || rebuild) ? 0 : pushedUpTo)
      // what came from the server is not ours to push back
      await db.run("UPDATE sync_change SET origin = 'server' WHERE seq > ?", [before])
      report.pulled += outcome.applied
      report.skipped.push(...outcome.skipped)
      if (first) {
        report.appliedOnServer = res.applied
        report.skipped.push(...res.skipped)
        report.conflicts.push(...res.conflicts)
        await setMeta(db, K.pushedSeq, String(first && (res.reset || rebuild) ? await maxSeq(db) : pushedUpTo))
      }
      if (!res.more) {
        await setMeta(db, K.since, String(res.cursor))
        await setMeta(db, K.serverEpoch, res.epoch)
        await setMeta(db, K.lastSyncAt, nowIso())
        await setMeta(db, K.historyUrl, cfg.url)
        await setMeta(db, K.rebuild, null)
      }
    })
    onProgress?.({ pages: report.pages, received: report.pulled, more: res.more })
    if (!res.more) return report
    request = { ...request, epoch: res.epoch, since: res.cursor, clientNow: nowIso(), changes: [] }
  }
}

/** The real transport: fetch with a timeout and readable errors. */
export function fetchTransport(timeoutMs = 60_000): Transport {
  return async <T,>(url: string, init: { method: 'GET' | 'POST'; token: string; body?: unknown }): Promise<T> => {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), timeoutMs)
    let res: Response
    try {
      res = await fetch(url, {
        method: init.method,
        headers: { Authorization: `Bearer ${init.token}`, 'Content-Type': 'application/json', Accept: 'application/json' },
        body: init.body === undefined ? undefined : JSON.stringify(init.body),
        signal: controller.signal,
      })
    } catch {
      throw new SyncError(
        "Can't reach your computer. Check that it's on, the Learning Tracker is running, and both are on the same Wi-Fi.",
        'unreachable',
      )
    } finally {
      clearTimeout(timer)
    }
    if (res.status === 401) throw new SyncError('Not paired: the token is wrong or was reset. Pair again.', 'not-paired')
    if (res.status === 403) throw new SyncError('The computer refused the connection (403). Update the desktop app.', 'server')
    if (!res.ok) {
      let message = `${res.status} ${res.statusText}`
      try {
        message = ((await res.json()) as { message?: string }).message ?? message
      } catch {
        // not JSON
      }
      throw new SyncError(`Sync failed on the computer: ${message}`, res.status === 409 ? 'protocol' : 'server')
    }
    return (await res.json()) as T
  }
}
