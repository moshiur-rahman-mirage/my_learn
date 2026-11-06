<script setup lang="ts">
import { computed, ref } from 'vue'
import { labelIndexes, niceMax, ticks, useElementWidth } from './chartUtils'

/**
 * Single-series line chart (2px line, recessive grid) with a crosshair + tooltip on hover.
 * Points with y = null are gaps (e.g. days before anything existed).
 */
export interface LinePoint {
  label: string
  y: number | null
  /** tooltip lines; the first is shown as the title */
  tooltip: string[]
}

const props = withDefaults(
  defineProps<{ points: LinePoint[]; height?: number; yMax?: number; yFormat?: (v: number) => string; label: string }>(),
  { height: 220, yMax: undefined, yFormat: (v: number) => String(Math.round(v)) },
)

const root = ref<HTMLElement | null>(null)
const width = useElementWidth(root)
const pad = { top: 12, right: 12, bottom: 26, left: 40 }
const hover = ref<number | null>(null)

const innerW = computed(() => Math.max(10, width.value - pad.left - pad.right))
const innerH = computed(() => props.height - pad.top - pad.bottom)
const max = computed(() => props.yMax ?? niceMax(Math.max(0, ...props.points.map((p) => p.y ?? 0))))
const yTicks = computed(() => ticks(max.value))

function x(i: number) {
  const n = props.points.length
  return pad.left + (n <= 1 ? innerW.value / 2 : (i / (n - 1)) * innerW.value)
}
function y(v: number) {
  return pad.top + innerH.value - (v / max.value) * innerH.value
}

/** Path with gaps where y is null. */
const path = computed(() => {
  let d = ''
  let pen = false
  props.points.forEach((p, i) => {
    if (p.y === null) {
      pen = false
      return
    }
    d += `${pen ? 'L' : 'M'}${x(i).toFixed(1)},${y(p.y).toFixed(1)}`
    pen = true
  })
  return d
})
const lastIndex = computed(() => {
  for (let i = props.points.length - 1; i >= 0; i--) if (props.points[i]!.y !== null) return i
  return -1
})
const xLabels = computed(() => labelIndexes(props.points.length, Math.max(2, Math.floor(innerW.value / 90))))

function onMove(e: PointerEvent) {
  const rect = (e.currentTarget as SVGElement).getBoundingClientRect()
  const px = e.clientX - rect.left
  const n = props.points.length
  if (n === 0) return
  const i = n === 1 ? 0 : Math.round(((px - pad.left) / innerW.value) * (n - 1))
  hover.value = Math.min(n - 1, Math.max(0, i))
}

const tip = computed(() => {
  if (hover.value === null) return null
  const p = props.points[hover.value]!
  const left = x(hover.value)
  return { p, left, alignRight: left > width.value * 0.6 }
})
</script>

<template>
  <div ref="root" class="relative w-full select-none">
    <svg
      :width="width"
      :height="height"
      role="img"
      :aria-label="label"
      class="block touch-none"
      @pointermove="onMove"
      @pointerdown="onMove"
      @pointerleave="hover = null"
    >
      <!-- grid + y axis -->
      <g>
        <template v-for="t in yTicks" :key="t">
          <line :x1="pad.left" :x2="width - pad.right" :y1="y(t)" :y2="y(t)" class="chart-grid" :class="t === 0 ? 'chart-baseline' : ''" />
          <text :x="pad.left - 6" :y="y(t)" dy="0.32em" text-anchor="end" class="chart-axis-text">{{ yFormat(t) }}</text>
        </template>
      </g>
      <!-- x labels -->
      <text
        v-for="i in xLabels"
        :key="i"
        :x="x(i)"
        :y="height - 6"
        :text-anchor="i === 0 ? 'start' : i === points.length - 1 ? 'end' : 'middle'"
        class="chart-axis-text"
      >
        {{ points[i]?.label }}
      </text>

      <path :d="path" class="chart-line" />
      <!-- end marker: latest value -->
      <circle v-if="lastIndex >= 0" :cx="x(lastIndex)" :cy="y(points[lastIndex]!.y!)" r="4" class="chart-dot" />

      <!-- crosshair -->
      <g v-if="tip && tip.p.y !== null">
        <line :x1="tip.left" :x2="tip.left" :y1="pad.top" :y2="pad.top + innerH" class="chart-crosshair" />
        <circle :cx="tip.left" :cy="y(tip.p.y)" r="5" class="chart-dot" />
      </g>
    </svg>

    <div
      v-if="tip"
      class="chart-tooltip"
      :style="{ left: `${tip.left}px`, top: '4px', transform: tip.alignRight ? 'translateX(calc(-100% - 10px))' : 'translateX(10px)' }"
    >
      <p class="font-medium">{{ tip.p.tooltip[0] }}</p>
      <p v-for="(line, i) in tip.p.tooltip.slice(1)" :key="i" class="muted">{{ line }}</p>
    </div>
  </div>
</template>
