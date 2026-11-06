<script setup lang="ts">
import type { Priority } from '../types/api'

defineProps<{ priority: Priority | null }>()

const styles: Record<Priority, string> = {
  MUST: 'bg-rose-100 text-rose-700 ring-rose-600/20 dark:bg-rose-500/15 dark:text-rose-300',
  SHOULD: 'bg-amber-100 text-amber-800 ring-amber-600/20 dark:bg-amber-500/15 dark:text-amber-300',
  NICE: 'bg-sky-100 text-sky-700 ring-sky-600/20 dark:bg-sky-500/15 dark:text-sky-300',
}
const labels: Record<Priority, string> = { MUST: 'Must', SHOULD: 'Should', NICE: 'Nice' }
</script>

<template>
  <span
    v-if="priority"
    class="inline-flex shrink-0 items-center rounded-md px-1.5 py-0.5 text-[11px] font-semibold ring-1 ring-inset"
    :class="styles[priority]"
    >{{ labels[priority] }}</span
  >
</template>
