<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { Check, Eye, Layers, LoaderCircle, PartyPopper, RotateCcw } from 'lucide-vue-next'
import { flashcardsApi } from '../api'
import { errorMessage } from '../api/client'
import type { DueCard } from '../types/api'
import { renderMarkdown } from '../lib/markdown'
import { renderDiagrams } from '../lib/diagrams'
import { usePlanList } from '../composables/usePlanList'
import { useDueCards } from '../composables/useDueCards'
import { useToast } from '../composables/useToast'

/**
 * Study session for flashcards written in notes (Q:/A:). Cards due today come one at a time:
 * show the answer, then "Again" (comes back later this session) or "Got it" (next interval).
 * Keys: Space/Enter shows the answer; then 1 = again, 2 or Space = got it.
 */
const route = useRoute()
const router = useRouter()
const toast = useToast()
const { state: planList } = usePlanList()
const dueCards = useDueCards()

const planId = ref<number | null>(Number(route.query.plan) || null)
const queue = ref<DueCard[]>([])
const total = ref(0)
const done = ref(0)
const again = ref(0)
const revealed = ref(false)
const loading = ref(true)
const busy = ref(false)
const error = ref<string | null>(null)
const cardEl = ref<HTMLElement | null>(null)

const current = computed(() => queue.value[0] ?? null)
const activePlans = computed(() => planList.plans.filter((p) => p.status === 'ACTIVE'))
const question = computed(() => (current.value ? renderMarkdown(current.value.question) : ''))
const answer = computed(() => (current.value ? renderMarkdown(current.value.answer) : ''))

async function load() {
  loading.value = true
  error.value = null
  try {
    const cards = await flashcardsApi.due(planId.value)
    queue.value = cards
    total.value = cards.length
    done.value = 0
    again.value = 0
    revealed.value = false
    if (!planId.value) dueCards.set(cards.length)
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    loading.value = false
  }
}

watch(planId, (id) => {
  void router.replace({ query: id ? { plan: id } : {} })
  void load()
})

async function reveal() {
  revealed.value = true
}

async function grade(remembered: boolean) {
  const card = current.value
  if (!card || busy.value) return
  busy.value = true
  try {
    await flashcardsApi.review(card.id, remembered)
    const [, ...rest] = queue.value
    if (remembered) {
      queue.value = rest
      done.value++
    } else {
      queue.value = [...rest, card] // due again today: later in this session
      again.value++
    }
    revealed.value = false
    void dueCards.refresh()
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    busy.value = false
  }
}

watch(
  [question, answer, revealed],
  () => {
    void nextTick(() => renderDiagrams(cardEl.value))
  },
  { flush: 'post' },
)

function onKey(e: KeyboardEvent) {
  if (e.ctrlKey || e.metaKey || e.altKey) return
  if ((e.target as HTMLElement | null)?.closest?.('input, textarea, select')) return
  if (!current.value) return
  if (!revealed.value && (e.key === ' ' || e.key === 'Enter')) {
    e.preventDefault()
    void reveal()
  } else if (revealed.value && (e.key === '1' || e.key === 'ArrowLeft')) {
    e.preventDefault()
    void grade(false)
  } else if (revealed.value && (e.key === '2' || e.key === ' ' || e.key === 'ArrowRight' || e.key === 'Enter')) {
    e.preventDefault()
    void grade(true)
  }
}
onMounted(() => {
  document.addEventListener('keydown', onKey)
  void load()
})
onBeforeUnmount(() => document.removeEventListener('keydown', onKey))
</script>

<template>
  <div class="mx-auto max-w-3xl space-y-4">
    <div class="flex flex-wrap items-end justify-between gap-3">
      <div>
        <h1 class="flex items-center gap-2 text-2xl font-semibold"><Layers class="size-6 text-violet-600" /> Flashcards</h1>
        <p class="muted text-sm">Cards come from your notes: a <code>Q: …</code> line followed by an <code>A: …</code> line.</p>
      </div>
      <select v-model="planId" class="input w-auto" aria-label="Plan">
        <option :value="null">All active plans</option>
        <option v-for="p in activePlans" :key="p.id" :value="p.id">{{ p.name }}</option>
      </select>
    </div>

    <div v-if="loading" class="muted flex items-center gap-2 py-10"><LoaderCircle class="size-4 animate-spin" /> Loading cards…</div>
    <div v-else-if="error" class="card p-4 text-sm text-red-700 dark:text-red-300">{{ error }}</div>

    <template v-else-if="current">
      <div class="flex items-center gap-3 text-xs">
        <div class="h-1.5 flex-1 overflow-hidden rounded-full bg-slate-200 dark:bg-slate-800">
          <div class="h-full bg-violet-600 transition-all" :style="{ width: `${total ? (done / total) * 100 : 0}%` }" />
        </div>
        <span class="muted tabular-nums">{{ done }} / {{ total }} done<template v-if="again"> · {{ again }} again</template></span>
      </div>

      <article ref="cardEl" class="card space-y-4 p-6">
        <p class="muted text-xs">
          {{ current.planName }} ›
          <RouterLink :to="{ path: `/plans/${current.planId}`, query: { item: current.itemId, notes: 1 } }" class="hover:underline">{{ current.itemName }}</RouterLink>
          <span v-if="current.stage > 0"> · reviewed {{ current.stage }}×</span>
        </p>
        <!-- eslint-disable-next-line vue/no-v-html -- sanitized by DOMPurify in renderMarkdown -->
        <div class="note-content md-preview prose max-w-none text-lg font-medium dark:prose-invert" v-html="question" />

        <div v-if="revealed" class="border-t border-slate-200 pt-4 dark:border-slate-800">
          <!-- eslint-disable-next-line vue/no-v-html -- sanitized by DOMPurify in renderMarkdown -->
          <div class="note-content md-preview prose max-w-none dark:prose-invert" v-html="answer" />
        </div>
      </article>

      <div class="flex flex-wrap justify-center gap-3">
        <button v-if="!revealed" class="btn-primary px-6" @click="reveal"><Eye class="size-4" /> Show answer <kbd class="kbd">Space</kbd></button>
        <template v-else>
          <button class="btn-secondary px-6" :disabled="busy" @click="grade(false)">
            <RotateCcw class="size-4" /> Again <kbd class="kbd">1</kbd>
          </button>
          <button class="btn-primary px-6" :disabled="busy" @click="grade(true)">
            <Check class="size-4" /> Got it <kbd class="kbd">2</kbd>
          </button>
        </template>
      </div>
      <p class="muted text-center text-xs">"Again" brings the card back later in this session; "Got it" schedules it in 1, 3, 7, 14, 30, 60, then 120 days.</p>
    </template>

    <div v-else class="card flex flex-col items-center gap-2 p-10 text-center">
      <PartyPopper class="size-8 text-violet-600" />
      <p class="font-medium">{{ total ? 'Session done!' : 'No cards due right now.' }}</p>
      <p class="muted text-sm">
        <template v-if="total">You went through {{ total }} card{{ total === 1 ? '' : 's' }}<template v-if="again">, {{ again }} needed another try</template>.</template>
        <template v-else>Add cards to any note with <code>Q: question</code> and <code>A: answer</code> lines; new cards are due right away.</template>
      </p>
    </div>
  </div>
</template>
