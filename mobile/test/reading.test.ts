import { describe, expect, it } from 'vitest'
import { saveNote } from '../src/data/notes'
import { lastReadItem, readingNav, rememberReading } from '../src/data/reading'
import { createItem, createPlan, deleteItem } from '../src/data/structure'
import { migratedDb } from './nodeDb'

describe('reading order', () => {
  it('walks the plan’s notes in roadmap order, skipping items without notes', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const m = await createItem(db, p, { name: 'M', type: 'MODULE', parentId: null })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: m })
    await createItem(db, p, { name: 'No note', type: 'TOPIC', parentId: m })
    const b = await createItem(db, p, { name: 'B', type: 'TOPIC', parentId: null })
    const other = await createPlan(db, { name: 'Other' })
    const x = await createItem(db, other, { name: 'X', type: 'TOPIC', parentId: null })
    for (const id of [m, a, b, x]) await saveNote(db, id, `note ${id}`)

    expect(await readingNav(db, m)).toEqual({ prevId: null, nextId: a, index: 1, count: 3 })
    expect(await readingNav(db, a)).toEqual({ prevId: m, nextId: b, index: 2, count: 3 })
    expect(await readingNav(db, b)).toEqual({ prevId: a, nextId: null, index: 3, count: 3 })
  })

  it('remembers the last topic read, forgetting deleted ones', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: null })
    expect(await lastReadItem(db)).toBeNull()
    await rememberReading(db, a)
    expect(await lastReadItem(db)).toBe(a)
    await deleteItem(db, a)
    expect(await lastReadItem(db)).toBeNull()
  })
})
