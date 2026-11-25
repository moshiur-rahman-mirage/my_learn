import { describe, expect, it } from 'vitest'
import { getPlan, listPlans, overallProgress } from '../src/data/plans'
import { loadTree } from '../src/data/tree'
import { percentLabel, progressOf } from '../src/domain/progress'
import { buildTree, findWithPath, walk } from '../src/domain/tree'
import { done, insertItem, insertPlan, row, T0 } from './fixtures'
import { migratedDb } from './nodeDb'

// Mirrors backend item/TreeBuilderTest: the two implementations must agree.
describe('buildTree (port of TreeBuilder)', () => {
  it('rolls up leaf progress through every level', () => {
    // the same tree as the Java test
    const phase = row('Phase 3', 'MODULE', null, 0)
    const fields = row('Fields', 'MODULE', phase, 0)
    const relational = row('Relational', 'TOPIC', fields, 0) // topic with sub-topics
    const many2one = done(row('Many2one', 'TOPIC', relational, 0))
    const one2many = row('One2many', 'TOPIC', relational, 1)
    const computed = done(row('Computed', 'TOPIC', fields, 1))
    const crud = row('CRUD', 'TOPIC', phase, 1)
    const tree = buildTree([phase, fields, relational, many2one, one2many, computed, crud])

    const r = tree[0]
    expect(r.progress).toEqual(progressOf(2, 4))
    expect(r.progress.percent).toBe(50)
    expect(r.descendantCount).toBe(6)
    const fieldsNode = r.children[0]
    expect(fieldsNode.progress).toEqual(progressOf(2, 3))
    expect(fieldsNode.progress.percent).toBe(66.7)
    const relNode = fieldsNode.children[0]
    expect(relNode.checkable).toBe(false)
    expect(relNode.leaf).toBe(false)
    expect(relNode.progress).toEqual(progressOf(1, 2))
  })

  it('makes only leaf topics checkable; an empty module is N/A', () => {
    const empty = row('Empty', 'MODULE')
    const leaf = done(row('Leaf', 'TOPIC'))
    const [e, l] = buildTree([empty, leaf])
    expect(e.leaf).toBe(true)
    expect(e.checkable).toBe(false)
    expect(e.progress.percent).toBeNull()
    expect(percentLabel(e.progress)).toBe('N/A')
    expect(l.checkable).toBe(true)
    expect(l.completed).toBe(true)
    expect(l.progress).toEqual(progressOf(1, 1))
  })

  it('does not let an empty module drag down its parent', () => {
    const parent = row('Parent', 'MODULE')
    const items = [parent, row('Empty', 'MODULE', parent, 0), done(row('Leaf', 'TOPIC', parent, 1))]
    expect(buildTree(items)[0].progress.percent).toBe(100)
  })

  it('ignores a stale completion on a parent', () => {
    const parent = done(row('Parent', 'TOPIC'))
    const tree = buildTree([parent, row('Child', 'TOPIC', parent)])
    expect(tree[0].completed).toBe(false)
    expect(tree[0].completedAt).toBeNull()
    expect(tree[0].progress).toEqual(progressOf(0, 1))
  })

  it('orders siblings by display order, then id', () => {
    const items = [row('C', 'TOPIC', null, 2), row('A', 'TOPIC', null, 0), row('B', 'TOPIC', null, 1)]
    expect(buildTree(items).map((n) => n.name)).toEqual(['A', 'B', 'C'])
  })

  it('gives an empty tree for an empty plan', () => {
    expect(buildTree([])).toEqual([])
  })

  it('rounds percentages to one decimal like Java', () => {
    expect(progressOf(1, 3).percent).toBe(33.3)
    expect(progressOf(2, 3).percent).toBe(66.7)
    expect(progressOf(1, 8).percent).toBe(12.5)
    expect(progressOf(0, 0).percent).toBeNull()
  })

  it('walks depth-first and finds a node with its path', () => {
    const a = row('A', 'MODULE')
    const b = row('B', 'MODULE', a)
    const c = row('C', 'TOPIC', b)
    const tree = buildTree([a, b, c])
    expect([...walk(tree)].map((n) => n.name)).toEqual(['A', 'B', 'C'])
    const found = findWithPath(tree, c.id)!
    expect(found.node.name).toBe('C')
    expect(found.path.map((n) => n.name)).toEqual(['A', 'B'])
  })
})

describe('plan and tree queries', () => {
  it('lists plans by name with leaf progress', async () => {
    const db = await migratedDb()
    const b = await insertPlan(db, 'beta')
    await insertPlan(db, 'Alpha')
    const m = await insertItem(db, b, 'M', 'MODULE')
    await insertItem(db, b, 'T1', 'TOPIC', m, { completed: true })
    await insertItem(db, b, 'T2', 'TOPIC', m, { order: 1 })
    await insertItem(db, b, 'Empty', 'MODULE', null, { order: 1 })

    const plans = await listPlans(db)
    expect(plans.map((p) => p.name)).toEqual(['Alpha', 'beta'])
    expect(plans[0].progress.percent).toBeNull()
    expect(plans[1].progress).toEqual(progressOf(1, 2))
    expect((await getPlan(db, b))?.progress).toEqual(progressOf(1, 2))
  })

  it('filters by status and search text', async () => {
    const db = await migratedDb()
    await insertPlan(db, 'Spring Boot')
    await insertPlan(db, 'Old stuff', 'ARCHIVED')
    expect((await listPlans(db, { status: 'ACTIVE' })).map((p) => p.name)).toEqual(['Spring Boot'])
    expect((await listPlans(db, { search: 'sprin' })).map((p) => p.name)).toEqual(['Spring Boot'])
    expect(await listPlans(db, { search: '100%' })).toEqual([])
  })

  it('weights overall progress by leaves across active plans only', async () => {
    const db = await migratedDb()
    const a = await insertPlan(db, 'A')
    const z = await insertPlan(db, 'Z', 'ARCHIVED')
    await insertItem(db, a, 'a1', 'TOPIC', null, { completed: true })
    await insertItem(db, a, 'a2', 'TOPIC')
    await insertItem(db, a, 'a3', 'TOPIC')
    await insertItem(db, z, 'z1', 'TOPIC', null, { completed: true })
    expect(await overallProgress(db)).toEqual(progressOf(1, 3))
  })

  it('loads the tree with extras: notes, tags, minutes, reviews, confidence, cards', async () => {
    const db = await migratedDb()
    const p = await insertPlan(db, 'P')
    const m = await insertItem(db, p, 'M', 'MODULE')
    const t = await insertItem(db, p, 'T', 'TOPIC', m, { completed: true })
    await db.run("INSERT INTO item_note (item_id, content, format, updated_at) VALUES (?, 'x', 'MARKDOWN', ?)", [t, T0])
    await db.run("INSERT INTO tag (name) VALUES ('zeta'), ('Alpha')")
    await db.run('INSERT INTO item_tag (item_id, tag_id) SELECT ?, id FROM tag', [t])
    await db.run('INSERT INTO time_log (item_id, minutes, logged_at) VALUES (?, 25, ?), (?, 15, ?)', [t, T0, t, T0])
    await db.run("INSERT INTO item_review (item_id, stage, due_on) VALUES (?, 0, '2026-11-26')", [t])
    await db.run('INSERT INTO item_confidence (item_id, level, rated_at) VALUES (?, 4, ?)', [t, T0])
    await db.run("INSERT INTO flashcard (item_id, card_key, question, answer, position, due_on, created_at) VALUES (?, 'k', 'q', 'a', 0, '2026-11-25', ?)", [t, T0])

    const [mod] = await loadTree(db, p)
    const topic = mod.children[0]
    expect(topic.hasNotes).toBe(true)
    expect(topic.tags).toEqual(['Alpha', 'zeta'])
    expect(topic.minutes).toBe(40)
    expect(mod.totalMinutes).toBe(40)
    expect(topic.reviewDueOn).toBe('2026-11-26')
    expect(topic.confidence).toBe(4)
    expect(topic.flashcards).toBe(1)
    expect(mod.progress.percent).toBe(100)
  })
})
