<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { Columns2, Eye, FileText, Folder, Pencil } from 'lucide-vue-next'
import { renderMarkdown, wikiKey } from '../lib/markdown'
import { renderDiagrams } from '../lib/diagrams'

/**
 * Plain-text Markdown editor with a rendered view. The note is stored as the Markdown source;
 * "Preview" shows it formatted, "Split" shows both side by side.
 * Rendering adds highlighted code, $math$, ```mermaid diagrams and [[links]] to other items:
 * typing [[ suggests item names, and clicking a link in the preview emits `wiki`.
 */
export type MarkdownView = 'write' | 'split' | 'preview'

export interface WikiTarget {
  name: string
  type: 'MODULE' | 'TOPIC'
}

const props = defineProps<{ modelValue: string; placeholder?: string; wikiTargets?: WikiTarget[] }>()
const emit = defineEmits<{ 'update:modelValue': [md: string]; wiki: [name: string] }>()
const view = defineModel<MarkdownView>('view', { default: 'split' })

const textarea = ref<HTMLTextAreaElement | null>(null)
const preview = ref<HTMLElement | null>(null)
const targetKeys = computed(() => new Set((props.wikiTargets ?? []).map((t) => wikiKey(t.name))))
const rendered = computed(() =>
  renderMarkdown(props.modelValue, props.wikiTargets ? { wikiExists: (n) => targetKeys.value.has(wikiKey(n)) } : {}),
)

// diagrams are drawn after the HTML is in place (debounced while typing)
let diagramTimer: ReturnType<typeof setTimeout> | undefined
watch(
  [rendered, view],
  () => {
    clearTimeout(diagramTimer)
    if (view.value === 'write') return
    diagramTimer = setTimeout(() => void renderDiagrams(preview.value), 300)
  },
  { flush: 'post', immediate: true },
)
onBeforeUnmount(() => clearTimeout(diagramTimer))

function onPreviewClick(e: MouseEvent | KeyboardEvent) {
  const link = (e.target as HTMLElement).closest<HTMLElement>('[data-wiki]')
  if (!link) return
  if (e instanceof KeyboardEvent && e.key !== 'Enter') return
  e.preventDefault()
  e.stopPropagation()
  emit('wiki', link.dataset.wiki ?? '')
}

// --- [[ autocomplete ---------------------------------------------------------------------------
const suggestQuery = ref<string | null>(null)
const suggestIndex = ref(0)
const suggestions = computed(() => {
  if (suggestQuery.value === null || !props.wikiTargets?.length) return []
  const q = wikiKey(suggestQuery.value)
  const starts: WikiTarget[] = []
  const contains: WikiTarget[] = []
  for (const t of props.wikiTargets) {
    const k = wikiKey(t.name)
    if (k.startsWith(q)) starts.push(t)
    else if (k.includes(q)) contains.push(t)
  }
  return [...starts, ...contains].slice(0, 8)
})

/** An open "[[" before the caret on the same line, without its closing "]]". */
function updateSuggest() {
  const ta = textarea.value
  if (!ta || !props.wikiTargets?.length || ta.selectionStart !== ta.selectionEnd) {
    suggestQuery.value = null
    return
  }
  const before = ta.value.slice(0, ta.selectionStart)
  const open = before.lastIndexOf('[[')
  const text = open < 0 ? '' : before.slice(open + 2)
  if (open < 0 || /[\]\n|]/.test(text) || text.length > 60) {
    suggestQuery.value = null
    return
  }
  if (suggestQuery.value !== text) suggestIndex.value = 0
  suggestQuery.value = text
}

function pickSuggestion(t: WikiTarget) {
  const ta = textarea.value
  if (!ta) return
  const caret = ta.selectionStart
  const open = ta.value.slice(0, caret).lastIndexOf('[[')
  const after = ta.value.slice(caret)
  const closing = after.startsWith(']]') ? 2 : 0
  const insert = `[[${t.name}]]`
  setValue(ta.value.slice(0, open) + insert + after.slice(closing), open + insert.length)
  suggestQuery.value = null
  void nextTick(() => ta.focus())
}

function onInput(e: Event) {
  emit('update:modelValue', (e.target as HTMLTextAreaElement).value)
  void nextTick(updateSuggest)
}

const views: { id: MarkdownView; label: string; icon: unknown }[] = [
  { id: 'write', label: 'Markdown', icon: Pencil },
  { id: 'split', label: 'Split', icon: Columns2 },
  { id: 'preview', label: 'Formatted', icon: Eye },
]

function setValue(value: string, selStart: number, selEnd = selStart) {
  emit('update:modelValue', value)
  void nextTick(() => {
    textarea.value?.setSelectionRange(selStart, selEnd)
  })
}

/** Wraps the selection in a marker (Ctrl+B → **text**), or unwraps it if already wrapped. */
function wrap(marker: string) {
  const ta = textarea.value
  if (!ta) return
  const { selectionStart: s, selectionEnd: e, value } = ta
  const before = value.slice(0, s)
  const sel = value.slice(s, e)
  const after = value.slice(e)
  if (before.endsWith(marker) && after.startsWith(marker)) {
    setValue(before.slice(0, -marker.length) + sel + after.slice(marker.length), s - marker.length, e - marker.length)
  } else {
    setValue(before + marker + sel + marker + after, s + marker.length, e + marker.length)
  }
}

function onKeydown(ev: KeyboardEvent) {
  const ta = textarea.value
  if (!ta) return
  const mod = ev.ctrlKey || ev.metaKey
  if (suggestions.value.length) {
    if (ev.key === 'ArrowDown' || ev.key === 'ArrowUp') {
      ev.preventDefault()
      const n = suggestions.value.length
      suggestIndex.value = (suggestIndex.value + (ev.key === 'ArrowDown' ? 1 : n - 1)) % n
      return
    }
    if (ev.key === 'Enter' || ev.key === 'Tab') {
      ev.preventDefault()
      pickSuggestion(suggestions.value[suggestIndex.value] ?? suggestions.value[0])
      return
    }
    if (ev.key === 'Escape') {
      ev.preventDefault()
      ev.stopPropagation()
      suggestQuery.value = null
      return
    }
  }
  if (mod && ev.key.toLowerCase() === 'b') {
    ev.preventDefault()
    wrap('**')
  } else if (mod && ev.key.toLowerCase() === 'i') {
    ev.preventDefault()
    wrap('*')
  } else if (mod && ev.key.toLowerCase() === 'e') {
    ev.preventDefault()
    wrap('`')
  } else if (ev.key === 'Tab' && !mod && !ev.altKey) {
    // indent / outdent the selected lines (for nested lists)
    ev.preventDefault()
    const { selectionStart: s, selectionEnd: e, value } = ta
    const lineStart = value.lastIndexOf('\n', s - 1) + 1
    const block = value.slice(lineStart, e)
    if (!ev.shiftKey && s === e) {
      setValue(value.slice(0, s) + '  ' + value.slice(e), s + 2)
      return
    }
    const lines = block.split('\n')
    const changed = ev.shiftKey ? lines.map((l) => l.replace(/^ {1,2}/, '')) : lines.map((l) => '  ' + l)
    const newBlock = changed.join('\n')
    const firstDelta = changed[0].length - lines[0].length
    setValue(value.slice(0, lineStart) + newBlock + value.slice(e), Math.max(lineStart, s + firstDelta), e + (newBlock.length - block.length))
  } else if (ev.key === 'Enter' && !mod && !ev.shiftKey) {
    // continue a list: "- ", "1. ", "- [ ] "; Enter on an empty item ends the list
    const { selectionStart: s, selectionEnd: e, value } = ta
    if (s !== e) return
    const lineStart = value.lastIndexOf('\n', s - 1) + 1
    const line = value.slice(lineStart, s)
    const m = line.match(/^(\s*)([-*+]|\d+[.)])(\s+\[[ xX]\])?\s+/)
    if (!m) return
    ev.preventDefault()
    if (line.trim() === m[0].trim()) {
      setValue(value.slice(0, lineStart) + value.slice(s), lineStart)
      return
    }
    const num = m[2].match(/^(\d+)([.)])$/)
    const bullet = num ? `${Number(num[1]) + 1}${num[2]}` : m[2]
    const insert = `\n${m[1]}${bullet}${m[3] ? ' [ ]' : ''} `
    setValue(value.slice(0, s) + insert + value.slice(s), s + insert.length)
  }
}

function focus() {
  if (view.value === 'preview') return
  const ta = textarea.value
  if (!ta) return
  ta.focus()
  ta.setSelectionRange(ta.value.length, ta.value.length)
}
defineExpose({ focus })

function editFromPreview() {
  if (view.value === 'preview') {
    view.value = 'write'
    void nextTick(focus)
  }
}
</script>

<template>
  <div class="overflow-hidden rounded-lg border border-slate-300 bg-white dark:border-slate-700 dark:bg-slate-900">
    <div class="flex items-center gap-1 border-b border-slate-200 bg-slate-50 p-1 dark:border-slate-800 dark:bg-slate-900">
      <div class="flex rounded-md bg-slate-200/70 p-0.5 dark:bg-slate-800" role="tablist" aria-label="Note view">
        <button
          v-for="v in views"
          :key="v.id"
          type="button"
          role="tab"
          :aria-selected="view === v.id"
          class="flex items-center gap-1 rounded px-2 py-1 text-xs font-medium"
          :class="view === v.id ? 'bg-white text-emerald-700 shadow-sm dark:bg-slate-950 dark:text-emerald-300' : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100'"
          @click="view = v.id"
        >
          <component :is="v.icon" class="size-3.5" /> {{ v.label }}
        </button>
      </div>
      <span class="muted ml-auto hidden pr-1 text-xs sm:inline">Ctrl+B bold · Ctrl+I italic · Ctrl+E code · Tab indent · [[ link</span>
    </div>

    <div class="grid" :class="view === 'split' ? 'md:grid-cols-2 md:divide-x divide-slate-200 dark:divide-slate-800' : ''">
      <div v-show="view !== 'preview'" class="relative">
        <textarea
          ref="textarea"
          :value="modelValue"
          class="min-h-96 w-full resize-y bg-transparent px-4 py-3 font-mono text-sm leading-relaxed focus:outline-none"
          :placeholder="placeholder ?? 'Write in Markdown…'"
          spellcheck="true"
          aria-label="Note (Markdown)"
          :aria-expanded="suggestions.length > 0"
          aria-autocomplete="list"
          @input="onInput"
          @keydown="onKeydown"
          @click="updateSuggest"
          @blur="suggestQuery = null"
        />
        <ul
          v-if="suggestions.length"
          class="absolute inset-x-3 bottom-3 z-20 max-h-60 overflow-auto rounded-md border border-slate-200 bg-white py-1 text-sm shadow-lg dark:border-slate-700 dark:bg-slate-900"
          role="listbox"
          aria-label="Link to an item"
        >
          <li
            v-for="(t, i) in suggestions"
            :key="t.name"
            role="option"
            :aria-selected="i === suggestIndex"
            class="flex cursor-pointer items-center gap-2 px-3 py-1"
            :class="i === suggestIndex ? 'bg-emerald-50 text-emerald-800 dark:bg-emerald-500/15 dark:text-emerald-200' : ''"
            @mousedown.prevent="pickSuggestion(t)"
          >
            <component :is="t.type === 'MODULE' ? Folder : FileText" class="size-3.5 shrink-0 opacity-60" />
            <span class="truncate">{{ t.name }}</span>
          </li>
        </ul>
      </div>
      <div
        v-show="view !== 'write'"
        ref="preview"
        class="note-content md-preview prose prose-sm max-w-none min-h-96 overflow-x-auto px-4 py-3 dark:prose-invert"
        :class="view === 'split' ? 'border-t border-slate-200 md:border-t-0 dark:border-slate-800' : ''"
        :title="view === 'preview' ? 'Double-click to edit' : undefined"
        @dblclick="editFromPreview"
        @click="onPreviewClick"
        @keydown="onPreviewClick"
      >
        <!-- eslint-disable-next-line vue/no-v-html -- sanitized by DOMPurify in renderMarkdown -->
        <div v-if="modelValue.trim()" v-html="rendered" />
        <p v-else class="muted italic">Nothing to preview yet.</p>
      </div>
    </div>
  </div>
</template>
