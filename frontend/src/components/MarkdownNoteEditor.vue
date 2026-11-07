<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { Columns2, Eye, Pencil } from 'lucide-vue-next'
import { renderMarkdown } from '../lib/markdown'

/**
 * Plain-text Markdown editor with a rendered view. The note is stored as the Markdown source;
 * "Preview" shows it formatted, "Split" shows both side by side.
 */
export type MarkdownView = 'write' | 'split' | 'preview'

const props = defineProps<{ modelValue: string; placeholder?: string }>()
const emit = defineEmits<{ 'update:modelValue': [md: string] }>()
const view = defineModel<MarkdownView>('view', { default: 'split' })

const textarea = ref<HTMLTextAreaElement | null>(null)
const rendered = computed(() => renderMarkdown(props.modelValue))

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
      <span class="muted ml-auto pr-1 text-xs">Ctrl+B bold · Ctrl+I italic · Ctrl+E code · Tab indent</span>
    </div>

    <div class="grid" :class="view === 'split' ? 'md:grid-cols-2 md:divide-x divide-slate-200 dark:divide-slate-800' : ''">
      <textarea
        v-show="view !== 'preview'"
        ref="textarea"
        :value="modelValue"
        class="min-h-96 w-full resize-y bg-transparent px-4 py-3 font-mono text-sm leading-relaxed focus:outline-none"
        :placeholder="placeholder ?? 'Write in Markdown…'"
        spellcheck="true"
        aria-label="Note (Markdown)"
        @input="emit('update:modelValue', ($event.target as HTMLTextAreaElement).value)"
        @keydown="onKeydown"
      />
      <div
        v-show="view !== 'write'"
        class="note-content md-preview prose prose-sm max-w-none min-h-96 overflow-x-auto px-4 py-3 dark:prose-invert"
        :class="view === 'split' ? 'border-t border-slate-200 md:border-t-0 dark:border-slate-800' : ''"
        :title="view === 'preview' ? 'Double-click to edit' : undefined"
        @dblclick="editFromPreview"
      >
        <!-- eslint-disable-next-line vue/no-v-html -- sanitized by DOMPurify in renderMarkdown -->
        <div v-if="modelValue.trim()" v-html="rendered" />
        <p v-else class="muted italic">Nothing to preview yet.</p>
      </div>
    </div>
  </div>
</template>
