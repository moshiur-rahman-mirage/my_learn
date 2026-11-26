import { describe, expect, it } from 'vitest'
import { ACCENTS, colorsFor } from '../src/ui/palette'
import type { Accent } from '../src/state/prefs'

/** WCAG relative luminance and contrast ratio. */
function luminance(hex: string): number {
  const [r, g, b] = [1, 3, 5].map((i) => parseInt(hex.slice(i, i + 2), 16) / 255).map((c) => (c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4))
  return 0.2126 * r + 0.7152 * g + 0.0722 * b
}
function contrast(a: string, b: string): number {
  const [x, y] = [luminance(a), luminance(b)].sort((p, q) => q - p)
  return (x + 0.05) / (y + 0.05)
}

describe('palette readability (WCAG AA)', () => {
  for (const dark of [false, true]) {
    for (const accent of Object.keys(ACCENTS) as Accent[]) {
      it(`${dark ? 'dark' : 'light'} · ${accent}`, () => {
        const c = colorsFor(dark, accent)
        expect(contrast(c.text, c.bg)).toBeGreaterThanOrEqual(7) // body text: AAA
        expect(contrast(c.text, c.card)).toBeGreaterThanOrEqual(7)
        expect(contrast(c.muted, c.card)).toBeGreaterThanOrEqual(4.5) // secondary text: AA
        expect(contrast(c.primaryText, c.primary)).toBeGreaterThanOrEqual(4.5) // text on buttons
        expect(contrast(c.primary, c.card)).toBeGreaterThanOrEqual(3) // accent links/icons on cards
        expect(contrast(c.heroText, c.hero)).toBeGreaterThanOrEqual(7)
        expect(contrast(c.heroAccent, c.hero)).toBeGreaterThanOrEqual(4.5)
      })
    }
  }

  it('dark mode is soft: text is not pure white and the background not pure black', () => {
    const c = colorsFor(true, 'teal')
    expect(c.text).not.toBe('#ffffff')
    expect(c.bg).not.toBe('#000000')
    expect(contrast(c.text, c.bg)).toBeLessThan(17) // less glare than white on black (21:1)
  })
})
