<script setup lang="ts">
import { computed } from 'vue'
import { CalendarClock } from 'lucide-vue-next'
import type { Pace } from '../types/api'
import { dayLabel } from '../lib/format'

/** Target-date status of a plan: a coloured pill, plus the numbers behind it when `detailed`. */
const props = defineProps<{ pace: Pace | null; detailed?: boolean }>()

const look = computed(() => {
  switch (props.pace?.status) {
    case 'ON_TRACK':
      return { text: 'On track', cls: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-500/15 dark:text-emerald-300' }
    case 'BEHIND':
      return { text: 'Behind', cls: 'bg-amber-100 text-amber-800 dark:bg-amber-500/15 dark:text-amber-300' }
    case 'OVERDUE':
      return { text: 'Overdue', cls: 'bg-red-100 text-red-800 dark:bg-red-500/15 dark:text-red-300' }
    case 'DONE':
      return { text: 'Done', cls: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-500/15 dark:text-emerald-300' }
    default:
      return { text: 'No topics', cls: 'bg-slate-200 text-slate-700 dark:bg-slate-800 dark:text-slate-300' }
  }
})

const days = computed(() => {
  const p = props.pace
  if (!p) return ''
  if (p.status === 'DONE') return `target ${dayLabel(p.targetDate)}`
  if (p.daysLeft === 0) return `was due ${dayLabel(p.targetDate)}`
  return p.daysLeft === 1 ? 'due today' : `${p.daysLeft} days left`
})
</script>

<template>
  <span v-if="pace" class="inline-flex flex-wrap items-center gap-x-2 gap-y-1 text-xs">
    <span class="inline-flex items-center gap-1 rounded-full px-2 py-0.5 font-medium" :class="look.cls" :title="`Target: ${dayLabel(pace.targetDate)}`">
      <CalendarClock class="size-3" /> {{ look.text }}
    </span>
    <span class="muted">{{ days }}</span>
    <template v-if="detailed && pace.status !== 'DONE' && pace.status !== 'NO_TOPICS'">
      <span v-if="pace.perWeekNeeded !== null" class="muted">· need <strong class="text-slate-700 dark:text-slate-200">{{ pace.perWeekNeeded }}</strong>/week</span>
      <span class="muted">· your pace {{ pace.perWeekRecent }}/week (last 4 weeks)</span>
      <span v-if="pace.projectedFinish" class="muted">· at this pace done by {{ dayLabel(pace.projectedFinish) }}</span>
    </template>
  </span>
</template>
