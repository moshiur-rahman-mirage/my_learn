<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { Clock, ExternalLink, Link2, LoaderCircle, Play, Plus, Square, Tag, Timer, Trash2, X } from 'lucide-vue-next'
import { linksApi, tagsApi, timeApi } from '../api'
import { errorMessage } from '../api/client'
import type { ItemTime, TreeNode } from '../types/api'
import { hostOf, minutesLabel, relativeTime } from '../lib/format'
import { useToast } from '../composables/useToast'
import { usePomodoro } from '../composables/usePomodoro'

/** Links, tags and time spent of one item (top of the notes panel). */
const props = defineProps<{ node: TreeNode; planId: number; allTags: string[] }>()
const emit = defineEmits<{ changed: [] }>()
const toast = useToast()
const pomodoro = usePomodoro()

// --- links ----------------------------------------------------------------------------------
const linkUrl = ref('')
const linkTitle = ref('')
const addingLink = ref(false)
const savingLink = ref(false)

async function addLink() {
  if (!linkUrl.value.trim()) return
  savingLink.value = true
  try {
    await linksApi.add(props.node.id, linkUrl.value.trim(), linkTitle.value.trim() || null)
    linkUrl.value = ''
    linkTitle.value = ''
    addingLink.value = false
    emit('changed')
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    savingLink.value = false
  }
}
async function removeLink(id: number) {
  try {
    await linksApi.remove(id)
    emit('changed')
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

// --- tags -----------------------------------------------------------------------------------
const tagInput = ref('')
const suggestions = computed(() => {
  const q = tagInput.value.trim().toLowerCase()
  const have = new Set(props.node.tags.map((t) => t.toLowerCase()))
  return props.allTags.filter((t) => !have.has(t.toLowerCase()) && (!q || t.toLowerCase().includes(q))).slice(0, 6)
})

async function saveTags(tags: string[]) {
  try {
    await tagsApi.set(props.node.id, tags)
    emit('changed')
  } catch (e) {
    toast.error(errorMessage(e))
  }
}
function addTag(name = tagInput.value) {
  const clean = name.trim().replace(/^#/, '')
  tagInput.value = ''
  if (!clean || props.node.tags.some((t) => t.toLowerCase() === clean.toLowerCase())) return
  void saveTags([...props.node.tags, clean])
}
function removeTag(name: string) {
  void saveTags(props.node.tags.filter((t) => t !== name))
}

// --- time -----------------------------------------------------------------------------------
const time = ref<ItemTime | null>(null)
const showEntries = ref(false)
const customMinutes = ref<number | null>(null)

async function loadTime() {
  try {
    time.value = await timeApi.forItem(props.node.id)
  } catch {
    time.value = null
  }
}
watch(() => props.node.id, loadTime, { immediate: true })
const stopListening = pomodoro.onLogged((itemId) => {
  if (itemId === props.node.id) {
    void loadTime()
    emit('changed')
  }
})
onBeforeUnmount(stopListening)

async function log(minutes: number | null) {
  if (!minutes || minutes < 1) return
  try {
    await timeApi.log(props.node.id, Math.round(minutes))
    toast.success(`${minutesLabel(Math.round(minutes))} logged`)
    customMinutes.value = null
    await loadTime()
    emit('changed')
  } catch (e) {
    toast.error(errorMessage(e))
  }
}
async function removeEntry(id: number) {
  try {
    await timeApi.remove(id)
    await loadTime()
    emit('changed')
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

const timerHere = computed(() => pomodoro.running.value?.itemId === props.node.id)
</script>

<template>
  <section class="space-y-4 border-b border-slate-200 px-4 py-3 text-sm dark:border-slate-800">
    <!-- links -->
    <div>
      <div class="mb-1.5 flex items-center justify-between">
        <h3 class="muted flex items-center gap-1 text-xs font-semibold tracking-wide uppercase"><Link2 class="size-3.5" /> Links</h3>
        <button v-if="!addingLink" class="btn-ghost px-2 py-0.5 text-xs" @click="addingLink = true"><Plus class="size-3.5" /> Add link</button>
      </div>
      <p v-if="node.links.length === 0 && !addingLink" class="muted text-xs">Videos, docs pages or course lessons for this item.</p>
      <ul class="space-y-1">
        <li v-for="l in node.links" :key="l.id" class="group flex items-center gap-2">
          <a :href="l.url" target="_blank" rel="noopener noreferrer" class="flex min-w-0 flex-1 items-center gap-1.5 text-emerald-700 hover:underline dark:text-emerald-400" :title="l.url">
            <ExternalLink class="size-3.5 shrink-0" />
            <span class="truncate">{{ l.title || hostOf(l.url) }}</span>
            <span v-if="l.title" class="muted shrink-0 text-xs">{{ hostOf(l.url) }}</span>
          </a>
          <button class="icon-btn size-6 opacity-0 group-hover:opacity-100 focus-visible:opacity-100" :aria-label="`Remove link ${l.title || l.url}`" @click="removeLink(l.id)">
            <Trash2 class="size-3.5" />
          </button>
        </li>
      </ul>
      <form v-if="addingLink" class="mt-1.5 flex flex-wrap gap-1.5" @submit.prevent="addLink">
        <input v-model="linkUrl" class="input min-w-48 flex-[2]" placeholder="https://…" aria-label="Link address" required />
        <input v-model="linkTitle" class="input min-w-32 flex-1" placeholder="Title (optional)" aria-label="Link title" />
        <button type="submit" class="btn-primary" :disabled="savingLink || !linkUrl.trim()">
          <LoaderCircle v-if="savingLink" class="size-4 animate-spin" /> Add
        </button>
        <button type="button" class="btn-ghost" @click="addingLink = false">Cancel</button>
      </form>
    </div>

    <!-- tags -->
    <div>
      <h3 class="muted mb-1.5 flex items-center gap-1 text-xs font-semibold tracking-wide uppercase"><Tag class="size-3.5" /> Tags</h3>
      <div class="flex flex-wrap items-center gap-1.5">
        <span v-for="t in node.tags" :key="t" class="inline-flex items-center gap-1 rounded-full bg-sky-100 px-2 py-0.5 text-xs font-medium text-sky-800 dark:bg-sky-500/15 dark:text-sky-300">
          #{{ t }}
          <button class="rounded-full hover:bg-sky-200 dark:hover:bg-sky-500/30" :aria-label="`Remove tag ${t}`" @click="removeTag(t)"><X class="size-3" /></button>
        </span>
        <form class="relative" @submit.prevent="addTag()">
          <input v-model="tagInput" class="input w-36 py-0.5 text-xs" placeholder="Add tag…" aria-label="Add tag" maxlength="40" list="tag-suggestions" />
          <datalist id="tag-suggestions">
            <option v-for="s in suggestions" :key="s" :value="s" />
          </datalist>
        </form>
      </div>
    </div>

    <!-- time -->
    <div>
      <div class="mb-1.5 flex items-center justify-between">
        <h3 class="muted flex items-center gap-1 text-xs font-semibold tracking-wide uppercase"><Clock class="size-3.5" /> Time spent</h3>
        <span class="text-xs font-medium tabular-nums">
          {{ minutesLabel(time?.totalMinutes ?? node.minutes) }}
          <span v-if="node.totalMinutes > node.minutes" class="muted font-normal">· {{ minutesLabel(node.totalMinutes) }} with sub-items</span>
        </span>
      </div>
      <div class="flex flex-wrap items-center gap-1.5">
        <button v-for="m in [15, 25, 50]" :key="m" class="chip" @click="log(m)">+{{ m }} min</button>
        <form class="flex items-center gap-1" @submit.prevent="log(customMinutes)">
          <input v-model.number="customMinutes" type="number" min="1" max="1440" class="input w-20 py-0.5 text-xs" placeholder="min" aria-label="Minutes to log" />
          <button type="submit" class="chip" :disabled="!customMinutes">Log</button>
        </form>
        <span class="mx-1 h-4 w-px bg-slate-200 dark:bg-slate-700" />
        <button v-if="!timerHere" class="chip" :disabled="!!pomodoro.running.value" :title="pomodoro.running.value ? 'Another Pomodoro is running' : 'Start a 25-minute focus timer'" @click="pomodoro.start(node, planId)">
          <Play class="size-3" /> Pomodoro 25:00
        </button>
        <button v-else class="chip chip-on" title="Stop without logging" @click="pomodoro.cancel()">
          <Timer class="size-3" /> {{ pomodoro.remainingLabel.value }} <Square class="size-3" />
        </button>
      </div>
      <button v-if="time && time.entries.length" class="muted mt-1.5 text-xs underline" @click="showEntries = !showEntries">
        {{ showEntries ? 'Hide' : 'Show' }} {{ time.entries.length }} session(s)
      </button>
      <ul v-if="showEntries && time" class="mt-1 max-h-40 space-y-0.5 overflow-y-auto text-xs">
        <li v-for="e in time.entries" :key="e.id" class="group flex items-center gap-2">
          <span class="w-14 tabular-nums">{{ minutesLabel(e.minutes) }}</span>
          <span class="muted flex-1" :title="e.loggedAt">{{ relativeTime(e.loggedAt) }}</span>
          <button class="icon-btn size-5 opacity-0 group-hover:opacity-100 focus-visible:opacity-100" aria-label="Delete this session" @click="removeEntry(e.id)"><Trash2 class="size-3" /></button>
        </li>
      </ul>
    </div>
  </section>
</template>
