/** Wire types of POST /api/sync (docs/SYNC_PROTOCOL.md §2); mirror backend sync/SyncDtos. */

export const PROTOCOL = 1

export type Entity = 'plan' | 'item' | 'note' | 'link' | 'timeLog' | 'itemTag' | 'review' | 'confidence' | 'flashcard'

/** Upserts are applied in this order (parents first); deletes in reverse. */
export const ENTITY_ORDER: Entity[] = ['plan', 'item', 'note', 'link', 'timeLog', 'itemTag', 'review', 'confidence', 'flashcard']

export type Snapshot = Record<string, string | number | boolean | null>

export interface Change {
  entity: Entity
  key: string
  changedAt: string
  deleted: boolean
  data: Snapshot | null
}

export interface SyncRequest {
  protocol: number
  deviceId: string
  deviceName: string | null
  epoch: string | null
  since: number
  clientNow: string
  changes: Change[]
}

export interface SyncResponse {
  protocol: number
  epoch: string
  reset: boolean
  cursor: number
  more: boolean
  serverNow: string
  applied: number
  skipped: { entity: string; key: string; reason: string }[]
  conflicts: { entity: string; key: string; resolution: string }[]
  changes: Change[]
}

export interface Ping {
  app: string
  protocol: number
  epoch: string
}

/** `<itemKey>|<rest>` split at the FIRST bar (item keys are UUIDs; a tag name may contain a bar). */
export function splitKey(key: string): [string, string] {
  const bar = key.indexOf('|')
  if (bar <= 0 || bar === key.length - 1) throw new Error(`Not a composite key: ${key}`)
  return [key.substring(0, bar), key.substring(bar + 1)]
}
