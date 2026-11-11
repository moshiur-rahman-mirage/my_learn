<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleAlert, ClipboardPaste, Download, FileSpreadsheet, LoaderCircle, TriangleAlert, Upload } from 'lucide-vue-next'
import { importApi, plansApi } from '../api'
import { errorMessage } from '../api/client'
import type { ColumnRole, ImportPreview, Plan } from '../types/api'
import { dateTime } from '../lib/format'
import { useToast } from '../composables/useToast'
import { usePlanList } from '../composables/usePlanList'
import PreviewTree from '../components/PreviewTree.vue'
import PlanBackupRestore from '../components/PlanBackupRestore.vue'

type Target = 'new' | 'existing' | 'allSheets'
type Source = 'file' | 'paste'

const PASTED_FILE_NAME = 'pasted.csv'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const planList = usePlanList()

const plans = ref<Plan[]>([])
const target = ref<Target>('new')
const planId = ref<number | null>(null)
const newPlanName = ref('')
const source = ref<Source>('file')
const pasteText = ref('')
const file = ref<File | null>(null)
const sheet = ref<string | null>(null)
const headerRow = ref<number | null>(null)
const roles = ref<ColumnRole[] | null>(null)
const preview = ref<ImportPreview | null>(null)
const previewError = ref<string | null>(null)
const loading = ref(false)
const importing = ref(false)
const allowDuplicate = ref(false)
const showAllWarnings = ref(false)
let lastSuggested = '' // the plan name we proposed, so we don't overwrite one the user typed

const TEMPLATE_COLUMNS = [
  { name: 'level', help: 'Required. 1 = top level, 2 = inside the row above it with level 1, 3 = one deeper, and so on.' },
  { name: 'type', help: 'MODULE (a group) or TOPIC (something to learn). Leave empty to decide automatically: rows with sub-rows become modules.' },
  { name: 'name', help: 'Required. The title of the module or topic.' },
  { name: 'description', help: 'Optional. What to learn, notes, links.' },
  { name: 'priority', help: 'Optional. MUST, SHOULD or NICE.' },
  { name: 'completed', help: 'Optional. TRUE if already done. Only counts for topics without sub-rows.' },
  { name: 'note', help: 'Optional (add the column yourself). The item\'s note, in Markdown. Exports include it, so an exported plan imports back with its notes.' },
  { name: 'note_format', help: 'Optional. MARKDOWN or HTML; detected from the note when left empty.' },
]

const ROLES: { value: ColumnRole; label: string }[] = [
  { value: 'LEVEL', label: 'Hierarchy level / name' },
  { value: 'DEPTH', label: 'Level number (1, 2, 3…)' },
  { value: 'DESCRIPTION', label: 'Description' },
  { value: 'PRIORITY', label: 'Priority' },
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'NOTE', label: 'Note (Markdown / rich text)' },
  { value: 'NOTE_FORMAT', label: 'Note format' },
  { value: 'TYPE', label: 'Type (module/topic)' },
  { value: 'ID', label: 'Id (from an export)' },
  { value: 'IGNORE', label: 'Ignore' },
]

onMounted(async () => {
  plans.value = await plansApi.list().catch(() => [])
  const fromQuery = Number(route.query.plan)
  if (fromQuery) {
    target.value = 'existing'
    planId.value = fromQuery
  }
})

const previewPlanId = computed(() => (target.value === 'existing' ? planId.value : null))
const multiSheet = computed(() => (preview.value?.sheets.length ?? 0) > 1)
const visibleWarnings = computed(() =>
  showAllWarnings.value ? preview.value?.warnings ?? [] : (preview.value?.warnings ?? []).slice(0, 5),
)
const canConfirm = computed(
  () =>
    !!preview.value?.canImport &&
    (!preview.value.duplicateOf || allowDuplicate.value) &&
    (target.value !== 'existing' || planId.value !== null) &&
    !importing.value,
)

function resetMapping() {
  sheet.value = null
  headerRow.value = null
  roles.value = null
  allowDuplicate.value = false
}

function onFile(e: Event) {
  const f = (e.target as HTMLInputElement).files?.[0] ?? null
  file.value = f
  resetMapping()
  if (f) void runPreview()
}

/** Pasted text is sent exactly like an uploaded CSV, so preview, validation and import are identical. */
function usePastedText() {
  const text = pasteText.value.trim()
  resetMapping()
  if (!text) {
    file.value = null
    preview.value = null
    previewError.value = null
    return
  }
  file.value = new File([text + '\n'], PASTED_FILE_NAME, { type: 'text/csv' })
  void runPreview()
}

let pasteTimer: ReturnType<typeof setTimeout> | undefined
watch(pasteText, () => {
  clearTimeout(pasteTimer)
  pasteTimer = setTimeout(usePastedText, 400)
})

watch(source, () => {
  clearTimeout(pasteTimer)
  file.value = null
  preview.value = null
  previewError.value = null
  resetMapping()
  if (source.value === 'paste' && pasteText.value.trim()) usePastedText()
})

function suggestedPlanName(result: ImportPreview): string {
  if (result.sheets.length > 1) return result.sheet
  if (result.fileName === PASTED_FILE_NAME) {
    // a single top-level module (e.g. "Phase 1 — Accounting Foundations") makes a good plan name
    return result.tree.length === 1 ? result.tree[0]!.name : 'Pasted plan'
  }
  return result.fileName.replace(/\.[^.]+$/, '')
}

async function runPreview() {
  if (!file.value) return
  loading.value = true
  previewError.value = null
  try {
    const result = await importApi.preview(previewPlanId.value, file.value, {
      sheet: sheet.value,
      headerRow: headerRow.value,
      columnRoles: roles.value,
    })
    preview.value = result
    sheet.value = result.sheet
    headerRow.value = result.headerRow > 0 ? result.headerRow : headerRow.value
    if (!newPlanName.value || newPlanName.value === lastSuggested) {
      newPlanName.value = lastSuggested = suggestedPlanName(result)
    }
  } catch (e) {
    preview.value = null
    previewError.value = errorMessage(e)
  } finally {
    loading.value = false
  }
}
function changeSheet(name: string) {
  sheet.value = name
  headerRow.value = null
  roles.value = null
  void runPreview()
}

function setRole(index: number, role: ColumnRole) {
  const current = roles.value ?? preview.value!.columns.map((c) => c.role)
  const next = [...current]
  next[index] = role
  roles.value = next
  void runPreview()
}

function applyHeaderRow() {
  roles.value = null // re-detect columns for the new header row
  void runPreview()
}

watch(target, () => {
  if (file.value) void runPreview()
})
watch(planId, () => {
  if (file.value && target.value === 'existing') void runPreview()
})

async function confirmImport() {
  if (!file.value || !canConfirm.value) return
  importing.value = true
  try {
    const result = await importApi.confirm(previewPlanId.value, file.value, {
      sheet: sheet.value,
      headerRow: headerRow.value,
      columnRoles: roles.value,
      newPlanName: target.value === 'new' ? newPlanName.value.trim() : null,
      allSheets: target.value === 'allSheets',
      allowDuplicate: allowDuplicate.value,
    })
    const total = result.plans.reduce((s, p) => s + p.itemsCreated, 0)
    toast.success(`Imported ${total} items into ${result.plans.map((p) => `“${p.planName}”`).join(', ')}`)
    void planList.refresh()
    await router.push(result.plans.length === 1 ? `/plans/${result.plans[0]!.planId}` : '/plans')
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <div class="space-y-5">
    <div>
      <h1 class="text-2xl font-semibold">Import a roadmap</h1>
      <p class="muted text-sm">CSV or Excel (.xlsx). Hierarchy, descriptions, priorities and ticks are kept. Existing content is never changed.</p>
    </div>

    <PlanBackupRestore />

    <!-- 0. template -->
    <section class="card space-y-3 p-4">
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 class="text-sm font-semibold">Start from a template</h2>
          <p class="muted text-sm">Download, fill it in with Excel or Google Sheets, save as CSV, then import it below.</p>
        </div>
        <div class="flex flex-wrap gap-2">
          <a :href="importApi.blankTemplateUrl" class="btn-primary" download><Download class="size-4" /> Blank CSV (headers only)</a>
          <a :href="importApi.exampleTemplateUrl" class="btn-secondary" download><Download class="size-4" /> Example CSV</a>
        </div>
      </div>
      <details class="text-sm">
        <summary class="cursor-pointer font-medium text-emerald-700 dark:text-emerald-400">How to fill in the columns</summary>
        <table class="mt-2 w-full text-left">
          <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
            <tr v-for="c in TEMPLATE_COLUMNS" :key="c.name">
              <td class="py-1.5 pr-4 align-top font-mono text-xs whitespace-nowrap">{{ c.name }}</td>
              <td class="muted py-1.5">{{ c.help }}</td>
            </tr>
          </tbody>
        </table>
        <p class="muted mt-2 text-xs">
          Example: level 1 “Spring Core” (MODULE) → level 2 “Dependency injection” (TOPIC). Rows are kept in the order you write them.
        </p>
      </details>
    </section>

    <!-- 1. target -->
    <section class="card space-y-3 p-4">
      <h2 class="text-sm font-semibold">1. Where should it go?</h2>
      <div class="grid gap-2 sm:grid-cols-3">
        <label
          v-for="t in ([
            { v: 'new', l: 'A new plan', d: 'Creates one plan from the chosen sheet' },
            { v: 'existing', l: 'An existing plan', d: 'Appends after its current items' },
            { v: 'allSheets', l: 'One plan per sheet', d: 'For workbooks with several roadmaps' },
          ] as const)"
          :key="t.v"
          class="flex cursor-pointer gap-2 rounded-lg border p-3 text-sm"
          :class="target === t.v ? 'border-emerald-600 bg-emerald-50 dark:bg-emerald-500/10' : 'border-slate-300 dark:border-slate-700'"
        >
          <input v-model="target" type="radio" :value="t.v" class="mt-0.5 accent-emerald-600" />
          <span><span class="block font-medium">{{ t.l }}</span><span class="muted text-xs">{{ t.d }}</span></span>
        </label>
      </div>
      <div v-if="target === 'new'" class="max-w-md">
        <label class="label" for="new-plan-name">Plan name</label>
        <input id="new-plan-name" v-model="newPlanName" class="input" placeholder="Defaults to the sheet or file name" />
      </div>
      <div v-else-if="target === 'existing'" class="max-w-md">
        <label class="label" for="target-plan">Plan</label>
        <select id="target-plan" v-model="planId" class="input">
          <option :value="null" disabled>Choose a plan…</option>
          <option v-for="p in plans" :key="p.id" :value="p.id">{{ p.name }}</option>
        </select>
      </div>
      <p v-else class="muted text-sm">Each sheet that contains data becomes a plan named after the sheet. Layouts are detected automatically per sheet.</p>
    </section>

    <!-- 2. file -->
    <section class="card space-y-3 p-4">
      <div class="flex flex-wrap items-center justify-between gap-2">
        <h2 class="text-sm font-semibold">2. {{ source === 'file' ? 'Choose the file' : 'Paste the CSV' }}</h2>
        <div class="flex gap-1 rounded-lg bg-slate-100 p-1 text-sm dark:bg-slate-800" role="tablist">
          <button
            v-for="s in ([{ v: 'file', l: 'Upload file', i: FileSpreadsheet }, { v: 'paste', l: 'Paste text', i: ClipboardPaste }] as const)"
            :key="s.v"
            type="button"
            role="tab"
            :aria-selected="source === s.v"
            class="flex items-center gap-1.5 rounded-md px-3 py-1"
            :class="source === s.v ? 'bg-white font-medium shadow-sm dark:bg-slate-900' : 'muted'"
            @click="source = s.v"
          >
            <component :is="s.i" class="size-4" /> {{ s.l }}
          </button>
        </div>
      </div>
      <div v-if="source === 'paste'" class="space-y-1">
        <textarea
          v-model="pasteText"
          rows="12"
          spellcheck="false"
          class="input font-mono text-xs leading-relaxed"
          placeholder="level,type,name,description,priority,completed&#10;1,MODULE,Spring Core,,MUST,&#10;2,TOPIC,Dependency injection,,MUST,TRUE"
          aria-label="CSV text"
        ></textarea>
        <p class="muted text-xs">
          Paste CSV with a header row (same columns as the template). Cells copied straight from Excel or Google Sheets work too.
          The preview updates as you type.
        </p>
      </div>
      <label
        v-else
        class="flex cursor-pointer flex-col items-center gap-2 rounded-lg border-2 border-dashed border-slate-300 px-4 py-8 text-center hover:border-emerald-500 dark:border-slate-700"
      >
        <FileSpreadsheet class="size-8 text-slate-400" />
        <span class="text-sm font-medium">{{ file ? file.name : 'Click to choose a .csv or .xlsx file' }}</span>
        <span class="muted text-xs">Up to 10 MB</span>
        <input type="file" accept=".csv,.xlsx,text/csv,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" class="sr-only" @change="onFile" />
      </label>
    </section>

    <div v-if="loading" class="muted flex items-center gap-2 text-sm"><LoaderCircle class="size-4 animate-spin" /> {{ source === 'paste' ? 'Reading text…' : 'Reading file…' }}</div>
    <div v-if="previewError" class="card flex items-start gap-2 border-red-300 p-4 text-sm text-red-700 dark:border-red-900 dark:text-red-300">
      <CircleAlert class="mt-0.5 size-4 shrink-0" /> {{ previewError }}
    </div>

    <!-- 3. preview -->
    <section v-if="preview" class="card space-y-4 p-4">
      <h2 class="text-sm font-semibold">3. Check the preview</h2>

      <div class="flex flex-wrap items-end gap-3">
        <div v-if="multiSheet">
          <label class="label" for="sheet">Sheet{{ target === 'allSheets' ? ' (preview only)' : '' }}</label>
          <select id="sheet" class="input" :value="sheet" @change="changeSheet(($event.target as HTMLSelectElement).value)">
            <option v-for="s in preview.sheets" :key="s" :value="s">{{ s }}</option>
          </select>
        </div>
        <div>
          <label class="label" for="header-row">Header row</label>
          <div class="flex gap-1">
            <input id="header-row" v-model.number="headerRow" type="number" min="1" class="input w-24" />
            <button class="btn-secondary" @click="applyHeaderRow">Apply</button>
          </div>
        </div>
        <div class="flex flex-wrap gap-2 text-xs">
          <span class="chip">{{ preview.stats.modules }} modules</span>
          <span class="chip">{{ preview.stats.topics }} topics</span>
          <span class="chip">{{ preview.stats.leafTopics }} checkable</span>
          <span class="chip">{{ preview.stats.completed }} already done</span>
          <span v-if="preview.stats.notes" class="chip">{{ preview.stats.notes }} with notes</span>
        </div>
      </div>

      <!-- column mapping -->
      <div v-if="preview.columns.length" class="overflow-x-auto">
        <table class="text-sm">
          <thead>
            <tr class="muted text-left text-xs">
              <th class="pr-3 pb-1 font-medium">Column</th>
              <th class="pr-3 pb-1 font-medium">Header</th>
              <th class="pb-1 font-medium">Used as</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in preview.columns" :key="c.index">
              <td class="muted pr-3 font-mono text-xs">{{ String.fromCharCode(65 + (c.index % 26)) }}</td>
              <td class="max-w-48 truncate pr-3">{{ c.header || '—' }}</td>
              <td class="py-0.5">
                <select class="input py-1" :value="c.role" :aria-label="`Role of column ${c.index + 1}`" @change="setRole(c.index, ($event.target as HTMLSelectElement).value as ColumnRole)">
                  <option v-for="r in ROLES" :key="r.value" :value="r.value">{{ r.label }}</option>
                </select>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="preview.errors.length" class="rounded-lg border border-red-300 bg-red-50 p-3 text-sm dark:border-red-900 dark:bg-red-950/40">
        <p class="mb-1 flex items-center gap-1.5 font-medium text-red-700 dark:text-red-300">
          <CircleAlert class="size-4" /> {{ preview.errors.length }} error(s): fix these in the {{ source === 'paste' ? 'text' : 'file' }} (or adjust the mapping) before importing
        </p>
        <ul class="list-disc pl-6 text-red-700 dark:text-red-300">
          <li v-for="(e, i) in preview.errors" :key="i"><span v-if="e.row">Row {{ e.row }}: </span>{{ e.message }}</li>
        </ul>
      </div>

      <div v-if="preview.warnings.length" class="rounded-lg border border-amber-300 bg-amber-50 p-3 text-sm dark:border-amber-900 dark:bg-amber-950/30">
        <p class="mb-1 flex items-center gap-1.5 font-medium text-amber-800 dark:text-amber-300">
          <TriangleAlert class="size-4" /> {{ preview.warnings.length }} warning(s): import will still work
        </p>
        <ul class="list-disc pl-6 text-amber-800 dark:text-amber-300">
          <li v-for="(w, i) in visibleWarnings" :key="i"><span v-if="w.row">Row {{ w.row }}: </span>{{ w.message }}</li>
        </ul>
        <button v-if="preview.warnings.length > 5" class="mt-1 text-xs underline" @click="showAllWarnings = !showAllWarnings">
          {{ showAllWarnings ? 'Show fewer' : `Show all ${preview.warnings.length}` }}
        </button>
      </div>

      <div v-if="preview.duplicateOf" class="rounded-lg border border-amber-300 p-3 text-sm dark:border-amber-900">
        <p>
          This content was already imported into <strong>{{ preview.duplicateOf.planName }}</strong> on
          {{ dateTime(preview.duplicateOf.importedAt) }}.
        </p>
        <label class="mt-2 flex items-center gap-2"><input v-model="allowDuplicate" type="checkbox" class="accent-emerald-600" /> Import it again anyway</label>
      </div>

      <div v-if="preview.tree.length" class="max-h-[28rem] overflow-y-auto rounded-lg border border-slate-200 p-2 dark:border-slate-800">
        <PreviewTree :nodes="preview.tree" />
      </div>

      <div class="flex justify-end">
        <button class="btn-primary" :disabled="!canConfirm" @click="confirmImport">
          <LoaderCircle v-if="importing" class="size-4 animate-spin" /><Upload v-else class="size-4" />
          {{ target === 'allSheets' ? `Import all ${preview.sheets.length} sheets` : 'Import' }}
        </button>
      </div>
    </section>
  </div>
</template>
