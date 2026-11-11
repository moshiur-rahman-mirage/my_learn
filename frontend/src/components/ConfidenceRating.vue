<script setup lang="ts">
/** 1–5 confidence picker. Clicking the current level clears the rating. */
const props = withDefaults(defineProps<{ modelValue: number | null; disabled?: boolean; compact?: boolean }>(), {
  disabled: false,
  compact: false,
})
const emit = defineEmits<{ 'update:modelValue': [level: number | null] }>()

const LEVELS = [
  { level: 1, label: 'No idea', on: 'bg-red-500 text-white border-red-500' },
  { level: 2, label: 'Shaky', on: 'bg-orange-500 text-white border-orange-500' },
  { level: 3, label: 'Okay', on: 'bg-amber-400 text-slate-900 border-amber-400' },
  { level: 4, label: 'Good', on: 'bg-lime-500 text-slate-900 border-lime-500' },
  { level: 5, label: 'Solid', on: 'bg-emerald-600 text-white border-emerald-600' },
]

function pick(level: number) {
  emit('update:modelValue', props.modelValue === level ? null : level)
}
</script>

<template>
  <div class="flex flex-wrap items-center gap-1" role="radiogroup" aria-label="Confidence">
    <button
      v-for="l in LEVELS"
      :key="l.level"
      type="button"
      role="radio"
      :aria-checked="modelValue === l.level"
      :disabled="disabled"
      class="rounded-full border text-xs font-medium transition-colors disabled:opacity-50"
      :class="[
        compact ? 'size-6' : 'px-2 py-0.5',
        modelValue === l.level ? l.on : 'border-slate-300 text-slate-600 hover:border-slate-400 dark:border-slate-700 dark:text-slate-300',
      ]"
      :title="`${l.level} – ${l.label}${modelValue === l.level ? ' (click again to clear)' : ''}`"
      @click="pick(l.level)"
    >
      {{ compact ? l.level : `${l.level} ${l.label}` }}
    </button>
  </div>
</template>
