import { describe, expect, it } from 'vitest'
import { reviewCard } from '../src/data/flashcards'
import { setCompletion } from '../src/data/items'
import { saveNote } from '../src/data/notes'
import { loadStats } from '../src/data/stats'
import { createItem, createPlan } from '../src/data/structure'
import { article, badgesOf, encouragement, levelOf, xpOf, type LearningTotals } from '../src/domain/gamification'
import { migratedDb } from './nodeDb'

const zero: LearningTotals = { topicsDone: 0, notes: 0, reviewStages: 0, cardStages: 0, plansFinished: 0, currentStreak: 0, bestStreak: 0 }

describe('levels and XP', () => {
  it('levels start at 0, 100, 300, 600, 1000 XP', () => {
    expect([0, 99, 100, 299, 300, 600, 1000].map((x) => levelOf(x).level)).toEqual([1, 1, 2, 2, 3, 4, 5])
    expect(levelOf(150)).toMatchObject({ level: 2, into: 50, span: 200, nextAt: 300 })
    expect(levelOf(-5).level).toBe(1)
  })

  it('weights topics, notes, reviews and cards', () => {
    expect(xpOf({ ...zero, topicsDone: 2, notes: 1, reviewStages: 3, cardStages: 4 })).toBe(40 + 15 + 30 + 20)
  })

  it('badges know what is earned and how close the rest are', () => {
    const b = badgesOf({ ...zero, topicsDone: 5, bestStreak: 7 })
    expect(b.find((x) => x.id === 'first-step')).toMatchObject({ earned: true, progress: 1 })
    expect(b.find((x) => x.id === 'topics-10')).toMatchObject({ earned: false, progress: 0.5 })
    expect(b.find((x) => x.id === 'streak-7')!.earned).toBe(true)
    expect(b.find((x) => x.id === 'streak-30')!.earned).toBe(false)
  })

  it('says "an 11-day streak" but "a 7-day streak"', () => {
    expect([1, 7, 8, 11, 18, 80, 100, 110, 11000, 18000, 21].map(article)).toEqual(['a', 'a', 'an', 'an', 'an', 'an', 'a', 'a', 'an', 'an', 'a'])
    expect(encouragement(0, 10, 11).message).toContain('an 11-day streak')
  })

  it('encourages according to the goal and streak', () => {
    expect(encouragement(10, 10, 4).title).toBe('Goal reached!')
    expect(encouragement(3, 10, 0).message).toBe("7 more to reach today's goal.")
    expect(encouragement(0, 10, 5).title).toBe('Keep your streak')
    expect(encouragement(0, 10, 0).title).toBe('Ready to learn?')
  })
})

describe('loadStats', () => {
  it('counts today’s actions (ticks, cards), the week and XP; imports earn nothing', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: null })
    const b = await createItem(db, p, { name: 'B', type: 'TOPIC', parentId: null })
    const imported = await createItem(db, p, { name: 'Imported', type: 'TOPIC', parentId: null })
    await db.run("UPDATE learning_item SET created_at = '2020-01-01T00:00:00.000Z'")
    await setCompletion(db, a, true)
    await setCompletion(db, imported, true)
    await db.run('UPDATE learning_item SET created_at = completed_at WHERE id = ?', [imported])
    await saveNote(db, b, 'Q: q\nA: a')
    const card = (await db.first<{ id: number }>('SELECT id FROM flashcard'))!
    await reviewCard(db, card.id, true)

    const s = await loadStats(db, 5)
    expect(s.totals).toMatchObject({ topicsDone: 1, notes: 1, cardStages: 1, plansFinished: 0, currentStreak: 1 })
    expect(s.actionsToday).toBe(2) // one tick + one card
    expect(s.week).toHaveLength(7)
    expect(s.week[6].actions).toBe(2)
    expect(s.topicsWeek[6].actions).toBe(1)
    expect(s.xp).toBe(20 + 15 + 5)
    expect(s.level.level).toBe(1)
    expect(s.badges.find((x) => x.id === 'first-step')!.earned).toBe(true)
  })

  it('counts finished plans', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: null })
    await createPlan(db, { name: 'Empty' })
    await db.run("UPDATE learning_item SET created_at = '2020-01-01T00:00:00.000Z'")
    await setCompletion(db, a, true)
    expect((await loadStats(db, 10)).totals.plansFinished).toBe(1)
  })

  it('notes imported with their plan earn no XP; notes you write later do', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'P' })
    const imported = await createItem(db, p, { name: 'Imported', type: 'TOPIC', parentId: null })
    const mine = await createItem(db, p, { name: 'Mine', type: 'TOPIC', parentId: null })
    await saveNote(db, imported, 'came with the plan') // saved in the same instant as its topic
    await db.run("UPDATE learning_item SET created_at = '2020-01-01T00:00:00.000Z' WHERE id = ?", [mine])
    await saveNote(db, mine, 'written later')
    expect((await loadStats(db, 10)).totals.notes).toBe(1)
  })
})
