<script setup lang="ts">
import type { Component } from 'vue'

defineProps<{ icon: Component; title: string; text?: string }>()
</script>

<template>
  <div class="card flex flex-col items-center px-6 py-12 text-center">
    <component :is="icon" class="mb-3 size-10 text-slate-300 dark:text-slate-600" />
    <h3 class="font-semibold">{{ title }}</h3>
    <p v-if="text" class="muted mt-1 max-w-md text-sm">{{ text }}</p>
    <div class="mt-4 flex flex-wrap justify-center gap-2"><slot /></div>
  </div>
</template>
