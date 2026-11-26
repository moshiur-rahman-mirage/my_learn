import { describe, expect, it } from 'vitest'
import { isShortReadPlanIncluded, isShortReadRead, loadUnreadShortReads, markShortRead, setShortReadPlanIncluded } from '../src/data/shortRead'
import { rememberReading } from '../src/data/reading'
import { saveNote } from '../src/data/notes'
import { createItem, createPlan } from '../src/data/structure'
import { estimateReadingTime, recommendRandomShortReads, recommendShortReads, type ShortReadCandidate } from '../src/domain/shortRead'
import { migratedDb } from './nodeDb'

const words = (count: number) => Array(count).fill('word').join(' ')
const candidate = (itemId: number, minutes: number): ShortReadCandidate => ({
  itemId, name: `Topic ${itemId}`, planName: 'Plan', parentName: null, minutes, wordCount: minutes * 180,
})

describe('short read estimates', () => {
  it('uses 180 WPM and weights code, tables, and images', () => {
    expect(estimateReadingTime(words(180), 'MARKDOWN')).toEqual({ wordCount: 180, minutes: 1 })
    expect(estimateReadingTime(`\`\`\`java\n${words(120)}\n\`\`\``, 'MARKDOWN')).toEqual({ wordCount: 120, minutes: 1 })
    expect(estimateReadingTime(`<p>${words(100)}</p><pre>${words(60)}</pre><table><tr><td>${words(20)}</td></tr></table>`, 'HTML'))
      .toEqual({ wordCount: 180, minutes: 2 })
    expect(estimateReadingTime(`<p>${words(150)}</p><img src="diagram.png">`, 'HTML').minutes).toBe(2)
  })

  it('selects unread notes that maximize time without exceeding the budget', () => {
    expect(recommendShortReads([candidate(1, 6), candidate(2, 4), candidate(3, 8), candidate(4, 12)], 10)
      .map((item) => item.itemId)).toEqual([1, 2])
    expect(recommendShortReads([candidate(1, 12)], 10)).toEqual([])
  })

  it('randomizes refreshes to lead with another fitting topic when possible', () => {
    const candidates = [candidate(1, 6), candidate(2, 4), candidate(3, 8)]
    const first = recommendRandomShortReads(candidates, 10, null, () => 0)
    const refreshed = recommendRandomShortReads(candidates, 10, first[0].itemId, () => 0)
    expect(refreshed[0].itemId).not.toBe(first[0].itemId)
    expect([...first, ...refreshed].every((item) => item.minutes <= 10)).toBe(true)
    expect(first.reduce((sum, item) => sum + item.minutes, 0)).toBeLessThanOrEqual(10)
    expect(refreshed.reduce((sum, item) => sum + item.minutes, 0)).toBeLessThanOrEqual(10)
  })
})

describe('unread short reads', () => {
  it('loads active unread leaf-topic notes, marks them read on request, and resets read state on edits', async () => {
    const db = await migratedDb()
    const plan = await createPlan(db, { name: 'Java' })
    const module = await createItem(db, plan, { name: 'Collections', type: 'MODULE', parentId: null })
    const topic = await createItem(db, plan, { name: 'HashMap', type: 'TOPIC', parentId: module })
    const noNote = await createItem(db, plan, { name: 'No note', type: 'TOPIC', parentId: module })
    await saveNote(db, topic, words(360))
    await saveNote(db, noNote, '')

    expect(await isShortReadPlanIncluded(db, plan)).toBe(true)
    await setShortReadPlanIncluded(db, plan, false)
    expect(await loadUnreadShortReads(db)).toEqual([])
    await setShortReadPlanIncluded(db, plan, true)

    const archived = await createPlan(db, { name: 'Archived' })
    await db.run("UPDATE learning_plan SET status = 'ARCHIVED' WHERE id = ?", [archived])
    const archivedTopic = await createItem(db, archived, { name: 'Old note', type: 'TOPIC', parentId: null })
    await saveNote(db, archivedTopic, 'archived note')

    expect(await loadUnreadShortReads(db)).toEqual([{
      itemId: topic, name: 'HashMap', planName: 'Java', parentName: 'Collections', wordCount: 360, minutes: 2,
    }])
    await rememberReading(db, topic)
    expect(await isShortReadRead(db, topic)).toBe(false)
    expect(await loadUnreadShortReads(db)).toHaveLength(1)
    await markShortRead(db, topic)
    expect(await isShortReadRead(db, topic)).toBe(true)
    expect(await loadUnreadShortReads(db)).toEqual([])
    await db.run("UPDATE item_note SET content = 'updated note', updated_at = '2026-11-26T00:00:00.000Z' WHERE item_id = ?", [topic])
    expect((await loadUnreadShortReads(db)).map((item) => item.itemId)).toEqual([topic])
  })
})
