import { addDays, formatDay } from './dates'
import type { Priority, TreeNode } from './types'

/** Ports of backend dashboard/TodayService.nextTopics and ActivityCalculator's streaks. */

export const NEXT_PER_PLAN = 3
/** Completed within this time of being created = came in ticked (an import), not done by you. */
export const IMPORTED_TOLERANCE_MS = 5_000

export interface NextTopic {
  itemId: number
  name: string
  priority: Priority | null
  /** ancestor names, root first */
  path: string[]
}

function rank(p: Priority | null): number {
  return p === null ? 2 : p === 'MUST' ? 0 : p === 'SHOULD' ? 1 : 3
}

/**
 * The first unfinished topics in roadmap order; MUST before SHOULD, no priority and NICE,
 * keeping roadmap order within each group (a stable sort).
 */
export function nextTopics(roots: TreeNode[], limit = NEXT_PER_PLAN): NextTopic[] {
  const open: NextTopic[] = []
  const collect = (nodes: TreeNode[], path: string[]) => {
    for (const n of nodes) {
      if (n.checkable && !n.completed) open.push({ itemId: n.id, name: n.name, priority: n.priority, path: [...path] })
      if (n.children.length) collect(n.children, [...path, n.name])
    }
  }
  collect(roots, [])
  return open
    .map((t, i) => ({ t, i }))
    .sort((a, b) => rank(a.t.priority) - rank(b.t.priority) || a.i - b.i)
    .slice(0, limit)
    .map((x) => x.t)
}

export interface LeafTimes {
  createdAt: string
  completedAt: string | null
}

/** Local calendar days on which you completed at least one topic (imports excluded). */
export function activeDays(leaves: LeafTimes[]): Set<string> {
  const days = new Set<string>()
  for (const l of leaves) {
    if (!l.completedAt) continue
    const imported = Math.abs(new Date(l.completedAt).getTime() - new Date(l.createdAt).getTime()) <= IMPORTED_TOLERANCE_MS
    if (!imported) days.add(formatDay(new Date(l.completedAt)))
  }
  return days
}

/** Consecutive days with a completion, ending today (or yesterday: today isn't over yet). */
export function currentStreak(days: Set<string>, today: string): number {
  let d = days.has(today) ? today : addDays(today, -1)
  let streak = 0
  while (days.has(d)) {
    streak++
    d = addDays(d, -1)
  }
  return streak
}

export function bestStreak(days: Set<string>): number {
  const sorted = [...days].sort()
  let best = 0
  let run = 0
  let previous: string | null = null
  for (const d of sorted) {
    run = previous !== null && addDays(previous, 1) === d ? run + 1 : 1
    best = Math.max(best, run)
    previous = d
  }
  return best
}

/** Completions by you on days in [from, to] (inclusive, yyyy-MM-dd). */
export function completedBetween(leaves: LeafTimes[], from: string, to: string): number {
  let n = 0
  for (const l of leaves) {
    if (!l.completedAt) continue
    if (Math.abs(new Date(l.completedAt).getTime() - new Date(l.createdAt).getTime()) <= IMPORTED_TOLERANCE_MS) continue
    const day = formatDay(new Date(l.completedAt))
    if (day >= from && day <= to) n++
  }
  return n
}
