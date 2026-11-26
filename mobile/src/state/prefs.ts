import { create } from 'zustand'
import type { Db } from '@/db/types'

/**
 * Appearance and reading preferences of THIS phone. Kept in app_meta (never synced: the desktop
 * may well want another theme), loaded once at start-up, written whenever one changes.
 */
export type ThemeMode = 'system' | 'light' | 'dark'
export type ReadingFont = 'sans' | 'serif' | 'mono'
export type ReadingTheme = 'auto' | 'paper' | 'sepia' | 'night'
export type Accent = 'teal' | 'blue' | 'green' | 'amber' | 'violet'

export interface Prefs {
  theme: ThemeMode
  /** the one accent colour of the app */
  accent: Accent
  readingFont: ReadingFont
  /** multiplies the reading text size (1 = 17px in the reader) */
  textScale: number
  /** reading line height */
  lineHeight: number
  readingTheme: ReadingTheme
  /** learning actions a day (ticks + reviews + cards) that count as "goal reached" */
  dailyGoal: number
}

export const DEFAULT_PREFS: Prefs = {
  theme: 'system',
  accent: 'teal',
  readingFont: 'serif',
  textScale: 1,
  lineHeight: 1.7,
  readingTheme: 'auto',
  dailyGoal: 10,
}

export const TEXT_SCALES = [0.85, 1, 1.15, 1.3, 1.5]
export const LINE_HEIGHTS = [1.45, 1.7, 2]
export const DAILY_GOALS = [5, 10, 20, 30]

const KEY = 'pref_'

function parse(key: keyof Prefs, raw: string): Prefs[keyof Prefs] | undefined {
  const d = DEFAULT_PREFS[key]
  if (typeof d === 'number') {
    const n = Number(raw)
    return Number.isFinite(n) && n > 0 ? n : undefined
  }
  const allowed: Record<string, string[]> = {
    theme: ['system', 'light', 'dark'],
    accent: ['teal', 'blue', 'green', 'amber', 'violet'],
    readingFont: ['sans', 'serif', 'mono'],
    readingTheme: ['auto', 'paper', 'sepia', 'night'],
  }
  return allowed[key]?.includes(raw) ? (raw as Prefs[keyof Prefs]) : undefined
}

export async function loadPrefs(db: Db): Promise<Prefs> {
  const rows = await db.all<{ key: string; value: string }>("SELECT key, value FROM app_meta WHERE key LIKE 'pref\\_%' ESCAPE '\\'")
  const prefs: Prefs = { ...DEFAULT_PREFS }
  for (const r of rows) {
    const k = r.key.slice(KEY.length) as keyof Prefs
    if (!(k in DEFAULT_PREFS)) continue
    const v = parse(k, r.value)
    if (v !== undefined) (prefs as unknown as Record<string, unknown>)[k] = v
  }
  return prefs
}

export async function savePref<K extends keyof Prefs>(db: Db, key: K, value: Prefs[K]): Promise<void> {
  await db.run('INSERT OR REPLACE INTO app_meta (key, value) VALUES (?, ?)', [KEY + key, String(value)])
}

interface PrefsState extends Prefs {
  loaded: boolean
  hydrate: (p: Prefs) => void
  set: <K extends keyof Prefs>(db: Db, key: K, value: Prefs[K]) => Promise<void>
}

export const usePrefs = create<PrefsState>((set) => ({
  ...DEFAULT_PREFS,
  loaded: false,
  hydrate: (p) => set({ ...p, loaded: true }),
  set: async (db, key, value) => {
    set({ [key]: value } as Partial<PrefsState>)
    await savePref(db, key, value)
  },
}))
