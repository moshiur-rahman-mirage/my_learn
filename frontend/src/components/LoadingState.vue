<script setup lang="ts">
import { LoaderCircle } from 'lucide-vue-next'

defineProps<{ text?: string }>()
</script>

<template>
  <div class="muted flex items-center justify-center gap-2 py-16 text-sm" role="status">
    <LoaderCircle class="size-5 animate-spin" /> {{ text ?? 'Loading…' }}
  </div>
</template>
