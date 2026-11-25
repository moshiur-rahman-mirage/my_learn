/** Search query helpers (port of backend search/SearchService). */

export const SEARCH_LIMIT = 50
export const HIT_START = '\u0002'
export const HIT_END = '\u0003'

const WORD = /[\p{L}\p{N}\p{M}_]+/gu

/**
 * Turns user input into a safe FTS5 query: every word becomes a quoted prefix term, so operators
 * and punctuation typed by the user can never cause a syntax error. "sprin boo" finds "Spring Boot".
 *
 * @return null when the input has no words
 */
export function toMatchQuery(text: string | null | undefined): string | null {
  if (!text) return null
  const terms: string[] = []
  for (const m of text.matchAll(WORD)) {
    if (terms.length >= 10) break
    terms.push(`"${m[0]}"*`)
  }
  return terms.length ? terms.join(' ') : null
}

/** A piece of a snippet; `hit` pieces are the matched words. */
export interface Segment {
  text: string
  hit: boolean
}

/** Splits FTS5 snippet text at the hit markers. */
export function segments(s: string): Segment[] {
  const out: Segment[] = []
  let buf = ''
  let inHit = false
  for (const c of s) {
    if (c === HIT_START || c === HIT_END) {
      if (buf) out.push({ text: buf, hit: inHit })
      buf = ''
      inHit = c === HIT_START
    } else {
      buf += c
    }
  }
  if (buf) out.push({ text: buf, hit: inHit })
  return out
}

export function hasHit(s: string | null): boolean {
  return s != null && s.includes(HIT_START)
}

export function shorten(s: string): string {
  return s.length <= 160 ? s : s.substring(0, 157) + '…'
}
