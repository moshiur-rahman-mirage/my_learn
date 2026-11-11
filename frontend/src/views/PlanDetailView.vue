<script setup lang="ts">
import { computed, defineAsyncComponent, nextTick, onBeforeUnmount, onMounted, provide, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import {
  Archive,
  ArchiveRestore,
  ArrowLeft,
  ChevronsDownUp,
  ChevronsUpDown,
  Download,
  FilePlus,
  FolderPlus,
  Layers,
  ListChecks,
  Pencil,
  RefreshCw,
  Search,
  Trash2,
  Upload,
  X,
} from 'lucide-vue-next'
import { itemsApi, plansApi, tagsApi } from '../api'
import { ApiError, errorMessage } from '../api/client'
import type { ItemType, Plan, Priority, TreeNode } from '../types/api'
import {
  allIds,
  filterTree,
  findNode,
  isFilterActive,
  isSelfOrDescendant,
  locate,
  rollup,
  tagsIn,
  totalProgress,
  type TreeFilter,
} from '../lib/tree'
import { TREE_CONTEXT, type DropZone } from '../lib/treeContext'
import { useExpanded } from '../composables/useExpanded'
import { useToast } from '../composables/useToast'
import { useConfirm } from '../composables/useConfirm'
import { usePlanList } from '../composables/usePlanList'
import ProgressBar from '../components/ProgressBar.vue'
import TreeRow from '../components/TreeRow.vue'
import ActionMenu, { type MenuAction } from '../components/ActionMenu.vue'
import ItemFormDialog, { type ItemFormValue } from '../components/ItemFormDialog.vue'
import MoveDialog from '../components/MoveDialog.vue'
import PlanFormDialog from '../components/PlanFormDialog.vue'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'
import PaceBadge from '../components/PaceBadge.vue'
import { minutesLabel } from '../lib/format'

// the rich-text editor is large: load it the first time a note is opened
const NotesPanel = defineAsyncComponent(() => import('../components/NotesPanel.vue'))

const props = defineProps<{ id: string }>()
const planId = computed(() => Number(props.id))
const router = useRouter()
const route = useRoute()
const toast = useToast()
const { confirm } = useConfirm()
const planList = usePlanList()

const plan = ref<Plan | null>(null)
const tree = ref<TreeNode[]>([])
const loading = ref(true)
const loadError = ref<string | null>(null)
const busy = ref(false)
const pending = ref(new Set<number>())
// the item whose note is open (by id, so it follows tree reloads and closes if the item is deleted)
const notesItemId = ref<number | null>(null)
const notesNode = computed(() => (notesItemId.value === null ? null : (findNode(tree.value, notesItemId.value) ?? null)))

function onNoteSaved(itemId: number, hasNotes: boolean) {
  const node = findNode(tree.value, itemId)
  if (node) node.hasNotes = hasNotes
}

let expandedState = useExpanded(planId.value)
const expandedVersion = ref(0) // forces re-render when the expanded set changes

// --- loading ------------------------------------------------------------------------------

async function load(showSpinner = true) {
  if (showSpinner) loading.value = true
  loadError.value = null
  try {
    const [p, t] = await Promise.all([plansApi.get(planId.value), itemsApi.tree(planId.value)])
    plan.value = p
    tree.value = t
    if (expandedState.isFirstVisit() && showSpinner) {
      expandedState.set(t.map((n) => n.id)) // first visit: show the top level open
    }
  } catch (e) {
    loadError.value = errorMessage(e)
  } finally {
    loading.value = false
  }
  if (route.query.item && !loadError.value) {
    await nextTick()
    await revealFromQuery()
  }
}

/** Reload after a structural change, keeping the expanded state; refresh sidebar figures too. */
async function reload() {
  await load(false)
  void planList.refresh()
}

watch(
  planId,
  () => {
    expandedState = useExpanded(planId.value)
    expandedVersion.value++
    void load()
  },
  { immediate: true },
)

const progress = computed(() => totalProgress(tree.value))
const totalMinutes = computed(() => tree.value.reduce((sum, n) => sum + n.totalMinutes, 0))

/** Links, tags or time of an item changed in the side panel. */
async function onDetailsChanged() {
  await load(false)
  void loadTags()
}

// --- filtering ----------------------------------------------------------------------------

const filter = ref<TreeFilter>({ text: '', priorities: [], status: 'all', types: [], tags: [] })
const planTags = computed(() => tagsIn(tree.value))
const allTags = ref<string[]>([])
async function loadTags() {
  allTags.value = (await tagsApi.all().catch(() => [])).map((t) => t.name)
}
onMounted(loadTags)
const filtered = computed(() => (isFilterActive(filter.value) ? filterTree(tree.value, filter.value) : null))
const visible = computed(() => filtered.value?.visible ?? null)
const matched = computed(() => filtered.value?.matched ?? null)
const shownRoots = computed(() => (visible.value ? tree.value.filter((n) => visible.value!.has(n.id)) : tree.value))

function togglePriority(p: Priority) {
  const list = filter.value.priorities
  filter.value.priorities = list.includes(p) ? list.filter((x) => x !== p) : [...list, p]
}
function toggleType(t: ItemType) {
  const list = filter.value.types
  filter.value.types = list.includes(t) ? list.filter((x) => x !== t) : [...list, t]
}
function toggleTag(t: string) {
  const list = filter.value.tags
  filter.value.tags = list.includes(t) ? list.filter((x) => x !== t) : [...list, t]
}
function clearFilter() {
  filter.value = { text: '', priorities: [], status: 'all', types: [], tags: [] }
}

// --- jump to an item (from search or Today: /plans/1?item=42) -------------------------------

const highlightId = ref<number | null>(null)

async function revealFromQuery() {
  const id = Number(route.query.item)
  if (!id || !findNode(tree.value, id)) return
  // open every ancestor
  let parentId = findNode(tree.value, id)!.parentId
  while (parentId !== null) {
    expandedState.expand(parentId)
    parentId = findNode(tree.value, parentId)?.parentId ?? null
  }
  expandedVersion.value++
  highlightId.value = id
  if (route.query.notes) notesItemId.value = id
  void router.replace({ query: {} })
  await nextTick()
  const row = document.querySelector<HTMLElement>(`[data-tree-row="${id}"]`)
  row?.scrollIntoView({ block: 'center', behavior: 'smooth' })
  row?.focus({ preventScroll: true })
  setTimeout(() => {
    if (highlightId.value === id) highlightId.value = null
  }, 2500)
}

// already on this plan (e.g. a second search result in the same plan)
watch(
  () => route.query.item,
  (item) => {
    if (item && !loading.value) void revealFromQuery()
  },
)

// --- page shortcuts: n = new topic, m = new module ------------------------------------------

function onPageKey(e: KeyboardEvent) {
  const t = e.target as HTMLElement | null
  if (e.ctrlKey || e.metaKey || e.altKey || itemDialog.value.open || moveDialog.value.open || planDialog.value) return
  if (t && (t.closest('input, textarea, select, [contenteditable="true"], [role="dialog"]') || t.dataset.treeRow)) return
  if (e.key === 'n') openAdd(null, 'TOPIC')
  else if (e.key === 'm') openAdd(null, 'MODULE')
  else return
  e.preventDefault()
}
onMounted(() => document.addEventListener('keydown', onPageKey))
onBeforeUnmount(() => document.removeEventListener('keydown', onPageKey))

// --- completion (optimistic) ----------------------------------------------------------------

async function toggleComplete(node: TreeNode) {
  const previous = node.completed
  node.completed = !previous
  rollup(tree.value) // instant feedback for every ancestor
  pending.value = new Set(pending.value).add(node.id)
  try {
    const saved = await itemsApi.setCompleted(node.id, node.completed)
    node.completedAt = saved.completedAt
    void planList.refresh()
  } catch (e) {
    node.completed = previous // restore on failure
    rollup(tree.value)
    toast.error(`Couldn't save: ${errorMessage(e)}`)
  } finally {
    const next = new Set(pending.value)
    next.delete(node.id)
    pending.value = next
  }
}

// --- create / edit ------------------------------------------------------------------------

const itemDialog = ref<{ open: boolean; item: TreeNode | null; defaults: Partial<ItemFormValue> }>({
  open: false,
  item: null,
  defaults: {},
})

function openAdd(parent: TreeNode | null, type: ItemType) {
  itemDialog.value = { open: true, item: null, defaults: { parentId: parent?.id ?? null, type } }
}
function openEdit(node: TreeNode) {
  itemDialog.value = { open: true, item: node, defaults: {} }
}

async function submitItem(value: ItemFormValue) {
  busy.value = true
  const editing = itemDialog.value.item
  try {
    const input = { name: value.name, type: value.type, description: value.description, priority: value.priority }
    if (editing) {
      await itemsApi.update(editing.id, { ...input, version: editing.version })
      if (value.parentId !== editing.parentId) await itemsApi.move(editing.id, value.parentId)
      toast.success('Saved')
    } else {
      await itemsApi.create(planId.value, { ...input, parentId: value.parentId })
      if (value.parentId !== null) expandedState.expand(value.parentId)
      toast.success(`${value.type === 'MODULE' ? 'Module' : 'Topic'} added`)
    }
    itemDialog.value.open = false
    await reload()
  } catch (e) {
    if (e instanceof ApiError && e.status === 409 && editing) {
      toast.error(`${e.message}`)
      await reload()
      itemDialog.value.open = false
    } else {
      toast.error(errorMessage(e))
    }
  } finally {
    busy.value = false
  }
}

// --- move / reorder / delete --------------------------------------------------------------

const moveDialog = ref<{ open: boolean; item: TreeNode | null }>({ open: false, item: null })

async function submitMove(parentId: number | null) {
  const item = moveDialog.value.item
  if (!item) return
  await structural(async () => {
    await itemsApi.move(item.id, parentId)
    if (parentId !== null) expandedState.expand(parentId)
    moveDialog.value.open = false
  }, 'Moved')
}

async function shift(node: TreeNode, delta: -1 | 1) {
  const loc = locate(tree.value, node.id)
  if (!loc) return
  await structural(() => itemsApi.reorder(node.id, loc.index + delta))
}

async function deleteItem(node: TreeNode) {
  const message =
    node.descendantCount > 0
      ? `“${node.name}” and all ${node.descendantCount} item(s) inside it will be permanently deleted, including their completion status.`
      : `“${node.name}” will be permanently deleted.`
  if (!(await confirm({ title: 'Delete item?', message, confirmLabel: 'Delete', danger: true }))) return
  await structural(() => itemsApi.remove(node.id), 'Deleted')
}

async function structural(action: () => Promise<unknown>, success?: string) {
  busy.value = true
  try {
    await action()
    if (success) toast.success(success)
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    busy.value = false
    await reload()
  }
}

// --- drag & drop --------------------------------------------------------------------------

const dragId = ref<number | null>(null)
const dropTarget = ref<{ id: number; zone: DropZone } | null>(null)

function canDrop(target: TreeNode): boolean {
  if (dragId.value === null) return false
  const dragged = findNode(tree.value, dragId.value)
  return !!dragged && !isSelfOrDescendant(dragged, target.id)
}

async function onDrop(target: TreeNode, zone: DropZone) {
  const id = dragId.value
  dragId.value = null
  if (id === null || id === target.id) return
  const dragged = findNode(tree.value, id)
  if (!dragged || isSelfOrDescendant(dragged, target.id)) return

  let parentId: number | null
  let position: number | null
  if (zone === 'inside') {
    parentId = target.id
    position = null // append
  } else {
    parentId = target.parentId
    const siblings = locate(tree.value, target.id)!.list
    position = siblings.indexOf(target) + (zone === 'after' ? 1 : 0)
    const from = siblings.indexOf(dragged) // the server removes the item first, then inserts
    if (from >= 0 && from < position) position--
    if (from === position) return // dropped where it already is
  }
  await structural(async () => {
    await itemsApi.move(id, parentId, position)
    if (zone === 'inside') expandedState.expand(target.id)
  })
}

// --- plan-level actions -------------------------------------------------------------------

const planDialog = ref(false)

async function savePlan(name: string, description: string | null, targetDate: string | null) {
  if (!plan.value) return
  busy.value = true
  try {
    plan.value = await plansApi.update({ ...plan.value, name, description, targetDate })
    planDialog.value = false
    toast.success('Plan saved')
    void planList.refresh()
  } catch (e) {
    toast.error(errorMessage(e))
    if (e instanceof ApiError && e.status === 409) await load(false)
  } finally {
    busy.value = false
  }
}

async function toggleArchive() {
  if (!plan.value) return
  const status = plan.value.status === 'ACTIVE' ? 'ARCHIVED' : 'ACTIVE'
  try {
    plan.value = await plansApi.update({ ...plan.value, status })
    toast.success(status === 'ARCHIVED' ? 'Plan archived' : 'Plan activated')
    void planList.refresh()
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

async function deletePlan() {
  if (!plan.value) return
  const ok = await confirm({
    title: 'Delete plan?',
    message: `This permanently deletes “${plan.value.name}” and all ${allIds(tree.value).length} of its items and progress.`,
    confirmLabel: 'Delete plan',
    danger: true,
    requireText: plan.value.name,
  })
  if (!ok) return
  try {
    await plansApi.remove(plan.value.id)
    toast.success('Plan deleted')
    void planList.refresh()
    await router.push('/plans')
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

function download(url: string) {
  window.location.href = url
}

const planActions = computed<MenuAction[]>(() => [
  { label: 'Edit plan', icon: Pencil, run: () => (planDialog.value = true) },
  { label: 'Import into this plan', icon: Upload, run: () => router.push({ path: '/import', query: { plan: planId.value } }) },
  { label: 'Export CSV', icon: Download, run: () => download(plansApi.exportUrl(planId.value)) },
  { label: 'Full backup (JSON)', icon: Download, run: () => download(plansApi.backupUrl(planId.value)) },
  { label: 'Study flashcards', icon: Layers, run: () => router.push({ name: 'flashcards', query: { plan: planId.value } }) },
  { label: 'Sync from edited file…', icon: RefreshCw, run: () => router.push(`/plans/${planId.value}/sync`) },
  {
    label: plan.value?.status === 'ACTIVE' ? 'Archive' : 'Activate',
    icon: plan.value?.status === 'ACTIVE' ? Archive : ArchiveRestore,
    divider: true,
    run: toggleArchive,
  },
  { label: 'Delete plan', icon: Trash2, danger: true, run: deletePlan },
])

function expandAll() {
  expandedState.set(allIds(tree.value))
  expandedVersion.value++
}
function collapseAll() {
  expandedState.set([])
  expandedVersion.value++
}

provide(TREE_CONTEXT, {
  isExpanded: (id) => {
    void expandedVersion.value // re-evaluate when the plan (and its saved state) changes
    return expandedState.expanded.value.has(id)
  },
  toggle: (id) => {
    expandedState.toggle(id)
    expandedVersion.value++
  },
  visible,
  matched,
  pending,
  highlightId,
  onToggleComplete: toggleComplete,
  onAdd: (parent, type) => openAdd(parent, type),
  onEdit: openEdit,
  onNotes: (node) => (notesItemId.value = node.id),
  onMove: (node) => (moveDialog.value = { open: true, item: node }),
  onShift: shift,
  onDelete: deleteItem,
  onExport: (node) => download(itemsApi.exportUrl(node.id)),
  dragId,
  dropTarget,
  canDrop,
  onDrop,
})
</script>

<template>
  <LoadingState v-if="loading" />

  <EmptyState v-else-if="loadError || !plan" :icon="ListChecks" title="Couldn't load this plan" :text="loadError ?? ''">
    <RouterLink to="/plans" class="btn-secondary">Back to plans</RouterLink>
    <button class="btn-primary" @click="load()">Retry</button>
  </EmptyState>

  <div v-else class="space-y-4">
    <!-- header -->
    <div>
      <RouterLink to="/plans" class="muted mb-2 inline-flex items-center gap-1 text-sm hover:underline">
        <ArrowLeft class="size-3.5" /> Plans
      </RouterLink>
      <div class="flex items-start justify-between gap-3">
        <div class="min-w-0">
          <h1 class="flex flex-wrap items-center gap-2 text-2xl font-semibold">
            <span class="break-words">{{ plan.name }}</span>
            <span
              v-if="plan.status === 'ARCHIVED'"
              class="rounded-md bg-slate-200 px-2 py-0.5 text-xs font-medium text-slate-600 dark:bg-slate-800 dark:text-slate-300"
              >Archived</span
            >
          </h1>
          <p v-if="plan.description" class="muted mt-1 text-sm whitespace-pre-line">{{ plan.description }}</p>
        </div>
        <ActionMenu :actions="planActions" label="Plan actions" />
      </div>
      <div class="card mt-4 p-4">
        <ProgressBar :progress="progress" size="lg" />
        <p class="muted mt-2 text-sm">
          <template v-if="progress.total">
            <strong class="text-slate-900 dark:text-slate-100">{{ progress.completed }}</strong> of {{ progress.total }} topics completed ·
            {{ progress.total - progress.completed }} remaining
          </template>
          <template v-else>No checkable topics yet.</template>
          <template v-if="totalMinutes"> · {{ minutesLabel(totalMinutes) }} spent</template>
        </p>
        <PaceBadge v-if="plan.pace" class="mt-2" :pace="plan.pace" detailed />
        <button v-else class="muted mt-1 text-xs underline" @click="planDialog = true">Set a target date</button>
      </div>
    </div>

    <!-- toolbar -->
    <div class="card space-y-3 p-3">
      <div class="flex flex-wrap items-center gap-2">
        <div class="relative min-w-48 flex-1">
          <Search class="absolute top-1/2 left-2.5 size-4 -translate-y-1/2 text-slate-400" />
          <input v-model="filter.text" class="input pl-8" placeholder="Filter this plan…  (press /)" aria-label="Filter this plan" data-shortcut-search />
        </div>
        <button class="btn-primary" @click="openAdd(null, 'MODULE')"><FolderPlus class="size-4" /> Module</button>
        <button class="btn-secondary" @click="openAdd(null, 'TOPIC')"><FilePlus class="size-4" /> Topic</button>
      </div>
      <div class="flex flex-wrap items-center gap-1.5 text-xs">
        <button
          v-for="p in (['MUST', 'SHOULD', 'NICE'] as const)"
          :key="p"
          class="chip"
          :class="{ 'chip-on': filter.priorities.includes(p) }"
          :aria-pressed="filter.priorities.includes(p)"
          @click="togglePriority(p)"
        >
          {{ p === 'MUST' ? 'Must' : p === 'SHOULD' ? 'Should' : 'Nice' }}
        </button>
        <span class="mx-1 h-4 w-px bg-slate-200 dark:bg-slate-700" />
        <button
          v-for="s in (['completed', 'pending'] as const)"
          :key="s"
          class="chip capitalize"
          :class="{ 'chip-on': filter.status === s }"
          :aria-pressed="filter.status === s"
          @click="filter.status = filter.status === s ? 'all' : s"
        >
          {{ s }}
        </button>
        <span class="mx-1 h-4 w-px bg-slate-200 dark:bg-slate-700" />
        <button
          v-for="t in (['MODULE', 'TOPIC'] as const)"
          :key="t"
          class="chip"
          :class="{ 'chip-on': filter.types.includes(t) }"
          :aria-pressed="filter.types.includes(t)"
          @click="toggleType(t)"
        >
          {{ t === 'MODULE' ? 'Modules' : 'Topics' }}
        </button>
        <template v-if="planTags.length">
          <span class="mx-1 h-4 w-px bg-slate-200 dark:bg-slate-700" />
          <button
            v-for="t in planTags"
            :key="t"
            class="chip"
            :class="{ 'chip-on': filter.tags.includes(t) }"
            :aria-pressed="filter.tags.includes(t)"
            @click="toggleTag(t)"
          >
            #{{ t }}
          </button>
        </template>
        <button v-if="filtered" class="chip" @click="clearFilter"><X class="size-3" /> Clear · {{ filtered.matched.size }} match(es)</button>
        <span class="flex-1" />
        <button class="btn-ghost px-2 py-1 text-xs" :disabled="!!filtered" @click="expandAll"><ChevronsUpDown class="size-3.5" /> Expand all</button>
        <button class="btn-ghost px-2 py-1 text-xs" :disabled="!!filtered" @click="collapseAll"><ChevronsDownUp class="size-3.5" /> Collapse all</button>
      </div>
    </div>

    <!-- tree -->
    <EmptyState
      v-if="tree.length === 0"
      :icon="ListChecks"
      title="This plan is empty"
      text="Add modules and topics, or import your existing spreadsheet."
    >
      <button class="btn-primary" @click="openAdd(null, 'MODULE')"><FolderPlus class="size-4" /> Add module</button>
      <button class="btn-secondary" @click="openAdd(null, 'TOPIC')"><FilePlus class="size-4" /> Add topic</button>
      <RouterLink :to="{ path: '/import', query: { plan: planId } }" class="btn-secondary"><Upload class="size-4" /> Import</RouterLink>
    </EmptyState>

    <div v-else-if="shownRoots.length === 0" class="card muted p-8 text-center text-sm">Nothing matches the current filters.</div>

    <div v-else class="card p-2" :class="busy ? 'pointer-events-none opacity-70' : ''">
      <ul role="tree" :aria-label="plan.name">
        <TreeRow
          v-for="node in shownRoots"
          :key="node.id"
          :node="node"
          :depth="0"
          :index="tree.indexOf(node)"
          :sibling-count="tree.length"
        />
      </ul>
      <p class="muted px-2 pt-2 text-xs">Tip: drag rows to reorder; drop onto the middle of a row to move inside it. Double-click to edit. Keyboard: ↑↓ move, ←→ collapse/expand, Space ticks, Enter opens details, e edits, n adds inside. Press ? for all shortcuts.</p>
    </div>

    <ItemFormDialog
      :open="itemDialog.open"
      :tree="tree"
      :item="itemDialog.item"
      :defaults="itemDialog.defaults"
      :busy="busy"
      @close="itemDialog.open = false"
      @submit="submitItem"
    />
    <MoveDialog :open="moveDialog.open" :tree="tree" :item="moveDialog.item" :busy="busy" @close="moveDialog.open = false" @submit="submitMove" />
    <PlanFormDialog :open="planDialog" :plan="plan" :busy="busy" @close="planDialog = false" @submit="savePlan" />
    <NotesPanel
      v-if="notesItemId !== null"
      :node="notesNode"
      :plan-id="planId"
      :all-tags="allTags"
      :tree="tree"
      @open="(n) => (notesItemId = n.id)"
      @close="notesItemId = null"
      @saved="onNoteSaved"
      @changed="onDetailsChanged"
    />
  </div>
</template>
