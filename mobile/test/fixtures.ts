import type { Db } from '../src/db/types'
import type { ItemRow, ItemType } from '../src/domain/types'

export const T0 = '2026-11-25T10:00:00.000Z'

let nextId = 1

/** An in-memory item for pure tree tests. */
export function row(name: string, type: ItemType, parent: ItemRow | null = null, order = 0): ItemRow {
  const id = nextId++
  return {
    id,
    externalId: `ext-${id}`,
    planId: 1,
    parentId: parent?.id ?? null,
    type,
    name,
    description: null,
    priority: null,
    completed: false,
    completedAt: null,
    displayOrder: order,
    version: 0,
  }
}

export function done(item: ItemRow): ItemRow {
  item.completed = true
  item.completedAt = T0
  return item
}

/** Inserts a plan into a real database; returns its id. */
export async function insertPlan(db: Db, name: string, status = 'ACTIVE'): Promise<number> {
  const r = await db.run('INSERT INTO learning_plan (name, status, created_at, updated_at) VALUES (?, ?, ?, ?)', [name, status, T0, T0])
  return r.lastInsertRowId
}

/** Inserts an item into a real database; returns its id. */
export async function insertItem(
  db: Db,
  planId: number,
  name: string,
  type: ItemType,
  parentId: number | null = null,
  opts: { order?: number; completed?: boolean } = {},
): Promise<number> {
  const r = await db.run(
    `INSERT INTO learning_item (external_id, plan_id, parent_id, item_type, name, is_completed, completed_at, display_order,
                                created_at, updated_at)
     VALUES (lower(hex(randomblob(16))), ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [planId, parentId, type, name, opts.completed ? 1 : 0, opts.completed ? T0 : null, opts.order ?? 0, T0, T0],
  )
  return r.lastInsertRowId
}
