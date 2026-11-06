<script setup lang="ts">
import { computed, inject } from 'vue'
import {
  ArrowDown,
  ArrowUp,
  ChevronRight,
  Download,
  FilePlus,
  FileText,
  Folder,
  FolderOpen,
  FolderPlus,
  GripVertical,
  MoveRight,
  NotebookPen,
  Pencil,
  Trash2,
} from 'lucide-vue-next'
import type { TreeNode } from '../types/api'
import { TREE_CONTEXT, type DropZone } from '../lib/treeContext'
import { percentLabel } from '../lib/format'
import PriorityBadge from './PriorityBadge.vue'
import ActionMenu, { type MenuAction } from './ActionMenu.vue'

defineOptions({ name: 'TreeRow' })

const props = defineProps<{ node: TreeNode; depth: number; index: number; siblingCount: number }>()
const ctx = inject(TREE_CONTEXT)!

const hasChildren = computed(() => props.node.children.length > 0)
const filtering = computed(() => ctx.visible.value !== null)
// While filtering, branches containing matches are shown open regardless of saved state.
const open = computed(() => hasChildren.value && (filtering.value || ctx.isExpanded(props.node.id)))
const shownChildren = computed(() =>
  ctx.visible.value ? props.node.children.filter((c) => ctx.visible.value!.has(c.id)) : props.node.children,
)
const dimmed = computed(() => filtering.value && !ctx.matched.value!.has(props.node.id))
const isPending = computed(() => ctx.pending.value.has(props.node.id))
const dropZone = computed(() => (ctx.dropTarget.value?.id === props.node.id ? ctx.dropTarget.value.zone : null))

const actions = computed<MenuAction[]>(() => {
  const n = props.node
  const list: MenuAction[] = []
  if (n.type === 'MODULE') {
    list.push({ label: 'Add module', icon: FolderPlus, run: () => ctx.onAdd(n, 'MODULE') })
    list.push({ label: 'Add topic', icon: FilePlus, run: () => ctx.onAdd(n, 'TOPIC') })
  } else {
    list.push({ label: 'Add sub-topic', icon: FilePlus, run: () => ctx.onAdd(n, 'TOPIC') })
  }
  list.push(
    { label: n.hasNotes ? 'Open notes' : 'Add notes', icon: NotebookPen, divider: true, run: () => ctx.onNotes(n) },
    { label: 'Edit', icon: Pencil, run: () => ctx.onEdit(n) },
    { label: 'Move to…', icon: MoveRight, run: () => ctx.onMove(n) },
    { label: 'Move up', icon: ArrowUp, disabled: props.index === 0, run: () => ctx.onShift(n, -1) },
    { label: 'Move down', icon: ArrowDown, disabled: props.index === props.siblingCount - 1, run: () => ctx.onShift(n, 1) },
  )
  if (hasChildren.value) list.push({ label: 'Export subtree (CSV)', icon: Download, run: () => ctx.onExport(n) })
  list.push({ label: 'Delete', icon: Trash2, danger: true, divider: true, run: () => ctx.onDelete(n) })
  return list
})

// --- drag & drop (native HTML5): top quarter = before, bottom quarter = after, middle = inside ---

function onDragStart(e: DragEvent) {
  ctx.dragId.value = props.node.id
  e.dataTransfer?.setData('text/plain', String(props.node.id))
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}
function zoneFor(e: DragEvent): DropZone {
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
  const y = (e.clientY - rect.top) / rect.height
  return y < 0.25 ? 'before' : y > 0.75 ? 'after' : 'inside'
}
function onDragOver(e: DragEvent) {
  if (ctx.dragId.value === null || !ctx.canDrop(props.node)) return
  e.preventDefault()
  if (e.dataTransfer) e.dataTransfer.dropEffect = 'move'
  ctx.dropTarget.value = { id: props.node.id, zone: zoneFor(e) }
}
function onDragLeave() {
  if (ctx.dropTarget.value?.id === props.node.id) ctx.dropTarget.value = null
}
function onDrop(e: DragEvent) {
  e.preventDefault()
  const zone = zoneFor(e)
  ctx.dropTarget.value = null
  ctx.onDrop(props.node, zone)
}
function onDragEnd() {
  ctx.dragId.value = null
  ctx.dropTarget.value = null
}
</script>

<template>
  <li role="treeitem" :aria-expanded="hasChildren ? open : undefined" :aria-selected="false">
    <div
      class="group relative flex items-center gap-1.5 rounded-lg py-1 pr-1 hover:bg-slate-100 dark:hover:bg-slate-800/60"
      :class="[
        dimmed ? 'opacity-60' : '',
        ctx.dragId.value === node.id ? 'opacity-40' : '',
        dropZone === 'inside' ? 'bg-emerald-50 ring-2 ring-emerald-500 dark:bg-emerald-500/10' : '',
      ]"
      :style="{ paddingLeft: `${depth * 1.25 + 0.25}rem` }"
      draggable="true"
      @dragstart="onDragStart"
      @dragover="onDragOver"
      @dragleave="onDragLeave"
      @drop="onDrop"
      @dragend="onDragEnd"
    >
      <div v-if="dropZone === 'before'" class="absolute -top-px right-0 left-0 h-0.5 bg-emerald-500" />
      <div v-if="dropZone === 'after'" class="absolute right-0 -bottom-px left-0 h-0.5 bg-emerald-500" />

      <GripVertical class="size-3.5 shrink-0 cursor-grab text-slate-300 opacity-0 group-hover:opacity-100 dark:text-slate-600" />

      <button
        v-if="hasChildren"
        class="icon-btn size-6 shrink-0"
        :aria-label="open ? 'Collapse' : 'Expand'"
        :disabled="filtering"
        @click="ctx.toggle(node.id)"
      >
        <ChevronRight class="size-4 transition-transform" :class="open ? 'rotate-90' : ''" />
      </button>
      <span v-else class="size-6 shrink-0" />

      <input
        v-if="node.checkable"
        type="checkbox"
        class="size-4 shrink-0 cursor-pointer accent-emerald-600"
        :checked="node.completed"
        :disabled="isPending"
        :aria-label="`Mark '${node.name}' as ${node.completed ? 'not completed' : 'completed'}`"
        @change="ctx.onToggleComplete(node)"
      />
      <component
        :is="node.type === 'MODULE' ? (open ? FolderOpen : Folder) : FileText"
        v-else
        class="size-4 shrink-0"
        :class="node.type === 'MODULE' ? 'text-amber-500' : 'text-slate-400'"
      />

      <div class="min-w-0 flex-1 cursor-default" @dblclick="ctx.onEdit(node)">
        <div class="flex items-center gap-2">
          <span
            class="truncate text-sm"
            :class="[
              node.type === 'MODULE' ? 'font-semibold' : '',
              node.checkable && node.completed ? 'text-slate-400 line-through dark:text-slate-500' : '',
            ]"
            :title="node.name"
            >{{ node.name }}</span
          >
          <PriorityBadge :priority="node.priority" />
          <button
            class="inline-flex shrink-0 items-center rounded p-0.5 hover:bg-slate-200 dark:hover:bg-slate-700"
            :class="node.hasNotes ? 'text-emerald-600 dark:text-emerald-400' : 'text-slate-400 opacity-0 group-hover:opacity-100 focus-visible:opacity-100'"
            :title="node.hasNotes ? 'Open notes' : 'Add notes'"
            :aria-label="`${node.hasNotes ? 'Open' : 'Add'} notes for ${node.name}`"
            @click.stop="ctx.onNotes(node)"
          >
            <NotebookPen class="size-3.5" />
          </button>
        </div>
        <p v-if="node.description" class="muted truncate text-xs" :title="node.description">{{ node.description }}</p>
      </div>

      <div v-if="!node.checkable" class="hidden w-40 shrink-0 items-center gap-2 sm:flex">
        <template v-if="node.progress.percent !== null">
          <div class="h-1.5 flex-1 overflow-hidden rounded-full bg-slate-200 dark:bg-slate-800">
            <div class="h-full rounded-full bg-emerald-500 transition-[width]" :style="{ width: `${node.progress.percent}%` }" />
          </div>
          <span class="w-20 text-right text-xs tabular-nums">
            <span class="muted">{{ node.progress.completed }}/{{ node.progress.total }}</span>
            <span class="ml-1 font-medium">{{ percentLabel(node.progress) }}</span>
          </span>
        </template>
        <span v-else class="muted w-full text-right text-xs" title="No checkable topics inside yet">N/A</span>
      </div>
      <span v-if="!node.checkable" class="text-xs font-medium tabular-nums sm:hidden">{{ percentLabel(node.progress) }}</span>

      <ActionMenu class="opacity-60 group-hover:opacity-100 focus-within:opacity-100" :actions="actions" :label="`Actions for ${node.name}`" />
    </div>

    <ul v-if="open" role="group">
      <TreeRow
        v-for="child in shownChildren"
        :key="child.id"
        :node="child"
        :depth="depth + 1"
        :index="node.children.indexOf(child)"
        :sibling-count="node.children.length"
      />
    </ul>
  </li>
</template>
