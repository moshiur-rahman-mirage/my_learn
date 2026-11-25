import { describe, expect, it } from 'vitest'
import { cardCounts, dueCards, reviewCard } from '../src/data/flashcards'
import { RuleError, setCompletion } from '../src/data/items'
import { backlinks, getNote, isEmptyNote, resolveLink, saveNote } from '../src/data/notes'
import { dueReviews, reviewTopic } from '../src/data/reviews'
import { search } from '../src/data/search'
import { addDays, today } from '../src/domain/dates'
import { segments, toMatchQuery } from '../src/domain/search'
import { insertItem, insertPlan } from './fixtures'
import { migratedDb } from './nodeDb'

async function setup() {
  const db = await migratedDb()
  const planId = await insertPlan(db, 'Spring')
  const moduleId = await insertItem(db, planId, 'Core', 'MODULE')
  const topicId = await insertItem(db, planId, 'Dependency Injection', 'TOPIC', moduleId)
  return { db, planId, moduleId, topicId }
}

const forceDue = (db: Awaited<ReturnType<typeof migratedDb>>, table: string) => db.run(`UPDATE ${table} SET due_on = ?`, [today()])

describe('completion (ItemService.setCompletion)', () => {
  it('ticks a leaf topic, schedules its first review tomorrow, bumps its version', async () => {
    const { db, topicId } = await setup()
    const item = await setCompletion(db, topicId, true)
    expect(item.completed).toBe(true)
    expect(item.completedAt).toMatch(/Z$/)
    expect(item.version).toBe(1)
    expect(await db.first('SELECT stage, due_on FROM item_review WHERE item_id = ?', [topicId]))
      .toEqual({ stage: 0, due_on: addDays(today(), 1) })
  })

  it('unticking clears completion and cancels the review', async () => {
    const { db, topicId } = await setup()
    await setCompletion(db, topicId, true)
    const item = await setCompletion(db, topicId, false)
    expect(item.completed).toBe(false)
    expect(item.completedAt).toBeNull()
    expect(await db.first('SELECT * FROM item_review WHERE item_id = ?', [topicId])).toBeNull()
  })

  it('ticking twice keeps the first completion time', async () => {
    const { db, topicId } = await setup()
    const first = await setCompletion(db, topicId, true)
    const second = await setCompletion(db, topicId, true)
    expect(second.completedAt).toBe(first.completedAt)
    expect(second.version).toBe(1)
  })

  it('refuses modules and topics with children', async () => {
    const { db, planId, moduleId, topicId } = await setup()
    await expect(setCompletion(db, moduleId, true)).rejects.toBeInstanceOf(RuleError)
    await insertItem(db, planId, 'Sub', 'TOPIC', topicId)
    await expect(setCompletion(db, topicId, true)).rejects.toThrow(/Only topics without sub-items/)
  })

  it('is recorded for sync', async () => {
    const { db, topicId } = await setup()
    const ext = (await db.first<{ external_id: string }>('SELECT external_id FROM learning_item WHERE id = ?', [topicId]))!.external_id
    const before = (await db.first<{ seq: number }>("SELECT seq FROM sync_change WHERE entity = 'item' AND key = ?", [ext]))!.seq
    await setCompletion(db, topicId, true)
    const after = (await db.first<{ seq: number }>("SELECT seq FROM sync_change WHERE entity = 'item' AND key = ?", [ext]))!.seq
    expect(after).toBeGreaterThan(before)
    expect(await db.first("SELECT deleted FROM sync_change WHERE entity = 'review' AND key = ?", [ext])).toEqual({ deleted: 0 })
  })
})

describe('topic reviews (ReviewService): 1, 7, 30 days', () => {
  it('walks through the stages and finishes', async () => {
    const { db, topicId } = await setup()
    await setCompletion(db, topicId, true)
    await forceDue(db, 'item_review')
    expect((await dueReviews(db)).map((r) => [r.name, r.review, r.of, r.parentName])).toEqual([['Dependency Injection', 1, 3, 'Core']])

    expect(await reviewTopic(db, topicId, true)).toEqual({ finished: false, nextDue: addDays(today(), 7) })
    expect(await reviewTopic(db, topicId, true)).toEqual({ finished: false, nextDue: addDays(today(), 30) })
    expect(await reviewTopic(db, topicId, true)).toEqual({ finished: true, nextDue: null })
    expect(await dueReviews(db)).toEqual([])
  })

  it('forgetting starts over at one day', async () => {
    const { db, topicId } = await setup()
    await setCompletion(db, topicId, true)
    await reviewTopic(db, topicId, true)
    expect(await reviewTopic(db, topicId, false)).toEqual({ finished: false, nextDue: addDays(today(), 1) })
  })

  it('hides reviews of archived plans', async () => {
    const { db, planId, topicId } = await setup()
    await setCompletion(db, topicId, true)
    await forceDue(db, 'item_review')
    await db.run("UPDATE learning_plan SET status = 'ARCHIVED' WHERE id = ?", [planId])
    expect(await dueReviews(db)).toEqual([])
  })
})

describe('notes (NoteService)', () => {
  it('saves a note and derives flashcards, links and search text', async () => {
    const { db, planId, topicId } = await setup()
    const target = await insertItem(db, planId, 'Bean Scopes', 'TOPIC')
    await saveNote(db, topicId, 'Spring wires beans. See [[bean  scopes]].\n\nQ: What is DI?\nA: Handing objects their dependencies.')

    expect((await getNote(db, topicId)).format).toBe('MARKDOWN')
    expect(await db.all('SELECT question, stage FROM flashcard WHERE item_id = ?', [topicId])).toEqual([{ question: 'What is DI?', stage: 0 }])
    expect((await backlinks(db, target)).map((b) => b.name)).toEqual(['Dependency Injection'])
    expect(await resolveLink(db, planId, 'BEAN scopes')).toBe(target)
    expect((await search(db, 'handing')).hits.map((h) => h.matchedIn)).toEqual(['note'])
  })

  it('an empty note is deleted with everything derived from it', async () => {
    const { db, topicId } = await setup()
    await saveNote(db, topicId, 'Q: a\nA: b')
    await saveNote(db, topicId, '   ')
    expect((await getNote(db, topicId)).content).toBeNull()
    expect(await db.all('SELECT * FROM flashcard WHERE item_id = ?', [topicId])).toEqual([])
    expect(await db.first("SELECT deleted FROM sync_change WHERE entity = 'note'")).toEqual({ deleted: 1 })
  })

  it('treats markup-only HTML as empty, but not an image or rule', () => {
    expect(isEmptyNote('<p>&nbsp;</p><p></p>', 'HTML')).toBe(true)
    expect(isEmptyNote('<p><img src="x"></p>', 'HTML')).toBe(false)
    expect(isEmptyNote('<hr>', 'HTML')).toBe(false)
    expect(isEmptyNote('<p></p>', 'MARKDOWN')).toBe(false)
  })

  it('editing an answer keeps the card schedule; removing a question deletes the card', async () => {
    const { db, topicId } = await setup()
    await saveNote(db, topicId, 'Q: Keep me\nA: old\n\nQ: Drop me\nA: x')
    const id = (await db.first<{ id: number }>("SELECT id FROM flashcard WHERE question = 'Keep me'"))!.id
    await db.run('UPDATE flashcard SET stage = 3 WHERE id = ?', [id])

    await saveNote(db, topicId, 'Q: keep   ME\nA: new answer')
    expect(await db.all('SELECT id, question, answer, stage FROM flashcard WHERE item_id = ?', [topicId]))
      .toEqual([{ id, question: 'keep   ME', answer: 'new answer', stage: 3 }])
  })
})

describe('flashcards (FlashcardService): 1, 3, 7, 14, 30, 60, 120 days', () => {
  it('remembered moves through the intervals; forgot is due again today', async () => {
    const { db, planId, topicId } = await setup()
    await saveNote(db, topicId, 'Q: q\nA: a')
    const [card] = await dueCards(db)
    expect(card.question).toBe('q')
    expect(await cardCounts(db, planId)).toEqual({ total: 1, due: 1 })

    expect(await reviewCard(db, card.id, true)).toEqual({ stage: 1, nextDue: addDays(today(), 1) })
    expect(await reviewCard(db, card.id, true)).toEqual({ stage: 2, nextDue: addDays(today(), 3) })
    expect(await reviewCard(db, card.id, false)).toEqual({ stage: 0, nextDue: today() })
    await db.run('UPDATE flashcard SET stage = 20 WHERE id = ?', [card.id])
    expect(await reviewCard(db, card.id, true)).toEqual({ stage: 21, nextDue: addDays(today(), 120) })
  })

  it('a schedule change is recorded for sync', async () => {
    const { db, topicId } = await setup()
    await saveNote(db, topicId, 'Q: q\nA: a')
    expect(await db.first("SELECT * FROM sync_change WHERE entity = 'flashcard'")).toBeNull()
    const [card] = await dueCards(db)
    await reviewCard(db, card.id, true)
    expect(await db.first("SELECT deleted FROM sync_change WHERE entity = 'flashcard'")).toEqual({ deleted: 0 })
  })
})

describe('search (SearchService)', () => {
  it('makes every word a safe quoted prefix', () => {
    expect(toMatchQuery('sprin boo')).toBe('"sprin"* "boo"*')
    expect(toMatchQuery('C++ "AND" (x) OR-y')).toBe('"C"* "AND"* "x"* "OR"* "y"*')
    expect(toMatchQuery('***')).toBeNull()
    expect(toMatchQuery('Ünïcödé 中文')).toBe('"Ünïcödé"* "中文"*')
  })

  it('finds by prefix with the path and name hits first', async () => {
    const { db, planId, topicId } = await setup()
    await insertItem(db, planId, 'Other', 'TOPIC', null, { order: 1 })
    await db.run("UPDATE learning_item SET description = 'about dependency handling' WHERE name = 'Other'")
    const r = await search(db, 'depend inj')
    expect(r.hits.map((h) => [h.name, h.path, h.matchedIn])).toEqual([['Dependency Injection', ['Core'], 'name']])
    expect(r.hits[0].itemId).toBe(topicId)
    const d = await search(db, 'handl')
    expect(d.hits[0].matchedIn).toBe('description')
    expect(d.hits[0].snippet.some((s) => s.hit && s.text.toLowerCase().startsWith('handl'))).toBe(true)
  })

  it('filters by tag, with or without words', async () => {
    const { db, topicId } = await setup()
    await db.run("INSERT INTO tag (name) VALUES ('Interview')")
    await db.run('INSERT INTO item_tag (item_id, tag_id) SELECT ?, id FROM tag', [topicId])
    expect((await search(db, '', 'interview')).hits.map((h) => h.name)).toEqual(['Dependency Injection'])
    expect((await search(db, 'core', 'interview')).hits).toEqual([])
    expect(await search(db, '  ')).toEqual({ hits: [], more: false })
  })

  it('splits snippets at the hit markers', () => {
    expect(segments('a \u0002hit\u0003 b')).toEqual([{ text: 'a ', hit: false }, { text: 'hit', hit: true }, { text: ' b', hit: false }])
  })
})
