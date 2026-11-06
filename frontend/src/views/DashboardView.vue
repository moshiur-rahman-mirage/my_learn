<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { CircleCheck, Clock, GraduationCap, Plus, Upload } from 'lucide-vue-next'
import { dashboardApi } from '../api'
import { errorMessage } from '../api/client'
import type { DashboardSummary } from '../types/api'
import { percentLabel, relativeTime } from '../lib/format'
import ProgressBar from '../components/ProgressBar.vue'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'
import ProgressCharts from '../components/ProgressCharts.vue'
import PaceBadge from '../components/PaceBadge.vue'

const summary = ref<DashboardSummary | null>(null)
const error = ref<string | null>(null)

async function load() {
  error.value = null
  try {
    summary.value = await dashboardApi.summary()
  } catch (e) {
    error.value = errorMessage(e)
  }
}
onMounted(load)
</script>

<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-semibold">Dashboard</h1>

    <EmptyState v-if="error" :icon="GraduationCap" title="Couldn't load the dashboard" :text="error">
      <button class="btn-primary" @click="load">Retry</button>
    </EmptyState>
    <LoadingState v-else-if="!summary" />

    <EmptyState
      v-else-if="summary.totalPlans === 0"
      :icon="GraduationCap"
      title="Welcome! Let's set up your first roadmap"
      text="Create a learning plan from scratch, or import the roadmaps you already keep in Excel or Google Sheets."
    >
      <RouterLink to="/plans?new=1" class="btn-primary"><Plus class="size-4" /> New plan</RouterLink>
      <RouterLink to="/import" class="btn-secondary"><Upload class="size-4" /> Import spreadsheet</RouterLink>
    </EmptyState>

    <template v-else>
      <!-- overall -->
      <section class="card p-5">
        <div class="flex flex-wrap items-end justify-between gap-2">
          <div>
            <p class="muted text-sm">Overall progress across active plans</p>
            <p class="mt-1 text-4xl font-semibold tabular-nums">{{ percentLabel(summary.overall) }}</p>
          </div>
          <p class="muted text-sm">{{ summary.completedLeaves }} of {{ summary.totalLeaves }} topics done</p>
        </div>
        <ProgressBar class="mt-4" :progress="summary.overall" size="lg" :show-label="false" />
      </section>

      <!-- stats -->
      <section class="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-5">
        <div v-for="s in [
          { label: 'Learning plans', value: summary.totalPlans },
          { label: 'Active plans', value: summary.activePlans },
          { label: 'Topics', value: summary.totalLeaves },
          { label: 'Completed', value: summary.completedLeaves },
          { label: 'Pending', value: summary.pendingLeaves },
        ]" :key="s.label" class="card p-4">
          <p class="muted text-xs">{{ s.label }}</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ s.value }}</p>
        </div>
      </section>

      <ProgressCharts :plans="summary.plans" />

      <div class="grid gap-6 lg:grid-cols-3">
        <!-- plans -->
        <section class="lg:col-span-2">
          <h2 class="mb-3 font-semibold">Active plans</h2>
          <div v-if="summary.plans.length === 0" class="card muted p-6 text-sm">All plans are archived.</div>
          <ul v-else class="card divide-y divide-slate-200 dark:divide-slate-800">
            <li v-for="p in summary.plans" :key="p.id">
              <RouterLink :to="`/plans/${p.id}`" class="block px-4 py-3 hover:bg-slate-50 dark:hover:bg-slate-800/50">
                <div class="mb-1.5 flex items-baseline justify-between gap-3">
                  <span class="truncate font-medium">{{ p.name }}</span>
                  <span class="muted shrink-0 text-xs">{{ p.progress.completed }}/{{ p.progress.total }}</span>
                </div>
                <ProgressBar :progress="p.progress" size="sm" />
                <PaceBadge v-if="p.pace" class="mt-1.5" :pace="p.pace" />
              </RouterLink>
            </li>
          </ul>
        </section>

        <div class="space-y-6">
          <section>
            <h2 class="mb-3 font-semibold">Recently updated</h2>
            <ul class="card divide-y divide-slate-200 dark:divide-slate-800">
              <li v-for="p in summary.recentlyUpdated" :key="p.id">
                <RouterLink :to="`/plans/${p.id}`" class="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-slate-50 dark:hover:bg-slate-800/50">
                  <Clock class="size-3.5 shrink-0 text-slate-400" />
                  <span class="flex-1 truncate">{{ p.name }}</span>
                  <span class="muted shrink-0 text-xs">{{ relativeTime(p.updatedAt) }}</span>
                </RouterLink>
              </li>
            </ul>
          </section>

          <section>
            <h2 class="mb-3 font-semibold">Recently completed</h2>
            <div v-if="summary.recentlyCompleted.length === 0" class="card muted p-4 text-sm">Nothing completed yet. Tick your first topic!</div>
            <ul v-else class="card divide-y divide-slate-200 dark:divide-slate-800">
              <li v-for="c in summary.recentlyCompleted" :key="c.itemId">
                <RouterLink :to="`/plans/${c.planId}`" class="flex items-start gap-2 px-4 py-2.5 text-sm hover:bg-slate-50 dark:hover:bg-slate-800/50">
                  <CircleCheck class="mt-0.5 size-3.5 shrink-0 text-emerald-600" />
                  <span class="min-w-0 flex-1">
                    <span class="block truncate">{{ c.itemName }}</span>
                    <span class="muted block truncate text-xs">{{ c.planName }} · {{ relativeTime(c.completedAt) }}</span>
                  </span>
                </RouterLink>
              </li>
            </ul>
          </section>
        </div>
      </div>
    </template>
  </div>
</template>
