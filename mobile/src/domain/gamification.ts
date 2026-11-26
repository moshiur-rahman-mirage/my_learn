/**
 * Gamification, derived entirely from synced learning data (ticked topics, review and flashcard
 * progress, notes), so levels, streaks and badges are the same on every device and can't drift.
 */

export const XP = { topic: 20, note: 15, reviewStage: 10, cardStage: 5 } as const

export interface LearningTotals {
  /** topics you completed yourself (imports that came in ticked don't count) */
  topicsDone: number
  notes: number
  /** review stages passed (1, 7, 30 days) on topics still under review, plus finished ones */
  reviewStages: number
  /** sum of flashcard stages: each "Remembered" in a row moves a card up a stage */
  cardStages: number
  /** plans with every topic done */
  plansFinished: number
  currentStreak: number
  bestStreak: number
}

export function xpOf(t: LearningTotals): number {
  return t.topicsDone * XP.topic + t.notes * XP.note + t.reviewStages * XP.reviewStage + t.cardStages * XP.cardStage
}

export interface Level {
  level: number
  /** XP earned inside this level, and the XP the level takes */
  into: number
  span: number
  /** total XP needed to reach the next level */
  nextAt: number
  title: string
}

const TITLES = ['Curious', 'Learner', 'Explorer', 'Practitioner', 'Scholar', 'Expert', 'Master', 'Sage', 'Luminary']

/** Level n starts at 50·n·(n−1) XP: 0, 100, 300, 600, 1000, … (each level takes 100 more than the last). */
export function levelOf(xp: number): Level {
  const n = Math.max(1, Math.floor((1 + Math.sqrt(1 + (8 * Math.max(0, xp)) / 100)) / 2))
  const start = 50 * n * (n - 1)
  const nextAt = 50 * (n + 1) * n
  return { level: n, into: xp - start, span: nextAt - start, nextAt, title: TITLES[Math.min(TITLES.length - 1, Math.floor((n - 1) / 3))] }
}

export interface Badge {
  id: string
  title: string
  description: string
  icon: string
  earned: boolean
  /** 0..1 towards earning it */
  progress: number
}

function badge(id: string, title: string, description: string, icon: string, value: number, target: number): Badge {
  return { id, title, description, icon, earned: value >= target, progress: Math.min(1, target === 0 ? 1 : value / target) }
}

/** Every badge, earned or not, in a stable order. */
export function badgesOf(t: LearningTotals): Badge[] {
  return [
    badge('first-step', 'First step', 'Complete your first topic', 'footsteps-outline', t.topicsDone, 1),
    badge('topics-10', 'Getting going', 'Complete 10 topics', 'checkmark-done-outline', t.topicsDone, 10),
    badge('topics-50', 'On a roll', 'Complete 50 topics', 'rocket-outline', t.topicsDone, 50),
    badge('topics-100', 'Centurion', 'Complete 100 topics', 'medal-outline', t.topicsDone, 100),
    badge('topics-500', 'Unstoppable', 'Complete 500 topics', 'trophy-outline', t.topicsDone, 500),
    badge('streak-3', 'Warming up', 'A 3-day streak', 'flame-outline', t.bestStreak, 3),
    badge('streak-7', 'One week strong', 'A 7-day streak', 'flame', t.bestStreak, 7),
    badge('streak-30', 'Habit formed', 'A 30-day streak', 'bonfire-outline', t.bestStreak, 30),
    badge('notes-10', 'Note taker', 'Write 10 notes', 'create-outline', t.notes, 10),
    badge('notes-100', 'Author', 'Write 100 notes', 'library-outline', t.notes, 100),
    badge('cards-100', 'Card shark', 'Move flashcards up 100 stages', 'albums-outline', t.cardStages, 100),
    badge('reviews-50', 'Memory keeper', 'Pass 50 topic reviews', 'refresh-circle-outline', t.reviewStages, 50),
    badge('plan-done', 'Finisher', 'Finish a whole plan', 'ribbon-outline', t.plansFinished, 1),
  ]
}

export interface DayActivity {
  /** yyyy-MM-dd */
  day: string
  /** learning actions that day (topics ticked, reviews and flashcards done) */
  actions: number
}

/** "a" or "an" before a number as it's spoken: an 8, an 11, an 18, an 80, an 11000; a 1, a 7, a 100. */
export function article(n: number): 'a' | 'an' {
  const s = String(Math.abs(Math.trunc(n)))
  return s.startsWith('8') || /^(11|18)(\d{3})*$/.test(s) ? 'an' : 'a'
}

/** A friendly line for the dashboard banner. */
export function encouragement(actionsToday: number, goal: number, streak: number): { title: string; message: string } {
  if (actionsToday >= goal) return { title: 'Goal reached!', message: streak > 1 ? `${streak} days in a row. Keep the streak alive tomorrow.` : 'Great work today.' }
  if (actionsToday > 0) return { title: 'Nice progress', message: `${goal - actionsToday} more to reach today's goal.` }
  if (streak > 0) return { title: 'Keep your streak', message: `You're on ${article(streak)} ${streak}-day streak. One topic or a few cards keeps it going.` }
  return { title: 'Ready to learn?', message: 'Start with a few flashcards or the next topic in a plan.' }
}
