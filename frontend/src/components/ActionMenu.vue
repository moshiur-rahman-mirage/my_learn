<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref, type Component } from 'vue'
import { EllipsisVertical } from 'lucide-vue-next'

export interface MenuAction {
  label: string
  icon?: Component
  danger?: boolean
  disabled?: boolean
  divider?: boolean
  run: () => void
}

defineProps<{ actions: MenuAction[]; label?: string }>()

const open = ref(false)
const root = ref<HTMLElement | null>(null)
const menu = ref<HTMLElement | null>(null)
const upward = ref(false)

function onDocClick(e: MouseEvent) {
  if (root.value && !root.value.contains(e.target as Node)) close()
}
function onKey(e: KeyboardEvent) {
  if (e.key === 'Escape') close()
}
async function toggle() {
  if (open.value) return close()
  open.value = true
  document.addEventListener('click', onDocClick, true)
  document.addEventListener('keydown', onKey)
  await nextTick()
  // open upwards when there isn't room below
  const rect = root.value!.getBoundingClientRect()
  upward.value = window.innerHeight - rect.bottom < (menu.value?.offsetHeight ?? 0) + 16
  menu.value?.querySelector<HTMLElement>('button:not([disabled])')?.focus()
}
function close() {
  open.value = false
  document.removeEventListener('click', onDocClick, true)
  document.removeEventListener('keydown', onKey)
}
function run(a: MenuAction) {
  close()
  a.run()
}
onBeforeUnmount(close)
</script>

<template>
  <div ref="root" class="relative">
    <button class="icon-btn" :aria-label="label ?? 'Actions'" aria-haspopup="menu" :aria-expanded="open" @click.stop="toggle">
      <EllipsisVertical class="size-4" />
    </button>
    <div
      v-if="open"
      ref="menu"
      role="menu"
      class="card absolute right-0 z-40 min-w-48 py-1 shadow-lg"
      :class="upward ? 'bottom-full mb-1' : 'top-full mt-1'"
    >
      <template v-for="a in actions" :key="a.label">
        <div v-if="a.divider" class="my-1 border-t border-slate-200 dark:border-slate-800" />
        <button
          role="menuitem"
          class="flex w-full items-center gap-2 px-3 py-1.5 text-left text-sm hover:bg-slate-100 focus:bg-slate-100 focus:outline-none disabled:opacity-40 dark:hover:bg-slate-800 dark:focus:bg-slate-800"
          :class="a.danger ? 'text-red-600 dark:text-red-400' : ''"
          :disabled="a.disabled"
          @click.stop="run(a)"
        >
          <component :is="a.icon" v-if="a.icon" class="size-4 shrink-0 opacity-70" />
          {{ a.label }}
        </button>
      </template>
    </div>
  </div>
</template>
