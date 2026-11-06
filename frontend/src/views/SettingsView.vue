<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Database, FolderOpen, History, LoaderCircle, Monitor, Moon, RotateCcw, Sun, Trash2, Upload } from 'lucide-vue-next'
import { adminApi } from '../api'
import { errorMessage } from '../api/client'
import type { BackupFile, BackupSettings, RestoreResult } from '../types/api'
import { dateTime, fileSize } from '../lib/format'
import { useTheme, type ThemeMode } from '../composables/useTheme'
import { useToast } from '../composables/useToast'
import { useConfirm } from '../composables/useConfirm'
import FolderPickerDialog from '../components/FolderPickerDialog.vue'

const { mode } = useTheme()
const toast = useToast()
const { confirm } = useConfirm()
const backups = ref<BackupFile[]>([])
const settings = ref<BackupSettings | null>(null)
const folderInput = ref('')
const working = ref(false)
const savingFolder = ref(false)
const restoring = ref<string | null>(null) // file name being restored
const deleting = ref<string | null>(null) // file name being deleted
const pickerOpen = ref(false)
const mirrorInput = ref('')
const savingMirror = ref(false)
const mirrorPickerOpen = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

const themes: { value: ThemeMode; label: string; icon: typeof Sun }[] = [
  { value: 'system', label: 'System', icon: Monitor },
  { value: 'light', label: 'Light', icon: Sun },
  { value: 'dark', label: 'Dark', icon: Moon },
]

async function loadBackups() {
  backups.value = await adminApi.backups().catch(() => [])
}
async function loadSettings() {
  try {
    settings.value = await adminApi.backupSettings()
    folderInput.value = settings.value.directory
    mirrorInput.value = settings.value.mirrorDirectory ?? ''
  } catch (e) {
    toast.error(errorMessage(e))
  }
}

async function backupNow() {
  working.value = true
  try {
    const b = await adminApi.backup()
    toast.success(`Backup saved: ${b.fileName}`)
    await loadBackups()
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    working.value = false
  }
}

async function saveFolder(directory: string) {
  savingFolder.value = true
  try {
    settings.value = await adminApi.setBackupDirectory(directory)
    folderInput.value = settings.value.directory
    toast.success(settings.value.custom ? `Backups will be saved to ${settings.value.directory}` : 'Backups go to the default folder again')
    await loadBackups()
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    savingFolder.value = false
  }
}
function onPicked(path: string) {
  pickerOpen.value = false
  void saveFolder(path)
}

async function saveMirror(directory: string) {
  savingMirror.value = true
  try {
    settings.value = await adminApi.setBackupMirror(directory)
    mirrorInput.value = settings.value.mirrorDirectory ?? ''
    toast.success(settings.value.mirrorDirectory ? `Backups will also be copied to ${settings.value.mirrorDirectory}` : 'Backups are no longer copied')
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    savingMirror.value = false
  }
}
function onMirrorPicked(path: string) {
  mirrorPickerOpen.value = false
  void saveMirror(path)
}

function restoredMessage(r: RestoreResult) {
  return `Restored from ${r.restoredFrom}: ${r.plans} plan(s), ${r.items} item(s). Your previous data was saved as ${r.safetyBackup}.`
}

async function confirmRestore(what: string): Promise<boolean> {
  return confirm({
    title: 'Restore this backup?',
    message:
      `All current plans, items and notes will be replaced by the content of ${what}.\n\n` +
      'A safety backup of your current data is taken first, so you can undo this by restoring that safety backup.',
    confirmLabel: 'Restore',
    danger: true,
  })
}

async function restoreBackup(b: BackupFile) {
  if (!(await confirmRestore(`the backup from ${dateTime(b.createdAt)}`))) return
  restoring.value = b.fileName
  try {
    const result = await adminApi.restoreBackup(b.fileName)
    finishRestore(result)
  } catch (e) {
    toast.error(errorMessage(e))
    restoring.value = null
  }
}

async function deleteBackup(b: BackupFile) {
  const ok = await confirm({
    title: 'Delete this backup?',
    message: `The backup from ${dateTime(b.createdAt)} will be permanently deleted:\n${b.path}`,
    confirmLabel: 'Delete',
    danger: true,
  })
  if (!ok) return
  deleting.value = b.fileName
  try {
    await adminApi.deleteBackup(b.fileName)
    toast.success(`Backup deleted: ${b.fileName}`)
    await loadBackups()
  } catch (e) {
    toast.error(errorMessage(e))
  } finally {
    deleting.value = null
  }
}

async function restoreFromFile(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = '' // allow choosing the same file again
  if (!file) return
  if (!(await confirmRestore(`“${file.name}”`))) return
  restoring.value = file.name
  try {
    finishRestore(await adminApi.restoreFile(file))
  } catch (err) {
    toast.error(errorMessage(err))
    restoring.value = null
  }
}

function finishRestore(result: RestoreResult) {
  // every page holds data from before the restore: start fresh
  sessionStorage.setItem('lt.restored', restoredMessage(result))
  window.location.reload()
}

onMounted(() => {
  void loadSettings()
  void loadBackups()
  const done = sessionStorage.getItem('lt.restored')
  if (done) {
    sessionStorage.removeItem('lt.restored')
    toast.success(done)
  }
})
</script>

<template>
  <div class="max-w-3xl space-y-6">
    <h1 class="text-2xl font-semibold">Settings</h1>

    <section class="card p-4">
      <h2 class="mb-3 font-semibold">Theme</h2>
      <div class="flex gap-2" role="radiogroup" aria-label="Theme">
        <button
          v-for="t in themes"
          :key="t.value"
          role="radio"
          :aria-checked="mode === t.value"
          class="chip px-3 py-1"
          :class="{ 'chip-on': mode === t.value }"
          @click="mode = t.value"
        >
          <component :is="t.icon" class="size-3.5" /> {{ t.label }}
        </button>
      </div>
    </section>

    <!-- backup location -->
    <section class="card space-y-3 p-4">
      <div class="flex flex-wrap items-center justify-between gap-2">
        <h2 class="font-semibold">Backups</h2>
        <button class="btn-primary" :disabled="working" @click="backupNow">
          <LoaderCircle v-if="working" class="size-4 animate-spin" /><Database v-else class="size-4" /> Back up now
        </button>
      </div>
      <p class="muted text-sm">
        A backup is taken automatically about once a day<template v-if="settings">, and the {{ settings.keep }} newest are kept</template>.
        Each file is a complete copy of your data (plans, items, notes).
      </p>

      <div>
        <label class="label" for="backup-folder">Backup folder</label>
        <form class="flex flex-wrap gap-2" @submit.prevent="saveFolder(folderInput)">
          <input id="backup-folder" v-model="folderInput" class="input min-w-0 flex-1 font-mono text-xs" placeholder="Full path, e.g. D:\Backups\LearningTracker" />
          <button type="button" class="btn-secondary" @click="pickerOpen = true"><FolderOpen class="size-4" /> Browse…</button>
          <button type="submit" class="btn-primary" :disabled="savingFolder || !folderInput.trim() || folderInput.trim() === settings?.directory">
            <LoaderCircle v-if="savingFolder" class="size-4 animate-spin" /> Save
          </button>
        </form>
        <p v-if="settings" class="muted mt-1 text-xs">
          <template v-if="settings.custom">
            Default: <code>{{ settings.defaultDirectory }}</code> ·
            <button class="underline" :disabled="savingFolder" @click="saveFolder('')">Use the default</button>
          </template>
          <template v-else>This is the default folder, inside the app's data folder.</template>
          Backups already in the old folder stay there.
        </p>
      </div>

      <div>
        <label class="label" for="mirror-folder">Also copy each backup to <span class="font-normal">(optional)</span></label>
        <form class="flex flex-wrap gap-2" @submit.prevent="saveMirror(mirrorInput)">
          <input
            id="mirror-folder"
            v-model="mirrorInput"
            class="input min-w-0 flex-1 font-mono text-xs"
            placeholder="A second place, e.g. your OneDrive folder or a USB drive"
          />
          <button type="button" class="btn-secondary" @click="mirrorPickerOpen = true"><FolderOpen class="size-4" /> Browse…</button>
          <button type="submit" class="btn-primary" :disabled="savingMirror || !mirrorInput.trim() || mirrorInput.trim() === settings?.mirrorDirectory">
            <LoaderCircle v-if="savingMirror" class="size-4 animate-spin" /> Save
          </button>
        </form>
        <p class="muted mt-1 text-xs">
          <template v-if="settings?.mirrorDirectory">
            Every new backup is also copied to <code>{{ settings.mirrorDirectory }}</code> (the {{ settings.keep }} newest are kept there too) ·
            <button class="underline" :disabled="savingMirror" @click="saveMirror('')">Stop copying</button>
          </template>
          <template v-else>If this computer's disk fails, a copy somewhere else still has your data.</template>
        </p>
        <p v-if="settings?.mirrorError" class="mt-1 text-xs text-red-600 dark:text-red-400">{{ settings.mirrorError }}. Is the drive connected?</p>
      </div>
    </section>

    <!-- restore -->
    <section class="card space-y-3 p-4">
      <div class="flex flex-wrap items-center justify-between gap-2">
        <h2 class="flex items-center gap-2 font-semibold"><History class="size-4" /> Restore</h2>
        <button class="btn-secondary" :disabled="restoring !== null" @click="fileInput?.click()">
          <Upload class="size-4" /> Restore from a file…
        </button>
        <input ref="fileInput" type="file" accept=".db,.sqlite,.sqlite3,application/octet-stream" class="sr-only" @change="restoreFromFile" />
      </div>
      <p class="muted text-sm">
        Pick a backup below, or any backup <code>.db</code> file (e.g. one copied to a USB drive). The file is checked first, and your
        current data is backed up before it is replaced. Backups from older app versions are upgraded automatically.
      </p>

      <p v-if="backups.length === 0" class="muted text-sm">No backups in this folder yet.</p>
      <ul v-else class="divide-y divide-slate-200 text-sm dark:divide-slate-800">
        <li v-for="b in backups" :key="b.fileName" class="flex items-center gap-3 py-2">
          <div class="min-w-0 flex-1">
            <div class="flex justify-between gap-2">
              <span class="font-medium">{{ dateTime(b.createdAt) }}</span>
              <span class="muted">{{ fileSize(b.sizeBytes) }}</span>
            </div>
            <code class="muted block truncate text-xs" :title="b.path">{{ b.path }}</code>
          </div>
          <button class="btn-secondary shrink-0" :disabled="restoring !== null" :aria-label="`Restore backup from ${dateTime(b.createdAt)}`" @click="restoreBackup(b)">
            <LoaderCircle v-if="restoring === b.fileName" class="size-4 animate-spin" /><RotateCcw v-else class="size-4" /> Restore
          </button>
          <button
            class="btn-secondary shrink-0 text-red-600 dark:text-red-400"
            :disabled="restoring !== null || deleting !== null"
            :title="`Delete backup from ${dateTime(b.createdAt)}`"
            :aria-label="`Delete backup from ${dateTime(b.createdAt)}`"
            @click="deleteBackup(b)"
          >
            <LoaderCircle v-if="deleting === b.fileName" class="size-4 animate-spin" /><Trash2 v-else class="size-4" />
          </button>
        </li>
      </ul>
      <p v-if="restoring" class="muted flex items-center gap-2 text-sm"><LoaderCircle class="size-4 animate-spin" /> Restoring {{ restoring }}…</p>
    </section>

    <FolderPickerDialog :open="pickerOpen" :start="settings?.directory ?? null" @close="pickerOpen = false" @pick="onPicked" />
    <FolderPickerDialog
      :open="mirrorPickerOpen"
      :start="settings?.mirrorDirectory ?? null"
      @close="mirrorPickerOpen = false"
      @pick="onMirrorPicked"
    />
  </div>
</template>
