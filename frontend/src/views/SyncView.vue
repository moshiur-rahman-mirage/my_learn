<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, CircleAlert, Download, FileUp, LoaderCircle, RefreshCw, TriangleAlert } from 'lucide-vue-next'
import { plansApi, syncApi } from '../api'
import { errorMessage } from '../api/client'
import type { Plan, SyncChange, SyncChangeKind, SyncPreview } from '../types/api'
import { useToast } from '../composables/useToast'
import { usePlanList } from '../composables/usePlanList'

/**
 * Sync an edited export back into its plan: upload, review every change, apply the chosen ones.
 * Deletions and conflicts (items edited in the app since the export) start unticked.
 */
const props = defineProps<{ id: string }>()
const planId = computed(() => Number(props.id))
const router = useRouter()
const toast = useToast()
const planList = usePlanList()

const plan = ref<Plan | null>(null)
const file = ref<File | null>(null)
const preview = ref<SyncPreview | null>(null)
const selected = ref(new Set<string>())
const loading = ref(false)
const applying = ref(false)
const dragging = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

onMounted(async () => {
  plan.value = await plansApi.get(planId.value).catch(() => null)
})

async function choose(f: File | undefined | null) {
  if (!f) return
  file.value = f
  loading.value = true
  preview.value = null
  try {
    const p = await syncApi.preview(planId.value, f)
    preview.value = p
    selected.value = new Set(p.changes.filter((c) => c.defaultSelected).map((c) => c.key))
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    loading.value = false
  }
}
function onPick(e: Event) {
  const input = e.target as HTMLInputElement
  void choose(input.files?.[0])
  input.value = ''
}
function onDrop(e: DragEvent) {
  dragging.value = false
  void choose(e.dataTransfer?.files?.[0])
}

const SECTIONS: { kind: SyncChangeKind; title: string; hint: string }[] = [
  { kind: 'UPDATE', title: 'Changed', hint: 'Fields edited in the file' },
  { kind: 'ADD', title: 'New', hint: 'Rows without a known id' },
  { kind: 'MOVE', title: 'Moved', hint: 'Under a different parent in the file' },
  { kind: 'ORDER', title: 'Order', hint: 'Rows in a different order' },
  { kind: 'DELETE', title: 'Missing from the file', hint: 'Ticked items are deleted with everything inside them, including progress and notes' },
]
const byKind = computed(() => {
  const map = new Map<SyncChangeKind, SyncChange[]>()
  for (const c of preview.value?.changes ?? []) {
    if (!map.has(c.kind)) map.set(c.kind, [])
    map.get(c.kind)!.push(c)
  }
  return map
})
const byKey = computed(() => new Map((preview.value?.changes ?? []).map((c) => [c.key, c])))

/** Ticking a change also ticks the new parent it needs; unticking a new parent unticks what needs it. */
function toggle(c: SyncChange, on: boolean) {
  const next = new Set(selected.value)
  if (on) {
    next.add(c.key)
    for (let dep = c.dependsOn; dep; dep = byKey.value.get(dep)?.dependsOn ?? null) next.add(dep)
  } else {
    next.delete(c.key)
    const drop = (key: string) => {
      for (const other of preview.value?.changes ?? []) {
        if (other.dependsOn === key && next.delete(other.key)) drop(other.key)
      }
    }
    drop(c.key)
  }
  selected.value = next
}
function setAll(kind: SyncChangeKind, on: boolean) {
  for (const c of byKind.value.get(kind) ?? []) toggle(c, on)
}

const deleteCount = computed(() => (byKind.value.get('DELETE') ?? []).filter((c) => selected.value.has(c.key)).length)

async function apply() {
  if (!file.value || !preview.value) return
  applying.value = true
  try {
    const r = await syncApi.apply(planId.value, file.value, [...selected.value])
    const parts = [
      r.updated && `${r.updated} changed`,
      r.added && `${r.added} added`,
      r.moved && `${r.moved} moved`,
      r.deleted && `${r.deleted} deleted`,
    ].filter(Boolean)
    toast.success(`Sync applied${parts.length ? ': ' + parts.join(', ') : ''}.`)
    r.skipped.forEach((s) => toast.info(s))
    void planList.refresh()
    await router.push(`/plans/${planId.value}`)
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    applying.value = false
  }
}

function fieldLabel(f: string) {
  return f.charAt(0).toUpperCase() + f.slice(1)
}
</script>

<template>
  <div class="max-w-4xl space-y-4">
    <RouterLink :to="`/plans/${planId}`" class="muted inline-flex items-center gap-1 text-sm hover:underline">
      <ArrowLeft class="size-3.5" /> {{ plan?.name ?? 'Plan' }}
    </RouterLink>
    <h1 class="flex items-center gap-2 text-2xl font-semibold"><RefreshCw class="size-6" /> Sync from an edited file</h1>

    <ol class="card muted list-inside list-decimal space-y-1 p-4 text-sm">
      <li>
        <a :href="plansApi.exportUrl(planId)" class="font-medium text-emerald-700 underline dark:text-emerald-400"><Download class="mr-0.5 inline size-3.5" />Export this plan as CSV</a>
        and edit it in Excel or Google Sheets: rename, add rows (leave <code>id</code> empty), move rows, change levels, tick topics, delete rows.
      </li>
      <li>Keep the <code>id</code> column: it's how rows are matched to items. Save as CSV or Excel.</li>
      <li>Upload it here, check the changes, and apply the ones you want. Nothing changes before you click Apply.</li>
    </ol>

    <div
      class="card flex flex-col items-center gap-2 border-2 border-dashed p-6 text-center"
      :class="dragging ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-500/10' : ''"
      @dragover.prevent="dragging = true"
      @dragleave="dragging = false"
      @drop.prevent="onDrop"
    >
      <FileUp class="size-8 text-slate-400" />
      <p class="text-sm">{{ file ? file.name : 'Drop the edited file here' }}</p>
      <button class="btn-secondary" :disabled="loading" @click="fileInput?.click()">
        <LoaderCircle v-if="loading" class="size-4 animate-spin" /> {{ file ? 'Choose another file' : 'Choose file…' }}
      </button>
      <input ref="fileInput" type="file" accept=".csv,.xlsx,.xls,text/csv" class="sr-only" @change="onPick" />
    </div>

    <template v-if="preview">
      <div v-if="preview.errors.length" class="rounded-lg border border-red-300 p-3 text-sm text-red-700 dark:border-red-900 dark:text-red-300">
        <p class="mb-1 flex items-center gap-1.5 font-semibold"><CircleAlert class="size-4" /> This file can't be synced</p>
        <ul class="list-inside list-disc">
          <li v-for="(e, i) in preview.errors" :key="i">{{ e }}</li>
        </ul>
      </div>
      <details v-if="preview.warnings.length" class="rounded-lg border border-amber-300 p-3 text-sm text-amber-800 dark:border-amber-900 dark:text-amber-300">
        <summary class="cursor-pointer font-medium">{{ preview.warnings.length }} warning(s)</summary>
        <ul class="mt-1 list-inside list-disc">
          <li v-for="(w, i) in preview.warnings" :key="i">{{ w }}</li>
        </ul>
      </details>
      <p v-if="!preview.errors.length && !preview.versionColumn" class="text-xs text-amber-700 dark:text-amber-400">
        This file has no <code>version</code> column (exported by an older version of the app), so edits made in the app since the export can't be detected. Check the changes carefully.
      </p>

      <p v-if="!preview.errors.length && preview.changes.length === 0" class="card muted p-6 text-center text-sm">
        The file matches the plan: nothing to sync.
      </p>

      <section v-for="s in SECTIONS.filter((x) => byKind.get(x.kind)?.length)" :key="s.kind" class="card">
        <header class="flex flex-wrap items-center justify-between gap-2 border-b border-slate-200 px-4 py-2 dark:border-slate-800">
          <div>
            <h2 class="text-sm font-semibold">{{ s.title }} ({{ byKind.get(s.kind)!.length }})</h2>
            <p class="muted text-xs">{{ s.hint }}</p>
          </div>
          <div class="flex gap-1 text-xs">
            <button class="btn-ghost px-2 py-0.5 text-xs" @click="setAll(s.kind, true)">Select all</button>
            <button class="btn-ghost px-2 py-0.5 text-xs" @click="setAll(s.kind, false)">None</button>
          </div>
        </header>
        <ul class="divide-y divide-slate-200 dark:divide-slate-800">
          <li v-for="c in byKind.get(s.kind)" :key="c.key">
            <label class="flex cursor-pointer items-start gap-3 px-4 py-2.5 hover:bg-slate-50 dark:hover:bg-slate-800/40">
              <input
                type="checkbox"
                class="mt-0.5 size-4 shrink-0 accent-emerald-600"
                :checked="selected.has(c.key)"
                @change="toggle(c, ($event.target as HTMLInputElement).checked)"
              />
              <span class="min-w-0 flex-1 text-sm">
                <span class="flex flex-wrap items-center gap-2">
                  <span v-if="c.name" class="font-medium">{{ c.name }}</span>
                  <span v-if="c.conflict" class="inline-flex items-center gap-1 rounded-full bg-amber-100 px-2 text-[11px] font-medium text-amber-800 dark:bg-amber-500/15 dark:text-amber-300">
                    <TriangleAlert class="size-3" /> Conflict
                  </span>
                  <span v-if="c.row" class="muted text-xs">row {{ c.row }}</span>
                </span>
                <span v-if="c.path" class="muted block truncate text-xs">{{ c.path }}</span>
                <span v-for="f in c.fields" :key="f.field" class="mt-0.5 flex flex-wrap items-center gap-1.5 text-xs">
                  <span class="muted w-20 shrink-0">{{ fieldLabel(f.field) }}</span>
                  <template v-if="c.kind !== 'ADD'">
                    <span class="rounded bg-red-50 px-1 text-red-800 line-through dark:bg-red-500/10 dark:text-red-300">{{ f.from ?? '(empty)' }}</span>
                    <ArrowRight class="size-3 text-slate-400" />
                  </template>
                  <span class="rounded bg-emerald-50 px-1 text-emerald-800 dark:bg-emerald-500/10 dark:text-emerald-300">{{ f.to ?? '(empty)' }}</span>
                </span>
                <span v-if="c.detail" class="mt-0.5 block text-xs" :class="c.conflict || c.kind === 'DELETE' ? 'text-amber-700 dark:text-amber-400' : 'muted'">{{ c.detail }}</span>
                <span v-if="c.dependsOn" class="muted block text-xs">Needs its new parent “{{ byKey.get(c.dependsOn)?.name }}” to be added too.</span>
              </span>
            </label>
          </li>
        </ul>
      </section>

      <div v-if="preview.changes.length && !preview.errors.length" class="sticky bottom-0 flex flex-wrap items-center justify-end gap-3 border-t border-slate-200 bg-slate-50/95 py-3 backdrop-blur dark:border-slate-800 dark:bg-slate-950/95">
        <span class="muted mr-auto text-sm">{{ selected.size }} of {{ preview.changes.length }} change(s) selected</span>
        <span v-if="deleteCount" class="text-sm text-red-700 dark:text-red-400">{{ deleteCount }} item(s) will be deleted</span>
        <RouterLink :to="`/plans/${planId}`" class="btn-secondary">Cancel</RouterLink>
        <button class="btn-primary" :disabled="applying || selected.size === 0" @click="apply">
          <LoaderCircle v-if="applying" class="size-4 animate-spin" /> Apply {{ selected.size }} change(s)
        </button>
      </div>
    </template>
  </div>
</template>
