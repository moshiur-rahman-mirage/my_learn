import { create } from 'zustand'
import type { Db } from '@/db/types'
import { logTime } from '@/data/extras'

/**
 * One app-wide Pomodoro timer (like the desktop's usePomodoro). Only the end time is stored
 * (app_meta 'pomodoro'), so a running timer survives the app being backgrounded or closed; when it
 * is over, its minutes are logged on the item it was started for.
 */
export interface Running {
  itemId: number
  itemName: string
  minutes: number
  /** epoch ms */
  endsAt: number
}

const KEY = 'pomodoro'

interface PomodoroState {
  running: Running | null
  load: (db: Db) => Promise<void>
  start: (db: Db, itemId: number, itemName: string, minutes?: number) => Promise<void>
  cancel: (db: Db) => Promise<void>
  /** Logs and clears a timer that is over; returns it, or null. */
  finishIfDue: (db: Db, now?: number) => Promise<Running | null>
}

async function persist(db: Db, r: Running | null) {
  if (r) await db.run('INSERT OR REPLACE INTO app_meta (key, value) VALUES (?, ?)', [KEY, JSON.stringify(r)])
  else await db.run('DELETE FROM app_meta WHERE key = ?', [KEY])
}

export const usePomodoro = create<PomodoroState>((set, get) => ({
  running: null,
  load: async (db) => {
    const row = await db.first<{ value: string }>('SELECT value FROM app_meta WHERE key = ?', [KEY])
    try {
      set({ running: row ? (JSON.parse(row.value) as Running) : null })
    } catch {
      set({ running: null })
    }
  },
  start: async (db, itemId, itemName, minutes = 25) => {
    const r: Running = { itemId, itemName, minutes, endsAt: Date.now() + minutes * 60_000 }
    await persist(db, r)
    set({ running: r })
  },
  cancel: async (db) => {
    await persist(db, null)
    set({ running: null })
  },
  finishIfDue: async (db, now = Date.now()) => {
    const r = get().running
    if (!r || now < r.endsAt) return null
    set({ running: null })
    await persist(db, null)
    try {
      await logTime(db, r.itemId, r.minutes)
    } catch {
      // the item was deleted meanwhile: nothing to log
    }
    return r
  },
}))
