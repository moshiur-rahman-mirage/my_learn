import { useColorScheme } from 'react-native'

/** A calm lavender palette shared by the app's light and dark themes. */
const light = {
  bg: '#f7f5fc',
  card: '#ffffff',
  border: '#ebe7f2',
  text: '#27243a',
  muted: '#79758b',
  faint: '#a5a1b3',
  primary: '#8b5cf6',
  primaryText: '#ffffff',
  done: '#8b5cf6',
  track: '#eeeaf5',
  danger: '#d94f68',
  warn: '#c98222',
  link: '#7550c7',
  must: '#d94f68',
  should: '#c98222',
  nice: '#5286bd',
  chipOn: '#f0eaff',
}

const dark: typeof light = {
  bg: '#171522',
  card: '#221f30',
  border: '#353044',
  text: '#f7f5ff',
  muted: '#b4aec6',
  faint: '#77718a',
  primary: '#ad8bff',
  primaryText: '#1b142b',
  done: '#c2a7ff',
  track: '#393448',
  danger: '#ff8796',
  warn: '#ffc878',
  link: '#c3a6ff',
  must: '#ff8796',
  should: '#ffc878',
  nice: '#91bdf0',
  chipOn: '#342a4e',
}

export type Colors = typeof light

export function useColors(): Colors {
  return useColorScheme() === 'dark' ? dark : light
}

export const space = { xs: 4, sm: 8, md: 12, lg: 16, xl: 24 }
export const radius = { sm: 12, md: 16, card: 20, pill: 999 }
