import { useColorScheme } from 'react-native'
import { usePrefs } from '@/state/prefs'
import { colorsFor, type Colors } from './palette'

export { ACCENTS, colorsFor, type Colors } from './palette'

export function useColors(): Colors {
  const dark = useColorScheme() === 'dark'
  const accent = usePrefs((s) => s.accent)
  return colorsFor(dark, accent)
}

export const space = { xs: 4, sm: 8, md: 12, lg: 16, xl: 24 }
export const radius = { sm: 12, md: 16, card: 20, pill: 999 }
