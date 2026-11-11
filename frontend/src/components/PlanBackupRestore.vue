<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArchiveRestore, CircleAlert, FileJson, LoaderCircle } from 'lucide-vue-next'
import { planBackupApi } from '../api'
import { errorMessage } from '../api/client'
import type { PlanBackupPreview } from '../types/api'
import { dateTime, minutesLabel } from '../lib/format'
import { usePlanList } from '../composables/usePlanList'
import { useToast } from '../composables/useToast'

/** Restores a full plan backup (.json from "Full backup (JSON)") as a new plan. */
const router = useRouter()
const toast = useToast()
const { refresh: refreshPlans } = usePlanList()

const file = ref<File | null>(null)
const preview = ref<PlanBackupPreview | null>(null)
const name = ref('')
const loading = ref(false)
const restoring = ref(false)
const error = ref<string | null>(null)

async function onFile(e: Event) {
  const input = e.target as HTMLInputElement
  const f = input.files?.[0] ?? null
  input.value = ''
  file.value = f
  preview.value = null
  error.value = null
  if (!f) return
  loading.value = true
  try {
    preview.value = await planBackupApi.preview(f)
    name.value = preview.value.suggestedName
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

async function restore() {
  if (!file.value || !preview.value) return
  restoring.value = true
  try {
    const result = await planBackupApi.restore(file.value, name.value.trim() || null)
    const plan = result.plans[0]
    toast.success(`Restored “${plan.planName}” with ${plan.itemsCreated} item(s).`)
    void refreshPlans()
    await router.push(`/plans/${plan.planId}`)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    restoring.value = false
  }
}
</script>

<template>
  <section class="card space-y-3 p-4">
    <div class="flex flex-wrap items-center justify-between gap-2">
      <div>
        <h2 class="flex items-center gap-2 text-sm font-semibold"><ArchiveRestore class="size-4" /> Restore a full plan backup</h2>
        <p class="muted text-xs">
          A <code>.json</code> file from a plan's menu → <em>Full backup (JSON)</em>. It brings back everything: notes, tags, links, time spent,
          review schedules, confidence ratings and flashcards, as a new plan.
        </p>
      </div>
      <label class="btn-secondary cursor-pointer">
        <FileJson class="size-4" /> Choose .json…
        <input type="file" accept=".json,application/json" class="sr-only" @change="onFile" />
      </label>
    </div>

    <div v-if="loading" class="muted flex items-center gap-2 text-sm"><LoaderCircle class="size-4 animate-spin" /> Reading {{ file?.name }}…</div>
    <div v-if="error" class="flex items-start gap-2 rounded-lg border border-red-300 p-3 text-sm text-red-700 dark:border-red-900 dark:text-red-300">
      <CircleAlert class="mt-0.5 size-4 shrink-0" /> {{ error }}
    </div>

    <div v-if="preview" class="space-y-3 border-t border-slate-200 pt-3 dark:border-slate-800">
      <p class="text-sm">
        <strong>{{ preview.planName }}</strong>
        <span v-if="preview.exportedAt" class="muted"> · backed up {{ dateTime(preview.exportedAt) }}</span>
      </p>
      <div class="flex flex-wrap gap-1.5 text-xs">
        <span class="chip">{{ preview.modules }} modules</span>
        <span class="chip">{{ preview.topics }} topics</span>
        <span class="chip">{{ preview.completed }} done</span>
        <span v-if="preview.notes" class="chip">{{ preview.notes }} notes</span>
        <span v-if="preview.flashcards" class="chip">{{ preview.flashcards }} flashcards</span>
        <span v-if="preview.tags" class="chip">{{ preview.tags }} tags</span>
        <span v-if="preview.links" class="chip">{{ preview.links }} links</span>
        <span v-if="preview.timeEntries" class="chip">{{ minutesLabel(preview.minutes) }} logged</span>
        <span v-if="preview.reviews" class="chip">{{ preview.reviews }} reviews scheduled</span>
        <span v-if="preview.ratings" class="chip">{{ preview.ratings }} ratings</span>
      </div>
      <form class="flex flex-wrap items-end gap-2" @submit.prevent="restore">
        <label class="min-w-56 flex-1">
          <span class="label">New plan name</span>
          <input v-model="name" class="input" required maxlength="200" />
        </label>
        <button type="submit" class="btn-primary" :disabled="restoring || !name.trim()">
          <LoaderCircle v-if="restoring" class="size-4 animate-spin" /> Restore as new plan
        </button>
      </form>
      <p v-if="preview.nameTaken" class="muted text-xs">A plan named “{{ preview.planName }}” already exists, so a different name is suggested.</p>
    </div>
  </section>
</template>
