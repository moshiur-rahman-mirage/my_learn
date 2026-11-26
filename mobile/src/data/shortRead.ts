import type { Db } from '@/db/types'
import { estimateReadingTime, type ShortReadCandidate } from '@/domain/shortRead'
import type { NoteFormat } from '@/domain/types'

const SEEN_PREFIX = 'short_read_seen_'
const PLAN_ENABLED_PREFIX = 'short_read_plan_enabled_'

interface ShortReadRow {
  item_id: number
  name: string
  plan_name: string
  parent_name: string | null
  content: string
  format: NoteFormat
  updated_at: string
}

/** Notes not marked read on active, included plans, in stable plan/topic order. */
export async function loadUnreadShortReads(db: Db): Promise<ShortReadCandidate[]> {
  const rows = await db.all<ShortReadRow>(
    `SELECT i.id AS item_id, i.name, p.name AS plan_name, parent.name AS parent_name, n.content, n.format
     FROM learning_item i
     JOIN learning_plan p ON p.id = i.plan_id AND p.status = 'ACTIVE'
     JOIN item_note n ON n.item_id = i.id
     LEFT JOIN learning_item parent ON parent.id = i.parent_id
     LEFT JOIN app_meta seen ON seen.key = (? || i.id) AND seen.value = n.updated_at
     LEFT JOIN app_meta plan_pref ON plan_pref.key = (? || p.id) AND plan_pref.value = '0'
     WHERE i.item_type = 'TOPIC'
       AND NOT EXISTS (SELECT 1 FROM learning_item child WHERE child.parent_id = i.id)
       AND seen.key IS NULL
       AND plan_pref.key IS NULL
     ORDER BY p.name COLLATE NOCASE, i.display_order, i.id`,
    [SEEN_PREFIX, PLAN_ENABLED_PREFIX],
  )
  return rows.flatMap((row) => {
    const estimate = estimateReadingTime(row.content, row.format)
    return estimate.wordCount === 0 ? [] : [{
      itemId: row.item_id,
      name: row.name,
      planName: row.plan_name,
      parentName: row.parent_name,
      ...estimate,
    }]
  })
}

/** Marks the current note revision as read, so later edits become unread again. */
export async function markShortRead(db: Db, itemId: number): Promise<void> {
  const note = await db.first<{ updated_at: string }>('SELECT updated_at FROM item_note WHERE item_id = ?', [itemId])
  if (!note) return
  await db.run('INSERT OR REPLACE INTO app_meta (key, value) VALUES (?, ?)', [`${SEEN_PREFIX}${itemId}`, note.updated_at])
}

export async function isShortReadRead(db: Db, itemId: number): Promise<boolean> {
  const note = await db.first<{ updated_at: string }>('SELECT updated_at FROM item_note WHERE item_id = ?', [itemId])
  if (!note) return false
  const marker = await db.first(
    'SELECT 1 FROM app_meta WHERE key = ? AND value = ?',
    [`${SEEN_PREFIX}${itemId}`, note.updated_at],
  )
  return marker !== null
}

/** Whether this plan can appear in Short Read suggestions; plans are included by default. */
export async function isShortReadPlanIncluded(db: Db, planId: number): Promise<boolean> {
  const preference = await db.first<{ value: string }>(
    'SELECT value FROM app_meta WHERE key = ?',
    [`${PLAN_ENABLED_PREFIX}${planId}`],
  )
  return preference?.value !== '0'
}

export async function setShortReadPlanIncluded(db: Db, planId: number, included: boolean): Promise<void> {
  await db.run(
    'INSERT OR REPLACE INTO app_meta (key, value) VALUES (?, ?)',
    [`${PLAN_ENABLED_PREFIX}${planId}`, included ? '1' : '0'],
  )
}
