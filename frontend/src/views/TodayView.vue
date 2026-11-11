<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { BrainCircuit, CircleCheck, Clock, Flame, Gauge, Layers, LoaderCircle, Play, Sun, ThumbsDown, ThumbsUp, Trophy, X } from 'lucide-vue-next'
import { confidenceApi, dashboardApi, flashcardsApi, itemsApi, reviewsApi } from '../api'
import { errorMessage } from '../api/client'
import type { Activity, DueCard, DueReview, NextTopic, ShakyItem, Today } from '../types/api'
import { dayLabel, minutesLabel, todayIso } from '../lib/format'
import { useToast } from '../composables/useToast'
import { usePomodoro } from '../composables/usePomodoro'
import { usePlanList } from '../composables/usePlanList'
import { useDueReviews } from '../composables/useDueReviews'
import { useDueCards } from '../composables/useDueCards'
import PriorityBadge from '../components/PriorityBadge.vue'
import LoadingState from '../components/LoadingState.vue'
import EmptyState from '../components/EmptyState.vue'
import ActivityHeatmap from '../components/ActivityHeatmap.vue'

const toast = useToast()
const pomodoro = usePomodoro()
const planList = usePlanList()
const dueReviews = useDueReviews()
const dueCardsBadge = useDueCards()
const cards = ref<DueCard[]>([])
const shaky = ref<ShakyItem[]>([])
const cardPlans = computed(() => {
  const byPlan = new Map<number, { planId: number; planName: string; count: number }>()
  for (const c of cards.value) {
    const e = byPlan.get(c.planId) ?? { planId: c.planId, planName: c.planName, count: 0 }
    e.count++
    byPlan.set(c.planId, e)
  }
  return [...byPlan.values()]
})
const LEVEL_LABEL = ['', 'No idea', 'Shaky', 'Okay', 'Good', 'Solid']

const today = ref<Today | null>(null)
const activity = ref<Activity | null>(null)
const error = ref<string | null>(null)
const working = ref(new Set<number>())

async function load() {
  error.value = null
  try {
    const [t, a, c, s] = await Promise.all([
      dashboardApi.today(),
      dashboardApi.activity(364, null),
      flashcardsApi.due().catch(() => [] as DueCard[]),
      confidenceApi.shaky().catch(() => [] as ShakyItem[]),
    ])
    today.value = t
    activity.value = a
    cards.value = c
    shaky.value = s
    dueReviews.set(t.dueReviews.length)
    dueCardsBadge.set(c.length)
  } catch (e) {
    error.value = errorMessage(e)
  }
}
onMounted(load)

function busy(id: number, on: boolean) {
  const next = new Set(working.value)
  if (on) next.add(id)
  else next.delete(id)
  working.value = next
}

async function review(r: DueReview, remembered: boolean) {
  busy(r.itemId, true)
  try {
    const result = await reviewsApi.review(r.itemId, remembered)
    toast.success(
      result.finished
        ? `“${r.name}”: all reviews done. Well remembered!`
        : remembered
          ? `Next review of “${r.name}” on ${dayLabel(result.nextDue!)}`
          : `“${r.name}” comes back tomorrow`,
    )
    removeReview(r)
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    busy(r.itemId, false)
  }
}

async function stopReviews(r: DueReview) {
  busy(r.itemId, true)
  try {
    await reviewsApi.stop(r.itemId)
    toast.info(`No more reviews for “${r.name}”`)
    removeReview(r)
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    busy(r.itemId, false)
  }
}

function removeReview(r: DueReview) {
  if (!today.value) return
  today.value.dueReviews = today.value.dueReviews.filter((x) => x.itemId !== r.itemId)
  dueReviews.set(today.value.dueReviews.length)
}

async function complete(t: NextTopic) {
  busy(t.itemId, true)
  try {
    await itemsApi.setCompleted(t.itemId, true)
    toast.success(`“${t.name}” completed. It comes back for a review tomorrow.`)
    void planList.refresh()
    await load()
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    busy(t.itemId, false)
  }
}

const overdue = (r: DueReview) => r.dueOn < todayIso()
const nothingToDo = computed(
  () => today.value && today.value.dueReviews.length === 0 && today.value.nextUp.length === 0 && cards.value.length === 0,
)
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="flex items-center gap-2 text-2xl font-semibold"><Sun class="size-6 text-amber-500" /> Today</h1>
      <p class="muted text-sm">{{ new Date().toLocaleDateString(undefined, { weekday: 'long', day: 'numeric', month: 'long' }) }}</p>
    </div>

    <EmptyState v-if="error" :icon="Sun" title="Couldn't load today's plan" :text="error">
      <button class="btn-primary" @click="load">Retry</button>
    </EmptyState>
    <LoadingState v-else-if="!today" />

    <template v-else>
      <!-- stats -->
      <section class="grid grid-cols-2 gap-3 lg:grid-cols-4">
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><Flame class="size-3.5 text-orange-500" /> Current streak</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity?.currentStreak ?? 0 }}</p>
          <p class="muted text-xs">day(s) in a row</p>
        </div>
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><Trophy class="size-3.5 text-amber-500" /> Best streak</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity?.bestStreak ?? 0 }}</p>
          <p class="muted text-xs">day(s)</p>
        </div>
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><CircleCheck class="size-3.5 text-emerald-600" /> Completed today</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ activity?.completedToday ?? 0 }}</p>
          <p class="muted text-xs">{{ activity?.completedLast7Days ?? 0 }} in the last 7 days</p>
        </div>
        <div class="card p-4">
          <p class="muted flex items-center gap-1 text-xs"><Clock class="size-3.5 text-sky-600" /> Time today</p>
          <p class="mt-1 text-2xl font-semibold tabular-nums">{{ minutesLabel(today.minutesToday) }}</p>
          <p class="muted text-xs">{{ minutesLabel(today.minutesLast7Days) }} in the last 7 days</p>
        </div>
      </section>

      <section v-if="activity" class="card p-4">
        <h2 class="mb-3 font-semibold">Your year</h2>
        <ActivityHeatmap :days="activity.days" />
      </section>

      <EmptyState
        v-if="nothingToDo"
        :icon="CircleCheck"
        title="All caught up"
        text="No reviews are due and every active plan is finished. Add topics to a plan, or set a new goal."
      >
        <RouterLink to="/plans" class="btn-primary">Go to plans</RouterLink>
      </EmptyState>

      <div class="grid gap-6 lg:grid-cols-2">
        <!-- reviews -->
        <section v-if="today.dueReviews.length">
          <h2 class="mb-1 flex items-center gap-2 font-semibold">
            <BrainCircuit class="size-4 text-violet-600" /> Review ({{ today.dueReviews.length }})
          </h2>
          <p class="muted mb-3 text-xs">Do you still remember these? Recall the key ideas first, then open your notes to check.</p>
          <ul class="card divide-y divide-slate-200 dark:divide-slate-800">
            <li v-for="r in today.dueReviews" :key="r.itemId" class="flex flex-wrap items-center gap-2 px-4 py-3">
              <div class="min-w-0 flex-1">
                <RouterLink :to="{ path: `/plans/${r.planId}`, query: { item: r.itemId, notes: 1 } }" class="block truncate font-medium hover:underline">
                  {{ r.name }}
                </RouterLink>
                <p class="muted truncate text-xs">
                  {{ r.planName }}<template v-if="r.parentName"> › {{ r.parentName }}</template> · review {{ r.review }} of {{ r.of }}
                  <span v-if="overdue(r)" class="text-amber-700 dark:text-amber-400"> · due {{ dayLabel(r.dueOn) }}</span>
                </p>
              </div>
              <div class="flex shrink-0 gap-1">
                <button class="btn-primary px-2.5" :disabled="working.has(r.itemId)" title="I remembered it: next review later" @click="review(r, true)">
                  <ThumbsUp class="size-4" /> Remembered
                </button>
                <button class="btn-secondary px-2.5" :disabled="working.has(r.itemId)" title="I forgot: review again tomorrow" @click="review(r, false)">
                  <ThumbsDown class="size-4" /> Forgot
                </button>
                <button class="icon-btn" :disabled="working.has(r.itemId)" :aria-label="`Stop reviewing ${r.name}`" title="Stop reviewing this topic" @click="stopReviews(r)">
                  <X class="size-4" />
                </button>
              </div>
            </li>
          </ul>
        </section>

        <!-- flashcards & shaky topics -->
        <section v-if="cards.length || shaky.length" class="space-y-6">
          <div v-if="cards.length">
            <h2 class="mb-1 flex items-center gap-2 font-semibold"><Layers class="size-4 text-violet-600" /> Flashcards ({{ cards.length }} due)</h2>
            <p class="muted mb-3 text-xs">Cards from your notes. A short session keeps them fresh.</p>
            <div class="card divide-y divide-slate-200 dark:divide-slate-800">
              <div v-for="p in cardPlans" :key="p.planId" class="flex items-center gap-2 px-4 py-2.5 text-sm">
                <span class="min-w-0 flex-1 truncate">{{ p.planName }}</span>
                <span class="chip">{{ p.count }}</span>
                <RouterLink :to="{ name: 'flashcards', query: { plan: p.planId } }" class="text-xs font-medium text-emerald-700 hover:underline dark:text-emerald-400">Study</RouterLink>
              </div>
              <div class="px-4 py-2.5">
                <RouterLink :to="{ name: 'flashcards' }" class="btn-primary w-full justify-center"><Layers class="size-4" /> Study all {{ cards.length }}</RouterLink>
              </div>
            </div>
          </div>
          <div v-if="shaky.length">
            <h2 class="mb-1 flex items-center gap-2 font-semibold"><Gauge class="size-4 text-orange-500" /> Shaky topics ({{ shaky.length }})</h2>
            <p class="muted mb-3 text-xs">You rated these 1–2. Revisit the notes, then rate them again.</p>
            <ul class="card divide-y divide-slate-200 dark:divide-slate-800">
              <li v-for="t in shaky.slice(0, 8)" :key="t.itemId" class="flex items-center gap-2 px-4 py-2 text-sm">
                <span class="rounded-full px-1.5 text-[11px] font-semibold text-white" :class="t.level === 1 ? 'bg-red-500' : 'bg-orange-500'" :title="LEVEL_LABEL[t.level]">{{ t.level }}</span>
                <RouterLink :to="{ path: `/plans/${t.planId}`, query: { item: t.itemId, notes: 1 } }" class="min-w-0 flex-1 truncate hover:underline">{{ t.name }}</RouterLink>
                <span class="muted shrink-0 truncate text-xs">{{ t.planName }}</span>
              </li>
            </ul>
          </div>
        </section>

        <!-- next up -->
        <section v-if="today.nextUp.length" :class="today.dueReviews.length || cards.length || shaky.length ? '' : 'lg:col-span-2'">
          <h2 class="mb-1 font-semibold">Next up</h2>
          <p class="muted mb-3 text-xs">The next unfinished topics of each active plan (Must topics first).</p>
          <div class="space-y-3">
            <div v-for="p in today.nextUp" :key="p.planId" class="card">
              <RouterLink :to="`/plans/${p.planId}`" class="block border-b border-slate-200 px-4 py-2 text-sm font-semibold hover:underline dark:border-slate-800">
                {{ p.planName }}
              </RouterLink>
              <ul class="divide-y divide-slate-200 dark:divide-slate-800">
                <li v-for="t in p.topics" :key="t.itemId" class="flex items-center gap-2.5 px-4 py-2">
                  <LoaderCircle v-if="working.has(t.itemId)" class="size-4 shrink-0 animate-spin text-slate-400" />
                  <input
                    v-else
                    type="checkbox"
                    class="size-4 shrink-0 cursor-pointer accent-emerald-600"
                    :aria-label="`Mark '${t.name}' as completed`"
                    @change="complete(t)"
                  />
                  <div class="min-w-0 flex-1">
                    <RouterLink :to="{ path: `/plans/${p.planId}`, query: { item: t.itemId } }" class="flex items-center gap-2 text-sm hover:underline">
                      <span class="truncate">{{ t.name }}</span>
                      <PriorityBadge :priority="t.priority" />
                    </RouterLink>
                    <p v-if="t.path.length" class="muted truncate text-xs">{{ t.path.join(' › ') }}</p>
                  </div>
                  <button
                    class="icon-btn shrink-0"
                    :disabled="!!pomodoro.running.value"
                    :title="pomodoro.running.value ? 'A Pomodoro is already running' : 'Start a 25-minute Pomodoro on this topic'"
                    :aria-label="`Start a Pomodoro on ${t.name}`"
                    @click="pomodoro.start({ id: t.itemId, name: t.name }, p.planId)"
                  >
                    <Play class="size-4" />
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </section>
      </div>
    </template>
  </div>
</template>
