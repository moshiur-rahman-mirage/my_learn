<script setup lang="ts">
import { Check, FileText, Folder } from 'lucide-vue-next'
import type { PreviewNode } from '../types/api'
import PriorityBadge from './PriorityBadge.vue'

defineOptions({ name: 'PreviewTree' })
defineProps<{ nodes: PreviewNode[]; depth?: number }>()
</script>

<template>
  <ul>
    <li v-for="n in nodes" :key="n.row">
      <div class="flex items-center gap-1.5 py-0.5 text-sm" :style="{ paddingLeft: `${(depth ?? 0) * 1.1}rem` }">
        <Folder v-if="n.type === 'MODULE'" class="size-3.5 shrink-0 text-amber-500" />
        <FileText v-else class="size-3.5 shrink-0 text-slate-400" />
        <span class="truncate" :class="n.type === 'MODULE' ? 'font-medium' : ''" :title="n.description ?? n.name">{{ n.name }}</span>
        <PriorityBadge :priority="n.priority" />
        <Check v-if="n.completed" class="size-3.5 shrink-0 text-emerald-600" aria-label="completed" />
        <span class="muted ml-auto shrink-0 pl-2 text-[11px] tabular-nums">row {{ n.row }}</span>
      </div>
      <PreviewTree v-if="n.children.length" :nodes="n.children" :depth="(depth ?? 0) + 1" />
    </li>
  </ul>
</template>
