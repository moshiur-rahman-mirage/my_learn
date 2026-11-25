import { describe, expect, it } from 'vitest'
import { createItem, createPlan, descendantCount, moveTargets, siblingPosition } from '../src/data/structure'
import { migratedDb } from './nodeDb'

describe('editing helpers', () => {
  it('counts descendants, finds positions and lists move targets without the subtree', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const m1 = await createItem(db, p, { name: 'M1', type: 'MODULE', parentId: null })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: m1 })
    const a1 = await createItem(db, p, { name: 'A1', type: 'TOPIC', parentId: a })
    await createItem(db, p, { name: 'A1x', type: 'TOPIC', parentId: a1 })
    const b = await createItem(db, p, { name: 'B', type: 'TOPIC', parentId: m1 })
    await createItem(db, p, { name: 'M2', type: 'MODULE', parentId: null })

    expect(await descendantCount(db, m1)).toBe(4)
    expect(await descendantCount(db, b)).toBe(0)
    expect(await siblingPosition(db, b)).toEqual({ index: 1, count: 2 })

    const targets = await moveTargets(db, a)
    expect(targets.map((t) => `${t.depth}:${t.name}`)).toEqual(['0:Top level of the plan', '1:B', '0:M2'])
    // already at the top level: no "Top level" option; everything else is offered
    expect((await moveTargets(db, m1)).map((t) => t.name)).toEqual(['M2'])
  })
})
