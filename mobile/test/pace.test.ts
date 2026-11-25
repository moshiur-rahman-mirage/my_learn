import { describe, expect, it } from 'vitest'
import { setCompletion } from '../src/data/items'
import { getPlan, recentCompletionsByPlan } from '../src/data/plans'
import { createItem, createPlan } from '../src/data/structure'
import { addDays, today } from '../src/domain/dates'
import { daysBetween, paceOf } from '../src/domain/pace'
import { progressOf } from '../src/domain/progress'
import { migratedDb } from './nodeDb'

const T = '2026-11-25'

describe('paceOf (plan/Pace)', () => {
  it('is null without a target date', () => {
    expect(paceOf(null, progressOf(1, 10), 4, T)).toBeNull()
  })

  it('counts the target day itself and needs remaining*7/days per week', () => {
    // 10 days left (25th..4th inclusive), 20 remaining → 14/week needed; 28 done in 28 days → 7/week
    const p = paceOf('2026-12-04', progressOf(10, 30), 28, T)!
    expect(p).toMatchObject({ daysLeft: 10, remaining: 20, perWeekNeeded: 14, perWeekRecent: 7, status: 'BEHIND' })
    // at 7/week (1/day) the 20 remaining take 20 days: day 1 is today → finishes on the 14th
    expect(p.projectedFinish).toBe('2026-12-14')
  })

  it('is on track when the recent rate finishes by the target day', () => {
    const p = paceOf('2026-12-31', progressOf(25, 30), 28, T)!
    expect(p.status).toBe('ON_TRACK')
    expect(p.projectedFinish).toBe('2026-11-29') // 5 remaining at 1/day
  })

  it('overdue, done and no topics', () => {
    expect(paceOf('2026-11-20', progressOf(1, 5), 0, T)).toMatchObject({ daysLeft: 0, status: 'OVERDUE', perWeekNeeded: null, projectedFinish: null })
    expect(paceOf('2026-12-31', progressOf(5, 5), 0, T)).toMatchObject({ status: 'DONE', remaining: 0 })
    expect(paceOf('2026-12-31', progressOf(0, 0), 0, T)).toMatchObject({ status: 'NO_TOPICS' })
  })

  it('target day today = 1 day left; no recent work = behind', () => {
    expect(paceOf(T, progressOf(0, 3), 0, T)).toMatchObject({ daysLeft: 1, perWeekNeeded: 21, status: 'BEHIND', projectedFinish: null })
  })

  it('rounds weekly rates to one decimal', () => {
    expect(paceOf('2026-12-31', progressOf(0, 10), 5, T)!.perWeekRecent).toBe(1.3) // 5*7/28 = 1.25 → 1.3
    expect(daysBetween('2026-03-28', '2026-03-30')).toBe(2) // across a DST change in many zones
  })
})

describe('pace on plans', () => {
  it('uses your completions of the last 28 days, not imports', async () => {
    const db = await migratedDb()
    const planId = await createPlan(db, { name: 'P', targetDate: addDays(today(), 13) })
    const a = await createItem(db, planId, { name: 'A', type: 'TOPIC', parentId: null })
    const b = await createItem(db, planId, { name: 'B', type: 'TOPIC', parentId: null })
    await createItem(db, planId, { name: 'C', type: 'TOPIC', parentId: null })
    await db.run("UPDATE learning_item SET created_at = '2020-01-01T00:00:00.000Z'")
    await setCompletion(db, a, true)
    await setCompletion(db, b, true)
    await db.run("UPDATE learning_item SET created_at = completed_at WHERE id = ?", [b]) // b came in ticked (import)

    expect((await recentCompletionsByPlan(db)).get(planId)).toBe(1)
    const plan = (await getPlan(db, planId))!
    expect(plan.pace).toMatchObject({ daysLeft: 14, remaining: 1, perWeekRecent: 0.3, perWeekNeeded: 0.5 })
  })
})
