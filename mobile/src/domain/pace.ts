import { addDays } from './dates'
import type { Progress } from './types'

/** How a plan with a target date is doing (port of backend plan/Pace; keep in step). */
export const RECENT_DAYS = 28

export type PaceStatus = 'DONE' | 'ON_TRACK' | 'BEHIND' | 'OVERDUE' | 'NO_TOPICS'

export interface Pace {
  targetDate: string
  /** days from today to the target day, counting the target day (0 = overdue) */
  daysLeft: number
  /** topics still to complete */
  remaining: number
  /** topics per week needed to finish on time (null when done or overdue) */
  perWeekNeeded: number | null
  /** topics per week you completed over the last 28 days */
  perWeekRecent: number
  /** when you'd finish at the recent rate (null when the rate is 0 or done) */
  projectedFinish: string | null
  status: PaceStatus
}

const round1 = (v: number) => Math.round(v * 10) / 10

/** Whole days from `a` to `b` (yyyy-MM-dd), like ChronoUnit.DAYS.between. */
export function daysBetween(a: string, b: string): number {
  const [ay, am, ad] = a.split('-').map(Number)
  const [by, bm, bd] = b.split('-').map(Number)
  return Math.round((Date.UTC(by, bm - 1, bd) - Date.UTC(ay, am - 1, ad)) / 86_400_000)
}

/** @return null when the plan has no target date */
export function paceOf(targetDate: string | null, progress: Progress, completedRecently: number, today: string): Pace | null {
  if (!targetDate) return null
  const daysLeft = Math.max(0, daysBetween(today, targetDate) + 1)
  const remaining = progress.total - progress.completed
  const recent = round1((completedRecently * 7) / RECENT_DAYS)
  if (progress.total === 0) {
    return { targetDate, daysLeft, remaining: 0, perWeekNeeded: null, perWeekRecent: recent, projectedFinish: null, status: 'NO_TOPICS' }
  }
  if (remaining === 0) {
    return { targetDate, daysLeft, remaining: 0, perWeekNeeded: null, perWeekRecent: recent, projectedFinish: null, status: 'DONE' }
  }
  const projected = recent > 0 ? addDays(today, Math.ceil(remaining / (recent / 7)) - 1) : null
  if (daysLeft === 0) {
    return { targetDate, daysLeft: 0, remaining, perWeekNeeded: null, perWeekRecent: recent, projectedFinish: projected, status: 'OVERDUE' }
  }
  const needed = round1((remaining * 7) / daysLeft)
  const status: PaceStatus = projected !== null && projected <= targetDate ? 'ON_TRACK' : 'BEHIND'
  return { targetDate, daysLeft, remaining, perWeekNeeded: needed, perWeekRecent: recent, projectedFinish: projected, status }
}
