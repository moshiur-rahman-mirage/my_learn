<script setup lang="ts">
import BaseModal from './BaseModal.vue'

defineProps<{ open: boolean }>()
const emit = defineEmits<{ close: [] }>()

const groups: { title: string; keys: [string, string][] }[] = [
  {
    title: 'Anywhere',
    keys: [
      ['/', 'Search (filters the tree on a plan page)'],
      ['g then t', 'Go to Today'],
      ['g then d', 'Go to Dashboard'],
      ['g then p', 'Go to Plans'],
      ['g then s', 'Go to Search'],
      ['?', 'Show this list'],
      ['Esc', 'Close a dialog or panel'],
    ],
  },
  {
    title: 'On a plan page',
    keys: [
      ['n', 'New topic at the top level'],
      ['m', 'New module at the top level'],
    ],
  },
  {
    title: 'In the tree (click a row or Tab to it first)',
    keys: [
      ['↑ / ↓', 'Previous / next row'],
      ['→ / ←', 'Expand / collapse (or go to the parent)'],
      ['Space or x', 'Tick or untick a topic'],
      ['Enter', 'Open details & notes'],
      ['e or F2', 'Edit'],
      ['n', 'Add a topic inside'],
      ['Delete', 'Delete'],
    ],
  },
  {
    title: 'In notes',
    keys: [['Ctrl+S', 'Save now (it also saves automatically)']],
  },
]
</script>

<template>
  <BaseModal :open="open" title="Keyboard shortcuts" width="max-w-xl" @close="emit('close')">
    <div class="grid gap-5 sm:grid-cols-2">
      <section v-for="g in groups" :key="g.title">
        <h3 class="muted mb-1.5 text-xs font-semibold tracking-wide uppercase">{{ g.title }}</h3>
        <dl class="space-y-1 text-sm">
          <div v-for="[k, what] in g.keys" :key="k" class="flex items-baseline gap-3">
            <dt class="w-24 shrink-0">
              <kbd class="rounded border border-slate-300 bg-slate-50 px-1.5 py-0.5 font-mono text-xs dark:border-slate-700 dark:bg-slate-800">{{ k }}</kbd>
            </dt>
            <dd>{{ what }}</dd>
          </div>
        </dl>
      </section>
    </div>
  </BaseModal>
</template>
