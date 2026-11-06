<script setup lang="ts">
import { computed } from 'vue'
import type { ActivityDay } from '../types/api'
import { dayLabel } from '../lib/format'

/** A year of daily completions as a grid: one column per week, Monday at the top. */
const props = defineProps<{ days: ActivityDay[] }>()

const LEVELS = [
  'bg-slate-200 dark:bg-slate-800',
  'bg-emerald-200 dark:bg-emerald-900',
  'bg-emerald-400 dark:bg-emerald-700',
  'bg-emerald-600 dark:bg-emerald-500',
  'bg-emerald-800 dark:bg-emerald-300',
]

function level(n: number): number {
  if (n <= 0) return 0
  if (n === 1) return 1
  if (n <= 3) return 2
  if (n <= 6) return 3
  return 4
}

/** Weeks of 7 cells (null = padding before the first day). */
const weeks = computed(() => {
  if (props.days.length === 0) return []
  const [y, m, d] = props.days[0].date.split('-').map(Number)
  const firstWeekday = (new Date(y, m - 1, d).getDay() + 6) % 7 // Monday = 0
  const cells: (ActivityDay | null)[] = [...Array(firstWeekday).fill(null), ...props.days]
  const out: (ActivityDay | null)[][] = []
  for (let i = 0; i < cells.length; i += 7) out.push(cells.slice(i, i + 7))
  return out
})

const monthLabels = computed(() =>
  weeks.value.map((week, i) => {
    const first = week.find((c) => c !== null)
    if (!first) return ''
    const month = first.date.slice(0, 7)
    const prev = i > 0 ? weeks.value[i - 1].find((c) => c !== null)?.date.slice(0, 7) : null
    if (month === prev) return ''
    const [y, m] = month.split('-').map(Number)
    return new Date(y, m - 1, 1).toLocaleDateString(undefined, { month: 'short' })
  }),
)

const total = computed(() => props.days.reduce((s, d) => s + d.completedByYou, 0))
const activeDays = computed(() => props.days.filter((d) => d.completedByYou > 0).length)
</script>

<template>
  <div>
    <div class="overflow-x-auto pb-1">
      <div class="inline-flex flex-col gap-1">
        <div class="flex gap-[3px] pl-7 text-[10px] text-slate-500 dark:text-slate-400" aria-hidden="true">
          <span v-for="(label, i) in monthLabels" :key="i" class="w-[11px] overflow-visible whitespace-nowrap">{{ label }}</span>
        </div>
        <div class="flex gap-[3px]">
          <div class="flex w-6 flex-col gap-[3px] text-[10px] leading-[11px] text-slate-500 dark:text-slate-400" aria-hidden="true">
            <span>Mon</span><span>&nbsp;</span><span>Wed</span><span>&nbsp;</span><span>Fri</span><span>&nbsp;</span><span>&nbsp;</span>
          </div>
          <div v-for="(week, w) in weeks" :key="w" class="flex flex-col gap-[3px]">
            <template v-for="(cell, i) in week" :key="i">
              <span
                v-if="cell"
                class="size-[11px] rounded-[2px]"
                :class="LEVELS[level(cell.completedByYou)]"
                :title="`${dayLabel(cell.date)}: ${cell.completedByYou} topic(s) completed`"
              />
              <span v-else class="size-[11px]" />
            </template>
          </div>
        </div>
      </div>
    </div>
    <div class="muted mt-2 flex flex-wrap items-center justify-between gap-2 text-xs">
      <span>{{ total }} topics completed on {{ activeDays }} day(s) in the last year</span>
      <span class="flex items-center gap-1" aria-hidden="true">
        Less <span v-for="(cls, i) in LEVELS" :key="i" class="size-[11px] rounded-[2px]" :class="cls" /> More
      </span>
    </div>
  </div>
</template>
