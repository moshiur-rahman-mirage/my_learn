import { useEffect, type ReactNode } from 'react'
import { Appearance } from 'react-native'
import * as SystemUI from 'expo-system-ui'
import { useDb } from '@/db/DbProvider'
import { useColors } from '@/ui/theme'
import { loadPrefs, usePrefs } from './prefs'

/**
 * Loads this phone's preferences once, and applies the theme: an explicit Light/Dark overrides
 * the system setting for the whole app (every `useColorScheme()`, including the note viewer).
 */
export function PrefsLoader({ children }: { children: ReactNode }) {
  const db = useDb()
  const loaded = usePrefs((s) => s.loaded)
  const hydrate = usePrefs((s) => s.hydrate)
  const theme = usePrefs((s) => s.theme)
  const c = useColors()

  useEffect(() => {
    if (!loaded) void loadPrefs(db).then(hydrate)
  }, [db, loaded, hydrate])

  useEffect(() => {
    if (loaded) Appearance.setColorScheme(theme === 'system' ? 'auto' : theme)
  }, [loaded, theme])

  // the window behind the screens (visible during transitions and the keyboard)
  useEffect(() => {
    void SystemUI.setBackgroundColorAsync(c.bg)
  }, [c.bg])

  return children
}
