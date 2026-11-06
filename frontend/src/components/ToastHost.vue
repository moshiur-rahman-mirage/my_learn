<script setup lang="ts">
import { CircleAlert, CircleCheck, Info, X } from 'lucide-vue-next'
import { useToast } from '../composables/useToast'

const { toasts, dismiss } = useToast()
</script>

<template>
  <div class="pointer-events-none fixed top-4 right-4 left-4 z-[60] flex flex-col items-end gap-2 sm:left-auto" aria-live="polite">
    <div
      v-for="t in toasts"
      :key="t.id"
      class="card pointer-events-auto flex w-full max-w-sm items-start gap-2.5 px-3.5 py-3 text-sm shadow-lg"
      :role="t.kind === 'error' ? 'alert' : 'status'"
    >
      <CircleCheck v-if="t.kind === 'success'" class="mt-0.5 size-4 shrink-0 text-emerald-600" />
      <CircleAlert v-else-if="t.kind === 'error'" class="mt-0.5 size-4 shrink-0 text-red-600" />
      <Info v-else class="mt-0.5 size-4 shrink-0 text-sky-600" />
      <span class="flex-1 break-words">{{ t.message }}</span>
      <button class="icon-btn -my-1 -mr-1 size-6" aria-label="Dismiss" @click="dismiss(t.id)"><X class="size-3.5" /></button>
    </div>
  </div>
</template>
