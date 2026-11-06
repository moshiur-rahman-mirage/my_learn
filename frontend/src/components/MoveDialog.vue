<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import BaseModal from './BaseModal.vue'
import type { TreeNode } from '../types/api'
import { flatten } from '../lib/tree'

const props = defineProps<{ open: boolean; tree: TreeNode[]; item: TreeNode | null; busy?: boolean }>()
const emit = defineEmits<{ close: []; submit: [parentId: number | null] }>()

const target = ref<number | null>(null)
watch(
  () => props.open,
  (open) => {
    if (open) target.value = props.item?.parentId ?? null
  },
)
const options = computed(() => (props.item ? flatten(props.tree, props.item.id) : []))
</script>

<template>
  <BaseModal :open="open" :title="`Move “${item?.name ?? ''}”`" @close="emit('close')">
    <p class="muted mb-3 text-sm">Choose the new parent. The item moves with all its sub-items and goes to the end of the list.</p>
    <div class="max-h-80 overflow-y-auto rounded-lg border border-slate-200 dark:border-slate-800">
      <label class="flex cursor-pointer items-center gap-2 px-3 py-1.5 text-sm hover:bg-slate-50 dark:hover:bg-slate-800">
        <input v-model="target" type="radio" :value="null" /> <em>Top level of the plan</em>
      </label>
      <label
        v-for="o in options"
        :key="o.id"
        class="flex cursor-pointer items-center gap-2 py-1.5 pr-3 text-sm hover:bg-slate-50 dark:hover:bg-slate-800"
        :style="{ paddingLeft: `${0.75 + o.depth * 1.1}rem` }"
      >
        <input v-model="target" type="radio" :value="o.id" />
        <span :class="o.type === 'MODULE' ? 'font-medium' : ''">{{ o.name }}</span>
      </label>
    </div>
    <div class="mt-4 flex justify-end gap-2">
      <button class="btn-secondary" @click="emit('close')">Cancel</button>
      <button class="btn-primary" :disabled="busy || target === (item?.parentId ?? null)" @click="emit('submit', target)">Move</button>
    </div>
  </BaseModal>
</template>
