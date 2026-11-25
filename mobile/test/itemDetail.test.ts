import { describe, expect, it } from 'vitest'
import { getItemDetail } from '../src/data/itemDetail'
import { setCompletion } from '../src/data/items'
import { saveNote } from '../src/data/notes'
import { insertItem, insertPlan, T0 } from './fixtures'
import { migratedDb } from './nodeDb'

describe('getItemDetail', () => {
  it('collects everything the item screen shows', async () => {
    const db = await migratedDb()
    const planId = await insertPlan(db, 'Java')
    const m = await insertItem(db, planId, 'Collections', 'MODULE')
    const list = await insertItem(db, planId, 'ArrayList', 'TOPIC', m)
    const map = await insertItem(db, planId, 'Hash  Map', 'TOPIC', m, { order: 1 })
    await saveNote(db, list, 'See [[hash map]].\n\nQ: Growth?\nA: 1.5x')
    await saveNote(db, map, 'Buckets.')
    await setCompletion(db, map, true)
    await db.run("INSERT INTO tag (name) VALUES ('core')")
    await db.run('INSERT INTO item_tag (item_id, tag_id) SELECT ?, id FROM tag', [map])
    await db.run("INSERT INTO item_link (item_id, url, title, created_at) VALUES (?, 'https://x.dev', 'X', ?)", [map, T0])
    await db.run('INSERT INTO time_log (item_id, minutes, logged_at) VALUES (?, 50, ?)', [map, T0])

    const d = (await getItemDetail(db, map))!
    expect(d.planName).toBe('Java')
    expect(d.path).toEqual(['Collections'])
    expect(d.checkable).toBe(true)
    expect(d.item.completed).toBe(true)
    expect(d.reviewDueOn).not.toBeNull()
    expect(d.tags).toEqual(['core'])
    expect(d.links.map((l) => l.title)).toEqual(['X'])
    expect(d.minutes).toBe(50)
    expect(d.note.content).toBe('Buckets.')
    expect(d.backlinks.map((b) => b.name)).toEqual(['ArrayList'])
    expect(d.planNames.sort()).toEqual(['arraylist', 'collections', 'hash map'])

    const mod = (await getItemDetail(db, m))!
    expect(mod.checkable).toBe(false)
    expect(mod.childCount).toBe(2)
    expect((await getItemDetail(db, list))!.flashcards).toBe(1)
    expect(await getItemDetail(db, 9999)).toBeNull()
  })
})
