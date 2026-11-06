import { onBeforeUnmount, onMounted, ref, type Ref } from 'vue'

/** Width of an element, kept up to date (charts redraw to fit their container). */
export function useElementWidth(el: Ref<HTMLElement | null>, fallback = 600) {
  const width = ref(fallback)
  let observer: ResizeObserver | undefined
  onMounted(() => {
    if (!el.value) return
    width.value = el.value.clientWidth || fallback
    observer = new ResizeObserver((entries) => {
      const w = entries[0]?.contentRect.width
      if (w) width.value = w
    })
    observer.observe(el.value)
  })
  onBeforeUnmount(() => observer?.disconnect())
  return width
}

/** A "nice" axis maximum (1, 2, 2.5, 5 × 10^n) at or above value. */
export function niceMax(value: number, minimum = 1): number {
  const v = Math.max(value, minimum)
  const pow = 10 ** Math.floor(Math.log10(v))
  for (const m of [1, 2, 2.5, 5, 10]) if (m * pow >= v) return m * pow
  return 10 * pow
}

/** Evenly spaced tick values from 0 to max. */
export function ticks(max: number, count = 4): number[] {
  return Array.from({ length: count + 1 }, (_, i) => (max / count) * i)
}

/** Indexes of about `count` evenly spaced x labels, always including the last one. */
export function labelIndexes(length: number, count: number): number[] {
  if (length <= count) return Array.from({ length }, (_, i) => i)
  const step = (length - 1) / (count - 1)
  return Array.from({ length: count }, (_, i) => Math.round(i * step))
}

/** "2026-11-06" as a local date (no UTC shift). */
export function parseDay(day: string): Date {
  const [y, m, d] = day.split('-').map(Number)
  return new Date(y!, m! - 1, d!)
}

export function shortDate(day: string): string {
  return parseDay(day).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
}

export function longDate(day: string): string {
  return parseDay(day).toLocaleDateString(undefined, { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' })
}
