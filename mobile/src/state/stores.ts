import { create } from 'zustand'

/**
 * App-wide state that several screens share. Everything else lives in the database and is read
 * per screen; Zustand is only used where a change on one screen must reach others.
 */

/** Bumped after any write (tick, note save, sync), so open screens reload from the database. */
export const useDataVersion = create<{ version: number; bump: () => void }>((set) => ({
  version: 0,
  bump: () => set((s) => ({ version: s.version + 1 })),
}))

/** Which tree rows are expanded, per plan (kept for the app session). */
export const useExpanded = create<{
  byPlan: Record<number, number[]>
  isSet: (planId: number) => boolean
  set: (planId: number, ids: number[]) => void
  toggle: (planId: number, id: number) => void
}>((set, get) => ({
  byPlan: {},
  isSet: (planId) => planId in get().byPlan,
  set: (planId, ids) => set((s) => ({ byPlan: { ...s.byPlan, [planId]: ids } })),
  toggle: (planId, id) =>
    set((s) => {
      const cur = s.byPlan[planId] ?? []
      return { byPlan: { ...s.byPlan, [planId]: cur.includes(id) ? cur.filter((x) => x !== id) : [...cur, id] } }
    }),
}))
