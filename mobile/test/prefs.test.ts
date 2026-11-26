import { describe, expect, it } from 'vitest'
import { DEFAULT_PREFS, loadPrefs, savePref } from '../src/state/prefs'
import { migratedDb } from './nodeDb'

describe('preferences (local, never synced)', () => {
  it('defaults, saves and reloads', async () => {
    const db = await migratedDb()
    expect(await loadPrefs(db)).toEqual(DEFAULT_PREFS)
    await savePref(db, 'theme', 'dark')
    await savePref(db, 'textScale', 1.3)
    await savePref(db, 'readingFont', 'mono')
    expect(await loadPrefs(db)).toEqual({ ...DEFAULT_PREFS, theme: 'dark', textScale: 1.3, readingFont: 'mono' })
  })

  it('ignores junk values and unknown keys', async () => {
    const db = await migratedDb()
    await db.run("INSERT INTO app_meta (key, value) VALUES ('pref_theme', 'purple'), ('pref_textScale', 'abc'), ('pref_unknown', 'x')")
    expect(await loadPrefs(db)).toEqual(DEFAULT_PREFS)
  })

  it('are not recorded for sync', async () => {
    const db = await migratedDb()
    await savePref(db, 'theme', 'light')
    expect(await db.first("SELECT COUNT(*) AS n FROM sync_change")).toEqual({ n: 0 })
  })
})
