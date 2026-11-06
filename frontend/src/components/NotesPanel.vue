<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { CircleAlert, Check, FileText, Folder, LoaderCircle, NotebookPen, X } from 'lucide-vue-next'
import { notesApi } from '../api'
import { errorMessage } from '../api/client'
import type { TreeNode } from '../types/api'
import { relativeTime } from '../lib/format'
import NoteEditor from './NoteEditor.vue'

/** Slide-over with the note of one module/topic. Saves automatically while you type. */
const props = defineProps<{ node: TreeNode | null }>()
const emit = defineEmits<{ close: []; saved: [itemId: number, hasNotes: boolean] }>()

type Status = 'loading' | 'idle' | 'pending' | 'saving' | 'saved' | 'error'
const SAVE_DELAY_MS = 800

const content = ref('')
const status = ref<Status>('loading')
const error = ref<string | null>(null)
const updatedAt = ref<string | null>(null)
const editorRef = ref<InstanceType<typeof NoteEditor> | null>(null)
let lastSaved = ''
let timer: ReturnType<typeof setTimeout> | undefined
const loadedFor = ref<number | null>(null)

const open = computed(() => props.node !== null)

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

watch(content, (html) => {
  if (loadedFor.value === null || html === lastSaved) return
  status.value = 'pending'
  clearTimeout(timer)
  const id = loadedFor.value
  timer = setTimeout(() => void save(id), SAVE_DELAY_MS)
})

async function save(itemId: number) {
  clearTimeout(timer)
  const html = content.value
  if (html === lastSaved) {
    if (status.value === 'pending') status.value = 'saved'
    return
  }
  status.value = 'saving'
  try {
    const note = await notesApi.save(itemId, html)
    lastSaved = html
    updatedAt.value = note.updatedAt
    error.value = null
    if (content.value === html) status.value = 'saved'
    emit('saved', itemId, note.content !== null)
  } catch (e) {
    status.value = 'error'
    error.value = errorMessage(e)
  }
}

/** Save anything typed but not yet sent (closing the panel or switching items). */
async function flush(itemId: number) {
  if (loadedFor.value === itemId && content.value !== lastSaved) await save(itemId)
}

async function close() {
  if (loadedFor.value !== null) await flush(loadedFor.value)
  emit('close')
}

function onKey(e: KeyboardEvent) {
  if (!open.value) return
  if (e.key === 'Escape') {
    e.preventDefault()
    void close()
  } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
    e.preventDefault()
    if (loadedFor.value !== null) void save(loadedFor.value)
  }
}
function beforeUnload(e: BeforeUnloadEvent) {
  if (loadedFor.value !== null && content.value !== lastSaved) {
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
        class="fixed inset-y-0 right-0 z-40 flex w-full max-w-2xl flex-col border-l border-slate-200 bg-white shadow-2xl dark:border-slate-800 dark:bg-slate-950"
        role="dialog"
        aria-modal="false"
        :aria-label="`Notes for ${node.name}`"
      >
        <header class="flex items-start gap-3 border-b border-slate-200 px-4 py-3 dark:border-slate-800">
          <NotebookPen class="mt-1 size-5 shrink-0 text-emerald-600" />
          <div class="min-w-0 flex-1">
            <p class="muted flex items-center gap-1 text-xs">
              <component :is="node.type === 'MODULE' ? Folder : FileText" class="size-3.5" />
              {{ node.type === 'MODULE' ? 'Module' : 'Topic' }} notes
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

        <div class="flex-1 overflow-y-auto p-4">
          <div v-if="error" class="mb-3 flex items-start gap-2 rounded-lg border border-red-300 p-3 text-sm text-red-700 dark:border-red-900 dark:text-red-300">
            <CircleAlert class="mt-0.5 size-4 shrink-0" />
            <span class="flex-1">{{ error }}</span>
            <button v-if="loadedFor !== null" class="btn-secondary py-0.5" @click="save(loadedFor!)">Retry</button>
          </div>
          <NoteEditor
            v-if="status !== 'loading' || content"
            ref="editorRef"
            v-model="content"
            :placeholder="`Summary of “${node.name}”: key ideas, examples, links, things to remember…`"
          />
          <p class="muted mt-2 text-xs">Saved automatically as you type (Ctrl+S saves immediately). Formatting: use the toolbar, or Markdown shortcuts like <code>## </code>, <code>- </code>, <code>1. </code>, <code>[ ] </code>, <code>**bold**</code>.</p>
        </div>
      </aside>
    </Transition>
  </Teleport>
</template>
