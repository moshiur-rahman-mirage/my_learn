<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { CalendarRange, CircleCheck } from 'lucide-vue-next'
import { dashboardApi } from '../api'
import { errorMessage } from '../api/client'
import type { CompletedHistory } from '../types/api'
import { todayIso } from '../lib/format'
import { usePlanList } from '../composables/usePlanList'
import PriorityBadge from './PriorityBadge.vue'

const planList = usePlanList()

function isoDaysAgo(n: number): string {
  const d = new Date()
  d.setDate(d.getDate() - n)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const PRESETS = [
  { key: 'today', label: 'Today', from: () => todayIso(), to: () => todayIso() },
  { key: 'yesterday', label: 'Yesterday', from: () => isoDaysAgo(1), to: () => isoDaysAgo(1) },
  { key: '7', label: 'Last 7 days', from: () => isoDaysAgo(6), to: () => todayIso() },
  { key: '30', label: 'Last 30 days', from: () => isoDaysAgo(29), to: () => todayIso() },
] as const

const from = ref(todayIso())
const to = ref(todayIso())
const planId = ref<number | null>(null)
const data = ref<CompletedHistory | null>(null)
const error = ref<string | null>(null)
const loading = ref(false)

const activePreset = computed(() => PRESETS.find((p) => p.from() === from.value && p.to() === to.value)?.key ?? null)
const invalid = computed(() => !from.value || !to.value || from.value > to.value)
const singleDay = computed(() => from.value === to.value)

let seq = 0
async function load() {
  if (invalid.value) return
  const mine = ++seq
  loading.value = true
  error.value = null
  try {
    const result = await dashboardApi.completed(from.value, to.value, planId.value)
    if (mine === seq) data.value = result
  } catch (e) {
    if (mine === seq) error.value = errorMessage(e)
  } finally {
    if (mine === seq) loading.value = false
  }
}

function preset(p: (typeof PRESETS)[number]) {
  from.value = p.from()
  to.value = p.to()
}

watch([from, to, planId], load)
onMounted(() => {
  if (!planList.state.loaded) void planList.refresh()
  void load()
})

function dayHeading(iso: string): string {
  if (iso === todayIso()) return 'Today'
  if (iso === isoDaysAgo(1)) return 'Yesterday'
  const [y, m, d] = iso.split('-').map(Number)
  return new Date(y, m - 1, d).toLocaleDateString(undefined, { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })
}

function timeOf(iso: string): string {
  return new Date(iso).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
}

defineExpose({ reload: load })
</script>

<template>
  <section class="card p-4">
    <div class="mb-3 flex flex-wrap items-center gap-2">
      <h2 class="flex items-center gap-2 font-semibold">
        <CircleCheck class="size-4 text-emerald-600" /> Completed topics
        <span v-if="data" class="chip pointer-events-none">{{ data.total }}</span>
      </h2>
    </div>

    <div class="mb-4 flex flex-wrap items-end gap-2">
      <div class="flex flex-wrap gap-1">
        <button
          v-for="p in PRESETS"
          :key="p.key"
          class="chip"
          :class="activePreset === p.key ? 'border-emerald-500 bg-emerald-50 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300' : ''"
          @click="preset(p)"
        >
          {{ p.label }}
        </button>
      </div>
      <div class="flex flex-wrap items-end gap-2 sm:ml-auto">
        <label class="w-36">
          <span class="label">From</span>
          <input v-model="from" type="date" class="input" :max="todayIso()" />
        </label>
        <label class="w-36">
          <span class="label">To</span>
          <input v-model="to" type="date" class="input" :min="from" :max="todayIso()" />
        </label>
        <label class="w-44">
          <span class="label">Plan</span>
          <select v-model="planId" class="input">
            <option :value="null">All plans</option>
            <option v-for="p in planList.state.plans" :key="p.id" :value="p.id">{{ p.name }}</option>
          </select>
        </label>
      </div>
    </div>

    <p v-if="invalid" class="text-sm text-amber-700 dark:text-amber-400">“From” must be on or before “To”.</p>
    <p v-else-if="error" class="text-sm text-red-600">
      {{ error }} <button class="underline" @click="load">Retry</button>
    </p>
    <p v-else-if="!data" class="muted text-sm">Loading…</p>
    <div v-else-if="data.days.length === 0" class="muted flex items-center gap-2 py-4 text-sm">
      <CalendarRange class="size-4" />
      {{ singleDay ? `Nothing completed ${from === todayIso() ? 'yet today' : 'on this day'}.` : 'Nothing completed in this range.' }}
    </div>

    <div v-else class="space-y-5" :class="loading ? 'opacity-60' : ''">
      <div v-for="day in data.days" :key="day.date">
        <h3 v-if="!singleDay" class="mb-2 flex items-center gap-2 text-sm font-semibold">
          {{ dayHeading(day.date) }}
          <span class="muted text-xs font-normal">{{ day.count }} topic{{ day.count === 1 ? '' : 's' }}</span>
        </h3>
        <div class="space-y-2">
          <div v-for="p in day.plans" :key="p.planId" class="rounded-lg border border-slate-200 dark:border-slate-800">
            <RouterLink
              :to="`/plans/${p.planId}`"
              class="flex items-center gap-2 border-b border-slate-200 px-3 py-1.5 text-sm font-medium hover:underline dark:border-slate-800"
            >
              <span class="min-w-0 flex-1 truncate">{{ p.planName }}</span>
              <span class="muted shrink-0 text-xs font-normal">{{ p.topics.length }}</span>
            </RouterLink>
            <ul class="divide-y divide-slate-200 dark:divide-slate-800">
              <li v-for="t in p.topics" :key="t.itemId" class="flex items-center gap-2.5 px-3 py-1.5">
                <CircleCheck class="size-4 shrink-0 text-emerald-600" />
                <div class="min-w-0 flex-1">
                  <RouterLink
                    :to="{ path: `/plans/${p.planId}`, query: { item: t.itemId } }"
                    class="flex items-center gap-2 text-sm hover:underline"
                  >
                    <span class="truncate">{{ t.name }}</span>
                    <PriorityBadge :priority="t.priority" />
                  </RouterLink>
                  <p v-if="t.path.length" class="muted truncate text-xs">{{ t.path.join(' › ') }}</p>
                </div>
                <span class="muted shrink-0 text-xs tabular-nums">{{ timeOf(t.completedAt) }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
