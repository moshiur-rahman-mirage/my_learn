/**
 * Text helpers that behave exactly like their Java counterparts on the desktop. Flashcard keys
 * and [[link]] targets must come out byte-for-byte the same on both devices, so JS shorthands
 * whose meaning differs from Java's are spelled out:
 *
 * - Java regex `\s` (no UNICODE flag) is ASCII only: [ \t\n\x0B\f\r]. JS `\s` is Unicode.
 * - Java `strip()`/`isBlank()` use Character.isWhitespace, which is neither of the above.
 * - Java regex `.` excludes \n, \r and U+0085, U+2028, U+2029; JS `.` matches U+0085.
 */

/** Java regex `\s`. */
export const JS = '[ \\t\\n\\x0B\\f\\r]'
/** Java regex `.` (without DOTALL). */
export const JDOT = '[^\\n\\r\\u0085\\u2028\\u2029]'

/** Code points for which Java's Character.isWhitespace is true (built from numbers: keeps this file ASCII). */
const JAVA_WHITESPACE = new Set<number>([
  0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, 0x1680,
  0x2000, 0x2001, 0x2002, 0x2003, 0x2004, 0x2005, 0x2006, 0x2008, 0x2009, 0x200a,
  0x2028, 0x2029, 0x205f, 0x3000,
])

export function isJavaWhitespace(ch: string): boolean {
  return JAVA_WHITESPACE.has(ch.charCodeAt(0))
}

/** Java String.strip(). */
export function javaStrip(s: string): string {
  let start = 0
  let end = s.length
  while (start < end && isJavaWhitespace(s[start])) start++
  while (end > start && isJavaWhitespace(s[end - 1])) end--
  return s.substring(start, end)
}

/** Java String.isBlank(). */
export function javaIsBlank(s: string): boolean {
  return javaStrip(s).length === 0
}

/** Java `s.replaceAll("\\s+", " ")`. */
export function collapseJavaSpaces(s: string): string {
  return s.replace(new RegExp(`${JS}+`, 'g'), ' ')
}

// --- HTML notes → text (backend common/HtmlText) ----------------------------------------------

const TAG = /<[^>]*>/g
const NUMERIC_ENTITY = /&#(x?)([0-9a-fA-F]+);/g
const BLOCK_END = /<br[ \t\n\x0B\f\r]*\/?>|<\/(p|div|li|h[1-6]|pre|blockquote|tr)>/gi

function decode(text: string): string {
  return text
    .replace(NUMERIC_ENTITY, (_m, x: string, digits: string) => {
      // Java parses hex digits after "&#" without "x" as decimal, and fails (→ space) on letters
      const code = x === '' && /[a-fA-F]/.test(digits) ? NaN : parseInt(digits, x === '' ? 10 : 16)
      return Number.isFinite(code) && code >= 0 && code <= 0x10ffff ? String.fromCodePoint(code) : ' '
    })
    .replaceAll('&nbsp;', ' ')
    .replaceAll('&lt;', '<')
    .replaceAll('&gt;', '>')
    .replaceAll('&quot;', '"')
    .replaceAll('&apos;', "'")
    .replaceAll('&amp;', '&')
}

/** Plain text of an HTML note, for searching (HtmlText.toPlainText). */
export function htmlToPlainText(html: string | null | undefined): string {
  if (!html) return ''
  return javaStrip(collapseJavaSpaces(decode(html.replace(TAG, ' '))))
}

/**
 * Plain text keeping the note's line structure: one line per paragraph, list item or heading,
 * blank lines between blocks (HtmlText.toLines; used for flashcards and [[links]]).
 */
export function htmlToLines(html: string | null | undefined): string {
  if (!html) return ''
  const marked = html.replace(BLOCK_END, (m) => (m.toLowerCase().startsWith('<br') ? '\n' : '\n\n'))
  const text = decode(marked.replace(TAG, ''))
  return javaStrip(text.replace(/[ \t\x0B\f\r]+\n/g, '\n').replace(/\n{3,}/g, '\n\n'))
}

/** How [[links]] are matched to item names: trimmed, single spaces, lower case (NoteLinks.normalize). */
export function wikiKey(name: string | null | undefined): string {
  return name == null ? '' : collapseJavaSpaces(javaStrip(name)).toLowerCase()
}
