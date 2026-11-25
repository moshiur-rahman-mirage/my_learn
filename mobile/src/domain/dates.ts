/**
 * Timestamps are ISO-8601 UTC with exactly three fraction digits ("2026-11-25T10:00:00.123Z"),
 * like the desktop, so they sort as plain strings. Calendar days (review due dates, target
 * dates) are the viewer's local yyyy-MM-dd, like Java's LocalDate.now().
 */

/** Now as stored text. Date#toISOString always has milliseconds. */
export function nowIso(): string {
  return new Date().toISOString()
}

/** Today's local calendar day, yyyy-MM-dd. */
export function today(now: Date = new Date()): string {
  return formatDay(now)
}

export function formatDay(d: Date): string {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/** A yyyy-MM-dd day plus n days (calendar arithmetic, no time-zone or DST drift). */
export function addDays(day: string, n: number): string {
  const [y, m, d] = day.split('-').map(Number)
  return formatDay(new Date(y, m - 1, d + n))
}
