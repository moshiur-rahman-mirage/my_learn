import { describe, expect, it } from 'vitest'
import { loadToday } from '../src/data/today'
import { setCompletion } from '../src/data/items'
import { saveNote } from '../src/data/notes'
import { addDays, today } from '../src/domain/dates'
import { activeDays, bestStreak, currentStreak, nextTopics } from '../src/domain/today'
import { buildTree } from '../src/domain/tree'
import { done, insertItem, insertPlan, row } from './fixtures'
import { migratedDb } from './nodeDb'

describe('nextTopics (TodayService)', () => {
  it('takes open topics in roadmap order, MUST first, then SHOULD, none, NICE; at most 3', () => {
    const m = row('M', 'MODULE', null, 0)
    const items = [
      m,
      { ...row('nice', 'TOPIC', m, 0), priority: 'NICE' as const },
      { ...row('none', 'TOPIC', m, 1) },
      done({ ...row('done must', 'TOPIC', m, 2), priority: 'MUST' as const }),
      { ...row('should', 'TOPIC', m, 3), priority: 'SHOULD' as const },
      { ...row('must', 'TOPIC', m, 4), priority: 'MUST' as const },
    ]
    const next = nextTopics(buildTree(items))
    expect(next.map((t) => t.name)).toEqual(['must', 'should', 'none'])
    expect(next[0].path).toEqual(['M'])
  })
})

describe('streaks (ActivityCalculator)', () => {
  const at = (day: string, h = 10) => new Date(`${day}T${String(h).padStart(2, '0')}:00:00`).toISOString()
  const t = today()

  it('counts consecutive days ending today or yesterday', () => {
    const leaves = [-0, -1, -2, -5].map((d) => ({ createdAt: at(addDays(t, -30)), completedAt: at(addDays(t, d)) }))
    const days = activeDays(leaves)
    expect(currentStreak(days, t)).toBe(3)
    expect(currentStreak(days, addDays(t, 1))).toBe(3) // tomorrow morning: still alive until the day ends
    expect(bestStreak(days)).toBe(3)
  })

  it('ignores topics that came in already ticked (an import)', () => {
    const imported = { createdAt: at(t), completedAt: new Date(new Date(at(t)).getTime() + 2000).toISOString() }
    expect(activeDays([imported]).size).toBe(0)
  })
})

describe('loadToday', () => {
  it('gathers next topics, due cards, time and today’s completions', async () => {
    const db = await migratedDb()
    const p = await insertPlan(db, 'Java')
    await insertPlan(db, 'Archived', 'ARCHIVED')
    const a = await insertItem(db, p, 'A', 'TOPIC')
    await insertItem(db, p, 'B', 'TOPIC', null, { order: 1 })
    await db.run("UPDATE learning_item SET created_at = '2020-01-01T00:00:00.000Z'")
    await setCompletion(db, a, true)
    await saveNote(db, a, 'Q: q\nA: a')
    await db.run('INSERT INTO time_log (item_id, minutes, logged_at) VALUES (?, 25, ?)', [a, new Date().toISOString()])

    const d = await loadToday(db)
    expect(d.nextUp).toEqual([{ planId: p, planName: 'Java', topics: [expect.objectContaining({ name: 'B' })] }])
    expect(d.dueCards).toBe(1)
    expect(d.minutesToday).toBe(25)
    expect(d.completedToday).toBe(1)
    expect(d.currentStreak).toBe(1)
    expect(d.dueReviews).toEqual([]) // first review is due tomorrow
  })
})
