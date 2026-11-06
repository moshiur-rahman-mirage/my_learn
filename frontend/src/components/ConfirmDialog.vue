<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import BaseModal from './BaseModal.vue'
import { useConfirm } from '../composables/useConfirm'

const { state, close } = useConfirm()
const typed = ref('')
watch(
  () => state.open,
  () => (typed.value = ''),
)
const canConfirm = computed(() => !state.requireText || typed.value.trim() === state.requireText.trim())
</script>

<template>
  <BaseModal :open="state.open" :title="state.title" width="max-w-md" @close="close(false)">
    <p class="text-sm whitespace-pre-line text-slate-600 dark:text-slate-300">{{ state.message }}</p>
    <div v-if="state.requireText" class="mt-4">
      <label class="label" for="confirm-text">Type <strong>{{ state.requireText }}</strong> to confirm</label>
      <input id="confirm-text" v-model="typed" class="input" autocomplete="off" @keydown.enter="canConfirm && close(true)" />
    </div>
    <div class="mt-5 flex justify-end gap-2">
      <button class="btn-secondary" data-autofocus @click="close(false)">Cancel</button>
      <button :class="state.danger ? 'btn-danger' : 'btn-primary'" :disabled="!canConfirm" @click="close(true)">
        {{ state.confirmLabel ?? 'Confirm' }}
      </button>
    </div>
  </BaseModal>
</template>
