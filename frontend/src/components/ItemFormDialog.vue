<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Folder, FileText } from 'lucide-vue-next'
import BaseModal from './BaseModal.vue'
import type { ItemType, Priority, TreeNode } from '../types/api'
import { flatten } from '../lib/tree'

export interface ItemFormValue {
  name: string
  type: ItemType
  parentId: number | null
  description: string | null
  priority: Priority | null
}

const props = defineProps<{
  open: boolean
  tree: TreeNode[]
  /** Item being edited; omit to create. */
  item?: TreeNode | null
  /** Defaults for a new item. */
  defaults?: Partial<ItemFormValue>
  busy?: boolean
}>()
const emit = defineEmits<{ close: []; submit: [value: ItemFormValue] }>()

const form = ref<ItemFormValue>({ name: '', type: 'TOPIC', parentId: null, description: null, priority: null })

watch(
  () => props.open,
  (open) => {
    if (!open) return
    form.value = props.item
      ? {
          name: props.item.name,
          type: props.item.type,
          parentId: props.item.parentId,
          description: props.item.description,
          priority: props.item.priority,
        }
      : { name: '', type: 'TOPIC', parentId: null, description: null, priority: null, ...props.defaults }
  },
)

// An item can't be moved under itself or its own sub-items.
const parentOptions = computed(() => flatten(props.tree, props.item?.id))
const willLoseTick = computed(() => props.item?.completed && form.value.type === 'MODULE')

function submit() {
  if (!form.value.name.trim()) return
  emit('submit', {
    ...form.value,
    name: form.value.name.trim(),
    description: form.value.description?.trim() || null,
  })
}
</script>

<template>
  <BaseModal :open="open" :title="item ? 'Edit item' : form.type === 'MODULE' ? 'Add module' : 'Add topic'" @close="emit('close')">
    <form class="space-y-4" @submit.prevent="submit">
      <div>
        <label class="label" for="item-name">Name</label>
        <input id="item-name" v-model="form.name" class="input" maxlength="300" required />
      </div>

      <fieldset>
        <legend class="label">Type</legend>
        <div class="grid grid-cols-2 gap-2">
          <label
            v-for="t in (['MODULE', 'TOPIC'] as const)"
            :key="t"
            class="flex cursor-pointer items-start gap-2 rounded-lg border p-2.5 text-sm"
            :class="form.type === t ? 'border-emerald-600 bg-emerald-50 dark:bg-emerald-500/10' : 'border-slate-300 dark:border-slate-700'"
          >
            <input v-model="form.type" type="radio" :value="t" class="sr-only" />
            <component :is="t === 'MODULE' ? Folder : FileText" class="mt-0.5 size-4 shrink-0 text-slate-500" />
            <span>
              <span class="block font-medium">{{ t === 'MODULE' ? 'Module' : 'Topic' }}</span>
              <span class="muted text-xs">{{ t === 'MODULE' ? 'Groups modules and topics' : 'Something to learn; checkable' }}</span>
            </span>
          </label>
        </div>
        <p v-if="willLoseTick" class="mt-1.5 text-xs text-amber-700 dark:text-amber-400">
          Modules can't be ticked, so this item's completion will be cleared.
        </p>
      </fieldset>

      <div>
        <label class="label" for="item-parent">Parent</label>
        <select id="item-parent" v-model="form.parentId" class="input">
          <option :value="null">— Top level of the plan —</option>
          <option v-for="o in parentOptions" :key="o.id" :value="o.id">
            {{ '  '.repeat(o.depth) }}{{ o.type === 'MODULE' ? '▸' : '·' }} {{ o.name }}
          </option>
        </select>
      </div>

      <div>
        <label class="label" for="item-desc">What to learn / description</label>
        <textarea id="item-desc" v-model="form.description" class="input" rows="3" maxlength="4000" />
      </div>

      <div>
        <span class="label">Priority</span>
        <div class="flex flex-wrap gap-1.5">
          <button
            v-for="p in ([null, 'MUST', 'SHOULD', 'NICE'] as const)"
            :key="String(p)"
            type="button"
            class="chip"
            :class="{ 'chip-on': form.priority === p }"
            @click="form.priority = p"
          >
            {{ p === null ? 'None' : p === 'MUST' ? 'Must' : p === 'SHOULD' ? 'Should' : 'Nice' }}
          </button>
        </div>
      </div>

      <div class="flex justify-end gap-2 pt-1">
        <button type="button" class="btn-secondary" @click="emit('close')">Cancel</button>
        <button type="submit" class="btn-primary" :disabled="busy || !form.name.trim()">{{ item ? 'Save' : 'Add' }}</button>
      </div>
    </form>
  </BaseModal>
</template>
