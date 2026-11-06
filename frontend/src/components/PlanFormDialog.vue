<script setup lang="ts">
import { ref, watch } from 'vue'
import BaseModal from './BaseModal.vue'
import type { Plan } from '../types/api'

const props = defineProps<{ open: boolean; plan?: Plan | null; busy?: boolean }>()
const emit = defineEmits<{ close: []; submit: [name: string, description: string | null] }>()

const name = ref('')
const description = ref('')

watch(
  () => props.open,
  (open) => {
    if (open) {
      name.value = props.plan?.name ?? ''
      description.value = props.plan?.description ?? ''
    }
  },
)

function submit() {
  if (!name.value.trim()) return
  emit('submit', name.value.trim(), description.value.trim() || null)
}
</script>

<template>
  <BaseModal :open="open" :title="plan ? 'Edit plan' : 'New learning plan'" @close="emit('close')">
    <form class="space-y-4" @submit.prevent="submit">
      <div>
        <label class="label" for="plan-name">Name</label>
        <input id="plan-name" v-model="name" class="input" maxlength="200" required placeholder="e.g. Spring Boot Mastery" />
      </div>
      <div>
        <label class="label" for="plan-desc">Description</label>
        <textarea id="plan-desc" v-model="description" class="input" rows="3" maxlength="4000" />
      </div>
      <div class="flex justify-end gap-2">
        <button type="button" class="btn-secondary" @click="emit('close')">Cancel</button>
        <button type="submit" class="btn-primary" :disabled="busy || !name.trim()">{{ plan ? 'Save' : 'Create plan' }}</button>
      </div>
    </form>
  </BaseModal>
</template>
