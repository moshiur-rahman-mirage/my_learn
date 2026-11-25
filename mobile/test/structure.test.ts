import { describe, expect, it } from 'vitest'
import { RuleError, setCompletion } from '../src/data/items'
import { listPlans } from '../src/data/plans'
import {
  createItem, createPlan, deleteItem, deletePlan, moveItem, reorderItem, StaleError, updateItem, updatePlan,
} from '../src/data/structure'
import { migratedDb } from './nodeDb'

async function setup() {
  const db = await migratedDb()
  const planId = await createPlan(db, { name: 'Java' })
  const mod = await createItem(db, planId, { name: 'Collections', type: 'MODULE', parentId: null })
  const a = await createItem(db, planId, { name: 'A', type: 'TOPIC', parentId: mod })
  const b = await createItem(db, planId, { name: 'B', type: 'TOPIC', parentId: mod })
  const c = await createItem(db, planId, { name: 'C', type: 'TOPIC', parentId: mod })
  return { db, planId, mod, a, b, c }
}

type DB = Awaited<ReturnType<typeof migratedDb>>
const order = async (db: DB, parentId: number | null, planId?: number) =>
  (await db.all<{ name: string; display_order: number }>(
    'SELECT name, display_order FROM learning_item WHERE parent_id IS ? AND (? IS NULL OR plan_id = ?) ORDER BY display_order',
    [parentId, planId ?? null, planId ?? null])).map((r) => `${r.display_order}:${r.name}`)
const version = async (db: DB, id: number) => (await db.first<{ version: number }>('SELECT version FROM learning_item WHERE id = ?', [id]))!.version

describe('items (ItemService)', () => {
  it('creates at the end, or at a position, keeping order gapless', async () => {
    const { db, planId, mod } = await setup()
    expect(await order(db, mod)).toEqual(['0:A', '1:B', '2:C'])
    await createItem(db, planId, { name: 'First', type: 'TOPIC', parentId: mod, position: 0 })
    expect(await order(db, mod)).toEqual(['0:First', '1:A', '2:B', '3:C'])
    await expect(createItem(db, planId, { name: 'X', type: 'TOPIC', parentId: mod, position: 9 })).rejects.toThrow(/out of range/)
    await expect(createItem(db, planId, { name: '  ', type: 'TOPIC', parentId: mod })).rejects.toThrow(/Enter a name/)
  })

  it('new items get a UUID and are recorded for sync', async () => {
    const { db, a } = await setup()
    const row = (await db.first<{ external_id: string; version: number }>('SELECT external_id, version FROM learning_item WHERE id = ?', [a]))!
    expect(row.external_id).toMatch(/^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/)
    expect(await db.first("SELECT deleted FROM sync_change WHERE entity = 'item' AND key = ?", [row.external_id])).toEqual({ deleted: 0 })
  })

  it('a ticked leaf that gains a child loses its tick and review', async () => {
    const { db, planId, a } = await setup()
    await setCompletion(db, a, true)
    await createItem(db, planId, { name: 'Sub', type: 'TOPIC', parentId: a })
    expect(await db.first('SELECT is_completed, completed_at FROM learning_item WHERE id = ?', [a])).toEqual({ is_completed: 0, completed_at: null })
    expect(await db.first('SELECT 1 FROM item_review WHERE item_id = ?', [a])).toBeNull()
  })

  it('updates content with the version last read; a stale version is refused', async () => {
    const { db, a } = await setup()
    const v = await version(db, a)
    await updateItem(db, a, { name: ' ArrayList ', type: 'TOPIC', description: '  dynamic array ', priority: 'MUST', version: v })
    expect(await db.first('SELECT name, description, priority, version FROM learning_item WHERE id = ?', [a]))
      .toEqual({ name: 'ArrayList', description: 'dynamic array', priority: 'MUST', version: v + 1 })
    await expect(updateItem(db, a, { name: 'Again', type: 'TOPIC', version: v })).rejects.toBeInstanceOf(StaleError)
  })

  it('turning a ticked topic into a module drops the tick', async () => {
    const { db, a } = await setup()
    await setCompletion(db, a, true)
    await updateItem(db, a, { name: 'A', type: 'MODULE', version: await version(db, a) })
    expect(await db.first('SELECT is_completed FROM learning_item WHERE id = ?', [a])).toEqual({ is_completed: 0 })
    expect(await db.first('SELECT 1 FROM item_review WHERE item_id = ?', [a])).toBeNull()
  })

  it('moves an item with its subtree to another parent or the top level, renumbering both sides', async () => {
    const { db, planId, mod, a, b } = await setup()
    const other = await createItem(db, planId, { name: 'Maps', type: 'MODULE', parentId: null })
    await createItem(db, planId, { name: 'A child', type: 'TOPIC', parentId: a })
    await moveItem(db, a, other)
    expect(await order(db, mod)).toEqual(['0:B', '1:C'])
    expect(await order(db, other)).toEqual(['0:A'])
    expect(await order(db, a)).toEqual(['0:A child'])
    await moveItem(db, b, null, 0)
    expect(await order(db, null, planId)).toEqual(['0:B', '1:Collections', '2:Maps'])
  })

  it('refuses to move an item into itself or its own subtree', async () => {
    const { db, planId, mod, a } = await setup()
    const sub = await createItem(db, planId, { name: 'Deep', type: 'TOPIC', parentId: a })
    await expect(moveItem(db, mod, mod)).rejects.toThrow(/its own parent/)
    await expect(moveItem(db, mod, sub)).rejects.toThrow(/into its own sub-item 'Deep'/)
    await expect(moveItem(db, a, sub)).rejects.toBeInstanceOf(RuleError)
  })

  it('refuses a parent from another plan', async () => {
    const { db, a } = await setup()
    const p2 = await createPlan(db, { name: 'Other' })
    const foreign = await createItem(db, p2, { name: 'F', type: 'MODULE', parentId: null })
    await expect(moveItem(db, a, foreign)).rejects.toThrow(/different plan/)
  })

  it('reorders among siblings; only moved rows get a new version', async () => {
    const { db, mod, a, b, c } = await setup()
    const [va, vb, vc] = [await version(db, a), await version(db, b), await version(db, c)]
    await reorderItem(db, c, 0)
    expect(await order(db, mod)).toEqual(['0:C', '1:A', '2:B'])
    expect([await version(db, a), await version(db, b), await version(db, c)]).toEqual([va + 1, vb + 1, vc + 1])
    await reorderItem(db, a, 1) // already at 1: nothing changes
    expect(await version(db, a)).toBe(va + 1)
    await expect(reorderItem(db, a, 5)).rejects.toThrow(/out of range/)
  })

  it('deletes a subtree and closes the gap; the topic left without children is checkable again', async () => {
    const { db, planId, mod, a, b } = await setup()
    const sub = await createItem(db, planId, { name: 'Sub', type: 'TOPIC', parentId: a })
    await deleteItem(db, sub)
    await setCompletion(db, a, true) // a leaf again
    await deleteItem(db, b)
    expect(await order(db, mod)).toEqual(['0:A', '1:C'])
    await deleteItem(db, mod)
    expect(await db.first<{ n: number }>('SELECT COUNT(*) AS n FROM learning_item WHERE plan_id = ?', [planId])).toEqual({ n: 0 })
  })
})

describe('plans (PlanService)', () => {
  it('creates with a unique name regardless of case', async () => {
    const db = await migratedDb()
    await createPlan(db, { name: ' Spring Boot ', targetDate: '2027-01-31' })
    expect((await listPlans(db))[0]).toMatchObject({ name: 'Spring Boot', targetDate: '2027-01-31', status: 'ACTIVE' })
    await expect(createPlan(db, { name: 'spring boot' })).rejects.toThrow(/already exists/)
    await expect(createPlan(db, { name: 'X', targetDate: '31/01/2027' })).rejects.toThrow(/YYYY-MM-DD/)
  })

  it('updates (archive, rename, target date) with the version last read', async () => {
    const db = await migratedDb()
    const id = await createPlan(db, { name: 'Old' })
    await createPlan(db, { name: 'Taken' })
    await updatePlan(db, id, { name: 'New', status: 'ARCHIVED', targetDate: null, version: 0 })
    expect((await listPlans(db, { status: 'ARCHIVED' })).map((p) => [p.name, p.version])).toEqual([['New', 1]])
    await expect(updatePlan(db, id, { name: 'New', status: 'ACTIVE', version: 0 })).rejects.toBeInstanceOf(StaleError)
    await expect(updatePlan(db, id, { name: 'TAKEN', status: 'ACTIVE', version: 1 })).rejects.toThrow(/already exists/)
    await updatePlan(db, id, { name: 'new', status: 'ACTIVE', version: 1 }) // its own name in another case is fine
  })

  it('deleting a plan removes everything in it and leaves tombstones for sync', async () => {
    const { db, planId, a } = await setup()
    const ext = (await db.first<{ external_id: string }>('SELECT external_id FROM learning_plan WHERE id = ?', [planId]))!.external_id
    await setCompletion(db, a, true)
    await deletePlan(db, planId)
    expect(await db.first('SELECT COUNT(*) AS n FROM learning_item')).toEqual({ n: 0 })
    expect(await db.first('SELECT COUNT(*) AS n FROM item_review')).toEqual({ n: 0 })
    expect(await db.first("SELECT deleted FROM sync_change WHERE entity = 'plan' AND key = ?", [ext])).toEqual({ deleted: 1 })
  })
})
