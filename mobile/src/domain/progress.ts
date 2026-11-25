import type { Progress } from './types'

/** Leaf-based progress (backend progress/Progress). Never stored, always computed on read. */
export const NO_PROGRESS: Progress = Object.freeze({ completed: 0, total: 0, percent: null }) as Progress

export function progressOf(completed: number, total: number): Progress {
  if (total === 0) return NO_PROGRESS
  // one decimal place; Java's Math.round and JS's agree for non-negative numbers
  return { completed, total, percent: Math.round((completed * 1000) / total) / 10 }
}

export function plus(a: Progress, b: Progress): Progress {
  return progressOf(a.completed + b.completed, a.total + b.total)
}

export function remaining(p: Progress): number {
  return p.total - p.completed
}

/** "66.7%", or "N/A" for a subtree without checkable topics (frontend lib/format percentLabel). */
export function percentLabel(p: Progress): string {
  return p.percent === null ? 'N/A' : `${p.percent}%`
}
