<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { X } from 'lucide-vue-next'

const props = withDefaults(defineProps<{ open: boolean; title: string; width?: string }>(), { width: 'max-w-lg' })
const emit = defineEmits<{ close: [] }>()
const panel = ref<HTMLElement | null>(null)

function onKey(e: KeyboardEvent) {
  if (e.key === 'Escape') emit('close')
}

watch(
  () => props.open,
  async (open) => {
    if (open) {
      document.addEventListener('keydown', onKey)
      await nextTick()
      panel.value?.querySelector<HTMLElement>('input, textarea, select, button[data-autofocus]')?.focus()
    } else {
      document.removeEventListener('keydown', onKey)
    }
  },
  { immediate: true },
)
onBeforeUnmount(() => document.removeEventListener('keydown', onKey))
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto p-4 pt-[10vh]">
      <div class="fixed inset-0 bg-slate-900/50" @click="emit('close')" />
      <div
        ref="panel"
        role="dialog"
        aria-modal="true"
        :aria-label="title"
        class="card relative w-full p-5 shadow-xl"
        :class="width"
      >
        <div class="mb-4 flex items-start justify-between gap-4">
          <h2 class="text-base font-semibold">{{ title }}</h2>
          <button class="icon-btn -mt-1 -mr-1" aria-label="Close" @click="emit('close')"><X class="size-4" /></button>
        </div>
        <slot />
      </div>
    </div>
  </Teleport>
</template>
