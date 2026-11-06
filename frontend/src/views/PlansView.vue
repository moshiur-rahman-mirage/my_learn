<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { Archive, ArchiveRestore, Download, ExternalLink, ListChecks, Pencil, Plus, Search, Trash2, Upload } from 'lucide-vue-next'
import { plansApi } from '../api'
import { errorMessage } from '../api/client'
import type { Plan, PlanStatus } from '../types/api'
import { relativeTime } from '../lib/format'
import { useToast } from '../composables/useToast'
import { useConfirm } from '../composables/useConfirm'
import { usePlanList } from '../composables/usePlanList'
import ProgressBar from '../components/ProgressBar.vue'
import ActionMenu, { type MenuAction } from '../components/ActionMenu.vue'
import PlanFormDialog from '../components/PlanFormDialog.vue'
import EmptyState from '../components/EmptyState.vue'
import LoadingState from '../components/LoadingState.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const { confirm } = useConfirm()
const planList = usePlanList()

const plans = ref<Plan[] | null>(null)
const error = ref<string | null>(null)
const search = ref('')
const status = ref<PlanStatus | null>(null)
const dialog = ref<{ open: boolean; plan: Plan | null }>({ open: false, plan: null })
const busy = ref(false)

async function load() {
  error.value = null
  try {
    plans.value = await plansApi.list(search.value.trim(), status.value)
  } catch (e) {
    error.value = errorMessage(e)
  }
}

let timer: ReturnType<typeof setTimeout> | undefined
watch(search, () => {
  clearTimeout(timer)
  timer = setTimeout(load, 250)
})
watch(status, load)
onMounted(() => {
  void load()
  if (route.query.new) {
    dialog.value = { open: true, plan: null }
    void router.replace('/plans')
  }
})

async function save(name: string, description: string | null) {
  busy.value = true
  try {
    const editing = dialog.value.plan
    if (editing) {
      await plansApi.update({ ...editing, name, description })
      toast.success('Plan saved')
    } else {
      const created = await plansApi.create(name, description)
      toast.success('Plan created')
      dialog.value.open = false
      void planList.refresh()
      await router.push(`/plans/${created.id}`)
      return
    }
    dialog.value.open = false
    await load()
    void planList.refresh()
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    busy.value = false
  }
}

async function toggleArchive(p: Plan) {
  try {
    await plansApi.update({ ...p, status: p.status === 'ACTIVE' ? 'ARCHIVED' : 'ACTIVE' })
    toast.success(p.status === 'ACTIVE' ? 'Plan archived' : 'Plan activated')
    await load()
    void planList.refresh()
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

async function remove(p: Plan) {
  const ok = await confirm({
    title: 'Delete plan?',
    message: `This permanently deletes “${p.name}” with all its items and progress.`,
    confirmLabel: 'Delete plan',
    danger: true,
    requireText: p.name,
  })
  if (!ok) return
  try {
    await plansApi.remove(p.id)
    toast.success('Plan deleted')
    await load()
    void planList.refresh()
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

function actionsFor(p: Plan): MenuAction[] {
  return [
    { label: 'Open', icon: ExternalLink, run: () => router.push(`/plans/${p.id}`) },
    { label: 'Edit', icon: Pencil, run: () => (dialog.value = { open: true, plan: p }) },
    { label: 'Export CSV', icon: Download, run: () => (window.location.href = plansApi.exportUrl(p.id)) },
    {
      label: p.status === 'ACTIVE' ? 'Archive' : 'Activate',
      icon: p.status === 'ACTIVE' ? Archive : ArchiveRestore,
      divider: true,
      run: () => toggleArchive(p),
    },
    { label: 'Delete', icon: Trash2, danger: true, run: () => remove(p) },
  ]
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <h1 class="text-2xl font-semibold">Learning plans</h1>
      <div class="flex gap-2">
        <RouterLink to="/import" class="btn-secondary"><Upload class="size-4" /> Import</RouterLink>
        <button class="btn-primary" @click="dialog = { open: true, plan: null }"><Plus class="size-4" /> New plan</button>
      </div>
    </div>

    <div class="flex flex-wrap items-center gap-2">
      <div class="relative min-w-48 flex-1">
        <Search class="absolute top-1/2 left-2.5 size-4 -translate-y-1/2 text-slate-400" />
        <input v-model="search" class="input pl-8" placeholder="Search plans…" aria-label="Search plans" />
      </div>
      <div class="flex gap-1" role="group" aria-label="Status filter">
        <button
          v-for="s in ([null, 'ACTIVE', 'ARCHIVED'] as const)"
          :key="String(s)"
          class="chip"
          :class="{ 'chip-on': status === s }"
          :aria-pressed="status === s"
          @click="status = s"
        >
          {{ s === null ? 'All' : s === 'ACTIVE' ? 'Active' : 'Archived' }}
        </button>
      </div>
    </div>

    <EmptyState v-if="error" :icon="ListChecks" title="Couldn't load plans" :text="error">
      <button class="btn-primary" @click="load">Retry</button>
    </EmptyState>
    <LoadingState v-else-if="!plans" />
    <EmptyState
      v-else-if="plans.length === 0"
      :icon="ListChecks"
      :title="search || status ? 'No plans match' : 'No learning plans yet'"
      :text="search || status ? 'Try a different search or filter.' : 'Create your first plan or import a spreadsheet.'"
    >
      <button v-if="!search && !status" class="btn-primary" @click="dialog = { open: true, plan: null }"><Plus class="size-4" /> New plan</button>
    </EmptyState>

    <ul v-else class="grid gap-3 md:grid-cols-2">
      <li v-for="p in plans" :key="p.id" class="card flex flex-col p-4">
        <div class="flex items-start justify-between gap-2">
          <RouterLink :to="`/plans/${p.id}`" class="min-w-0 font-semibold hover:underline">
            <span class="break-words">{{ p.name }}</span>
          </RouterLink>
          <div class="flex shrink-0 items-center gap-1">
            <span
              v-if="p.status === 'ARCHIVED'"
              class="rounded-md bg-slate-200 px-1.5 py-0.5 text-[11px] font-medium text-slate-600 dark:bg-slate-800 dark:text-slate-300"
              >Archived</span
            >
            <ActionMenu :actions="actionsFor(p)" :label="`Actions for ${p.name}`" />
          </div>
        </div>
        <p v-if="p.description" class="muted mt-1 line-clamp-2 text-sm">{{ p.description }}</p>
        <div class="mt-auto pt-4">
          <ProgressBar :progress="p.progress" />
          <p class="muted mt-1.5 flex justify-between text-xs">
            <span v-if="p.progress.total">{{ p.progress.completed }} done · {{ p.progress.total - p.progress.completed }} remaining</span>
            <span v-else>No topics yet</span>
            <span>Updated {{ relativeTime(p.updatedAt) }}</span>
          </p>
        </div>
      </li>
    </ul>

    <PlanFormDialog :open="dialog.open" :plan="dialog.plan" :busy="busy" @close="dialog.open = false" @submit="save" />
  </div>
</template>
