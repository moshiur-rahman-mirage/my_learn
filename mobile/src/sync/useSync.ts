import { Platform } from 'react-native'
import { create } from 'zustand'
import type { Db } from '@/db/types'
import { useDataVersion } from '@/state/stores'
import { fetchTransport, SyncError, syncNow, type SyncProgress, type SyncReport } from './client'
import { phoneName } from './phoneName'

/** Sync status shared by the Sync tab and pull-to-refresh on other screens. */
interface SyncState {
  running: boolean
  /** while running: pages and changes received so far */
  progress: SyncProgress | null
  lastReport: SyncReport | null
  lastError: string | null
  errorKind: SyncError['kind'] | null
  run: (db: Db) => Promise<SyncReport | null>
}

/** A readable name for this phone, shown on the desktop's list of synced devices. */
export function deviceName(): string {
  const c = Platform.constants as { Model?: string; Brand?: string }
  return phoneName(c.Brand, c.Model) || `${Platform.OS === 'android' ? 'Android' : 'iOS'} phone`
}

export const useSync = create<SyncState>((set, get) => ({
  running: false,
  progress: null,
  lastReport: null,
  lastError: null,
  errorKind: null,
  run: async (db) => {
    if (get().running) return null
    set({ running: true, progress: null, lastError: null, errorKind: null })
    try {
      const report = await syncNow(db, fetchTransport(), deviceName(), (progress) => {
        set({ progress })
        useDataVersion.getState().bump() // show each page as it lands
      })
      set({ lastReport: report })
      return report
    } catch (e) {
      set({ lastError: e instanceof Error ? e.message : String(e), errorKind: e instanceof SyncError ? e.kind : 'server' })
      return null
    } finally {
      set({ running: false, progress: null })
      useDataVersion.getState().bump()
    }
  },
}))
