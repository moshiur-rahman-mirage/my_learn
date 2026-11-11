<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, shallowRef, watch } from 'vue'
import { RouterLink } from 'vue-router'
import {
  AArrowDown,
  AArrowUp,
  BookOpen,
  Expand,
  Layers,
  Link as LinkIcon,
  ListTree,
  Settings2,
  Shrink,
  CircleAlert,
  Check,
  FileCode2,
  FileText,
  Folder,
  LoaderCircle,
  Maximize2,
  Minimize2,
  NotebookPen,
  Type,
  X,
} from 'lucide-vue-next'
import { flashcardsApi, notesApi } from '../api'
import { errorMessage } from '../api/client'
import type { Backlink, ItemCard, NoteFormat, TreeNode } from '../types/api'
import { relativeTime } from '../lib/format'
import { htmlToMarkdown, markdownToEditorHtml, wikiKey } from '../lib/markdown'
import { useConfirm } from '../composables/useConfirm'
import { useToast } from '../composables/useToast'
import NoteEditor from './NoteEditor.vue'
import MarkdownNoteEditor, { type MarkdownView, type WikiTarget } from './MarkdownNoteEditor.vue'
import ItemDetails from './ItemDetails.vue'

/**
 * Slide-over with the note of one module/topic. Saves automatically while you type.
 * A note is either Markdown (source stored, shown formatted on demand) or rich text (HTML).
 * For comfortable reading the panel can be dragged wider by its left edge, maximized to the whole
 * window or the whole screen, switched to reading mode (details hidden, formatted, larger text,
 * outline of the headings, remembered scroll position) and its font, line width and size changed.
 * [[Name]] in a Markdown note links to another item of the plan; the panel lists the notes that
 * link here and the flashcards (Q:/A:) the note contains.
 */
const props = defineProps<{ node: TreeNode | null; planId: number; allTags: string[]; tree?: TreeNode[] }>()
const emit = defineEmits<{ close: []; saved: [itemId: number, hasNotes: boolean]; changed: []; open: [node: TreeNode] }>()
const toast = useToast()

type Status = 'loading' | 'idle' | 'pending' | 'saving' | 'saved' | 'error'
const SAVE_DELAY_MS = 800
const FORMATS = ['MARKDOWN', 'HTML'] as const
const MD_VIEWS = ['write', 'split', 'preview'] as const
const formatOptions = [
  { id: 'MARKDOWN' as const, label: 'Markdown', icon: FileCode2 },
  { id: 'HTML' as const, label: 'Rich text', icon: Type },
]

// per-viewer preferences: the format for new notes and the Markdown view
function readPref<T extends string>(key: string, allowed: readonly T[], fallback: T): T {
  try {
    const v = localStorage.getItem(key) as T | null
    return v && allowed.includes(v) ? v : fallback
  } catch {
    return fallback
  }
}
function writePref(key: string, value: string) {
  try {
    localStorage.setItem(key, value)
  } catch {
    // storage unavailable: the preference just isn't remembered
  }
}

function readNumber(key: string, fallback: number): number {
  try {
    const v = Number(localStorage.getItem(key))
    return Number.isFinite(v) && v > 0 ? v : fallback
  } catch {
    return fallback
  }
}

// --- size & reading comfort (remembered per viewer) ---------------------------------------
const MIN_WIDTH = 360
const SCALES = [0.85, 1, 1.15, 1.3, 1.5, 1.75, 2]
/** Width in px chosen by dragging; 0 = the default width. */
const width = ref(readNumber('notes.width', 0))
const maximized = ref(readPref('notes.maximized', ['1', '0'] as const, '0') === '1')
const reading = ref(readPref('notes.reading', ['1', '0'] as const, '0') === '1')
const scale = ref(readNumber('notes.scale', 1))
const dragging = ref(false)
watch(maximized, (v) => writePref('notes.maximized', v ? '1' : '0'))
watch(reading, (v) => writePref('notes.reading', v ? '1' : '0'))
watch(scale, (v) => writePref('notes.scale', String(v)))

/** Width the panel may take without covering the left sidebar (matches --notes-avail in CSS). */
function availableWidth() {
  return document.documentElement.clientWidth - (window.matchMedia('(min-width: 48rem)').matches ? 256 : 0)
}
const panelStyle = computed(() => {
  const style: Record<string, string> = { '--note-scale': String(scale.value) }
  if (maximized.value) {
    style.maxWidth = 'var(--notes-avail)'
  } else if (width.value > 0) {
    style.maxWidth = 'var(--notes-avail)'
    style.width = `min(${width.value}px, var(--notes-avail))`
  } else {
    style.maxWidth = `min(${wide.value ? '72rem' : '42rem'}, var(--notes-avail))`
  }
  return style
})

function zoom(step: number) {
  const i = SCALES.findIndex((v) => v >= scale.value - 0.001)
  const next = Math.min(SCALES.length - 1, Math.max(0, (i < 0 ? 1 : i) + step))
  scale.value = SCALES[next]
}

function startResize(e: PointerEvent) {
  if (e.button !== 0) return
  e.preventDefault()
  const handle = e.currentTarget as HTMLElement
  handle.setPointerCapture(e.pointerId)
  dragging.value = true
  const move = (ev: PointerEvent) => {
    const w = Math.round(document.documentElement.clientWidth - ev.clientX)
    maximized.value = false
    width.value = Math.max(MIN_WIDTH, Math.min(availableWidth(), w))
  }
  const up = () => {
    dragging.value = false
    handle.removeEventListener('pointermove', move)
    handle.removeEventListener('pointerup', up)
    handle.removeEventListener('pointercancel', up)
    if (width.value >= availableWidth() - 16) {
      maximized.value = true
      width.value = 0
    }
    writePref('notes.width', String(width.value))
  }
  handle.addEventListener('pointermove', move)
  handle.addEventListener('pointerup', up)
  handle.addEventListener('pointercancel', up)
}

/** Double-clicking the edge goes back to the default width. */
function resetWidth() {
  maximized.value = false
  width.value = 0
  writePref('notes.width', '0')
}

function toggleReading() {
  reading.value = !reading.value
  if (!reading.value) void nextTick(() => editorRef.value?.focus())
}

/** Markdown notes are shown formatted while reading; picking another view leaves reading mode. */
const shownMdView = computed<MarkdownView>(() => (reading.value ? 'preview' : mdView.value))
function onMdView(v: MarkdownView) {
  reading.value = false
  mdView.value = v
}

// --- reading settings: font, line width, true fullscreen ----------------------------------
const FONTS = ['sans', 'serif'] as const
const WIDTHS = ['narrow', 'medium', 'wide', 'full'] as const
type ReadingWidth = (typeof WIDTHS)[number]
const WIDTH_REM: Record<ReadingWidth, string> = { narrow: '38rem', medium: '48rem', wide: '64rem', full: 'none' }
const readingFont = ref(readPref('notes.font', FONTS, 'sans'))
const readingWidth = ref<ReadingWidth>(readPref('notes.lineWidth', WIDTHS, 'medium'))
watch(readingFont, (v) => writePref('notes.font', v))
watch(readingWidth, (v) => writePref('notes.lineWidth', v))
const settingsOpen = ref(false)

const fullscreen = ref(false)
let maximizedBeforeFullscreen = false
async function toggleFullscreen() {
  try {
    if (document.fullscreenElement) {
      await document.exitFullscreen()
    } else {
      maximizedBeforeFullscreen = maximized.value
      await document.documentElement.requestFullscreen()
      maximized.value = true
    }
  } catch {
    toast.error('Full screen is not available here. Try F11.')
  }
}
function onFullscreenChange() {
  fullscreen.value = document.fullscreenElement !== null
  if (!fullscreen.value) maximized.value = maximizedBeforeFullscreen
}
document.addEventListener('fullscreenchange', onFullscreenChange)

// --- outline of the note's headings (reading mode) ------------------------------------------
interface OutlineEntry {
  level: number
  text: string
  el: HTMLElement
}
const scroller = ref<HTMLElement | null>(null)
const noteArea = ref<HTMLElement | null>(null)
const outline = shallowRef<OutlineEntry[]>([])
const activeHeading = ref(-1)
let outlineObserver: MutationObserver | null = null
let outlineTimer: ReturnType<typeof setTimeout> | undefined

function refreshOutline() {
  const area = noteArea.value
  if (!area || !reading.value) {
    outline.value = []
    return
  }
  // the visible rendering only: the Markdown preview, or the rich-text editor
  const root = area.querySelector<HTMLElement>('.md-preview') ?? area.querySelector<HTMLElement>('.note-content')
  const headings = root ? Array.from(root.querySelectorAll<HTMLElement>('h1, h2, h3, h4')) : []
  outline.value = headings
    .map((el) => ({ level: Number(el.tagName[1]), text: (el.textContent ?? '').trim(), el }))
    .filter((h) => h.text)
  updateActiveHeading()
}
function scheduleOutline() {
  clearTimeout(outlineTimer)
  outlineTimer = setTimeout(refreshOutline, 200)
}
const outlineMinLevel = computed(() => Math.min(...outline.value.map((h) => h.level), 6))

function updateActiveHeading() {
  const box = scroller.value
  if (!box || !outline.value.length) {
    activeHeading.value = -1
    return
  }
  const top = box.getBoundingClientRect().top + 90
  let active = 0
  outline.value.forEach((h, i) => {
    if (h.el.getBoundingClientRect().top <= top) active = i
  })
  activeHeading.value = active
}
function jumpTo(h: OutlineEntry) {
  h.el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

watch(
  [reading, noteArea],
  ([r, area]) => {
    outlineObserver?.disconnect()
    outlineObserver = null
    if (r && area) {
      outlineObserver = new MutationObserver(scheduleOutline)
      outlineObserver.observe(area, { childList: true, subtree: true, characterData: true })
    }
    void nextTick(refreshOutline)
  },
  { flush: 'post' },
)

// --- remembered reading position per note ----------------------------------------------------
const SCROLL_KEY = 'notes.scroll'
function readScrollMap(): Record<string, number> {
  try {
    return JSON.parse(localStorage.getItem(SCROLL_KEY) ?? '{}') as Record<string, number>
  } catch {
    return {}
  }
}
let scrollSaveTimer: ReturnType<typeof setTimeout> | undefined
function onScroll() {
  updateActiveHeading()
  if (!reading.value || loadedFor.value === null) return
  const id = String(loadedFor.value)
  clearTimeout(scrollSaveTimer)
  scrollSaveTimer = setTimeout(() => {
    const map = readScrollMap()
    delete map[id]
    map[id] = Math.round(scroller.value?.scrollTop ?? 0)
    const keys = Object.keys(map)
    for (const k of keys.slice(0, Math.max(0, keys.length - 100))) delete map[k] // keep the last 100 notes
    writePref(SCROLL_KEY, JSON.stringify(map))
  }, 250)
}
/** Back to where you stopped reading this note (diagrams and math may still be settling). */
function restoreScroll() {
  if (!reading.value || loadedFor.value === null) return
  const top = readScrollMap()[String(loadedFor.value)]
  if (!top) return
  for (const delay of [60, 400]) {
    setTimeout(() => {
      if (scroller.value) scroller.value.scrollTop = top
    }, delay)
  }
}
watch(reading, (r) => {
  if (r) void nextTick(restoreScroll)
})

// --- [[links]], "linked from" and flashcards ----------------------------------------------------
function flatten(nodes: TreeNode[], out: TreeNode[] = []): TreeNode[] {
  for (const n of nodes) {
    out.push(n)
    flatten(n.children, out)
  }
  return out
}
const allNodes = computed(() => flatten(props.tree ?? []))
const byWikiKey = computed(() => {
  const map = new Map<string, TreeNode>()
  for (const n of allNodes.value) if (!map.has(wikiKey(n.name))) map.set(wikiKey(n.name), n)
  return map
})
const wikiTargets = computed<WikiTarget[] | undefined>(() =>
  props.tree ? allNodes.value.filter((n) => n.id !== props.node?.id).map((n) => ({ name: n.name, type: n.type })) : undefined,
)
function openWiki(name: string) {
  const target = byWikiKey.value.get(wikiKey(name))
  if (target) emit('open', target)
  else toast.info(`No item named “${name}” in this plan yet.`)
}
function openById(itemId: number) {
  const target = allNodes.value.find((n) => n.id === itemId)
  if (target) emit('open', target)
}

const backlinks = ref<Backlink[]>([])
const cards = ref<ItemCard[]>([])
const today = () => new Date().toLocaleDateString('en-CA') // yyyy-MM-dd in the viewer's time zone
const dueCards = computed(() => cards.value.filter((c) => c.dueOn <= today()).length)
async function loadExtras(itemId: number) {
  try {
    const [b, c] = await Promise.all([notesApi.backlinks(itemId), flashcardsApi.forItem(itemId)])
    if (props.node?.id !== itemId) return
    backlinks.value = b
    cards.value = c
  } catch {
    // extras are non-essential
  }
}

const content = ref('')
const format = ref<NoteFormat>(readPref('notes.format', FORMATS, 'MARKDOWN'))
const mdView = ref<MarkdownView>(readPref('notes.mdView', MD_VIEWS, 'split'))
watch(mdView, (v) => writePref('notes.mdView', v))
const status = ref<Status>('loading')
const error = ref<string | null>(null)
const updatedAt = ref<string | null>(null)
const editorRef = ref<{ focus: () => void } | null>(null)
let lastSaved = ''
let lastSavedFormat: NoteFormat = format.value
let timer: ReturnType<typeof setTimeout> | undefined
const loadedFor = ref<number | null>(null)
const { confirm } = useConfirm()

const open = computed(() => props.node !== null)
/** Split view needs room for two columns. */
const wide = computed(() => format.value === 'MARKDOWN' && mdView.value === 'split')
/** A format change alone only matters once there is content to save. */
const dirty = () => content.value !== lastSaved || (content.value !== '' && format.value !== lastSavedFormat)

watch(
  () => props.node?.id ?? null,
  async (id, previousId) => {
    if (previousId != null) await flush(previousId)
    if (id == null) return
    status.value = 'loading'
    error.value = null
    loadedFor.value = null
    backlinks.value = []
    cards.value = []
    void loadExtras(id)
    try {
      const note = await notesApi.get(id)
      if (props.node?.id !== id) return // switched again meanwhile
      content.value = lastSaved = note.content ?? ''
      format.value = lastSavedFormat = note.format ?? readPref('notes.format', FORMATS, 'MARKDOWN')
      updatedAt.value = note.updatedAt
      loadedFor.value = id
      status.value = 'idle'
      await nextTick()
      if (scroller.value) scroller.value.scrollTop = 0
      if (!reading.value) editorRef.value?.focus()
      restoreScroll()
    } catch (e) {
      status.value = 'error'
      error.value = errorMessage(e)
    }
  },
  { immediate: true },
)

watch([content, format], () => {
  if (loadedFor.value === null || !dirty()) return
  status.value = 'pending'
  clearTimeout(timer)
  const id = loadedFor.value
  timer = setTimeout(() => void save(id), SAVE_DELAY_MS)
})

async function save(itemId: number) {
  clearTimeout(timer)
  const text = content.value
  const fmt = format.value
  if (!dirty()) {
    if (status.value === 'pending') status.value = 'saved'
    return
  }
  status.value = 'saving'
  try {
    const note = await notesApi.save(itemId, text, fmt)
    lastSaved = text
    lastSavedFormat = fmt
    updatedAt.value = note.updatedAt
    error.value = null
    if (content.value === text && format.value === fmt) status.value = 'saved'
    emit('saved', itemId, note.content !== null)
    if (/^\s*(?:[-*+]\s+|\d+[.)]\s+)?(?:\*\*|__)?[QA](?:\*\*|__)?\s*[:：]/im.test(text) || cards.value.length) {
      void flashcardsApi.forItem(itemId).then((c) => {
        if (props.node?.id === itemId) cards.value = c
      })
    }
  } catch (e) {
    status.value = 'error'
    error.value = errorMessage(e)
  }
}

/** Save anything typed but not yet sent (closing the panel or switching items). */
async function flush(itemId: number) {
  if (loadedFor.value === itemId && dirty()) await save(itemId)
}

async function close() {
  if (loadedFor.value !== null) await flush(loadedFor.value)
  if (document.fullscreenElement) await document.exitFullscreen().catch(() => undefined)
  emit('close')
}

/** Switches between Markdown and rich text, converting the current note. */
async function switchFormat(target: NoteFormat) {
  if (target === format.value) return
  if (content.value.trim()) {
    const ok = await confirm({
      title: target === 'MARKDOWN' ? 'Convert this note to Markdown?' : 'Convert this note to rich text?',
      message:
        target === 'MARKDOWN'
          ? 'The note becomes Markdown source. Most formatting carries over; highlights and underlines stay as inline HTML.'
          : 'The Markdown becomes rich text. Tables and raw HTML may not survive the conversion.',
      confirmLabel: 'Convert',
    })
    if (!ok) return
    content.value = target === 'MARKDOWN' ? htmlToMarkdown(content.value) : markdownToEditorHtml(content.value)
  }
  writePref('notes.format', target)
  format.value = target
  await nextTick()
  editorRef.value?.focus()
}

function onKey(e: KeyboardEvent) {
  if (!open.value) return
  if (e.key === 'Escape') {
    if (document.querySelector('[role="alertdialog"], [aria-modal="true"]')) return // a confirm dialog is open
    e.preventDefault()
    if (settingsOpen.value) {
      settingsOpen.value = false
      return
    }
    void close()
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
    e.preventDefault()
    if (loadedFor.value !== null) void save(loadedFor.value)
  } else if (e.altKey && !e.ctrlKey && !e.metaKey && e.key === 'Enter') {
    e.preventDefault()
    maximized.value = !maximized.value
  } else if (e.altKey && !e.ctrlKey && !e.metaKey && e.key.toLowerCase() === 'r') {
    e.preventDefault()
    toggleReading()
  } else if ((e.ctrlKey || e.metaKey) && e.altKey && (e.key === '=' || e.key === '+')) {
    e.preventDefault()
    zoom(1)
  } else if ((e.ctrlKey || e.metaKey) && e.altKey && e.key === '-') {
    e.preventDefault()
    zoom(-1)
  }
}
function beforeUnload(e: BeforeUnloadEvent) {
  if (loadedFor.value !== null && dirty()) {
    void save(loadedFor.value)
    e.preventDefault()
  }
}
watch(
  open,
  (o) => {
    if (o) {
      document.addEventListener('keydown', onKey)
      window.addEventListener('beforeunload', beforeUnload)
    } else {
      document.removeEventListener('keydown', onKey)
      window.removeEventListener('beforeunload', beforeUnload)
    }
  },
  { immediate: true },
)
onBeforeUnmount(() => {
  document.removeEventListener('keydown', onKey)
  document.removeEventListener('fullscreenchange', onFullscreenChange)
  outlineObserver?.disconnect()
  clearTimeout(outlineTimer)
  clearTimeout(scrollSaveTimer)
  if (document.fullscreenElement) void document.exitFullscreen().catch(() => undefined)
  window.removeEventListener('beforeunload', beforeUnload)
  if (loadedFor.value !== null) void flush(loadedFor.value)
})
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-from-class="translate-x-full"
      enter-active-class="transition-transform duration-200"
      leave-to-class="translate-x-full"
      leave-active-class="transition-transform duration-150"
    >
      <aside
        v-if="node"
        class="notes-panel @container fixed inset-y-0 right-0 z-40 flex w-full flex-col border-l border-slate-200 bg-white shadow-2xl dark:border-slate-800 dark:bg-slate-950"
        :class="[
          {
            'notes-reading': reading,
            'notes-serif': readingFont === 'serif',
            'transition-[max-width] duration-150': !dragging,
            'select-none': dragging,
          },
        ]"
        :style="panelStyle"
        role="dialog"
        aria-modal="false"
        :aria-label="`Notes for ${node.name}`"
      >
        <div
          class="absolute inset-y-0 left-0 z-10 hidden w-2 cursor-col-resize hover:bg-emerald-500/30 sm:block"
          :class="dragging ? 'bg-emerald-500/40' : ''"
          role="separator"
          aria-orientation="vertical"
          aria-label="Drag to resize the notes panel"
          title="Drag to resize · double-click for the default width"
          @pointerdown="startResize"
          @dblclick="resetWidth"
        />
        <header class="flex items-start gap-3 border-b border-slate-200 px-4 py-3 dark:border-slate-800">
          <NotebookPen class="mt-1 size-5 shrink-0 text-emerald-600" />
          <div class="min-w-0 flex-1">
            <p class="muted flex items-center gap-1 text-xs">
              <component :is="node.type === 'MODULE' ? Folder : FileText" class="size-3.5" />
              {{ node.type === 'MODULE' ? 'Module' : 'Topic' }} details &amp; notes
            </p>
            <h2 class="truncate text-lg font-semibold" :title="node.name">{{ node.name }}</h2>
          </div>
          <div class="flex shrink-0 items-center gap-2 pt-1 text-xs" aria-live="polite">
            <span v-if="status === 'loading'" class="muted flex items-center gap-1"><LoaderCircle class="size-3.5 animate-spin" /> Loading</span>
            <span v-else-if="status === 'pending' || status === 'saving'" class="muted flex items-center gap-1">
              <LoaderCircle class="size-3.5 animate-spin" /> Saving…
            </span>
            <span v-else-if="status === 'error'" class="flex items-center gap-1 text-red-600 dark:text-red-400"><CircleAlert class="size-3.5" /> Not saved</span>
            <span v-else-if="updatedAt" class="muted flex items-center gap-1" :title="updatedAt">
              <Check class="size-3.5 text-emerald-600" /> Saved {{ relativeTime(updatedAt) }}
            </span>
          </div>
          <div class="flex shrink-0 items-center">
            <button class="icon-btn" aria-label="Smaller text" title="Smaller text (Ctrl+Alt+−)" :disabled="scale <= SCALES[0]" @click="zoom(-1)">
              <AArrowDown class="size-4" />
            </button>
            <button class="icon-btn" aria-label="Larger text" title="Larger text (Ctrl+Alt++)" :disabled="scale >= SCALES[SCALES.length - 1]" @click="zoom(1)">
              <AArrowUp class="size-4" />
            </button>
            <div class="relative">
              <button
                class="icon-btn"
                :class="settingsOpen ? 'bg-slate-200 dark:bg-slate-800' : ''"
                aria-label="Reading settings"
                title="Reading settings: font, line width"
                :aria-expanded="settingsOpen"
                @click="settingsOpen = !settingsOpen"
              >
                <Settings2 class="size-4" />
              </button>
              <div
                v-if="settingsOpen"
                class="card absolute right-0 top-9 z-30 w-64 space-y-3 p-3 text-xs shadow-xl"
                role="dialog"
                aria-label="Reading settings"
              >
                <div>
                  <p class="muted mb-1 font-medium">Font</p>
                  <div class="flex rounded-md bg-slate-200/70 p-0.5 dark:bg-slate-800">
                    <button
                      v-for="f in FONTS"
                      :key="f"
                      type="button"
                      class="flex-1 rounded px-2 py-1"
                      :class="[readingFont === f ? 'bg-white shadow-sm dark:bg-slate-950' : '', f === 'serif' ? 'font-serif' : 'font-sans']"
                      :aria-pressed="readingFont === f"
                      @click="readingFont = f"
                    >
                      {{ f === 'serif' ? 'Serif' : 'Sans-serif' }}
                    </button>
                  </div>
                </div>
                <div>
                  <p class="muted mb-1 font-medium">Line width in reading mode</p>
                  <div class="flex rounded-md bg-slate-200/70 p-0.5 dark:bg-slate-800">
                    <button
                      v-for="w in WIDTHS"
                      :key="w"
                      type="button"
                      class="flex-1 rounded px-1 py-1 capitalize"
                      :class="readingWidth === w ? 'bg-white shadow-sm dark:bg-slate-950' : ''"
                      :aria-pressed="readingWidth === w"
                      @click="readingWidth = w"
                    >
                      {{ w }}
                    </button>
                  </div>
                </div>
                <div class="flex items-center justify-between">
                  <span class="muted font-medium">Text size {{ Math.round(scale * 100) }}%</span>
                  <span class="flex">
                    <button class="icon-btn size-7" aria-label="Smaller text" :disabled="scale <= SCALES[0]" @click="zoom(-1)"><AArrowDown class="size-4" /></button>
                    <button class="icon-btn size-7" aria-label="Larger text" :disabled="scale >= SCALES[SCALES.length - 1]" @click="zoom(1)"><AArrowUp class="size-4" /></button>
                  </span>
                </div>
                <button class="btn-secondary w-full justify-center" @click="toggleFullscreen">
                  <Shrink v-if="fullscreen" class="size-4" />
                  <Expand v-else class="size-4" />
                  {{ fullscreen ? 'Exit full screen' : 'Full screen' }}
                </button>
              </div>
            </div>
            <button
              class="icon-btn"
              :class="reading ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-300' : ''"
              :aria-pressed="reading"
              aria-label="Reading mode"
              title="Reading mode: hide details, show formatted, larger text (Alt+R)"
              @click="toggleReading"
            >
              <BookOpen class="size-4" />
            </button>
            <button
              class="icon-btn"
              :aria-label="fullscreen ? 'Exit full screen' : 'Full screen'"
              :title="fullscreen ? 'Exit full screen (Esc)' : 'Full screen: the note fills the whole screen'"
              @click="toggleFullscreen"
            >
              <Shrink v-if="fullscreen" class="size-4" />
              <Expand v-else class="size-4" />
            </button>
            <button
              v-if="!fullscreen"
              class="icon-btn"
              :aria-label="maximized ? 'Restore panel size' : 'Maximize panel'"
              :title="maximized ? 'Restore size (Alt+Enter)' : 'Maximize to full window (Alt+Enter)'"
              @click="maximized = !maximized"
            >
              <Minimize2 v-if="maximized" class="size-4" />
              <Maximize2 v-else class="size-4" />
            </button>
            <button class="icon-btn" aria-label="Close notes (Esc)" title="Close (Esc)" @click="close"><X class="size-4" /></button>
          </div>
        </header>

        <div ref="scroller" class="flex-1 overflow-y-auto" @scroll.passive="onScroll" @click="settingsOpen = false">
          <ItemDetails v-show="!reading" :node="node" :plan-id="planId" :all-tags="allTags" @changed="emit('changed')" />
          <div :class="reading ? 'flex justify-center gap-6 px-2' : ''">
          <nav
            v-if="reading && outline.length >= 2"
            class="sticky top-0 hidden max-h-[calc(100vh-5rem)] w-60 shrink-0 self-start overflow-y-auto py-6 pr-2 text-sm @4xl:block"
            aria-label="Outline"
          >
            <p class="muted mb-2 flex items-center gap-1 text-xs font-semibold tracking-wide uppercase"><ListTree class="size-3.5" /> Outline</p>
            <ul class="space-y-0.5 border-l border-slate-200 dark:border-slate-800">
              <li v-for="(h, i) in outline" :key="i">
                <button
                  type="button"
                  class="-ml-px block w-full truncate border-l-2 py-0.5 pr-1 text-left hover:text-emerald-700 dark:hover:text-emerald-300"
                  :class="i === activeHeading ? 'border-emerald-600 font-medium text-emerald-700 dark:text-emerald-300' : 'border-transparent muted'"
                  :style="{ paddingLeft: `${0.6 + (h.level - outlineMinLevel) * 0.75}rem` }"
                  :title="h.text"
                  @click="jumpTo(h)"
                >
                  {{ h.text }}
                </button>
              </li>
            </ul>
          </nav>
          <div class="p-4" :class="reading ? 'w-full min-w-0 py-6' : ''" :style="reading ? { maxWidth: WIDTH_REM[readingWidth] } : undefined">
            <div v-if="error" class="mb-3 flex items-start gap-2 rounded-lg border border-red-300 p-3 text-sm text-red-700 dark:border-red-900 dark:text-red-300">
              <CircleAlert class="mt-0.5 size-4 shrink-0" />
              <span class="flex-1">{{ error }}</span>
              <button v-if="loadedFor !== null" class="btn-secondary py-0.5" @click="save(loadedFor!)">Retry</button>
            </div>

            <div v-show="!reading" class="mb-2 flex items-center gap-2">
              <span class="muted text-xs">Note format</span>
              <div class="flex rounded-md bg-slate-200/70 p-0.5 dark:bg-slate-800" role="radiogroup" aria-label="Note format">
                <button
                  v-for="f in formatOptions"
                  :key="f.id"
                  type="button"
                  role="radio"
                  :aria-checked="format === f.id"
                  class="flex items-center gap-1 rounded px-2 py-0.5 text-xs font-medium"
                  :class="format === f.id ? 'bg-white text-emerald-700 shadow-sm dark:bg-slate-950 dark:text-emerald-300' : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100'"
                  :disabled="status === 'loading'"
                  @click="switchFormat(f.id)"
                >
                  <component :is="f.icon" class="size-3.5" /> {{ f.label }}
                </button>
              </div>
            </div>

            <div ref="noteArea">
            <template v-if="status !== 'loading' || content">
              <MarkdownNoteEditor
                v-if="format === 'MARKDOWN'"
                ref="editorRef"
                v-model="content"
                :view="shownMdView"
                :wiki-targets="wikiTargets"
                @update:view="onMdView"
                @wiki="openWiki"
                :placeholder="`Summary of “${node.name}” in Markdown: # headings, - lists, - [ ] checklists, **bold**, code, [links](https://…)`"
              />
              <NoteEditor
                v-else
                ref="editorRef"
                v-model="content"
                :placeholder="`Summary of “${node.name}”: key ideas, examples, links, things to remember…`"
              />
            </template>
            </div>

            <!-- flashcards & notes linking here -->
            <div v-if="cards.length || backlinks.length" class="mt-4 space-y-2 rounded-lg border border-slate-200 p-3 text-sm dark:border-slate-800">
              <div v-if="cards.length" class="flex flex-wrap items-center gap-2">
                <Layers class="size-4 text-violet-600" />
                <span>{{ cards.length }} flashcard{{ cards.length === 1 ? '' : 's' }} in this note</span>
                <span v-if="dueCards" class="chip">{{ dueCards }} due</span>
                <RouterLink :to="{ name: 'flashcards', query: { plan: planId } }" class="ml-auto text-xs font-medium text-emerald-700 hover:underline dark:text-emerald-400">
                  Study this plan's cards →
                </RouterLink>
              </div>
              <div v-if="backlinks.length" class="flex flex-wrap items-center gap-1.5">
                <LinkIcon class="size-4 text-sky-600" />
                <span class="muted text-xs">Linked from</span>
                <button
                  v-for="b in backlinks"
                  :key="b.itemId"
                  type="button"
                  class="chip"
                  :title="`Open the note of “${b.name}”`"
                  @click="openById(b.itemId)"
                >
                  <component :is="b.type === 'MODULE' ? Folder : FileText" class="size-3" /> {{ b.name }}
                </button>
              </div>
            </div>
            <p v-if="reading" class="muted mt-2 text-xs">
              Reading mode — Alt+R or the book button to edit again (double-click a Markdown note to jump into editing).
            </p>
            <p v-else-if="format === 'MARKDOWN'" class="muted mt-2 text-xs">
              Saved automatically as you type (Ctrl+S saves immediately). Markdown (GitHub style) plus: <code>```java</code> highlighted code,
              <code>$E=mc^2$</code> / <code>$$…$$</code> math, <code>```mermaid</code> diagrams, <code>[[Topic name]]</code> links to other items, and
              flashcards as <code>Q: …</code> then <code>A: …</code> lines.
            </p>
            <p v-else class="muted mt-2 text-xs">
              Saved automatically as you type (Ctrl+S saves immediately). Formatting: use the toolbar, or Markdown shortcuts like <code>## </code>, <code>- </code>,
              <code>1. </code>, <code>[ ] </code>, <code>**bold**</code>. Flashcards: a <code>Q: …</code> paragraph followed by an <code>A: …</code> paragraph.
            </p>
          </div>
          </div>
        </div>
      </aside>
    </Transition>
  </Teleport>
</template>
