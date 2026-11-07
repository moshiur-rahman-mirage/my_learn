<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { CircleAlert, Check, FileCode2, FileText, Folder, LoaderCircle, NotebookPen, Type, X } from 'lucide-vue-next'
import { notesApi } from '../api'
import { errorMessage } from '../api/client'
import type { NoteFormat, TreeNode } from '../types/api'
import { relativeTime } from '../lib/format'
import { htmlToMarkdown, markdownToEditorHtml } from '../lib/markdown'
import { useConfirm } from '../composables/useConfirm'
import NoteEditor from './NoteEditor.vue'
import MarkdownNoteEditor, { type MarkdownView } from './MarkdownNoteEditor.vue'
import ItemDetails from './ItemDetails.vue'

/**
 * Slide-over with the note of one module/topic. Saves automatically while you type.
 * A note is either Markdown (source stored, shown formatted on demand) or rich text (HTML).
 */
const props = defineProps<{ node: TreeNode | null; planId: number; allTags: string[] }>()
const emit = defineEmits<{ close: []; saved: [itemId: number, hasNotes: boolean]; changed: [] }>()

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
    try {
      const note = await notesApi.get(id)
      if (props.node?.id !== id) return // switched again meanwhile
      content.value = lastSaved = note.content ?? ''
      format.value = lastSavedFormat = note.format ?? readPref('notes.format', FORMATS, 'MARKDOWN')
      updatedAt.value = note.updatedAt
      loadedFor.value = id
      status.value = 'idle'
      await nextTick()
      editorRef.value?.focus()
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
    void close()
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
    e.preventDefault()
    if (loadedFor.value !== null) void save(loadedFor.value)
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
        class="fixed inset-y-0 right-0 z-40 flex w-full flex-col border-l border-slate-200 bg-white shadow-2xl transition-[max-width] duration-150 dark:border-slate-800 dark:bg-slate-950"
        :class="wide ? 'max-w-6xl' : 'max-w-2xl'"
        role="dialog"
        aria-modal="false"
        :aria-label="`Notes for ${node.name}`"
      >
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
          <button class="icon-btn" aria-label="Close notes (Esc)" title="Close (Esc)" @click="close"><X class="size-4" /></button>
        </header>

        <div class="flex-1 overflow-y-auto">
          <ItemDetails :node="node" :plan-id="planId" :all-tags="allTags" @changed="emit('changed')" />
          <div class="p-4">
            <div v-if="error" class="mb-3 flex items-start gap-2 rounded-lg border border-red-300 p-3 text-sm text-red-700 dark:border-red-900 dark:text-red-300">
              <CircleAlert class="mt-0.5 size-4 shrink-0" />
              <span class="flex-1">{{ error }}</span>
              <button v-if="loadedFor !== null" class="btn-secondary py-0.5" @click="save(loadedFor!)">Retry</button>
            </div>

            <div class="mb-2 flex items-center gap-2">
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

            <template v-if="status !== 'loading' || content">
              <MarkdownNoteEditor
                v-if="format === 'MARKDOWN'"
                ref="editorRef"
                v-model="content"
                v-model:view="mdView"
                :placeholder="`Summary of “${node.name}” in Markdown: # headings, - lists, - [ ] checklists, **bold**, code, [links](https://…)`"
              />
              <NoteEditor
                v-else
                ref="editorRef"
                v-model="content"
                :placeholder="`Summary of “${node.name}”: key ideas, examples, links, things to remember…`"
              />
            </template>
            <p v-if="format === 'MARKDOWN'" class="muted mt-2 text-xs">
              Saved automatically as you type (Ctrl+S saves immediately). Paste or write Markdown (GitHub style: tables, checklists, code blocks); pick
              <strong>Formatted</strong> to read it rendered, or <strong>Split</strong> to see both.
            </p>
            <p v-else class="muted mt-2 text-xs">
              Saved automatically as you type (Ctrl+S saves immediately). Formatting: use the toolbar, or Markdown shortcuts like <code>## </code>, <code>- </code>,
              <code>1. </code>, <code>[ ] </code>, <code>**bold**</code>.
            </p>
          </div>
        </div>
      </aside>
    </Transition>
  </Teleport>
</template>
