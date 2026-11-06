<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { CircleCheck, FileText, Folder, LoaderCircle, Search, Square, X } from 'lucide-vue-next'
import { searchApi, tagsApi } from '../api'
import { errorMessage } from '../api/client'
import type { SearchResult, TagCount } from '../types/api'

/** Search all plans (names, descriptions, notes), optionally by tag. State lives in the URL. */
const route = useRoute()
const router = useRouter()

const q = ref(typeof route.query.q === 'string' ? route.query.q : '')
const tag = ref<string | null>(typeof route.query.tag === 'string' ? route.query.tag : null)
const tags = ref<TagCount[]>([])
const result = ref<SearchResult | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const input = ref<HTMLInputElement | null>(null)
let timer: ReturnType<typeof setTimeout> | undefined
let seq = 0

async function run() {
  const mySeq = ++seq
  void router.replace({ query: { ...(q.value.trim() ? { q: q.value.trim() } : {}), ...(tag.value ? { tag: tag.value } : {}) } })
  if (!q.value.trim() && !tag.value) {
    result.value = null
    return
  }
  loading.value = true
  error.value = null
  try {
    const r = await searchApi.search(q.value, tag.value)
    if (mySeq === seq) result.value = r
  } catch (e) {
    if (mySeq === seq) error.value = errorMessage(e)
  } finally {
    if (mySeq === seq) loading.value = false
  }
}

watch(q, () => {
  clearTimeout(timer)
  timer = setTimeout(run, 250)
})
watch(tag, run)

onMounted(async () => {
  input.value?.focus()
  tags.value = await tagsApi.all().catch(() => [])
  if (q.value || tag.value) void run()
})

const grouped = computed(() => {
  const groups = new Map<number, { planId: number; planName: string; archived: boolean; hits: SearchResult['hits'] }>()
  for (const h of result.value?.hits ?? []) {
    if (!groups.has(h.planId)) groups.set(h.planId, { planId: h.planId, planName: h.planName, archived: h.planArchived, hits: [] })
    groups.get(h.planId)!.hits.push(h)
  }
  return [...groups.values()]
})

const where: Record<string, string> = { name: 'name', description: 'description', note: 'notes' }
</script>

<template>
  <div class="max-w-4xl space-y-4">
    <h1 class="text-2xl font-semibold">Search</h1>

    <div class="relative">
      <Search class="absolute top-1/2 left-3 size-4 -translate-y-1/2 text-slate-400" />
      <input
        ref="input"
        v-model="q"
        type="search"
        class="input py-2 pl-9 text-base"
        placeholder="Search all plans: topic names, descriptions and your notes…"
        aria-label="Search all plans"
        data-shortcut-search
      />
      <LoaderCircle v-if="loading" class="absolute top-1/2 right-3 size-4 -translate-y-1/2 animate-spin text-slate-400" />
    </div>

    <div v-if="tags.length" class="flex flex-wrap items-center gap-1.5 text-xs">
      <span class="muted mr-1">Tags:</span>
      <button
        v-for="t in tags"
        :key="t.name"
        class="chip"
        :class="{ 'chip-on': tag === t.name }"
        :aria-pressed="tag === t.name"
        @click="tag = tag === t.name ? null : t.name"
      >
        #{{ t.name }} <span class="opacity-70">{{ t.items }}</span>
      </button>
      <button v-if="tag" class="chip" @click="tag = null"><X class="size-3" /> Any tag</button>
    </div>

    <p v-if="error" class="text-sm text-red-600 dark:text-red-400">{{ error }}</p>

    <p v-else-if="!result" class="muted text-sm">
      Type a few letters of any word: every word matches the start of a word, so <code>sprin boo</code> finds “Spring Boot”. Or pick a tag to see everything carrying it.
    </p>

    <p v-else-if="result.hits.length === 0" class="card muted p-6 text-center text-sm">Nothing found{{ tag ? ` with #${tag}` : '' }}.</p>

    <template v-else>
      <p class="muted text-xs">
        {{ result.hits.length }}{{ result.more ? '+' : '' }} result(s){{ result.more ? ': showing the best 50, add words to narrow it down' : '' }}
      </p>
      <section v-for="g in grouped" :key="g.planId">
        <h2 class="mb-1.5 flex items-center gap-2 text-sm font-semibold">
          <RouterLink :to="`/plans/${g.planId}`" class="hover:underline">{{ g.planName }}</RouterLink>
          <span v-if="g.archived" class="rounded bg-slate-200 px-1.5 text-[11px] font-medium text-slate-600 dark:bg-slate-800 dark:text-slate-300">Archived</span>
        </h2>
        <ul class="card divide-y divide-slate-200 dark:divide-slate-800">
          <li v-for="h in g.hits" :key="h.itemId">
            <RouterLink
              :to="{ path: `/plans/${h.planId}`, query: { item: h.itemId, ...(h.matchedIn === 'note' ? { notes: 1 } : {}) } }"
              class="flex items-start gap-2.5 px-4 py-2.5 hover:bg-slate-50 dark:hover:bg-slate-800/50"
            >
              <CircleCheck v-if="h.checkable && h.completed" class="mt-0.5 size-4 shrink-0 text-emerald-600" />
              <Square v-else-if="h.checkable" class="mt-0.5 size-4 shrink-0 text-slate-400" />
              <component :is="h.type === 'MODULE' ? Folder : FileText" v-else class="mt-0.5 size-4 shrink-0" :class="h.type === 'MODULE' ? 'text-amber-500' : 'text-slate-400'" />
              <span class="min-w-0 flex-1">
                <span class="block truncate text-sm font-medium" :class="h.completed ? 'text-slate-500 line-through' : ''">{{ h.name }}</span>
                <span v-if="h.path.length" class="muted block truncate text-xs">{{ h.path.join(' › ') }}</span>
                <span v-if="h.snippet.length" class="mt-0.5 block text-xs text-slate-600 dark:text-slate-300">
                  <span v-if="h.matchedIn && h.matchedIn !== 'name'" class="muted">in {{ where[h.matchedIn] }}: </span>
                  <template v-for="(s, i) in h.snippet" :key="i">
                    <mark v-if="s.hit" class="rounded bg-amber-200 px-0.5 text-slate-900 dark:bg-amber-500/40 dark:text-slate-100">{{ s.text }}</mark>
                    <template v-else>{{ s.text }}</template>
                  </template>
                </span>
              </span>
            </RouterLink>
          </li>
        </ul>
      </section>
    </template>
  </div>
</template>
