<script setup lang="ts">
import { computed, ref } from 'vue'
import { labelIndexes, niceMax, ticks, useElementWidth } from './chartUtils'

/**
 * Single-series column chart: bars at most 24px wide with a 4px rounded top, square at the
 * baseline, at least a 2px gap between bars; per-bar hover tooltip over the whole column band.
 */
export interface Bar {
  label: string
  value: number
  tooltip: string[]
}

const props = withDefaults(defineProps<{ bars: Bar[]; height?: number; label: string }>(), { height: 200 })

const root = ref<HTMLElement | null>(null)
const width = useElementWidth(root)
const pad = { top: 12, right: 12, bottom: 26, left: 40 }
const hover = ref<number | null>(null)

const innerW = computed(() => Math.max(10, width.value - pad.left - pad.right))
const innerH = computed(() => props.height - pad.top - pad.bottom)
// counts: whole-number steps (0, 2, 4, 6, 8 rather than 0, 1.25, 2.5…)
const step = computed(() => niceMax(Math.ceil(Math.max(1, ...props.bars.map((b) => b.value)) / 4)))
const max = computed(() => step.value * 4)
const yTicks = computed(() => ticks(max.value, 4))
const band = computed(() => innerW.value / Math.max(1, props.bars.length))
const barW = computed(() => Math.max(1, Math.min(24, band.value - 2, band.value * 0.7)))

function y(v: number) {
  return pad.top + innerH.value - (v / max.value) * innerH.value
}
function bandX(i: number) {
  return pad.left + i * band.value
}

/** Column with rounded top corners only. */
function barPath(i: number, v: number): string {
  const w = barW.value
  const x0 = bandX(i) + (band.value - w) / 2
  const base = pad.top + innerH.value
  const top = y(v)
  const h = base - top
  const r = Math.min(4, w / 2, h)
  return `M${x0},${base}V${top + r}Q${x0},${top} ${x0 + r},${top}H${x0 + w - r}Q${x0 + w},${top} ${x0 + w},${top + r}V${base}Z`
}

const xLabels = computed(() => labelIndexes(props.bars.length, Math.max(2, Math.floor(innerW.value / 90))))
const tip = computed(() => {
  if (hover.value === null) return null
  const b = props.bars[hover.value]!
  const center = bandX(hover.value) + band.value / 2
  return { b, center, alignRight: center > width.value * 0.6 }
})
</script>

<template>
  <div ref="root" class="relative w-full select-none">
    <svg :width="width" :height="height" role="img" :aria-label="label" class="block" @pointerleave="hover = null">
      <template v-for="t in yTicks" :key="t">
        <line :x1="pad.left" :x2="width - pad.right" :y1="y(t)" :y2="y(t)" class="chart-grid" :class="t === 0 ? 'chart-baseline' : ''" />
        <text :x="pad.left - 6" :y="y(t)" dy="0.32em" text-anchor="end" class="chart-axis-text">{{ Math.round(t) }}</text>
      </template>
      <text
        v-for="i in xLabels"
        :key="'l' + i"
        :x="bandX(i) + band / 2"
        :y="height - 6"
        :text-anchor="i === 0 && bars.length > 1 ? 'start' : i === bars.length - 1 && bars.length > 1 ? 'end' : 'middle'"
        class="chart-axis-text"
      >
        {{ bars[i]?.label }}
      </text>

      <template v-for="(b, i) in bars" :key="i">
        <path v-if="b.value > 0" :d="barPath(i, b.value)" class="chart-bar" :class="hover !== null && hover !== i ? 'opacity-60' : ''" />
      </template>
      <!-- hit targets: the whole band, taller than the bar -->
      <rect
        v-for="(_, i) in bars"
        :key="'h' + i"
        :x="bandX(i)"
        :y="pad.top"
        :width="band"
        :height="innerH"
        fill="transparent"
        @pointerenter="hover = i"
        @pointerdown="hover = i"
      />
    </svg>

    <div
      v-if="tip"
      class="chart-tooltip"
      :style="{ left: `${tip.center}px`, top: '4px', transform: tip.alignRight ? 'translateX(calc(-100% - 10px))' : 'translateX(10px)' }"
    >
      <p class="font-medium">{{ tip.b.tooltip[0] }}</p>
      <p v-for="(line, i) in tip.b.tooltip.slice(1)" :key="i" class="muted">{{ line }}</p>
    </div>
  </div>
</template>
