<script setup lang="ts">
import { onBeforeUnmount, watch } from 'vue'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { Placeholder } from '@tiptap/extensions'
import { TaskList } from '@tiptap/extension-task-list'
import { TaskItem } from '@tiptap/extension-task-item'
import Highlight from '@tiptap/extension-highlight'
import {
  Bold,
  Code,
  Heading2,
  Heading3,
  Highlighter,
  Italic,
  Link,
  List,
  ListOrdered,
  ListTodo,
  Minus,
  Quote,
  Redo2,
  SquareCode,
  Strikethrough,
  Underline,
  Undo2,
} from 'lucide-vue-next'

/**
 * Rich-text (WYSIWYG) editor for notes. Emits HTML. Only the formats in the toolbar exist in the
 * editor's schema, so loading stored HTML also drops anything else (scripts, styles, images).
 */
const props = defineProps<{ modelValue: string; placeholder?: string }>()
const emit = defineEmits<{ 'update:modelValue': [html: string] }>()

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit.configure({
      heading: { levels: [2, 3] },
      link: { openOnClick: false, autolink: true, defaultProtocol: 'https' },
    }),
    Highlight,
    TaskList,
    TaskItem.configure({ nested: true }),
    Placeholder.configure({ placeholder: props.placeholder ?? 'Write what you learned…' }),
  ],
  editorProps: {
    attributes: {
      class: 'note-content prose prose-sm max-w-none dark:prose-invert min-h-64 px-4 py-3 focus:outline-none',
      'aria-label': 'Note',
    },
  },
  onUpdate: ({ editor }) => emit('update:modelValue', editor.isEmpty ? '' : editor.getHTML()),
})

// a different item's note was loaded
watch(
  () => props.modelValue,
  (html) => {
    const e = editor.value
    if (!e) return
    const current = e.isEmpty ? '' : e.getHTML()
    if (html !== current) e.commands.setContent(html || '', { emitUpdate: false })
  },
)

onBeforeUnmount(() => editor.value?.destroy())

function setLink() {
  const e = editor.value
  if (!e) return
  const previous = e.getAttributes('link').href as string | undefined
  const url = window.prompt('Link address (leave empty to remove the link)', previous ?? 'https://')
  if (url === null) return
  if (url.trim() === '' || url.trim() === 'https://') {
    e.chain().focus().extendMarkRange('link').unsetLink().run()
  } else {
    e.chain().focus().extendMarkRange('link').setLink({ href: url.trim() }).run()
  }
}

function focus() {
  editor.value?.commands.focus('end')
}
defineExpose({ focus })

type Tool = { label: string; icon: unknown; active?: () => boolean; run: () => void; disabled?: () => boolean } | 'sep'
const tools: Tool[] = [
  { label: 'Bold (Ctrl+B)', icon: Bold, active: () => !!editor.value?.isActive('bold'), run: () => editor.value?.chain().focus().toggleBold().run() },
  { label: 'Italic (Ctrl+I)', icon: Italic, active: () => !!editor.value?.isActive('italic'), run: () => editor.value?.chain().focus().toggleItalic().run() },
  { label: 'Underline (Ctrl+U)', icon: Underline, active: () => !!editor.value?.isActive('underline'), run: () => editor.value?.chain().focus().toggleUnderline().run() },
  { label: 'Strikethrough', icon: Strikethrough, active: () => !!editor.value?.isActive('strike'), run: () => editor.value?.chain().focus().toggleStrike().run() },
  { label: 'Highlight', icon: Highlighter, active: () => !!editor.value?.isActive('highlight'), run: () => editor.value?.chain().focus().toggleHighlight().run() },
  { label: 'Inline code', icon: Code, active: () => !!editor.value?.isActive('code'), run: () => editor.value?.chain().focus().toggleCode().run() },
  'sep',
  { label: 'Heading', icon: Heading2, active: () => !!editor.value?.isActive('heading', { level: 2 }), run: () => editor.value?.chain().focus().toggleHeading({ level: 2 }).run() },
  { label: 'Sub-heading', icon: Heading3, active: () => !!editor.value?.isActive('heading', { level: 3 }), run: () => editor.value?.chain().focus().toggleHeading({ level: 3 }).run() },
  'sep',
  { label: 'Bullet list', icon: List, active: () => !!editor.value?.isActive('bulletList'), run: () => editor.value?.chain().focus().toggleBulletList().run() },
  { label: 'Numbered list', icon: ListOrdered, active: () => !!editor.value?.isActive('orderedList'), run: () => editor.value?.chain().focus().toggleOrderedList().run() },
  { label: 'Checklist', icon: ListTodo, active: () => !!editor.value?.isActive('taskList'), run: () => editor.value?.chain().focus().toggleTaskList().run() },
  'sep',
  { label: 'Quote', icon: Quote, active: () => !!editor.value?.isActive('blockquote'), run: () => editor.value?.chain().focus().toggleBlockquote().run() },
  { label: 'Code block', icon: SquareCode, active: () => !!editor.value?.isActive('codeBlock'), run: () => editor.value?.chain().focus().toggleCodeBlock().run() },
  { label: 'Divider', icon: Minus, run: () => editor.value?.chain().focus().setHorizontalRule().run() },
  { label: 'Link', icon: Link, active: () => !!editor.value?.isActive('link'), run: setLink },
  'sep',
  { label: 'Undo (Ctrl+Z)', icon: Undo2, run: () => editor.value?.chain().focus().undo().run(), disabled: () => !editor.value?.can().undo() },
  { label: 'Redo (Ctrl+Y)', icon: Redo2, run: () => editor.value?.chain().focus().redo().run(), disabled: () => !editor.value?.can().redo() },
]
</script>

<template>
  <div class="overflow-hidden rounded-lg border border-slate-300 bg-white dark:border-slate-700 dark:bg-slate-900">
    <div
      class="sticky top-0 z-10 flex flex-wrap items-center gap-0.5 border-b border-slate-200 bg-slate-50 p-1 dark:border-slate-800 dark:bg-slate-900"
      role="toolbar"
      aria-label="Formatting"
    >
      <template v-for="(t, i) in tools" :key="i">
        <span v-if="t === 'sep'" class="mx-1 h-5 w-px bg-slate-300 dark:bg-slate-700" />
        <button
          v-else
          type="button"
          class="icon-btn"
          :class="t.active?.() ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-300' : ''"
          :title="t.label"
          :aria-label="t.label"
          :aria-pressed="t.active ? t.active() : undefined"
          :disabled="t.disabled?.()"
          @mousedown.prevent
          @click="t.run()"
        >
          <component :is="t.icon" class="size-4" />
        </button>
      </template>
    </div>
    <EditorContent :editor="editor" />
  </div>
</template>
