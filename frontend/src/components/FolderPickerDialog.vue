<script setup lang="ts">
import { ref, watch } from 'vue'
import { ArrowUp, Folder, HardDrive, LoaderCircle } from 'lucide-vue-next'
import { adminApi } from '../api'
import { errorMessage } from '../api/client'
import type { FolderListing } from '../types/api'
import BaseModal from './BaseModal.vue'

/** Browse folders on the computer running the app (a web page can't open a native folder picker for it). */
const props = defineProps<{ open: boolean; start: string | null }>()
const emit = defineEmits<{ close: []; pick: [path: string] }>()

const listing = ref<FolderListing | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const typed = ref('')

async function go(path: string | null) {
  loading.value = true
  error.value = null
  try {
    listing.value = await adminApi.folders(path)
    typed.value = listing.value.path ?? ''
  } catch (e) {
    error.value = errorMessage(e)
    if (!listing.value) listing.value = await adminApi.folders(null).catch(() => null)
  } finally {
    loading.value = false
  }
}

watch(
  () => props.open,
  (o) => {
    if (o) void go(props.start)
  },
  { immediate: true },
)
</script>

<template>
  <BaseModal :open="open" title="Choose backup folder" width="max-w-xl" @close="emit('close')">
    <form class="mb-3 flex gap-2" @submit.prevent="go(typed.trim() || null)">
      <input v-model="typed" class="input font-mono text-xs" placeholder="Type a path, or pick below" aria-label="Folder path" />
      <button type="submit" class="btn-secondary">Go</button>
    </form>

    <p v-if="error" class="mb-2 text-sm text-red-600 dark:text-red-400">{{ error }}</p>

    <div class="h-72 overflow-y-auto rounded-lg border border-slate-200 dark:border-slate-800">
      <div v-if="loading" class="muted flex items-center gap-2 p-3 text-sm"><LoaderCircle class="size-4 animate-spin" /> Loading…</div>
      <ul v-else-if="listing" class="text-sm">
        <li v-if="listing.path !== null">
          <button class="flex w-full items-center gap-2 px-3 py-1.5 text-left hover:bg-slate-100 dark:hover:bg-slate-800" @click="go(listing.parent || null)">
            <ArrowUp class="size-4 text-slate-400" /> <span class="muted">Up</span>
          </button>
        </li>
        <li v-for="f in listing.folders" :key="f.path">
          <button
            class="flex w-full items-center gap-2 px-3 py-1.5 text-left hover:bg-slate-100 dark:hover:bg-slate-800"
            :title="f.path"
            @click="go(f.path)"
          >
            <component :is="listing.path === null ? HardDrive : Folder" class="size-4 shrink-0 text-amber-500" />
            <span class="truncate">{{ f.name }}</span>
          </button>
        </li>
        <li v-if="listing.folders.length === 0" class="muted px-3 py-2">No sub-folders.</li>
      </ul>
    </div>

    <div class="mt-4 flex items-center justify-between gap-2">
      <code class="muted min-w-0 truncate text-xs" :title="listing?.path ?? ''">{{ listing?.path ?? 'Pick a drive or folder' }}</code>
      <div class="flex shrink-0 gap-2">
        <button class="btn-secondary" @click="emit('close')">Cancel</button>
        <button class="btn-primary" :disabled="!listing?.path" @click="emit('pick', listing!.path!)">Use this folder</button>
      </div>
    </div>
    <p class="muted mt-2 text-xs">Tip: choose a folder that is synced (OneDrive, Google Drive, Dropbox) or on another drive, so a copy survives a disk failure.</p>
  </BaseModal>
</template>
