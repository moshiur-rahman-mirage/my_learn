<script setup lang="ts">
import { computed } from 'vue'
import type { Progress } from '../types/api'
import { percentLabel } from '../lib/format'

const props = withDefaults(defineProps<{ progress: Progress; size?: 'sm' | 'md' | 'lg'; showLabel?: boolean }>(), {
  size: 'md',
  showLabel: true,
})

const width = computed(() => `${props.progress.percent ?? 0}%`)
const height = computed(() => ({ sm: 'h-1.5', md: 'h-2', lg: 'h-3' })[props.size])
const done = computed(() => props.progress.percent === 100)
</script>

<template>
  <div class="flex items-center gap-2">
    <div
      class="flex-1 overflow-hidden rounded-full bg-slate-200 dark:bg-slate-800"
      :class="height"
      role="progressbar"
      :aria-valuenow="progress.percent ?? undefined"
      aria-valuemin="0"
      aria-valuemax="100"
      :aria-label="percentLabel(progress)"
    >
      <div
        class="h-full rounded-full transition-[width] duration-300"
        :class="done ? 'bg-emerald-500' : 'bg-emerald-600 dark:bg-emerald-500'"
        :style="{ width }"
      />
    </div>
    <span
      v-if="showLabel"
      class="w-12 shrink-0 text-right text-xs tabular-nums"
      :class="progress.percent === null ? 'muted' : 'font-medium'"
      >{{ percentLabel(progress) }}</span
    >
  </div>
</template>
