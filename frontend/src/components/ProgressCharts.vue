<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { CalendarCheck, Flame, LoaderCircle, Trophy } from 'lucide-vue-next'
import { dashboardApi } from '../api'
import { errorMessage } from '../api/client'
import type { Activity, ActivityDay, Plan } from '../types/api'
import LineChart, { type LinePoint } from './charts/LineChart.vue'
import BarChart, { type Bar } from './charts/BarChart.vue'
import { longDate, parseDay, shortDate } from './charts/chartUtils'

/** "Progress over time" on the dashboard: stat tiles, % line, completions per day, table view. */
const props = defineProps<{ plans: Plan[] }>()

const RANGES = [
  { days: 30, label: '30 days' },
  { days: 90, label: '90 days' },
  { days: 365, label: '1 year' },
  { days: 0, label: 'All' },
] as const
const STORE_KEY = 'lt.dashboard.range'

const days = ref<number>(readStoredRange())
const planId = ref<number | null>(null)
const activity = ref<Activity | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

function readStoredRange(): number {
  try {
    const v = Number(localStorage.getItem(STORE_KEY))
    return RANGES.some((r) => r.days === v) && localStorage.getItem(STORE_KEY) !== null ? v : 90
  } catch {
    return 90
  }
}

async function load() {
  loading.value = true
  error.value = null
  try {
    activity.value = await dashboardApi.activity(days.value, planId.value)
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    loading.value = false
  }
}
onMounted(load)
watch([days, planId], () => {
  try {
    localStorage.setItem(STORE_KEY, String(days.value))
  } catch {
    /* private mode: just don't remember */
  }
  void load()
})
// a filtered plan that was archived/deleted disappears from the list
watch(
  () => props.plans,
  (list) => {
    if (planId.value !== null && !list.some((p) => p.id === planId.value)) planId.value = null
  },
)

const series = computed<ActivityDay[]>(() => activity.value?.days ?? [])
const plural = (n: number, word: string) => `${n} ${word}${n === 1 ? '' : 's'}`

const linePoints = computed<LinePoint[]>(() =>
  series.value.map((d) => ({
    label: shortDate(d.date),
    y: d.percent,
    tooltip:
      d.percent === null
        ? [longDate(d.date), 'No topics yet']
        : [longDate(d.date), `${d.percent}% complete`, `${d.completedLeaves} of ${d.totalLeaves} topics`],
  })),
)
const lineMax = computed(() => {
  const top = Math.max(0, ...series.value.map((d) => d.percent ?? 0))
  return Math.min(100, Math.max(10, Math.ceil((top + 5) / 10) * 10))
})

/** Per day for short ranges; per week (> 120 days) or per month (> 2 years) for long ones. */
const grouping = computed<'day' | 'week' | 'month'>(() =>
  series.value.length > 730 ? 'month' : series.value.length > 120 ? 'week' : 'day',
)
const bars = computed<Bar[]>(() => {
  if (grouping.value === 'day') {
    return series.value.map((d) => ({
      label: shortDate(d.date),
      value: d.completedByYou,
      tooltip: [longDate(d.date), plural(d.completedByYou, 'topic') + ' completed'],
    }))
  }
  const groups = new Map<string, { start: string; value: number }>()
  for (const d of series.value) {
    const date = parseDay(d.date)
    let key: string
    if (grouping.value === 'week') {
      const monday = new Date(date)
      monday.setDate(date.getDate() - ((date.getDay() + 6) % 7))
      key = `${monday.getFullYear()}-${String(monday.getMonth() + 1).padStart(2, '0')}-${String(monday.getDate()).padStart(2, '0')}`
    } else {
      key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-01`
    }
    const g = groups.get(key) ?? { start: key, value: 0 }
    g.value += d.completedByYou
    groups.set(key, g)
  }
  return [...groups.values()].map((g) =>
    grouping.value === 'week'
      ? { label: shortDate(g.start), value: g.value, tooltip: [`Week of ${longDate(g.start)}`, plural(g.value, 'topic') + ' completed'] }
      : {
          label: parseDay(g.start).toLocaleDateString(undefined, { month: 'short', year: '2-digit' }),
          value: g.value,
          tooltip: [parseDay(g.start).toLocaleDateString(undefined, { month: 'long', year: 'numeric' }), plural(g.value, 'topic') + ' completed'],
        },
  )
})
const completedInRange = computed(() => series.value.reduce((s, d) => s + d.completedByYou, 0))
const importedInRange = computed(() => series.value.reduce((s, d) => s + d.completedImported, 0))
const tableRows = computed(() => [...series.value].reverse().filter((d) => d.completedByYou > 0 || d.completedImported > 0))
const rangeLabel = computed(() => RANGES.find((r) => r.days === days.value)?.label ?? '')
</script>

<template>
  <section class="space-y-3">
    <div class="flex flex-wrap items-center justify-between gap-2">
      <h2 class="font-semibold">Progress over time</h2>
      <div class="flex flex-wrap items-center gap-2">
        <select v-model="planId" class="input w-auto py-1" aria-label="Plan">
          <option :value="null">All active plans</option>
          <option v-for="p in plans" :key="p.id" :value="p.id">{{ p.name }}</option>
        </select>
        <div class="flex gap-1" role="radiogroup" aria-label="Time range">
          <button
            v-for="r in RANGES"
            :key="r.days"
            role="radio"
            :aria-checked="days === r.days"
            class="chip px-3 py-1"
            :class="{ 'chip-on': days === r.days }"
            @click="days = r.days"
          >
            {{ r.label }}
          </button>
        </div>
        <LoaderCircle v-if="loading" class="muted size-4 animate-spin" aria-label="Loading" />
      </div>
    </div>

    <div v-if="error" class="card p-4 text-sm text-red-700 dark:text-red-300">
      Couldn't load the history: {{ error }} <button class="btn-secondary ml-2 py-0.5" @click="load">Retry</button>
    </div>

    <template v-else-if="activity">
      <!-- figures -->
      <div class="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-5">
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><CalendarCheck class="size-3.5" /> Today</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity.completedToday }}</p>
          <p class="muted text-xs">topics completed</p>
        </div>
        <div class="card p-4">
          <p class="muted text-xs">Last 7 days</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity.completedLast7Days }}</p>
          <p class="muted text-xs">topics completed</p>
        </div>
        <div class="card p-4">
          <p class="muted text-xs">Last 30 days</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity.completedLast30Days }}</p>
          <p class="muted text-xs">topics completed</p>
        </div>
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><Flame class="size-3.5 text-orange-500" /> Current streak</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity.currentStreak }}</p>
          <p class="muted text-xs">{{ activity.currentStreak === 1 ? 'day' : 'days' }} in a row</p>
        </div>
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><Trophy class="size-3.5 text-amber-500" /> Best streak</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity.bestStreak }}</p>
          <p class="muted text-xs">{{ activity.bestStreak === 1 ? 'day' : 'days' }}</p>
        </div>
      </div>

      <div class="grid gap-3 xl:grid-cols-2">
        <div class="card p-4">
          <div class="mb-2 flex items-baseline justify-between gap-2">
            <h3 class="text-sm font-semibold">Overall progress, %</h3>
            <span class="muted text-xs">{{ rangeLabel }}</span>
          </div>
          <LineChart
            :points="linePoints"
            :y-max="lineMax"
            :y-format="(v) => `${Math.round(v)}%`"
            :label="`Percentage of topics completed per day over ${rangeLabel}`"
          />
        </div>
        <div class="card p-4">
          <div class="mb-2 flex items-baseline justify-between gap-2">
            <h3 class="text-sm font-semibold">Topics completed per {{ grouping }}</h3>
            <span class="muted text-xs">{{ completedInRange }} in {{ rangeLabel.toLowerCase() === 'all' ? 'total' : rangeLabel }}</span>
          </div>
          <BarChart :bars="bars" :label="`Topics completed per ${grouping} over ${rangeLabel}`" />
          <p v-if="importedInRange" class="muted mt-1 text-xs">
            {{ plural(importedInRange, 'topic') }} imported as already done are counted in the progress line, not here.
          </p>
        </div>
      </div>

      <details class="card p-4 text-sm">
        <summary class="cursor-pointer font-medium">Show as table</summary>
        <p v-if="tableRows.length === 0" class="muted mt-2">No topics completed in this period.</p>
        <div v-else class="mt-2 max-h-80 overflow-y-auto">
          <table class="w-full text-left tabular-nums">
            <thead class="muted sticky top-0 bg-white text-xs dark:bg-slate-900">
              <tr>
                <th class="py-1 pr-3 font-medium">Date</th>
                <th class="py-1 pr-3 text-right font-medium">Completed by you</th>
                <th class="py-1 pr-3 text-right font-medium">Imported as done</th>
                <th class="py-1 text-right font-medium">Progress at end of day</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
              <tr v-for="d in tableRows" :key="d.date">
                <td class="py-1 pr-3">{{ longDate(d.date) }}</td>
                <td class="py-1 pr-3 text-right">{{ d.completedByYou }}</td>
                <td class="py-1 pr-3 text-right">{{ d.completedImported || '' }}</td>
                <td class="py-1 text-right">{{ d.percent === null ? 'N/A' : `${d.percent}% (${d.completedLeaves}/${d.totalLeaves})` }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </details>
    </template>
  </section>
</template>
