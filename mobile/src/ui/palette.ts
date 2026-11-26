import type { Accent } from '@/state/prefs'

/**
 * Calm, study-friendly colours: neutral bases (warm off-white by day, charcoal without a colour
 * cast by night, with softened text to cut glare) and ONE restrained accent the user can pick in
 * Settings. Accents have a deeper shade for light mode and a softer, lighter one for dark mode, so
 * they never glow on a dark screen.
 */
const lightBase = {
  bg: '#f6f7f5',
  card: '#ffffff',
  border: '#e3e7e4',
  text: '#1c2321',
  muted: '#5f6b67',
  faint: '#949f9b',
  track: '#e7ebe8',
  danger: '#c2410c',
  warn: '#b45309',
  link: '#2563eb',
  must: '#c2410c',
  should: '#b45309',
  nice: '#2563eb',
  /** the dark feature cards (Continue reading, library header, focus timer) */
  hero: '#1f2a2c',
  heroText: '#f3f6f5',
  heroMuted: '#a9b5b2',
}

const darkBase: typeof lightBase = {
  bg: '#111315',
  card: '#191c1f',
  border: '#272b2f',
  text: '#e6e9eb',
  muted: '#9ba4aa',
  faint: '#646d73',
  track: '#2a2f33',
  danger: '#f2937a',
  warn: '#e8b462',
  link: '#8ab4f8',
  must: '#f2937a',
  should: '#e8b462',
  nice: '#8ab4f8',
  hero: '#1d2226',
  heroText: '#eef2f3',
  heroMuted: '#a3adb2',
}

interface AccentShades {
  primary: string
  primaryText: string
  /** soft tinted background for selected chips, banners, icon circles */
  chipOn: string
}

/** Each accent: [light mode, dark mode]. */
export const ACCENTS: Record<Accent, { label: string; light: AccentShades; dark: AccentShades }> = {
  teal: {
    label: 'Teal',
    light: { primary: '#0f766e', primaryText: '#ffffff', chipOn: '#e3f2ef' },
    dark: { primary: '#5ec4b6', primaryText: '#0b1f1c', chipOn: '#16302d' },
  },
  blue: {
    label: 'Blue',
    light: { primary: '#2f5fd0', primaryText: '#ffffff', chipOn: '#e7eefc' },
    dark: { primary: '#86a8f0', primaryText: '#0e1a33', chipOn: '#1a2640' },
  },
  green: {
    label: 'Green',
    light: { primary: '#2f7d4b', primaryText: '#ffffff', chipOn: '#e5f3ea' },
    dark: { primary: '#79c495', primaryText: '#0e2117', chipOn: '#182d21' },
  },
  amber: {
    label: 'Amber',
    light: { primary: '#a8590f', primaryText: '#ffffff', chipOn: '#f8eddf' },
    dark: { primary: '#e2ad66', primaryText: '#2a1a07', chipOn: '#33281a' },
  },
  violet: {
    label: 'Violet',
    light: { primary: '#5f52c4', primaryText: '#ffffff', chipOn: '#ecebfa' },
    dark: { primary: '#a59cec', primaryText: '#1a1636', chipOn: '#24213e' },
  },
}

export type Colors = typeof lightBase & AccentShades & {
  /** completed / progress fill */
  done: string
  /** the accent as it reads on the dark hero cards (always the soft dark-mode shade) */
  heroAccent: string
}

export function colorsFor(dark: boolean, accent: Accent): Colors {
  const base = dark ? darkBase : lightBase
  const shades = (ACCENTS[accent] ?? ACCENTS.teal)[dark ? 'dark' : 'light']
  return { ...base, ...shades, done: shades.primary, heroAccent: (ACCENTS[accent] ?? ACCENTS.teal).dark.primary }
}
