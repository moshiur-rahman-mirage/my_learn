import type { Db } from '@/db/types'
import { nowIso } from '@/domain/dates'
import type { NoteFormat } from '@/domain/types'
import { deriveFromNote } from '@/data/notes'
import { ENTITY_ORDER, splitKey, type Change, type Snapshot } from './protocol'

/**
 * Applies the server's changes to the phone (docs/SYNC_PROTOCOL.md §4). The server already
 * merged both sides, so its version wins, except for rows this phone changed while the request
 * was under way: those are kept and pushed next time. Runs inside the caller's transaction.
 */
export interface ApplyOutcome {
  applied: number
  skipped: { entity: string; key: string; reason: string }[]
}

const PRIORITIES = new Set(['MUST', 'SHOULD', 'NICE'])

class Skip extends Error {
  constructor(readonly reason: string) {
    super(reason)
  }
}

export async function applyServerChanges(db: Db, changes: Change[], pushedUpTo: number): Promise<ApplyOutcome> {
  const out: ApplyOutcome = { applied: 0, skipped: [] }
  const touchedParents = new Map<string, [number, number | null]>()

  const one = async (c: Change) => {
    try {
      if (!c.key || (!c.deleted && !c.data)) throw new Skip('INVALID')
      const line = await db.first<{ seq: number; origin: string | null }>('SELECT seq, origin FROM sync_change WHERE entity = ? AND key = ?', [c.entity, c.key])
      if (line && line.origin === null && line.seq > pushedUpTo) return // edited here during the sync: pushed next time
      await applyOne(db, c, touchedParents)
      out.applied++
    } catch (e) {
      if (e instanceof Skip) out.skipped.push({ entity: c.entity, key: c.key, reason: e.reason })
      else throw e
    }
  }

  for (const entity of ENTITY_ORDER) {
    const upserts = changes.filter((c) => c.entity === entity && !c.deleted)
    if (entity === 'item') {
      // parents first: retry children whose parent arrives later in this batch
      let pending = upserts
      let progress = true
      while (pending.length && progress) {
        progress = false
        const retry: Change[] = []
        for (const c of pending) {
          const parentKey = str(c.data, 'parentKey')
          const waiting = parentKey !== null && (await itemId(db, parentKey)) === null && pending.some((p) => p !== c && p.key === parentKey)
          if (waiting) retry.push(c)
          else {
            await one(c)
            progress = true
          }
        }
        pending = retry
      }
      pending.forEach((c) => out.skipped.push({ entity: c.entity, key: c.key, reason: 'PARENT_DELETED' }))
    } else {
      for (const c of upserts) await one(c)
    }
  }
  for (const entity of [...ENTITY_ORDER].reverse()) {
    for (const c of changes.filter((x) => x.entity === entity && x.deleted)) await one(c)
  }
  await repair(db, [...touchedParents.values()])
  return out
}

async function applyOne(db: Db, c: Change, touched: Map<string, [number, number | null]>): Promise<void> {
  const d = c.data
  const touch = (planId: number, parentId: number | null) => touched.set(`${planId}/${parentId}`, [planId, parentId])
  switch (c.entity) {
    case 'plan': {
      const id = await planId(db, c.key)
      if (c.deleted) {
        if (id !== null) await db.run('DELETE FROM learning_plan WHERE id = ?', [id])
        return
      }
      const status = str(d, 'status') ?? 'ACTIVE'
      if (status !== 'ACTIVE' && status !== 'ARCHIVED') throw new Skip('INVALID')
      const name = await freePlanName(db, requireText(d, 'name', 200), c.key)
      const now = nowIso()
      if (id === null) {
        await db.run(
          `INSERT INTO learning_plan (external_id, name, description, status, target_date, version, created_at, updated_at)
           VALUES (?, ?, ?, ?, ?, 0, ?, ?)`,
          [c.key, name, str(d, 'description'), status, str(d, 'targetDate'), str(d, 'createdAt') ?? now, str(d, 'updatedAt') ?? now])
      } else {
        await db.run(
          `UPDATE learning_plan SET name = ?, description = ?, status = ?, target_date = ?, updated_at = ?, version = version + 1 WHERE id = ?`,
          [name, str(d, 'description'), status, str(d, 'targetDate'), str(d, 'updatedAt') ?? now, id])
      }
      return
    }
    case 'item': {
      const existing = await db.first<{ id: number; plan_id: number; parent_id: number | null }>(
        'SELECT id, plan_id, parent_id FROM learning_item WHERE external_id = ?', [c.key])
      if (c.deleted) {
        if (existing) {
          touch(existing.plan_id, existing.parent_id)
          await db.run('DELETE FROM learning_item WHERE id = ?', [existing.id])
        }
        return
      }
      const pid = await planId(db, str(d, 'planKey'))
      if (pid === null) throw new Skip('PLAN_DELETED')
      let parentId: number | null = null
      const parentKey = str(d, 'parentKey')
      if (parentKey !== null) {
        const parent = await db.first<{ id: number; plan_id: number }>('SELECT id, plan_id FROM learning_item WHERE external_id = ?', [parentKey])
        if (!parent) throw new Skip('PARENT_DELETED')
        if (parent.plan_id !== pid) throw new Skip('INVALID')
        parentId = parent.id
      }
      const type = str(d, 'type')
      if (type !== 'MODULE' && type !== 'TOPIC') throw new Skip('INVALID')
      const priority = str(d, 'priority')
      if (priority !== null && !PRIORITIES.has(priority)) throw new Skip('INVALID')
      const name = requireText(d, 'name', 300)
      const completed = d?.completed === true
      const order = Math.max(0, num(d, 'displayOrder') ?? 0)
      const now = nowIso()
      if (!existing) {
        await db.run(
          `INSERT INTO learning_item (external_id, plan_id, parent_id, item_type, name, description, priority,
                                      is_completed, completed_at, display_order, version, created_at, updated_at)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?)`,
          [c.key, pid, parentId, type, name, str(d, 'description'), priority, completed ? 1 : 0,
            completed ? str(d, 'completedAt') : null, order, str(d, 'createdAt') ?? now, str(d, 'updatedAt') ?? now])
      } else {
        if (existing.plan_id !== pid) throw new Skip('INVALID')
        if (parentId !== null && (parentId === existing.id || (await ancestors(db, parentId)).has(existing.id))) throw new Skip('CYCLE')
        touch(existing.plan_id, existing.parent_id)
        await db.run(
          `UPDATE learning_item SET parent_id = ?, item_type = ?, name = ?, description = ?, priority = ?, is_completed = ?,
                  completed_at = ?, display_order = ?, updated_at = ?, version = version + 1 WHERE id = ?`,
          [parentId, type, name, str(d, 'description'), priority, completed ? 1 : 0, completed ? str(d, 'completedAt') : null,
            order, str(d, 'updatedAt') ?? now, existing.id])
      }
      touch(pid, parentId)
      return
    }
    case 'note': {
      const id = await itemId(db, c.key)
      if (id === null) {
        if (c.deleted) return
        throw new Skip('ITEM_DELETED')
      }
      if (c.deleted) {
        await db.run('DELETE FROM item_note WHERE item_id = ?', [id])
        await deriveFromNote(db, id, null, 'MARKDOWN')
        return
      }
      const content = requireText(d, 'content', Number.MAX_SAFE_INTEGER)
      const format = (str(d, 'format') ?? 'HTML') as NoteFormat
      if (format !== 'HTML' && format !== 'MARKDOWN') throw new Skip('INVALID')
      const updatedAt = str(d, 'updatedAt') ?? nowIso()
      const r = await db.run('UPDATE item_note SET content = ?, format = ?, updated_at = ? WHERE item_id = ?', [content, format, updatedAt, id])
      if (r.changes === 0) await db.run('INSERT INTO item_note (item_id, content, format, updated_at) VALUES (?, ?, ?, ?)', [id, content, format, updatedAt])
      await deriveFromNote(db, id, content, format)
      return
    }
    case 'link': {
      if (c.deleted) {
        await db.run('DELETE FROM item_link WHERE external_id = ?', [c.key])
        return
      }
      const id = await requireItem(db, str(d, 'itemKey'))
      const url = requireText(d, 'url', 2000)
      const order = Math.max(0, num(d, 'displayOrder') ?? 0)
      const r = await db.run('UPDATE item_link SET item_id = ?, url = ?, title = ?, display_order = ? WHERE external_id = ?', [id, url, str(d, 'title'), order, c.key])
      if (r.changes === 0) {
        await db.run('INSERT INTO item_link (external_id, item_id, url, title, display_order, created_at) VALUES (?, ?, ?, ?, ?, ?)',
          [c.key, id, url, str(d, 'title'), order, str(d, 'createdAt') ?? nowIso()])
      }
      return
    }
    case 'timeLog': {
      if (c.deleted) {
        await db.run('DELETE FROM time_log WHERE external_id = ?', [c.key])
        return
      }
      const id = await requireItem(db, str(d, 'itemKey'))
      const minutes = num(d, 'minutes') ?? 0
      if (minutes < 1 || minutes > 1440) throw new Skip('INVALID')
      const loggedAt = requireText(d, 'loggedAt', 40)
      const r = await db.run('UPDATE time_log SET item_id = ?, minutes = ?, logged_at = ? WHERE external_id = ?', [id, minutes, loggedAt, c.key])
      if (r.changes === 0) await db.run('INSERT INTO time_log (external_id, item_id, minutes, logged_at) VALUES (?, ?, ?, ?)', [c.key, id, minutes, loggedAt])
      return
    }
    case 'itemTag': {
      const [itemKey, lowerName] = splitKey(c.key)
      if (c.deleted) {
        const id = await itemId(db, itemKey)
        if (id !== null) {
          await db.run('DELETE FROM item_tag WHERE item_id = ? AND tag_id IN (SELECT id FROM tag WHERE lower(name) = ?)', [id, lowerName])
          await db.run('DELETE FROM tag WHERE id NOT IN (SELECT tag_id FROM item_tag)')
        }
        return
      }
      const id = await requireItem(db, str(d, 'itemKey'))
      const name = requireText(d, 'tagName', 40).trim().replace(/[ \t\n\x0B\f\r]+/g, ' ')
      let tag = await db.first<{ id: number }>('SELECT id FROM tag WHERE name = ? COLLATE NOCASE', [name])
      if (!tag) tag = { id: (await db.run('INSERT INTO tag (name) VALUES (?)', [name])).lastInsertRowId }
      await db.run('INSERT OR IGNORE INTO item_tag (item_id, tag_id) VALUES (?, ?)', [id, tag.id])
      return
    }
    case 'review': {
      if (c.deleted) {
        const id = await itemId(db, c.key)
        if (id !== null) await db.run('DELETE FROM item_review WHERE item_id = ?', [id])
        return
      }
      const id = await requireItem(db, c.key)
      const stage = num(d, 'stage') ?? -1
      if (stage < 0) throw new Skip('INVALID')
      await db.run('INSERT OR REPLACE INTO item_review (item_id, stage, due_on, last_reviewed_at) VALUES (?, ?, ?, ?)',
        [id, stage, requireDay(d, 'dueOn'), str(d, 'lastReviewedAt')])
      return
    }
    case 'confidence': {
      if (c.deleted) {
        const id = await itemId(db, c.key)
        if (id !== null) await db.run('DELETE FROM item_confidence WHERE item_id = ?', [id])
        return
      }
      const id = await requireItem(db, c.key)
      const level = num(d, 'level') ?? 0
      if (level < 1 || level > 5) throw new Skip('INVALID')
      await db.run('INSERT OR REPLACE INTO item_confidence (item_id, level, rated_at) VALUES (?, ?, ?)', [id, level, str(d, 'ratedAt') ?? nowIso()])
      return
    }
    case 'flashcard': {
      if (c.deleted) return // cards come and go with their notes on each device
      const [itemKey, cardKey] = splitKey(c.key)
      const id = await requireItem(db, itemKey)
      const stage = num(d, 'stage') ?? -1
      if (stage < 0) throw new Skip('INVALID')
      const r = await db.run('UPDATE flashcard SET stage = ?, due_on = ?, last_reviewed_at = ? WHERE item_id = ? AND card_key = ?',
        [stage, requireDay(d, 'dueOn'), str(d, 'lastReviewedAt'), id, cardKey])
      if (r.changes === 0) throw new Skip('CARD_NOT_FOUND')
      return
    }
    default:
      throw new Skip('INVALID')
  }
}

/** Gapless sibling order, and a topic with children has no completion or review (as on the server). */
export async function repair(db: Db, groups: [number, number | null][]): Promise<void> {
  for (const [planId, parentId] of groups) {
    const ids = await db.all<{ id: number }>(
      'SELECT id FROM learning_item WHERE plan_id = ? AND parent_id IS ? ORDER BY display_order, updated_at, id', [planId, parentId])
    for (let i = 0; i < ids.length; i++) {
      await db.run('UPDATE learning_item SET display_order = ? WHERE id = ? AND display_order <> ?', [i, ids[i].id, i])
    }
    if (parentId !== null) {
      const stale = await db.first(
        'SELECT 1 FROM learning_item WHERE id = ? AND is_completed = 1 AND EXISTS (SELECT 1 FROM learning_item c WHERE c.parent_id = ?)',
        [parentId, parentId])
      if (stale) {
        await db.run('UPDATE learning_item SET is_completed = 0, completed_at = NULL, version = version + 1 WHERE id = ?', [parentId])
        await db.run('DELETE FROM item_review WHERE item_id = ?', [parentId])
      }
    }
  }
}

// --- helpers -------------------------------------------------------------------------------------

async function freePlanName(db: Db, name: string, key: string): Promise<string> {
  let candidate = name
  for (let n = 2; await db.first('SELECT 1 FROM learning_plan WHERE name = ? COLLATE NOCASE AND external_id <> ?', [candidate, key]); n++) {
    candidate = `${name} (${n})`
  }
  return candidate
}

async function ancestors(db: Db, itemId: number): Promise<Set<number>> {
  const rows = await db.all<{ id: number }>(
    `WITH RECURSIVE up(id) AS (
       SELECT parent_id FROM learning_item WHERE id = ?
       UNION SELECT i.parent_id FROM learning_item i JOIN up ON i.id = up.id WHERE i.parent_id IS NOT NULL)
     SELECT id FROM up WHERE id IS NOT NULL`, [itemId])
  return new Set(rows.map((r) => r.id))
}

async function planId(db: Db, key: string | null): Promise<number | null> {
  if (key === null) return null
  return (await db.first<{ id: number }>('SELECT id FROM learning_plan WHERE external_id = ?', [key]))?.id ?? null
}

async function itemId(db: Db, key: string | null): Promise<number | null> {
  if (key === null) return null
  return (await db.first<{ id: number }>('SELECT id FROM learning_item WHERE external_id = ?', [key]))?.id ?? null
}

async function requireItem(db: Db, key: string | null): Promise<number> {
  const id = await itemId(db, key)
  if (id === null) throw new Skip('ITEM_DELETED')
  return id
}

function str(d: Snapshot | null, field: string): string | null {
  const v = d?.[field]
  return v === null || v === undefined ? null : String(v)
}

function num(d: Snapshot | null, field: string): number | null {
  const v = d?.[field]
  return typeof v === 'number' ? v : null
}

function requireText(d: Snapshot | null, field: string, max: number): string {
  const v = str(d, field)
  if (v === null || v.trim() === '' || v.length > max) throw new Skip('INVALID')
  return v
}

function requireDay(d: Snapshot | null, field: string): string {
  const v = str(d, field)
  if (v === null || !/^\d{4}-\d{2}-\d{2}$/.test(v)) throw new Skip('INVALID')
  return v
}
