<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import {
  CheckCheck,
  GraduationCap,
  Keyboard,
  LayoutDashboard,
  ListChecks,
  Menu,
  Monitor,
  Moon,
  Search,
  Settings,
  Square,
  Sun,
  Sunrise,
  Timer,
  Upload,
  X,
} from 'lucide-vue-next'
import { usePlanList } from './composables/usePlanList'
import { useTheme } from './composables/useTheme'
import { usePomodoro } from './composables/usePomodoro'
import { useDueReviews } from './composables/useDueReviews'
import { percentLabel } from './lib/format'
import ToastHost from './components/ToastHost.vue'
import ConfirmDialog from './components/ConfirmDialog.vue'
import ShortcutsDialog from './components/ShortcutsDialog.vue'

const route = useRoute()
const router = useRouter()
const { state: planList, refresh } = usePlanList()
const { mode, cycle } = useTheme()
const pomodoro = usePomodoro()
const dueReviews = useDueReviews()
const mobileOpen = ref(false)
const shortcutsOpen = ref(false)

const nav = [
  { to: '/today', label: 'Today', icon: Sunrise, exact: true },
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, exact: true },
  { to: '/plans', label: 'Plans', icon: ListChecks, exact: false },
  { to: '/search', label: 'Search', icon: Search, exact: true },
  { to: '/import', label: 'Import', icon: Upload, exact: false },
  { to: '/settings', label: 'Settings', icon: Settings, exact: false },
]

// --- global keyboard shortcuts ------------------------------------------------------------

let pendingG = false
let gTimer: ReturnType<typeof setTimeout> | undefined

function typingIn(t: EventTarget | null): boolean {
  const el = t as HTMLElement | null
  return !!el?.closest?.('input, textarea, select, [contenteditable="true"]')
}

function onGlobalKey(e: KeyboardEvent) {
  if (e.ctrlKey || e.metaKey || e.altKey || typingIn(e.target)) return
  if (document.querySelector('[aria-modal="true"]')) return // a dialog is open
  if (pendingG) {
    pendingG = false
    clearTimeout(gTimer)
    const to = { t: '/today', d: '/', p: '/plans', s: '/search' }[e.key]
    if (to) {
      e.preventDefault()
      void router.push(to)
    }
    return
  }
  if (e.key === '/') {
    e.preventDefault()
    const field = document.querySelector<HTMLInputElement>('[data-shortcut-search]')
    if (field) field.focus()
    else void router.push('/search')
  } else if (e.key === '?') {
    e.preventDefault()
    shortcutsOpen.value = true
  } else if (e.key === 'g') {
    pendingG = true
    gTimer = setTimeout(() => (pendingG = false), 1200)
  }
}
onMounted(() => document.addEventListener('keydown', onGlobalKey))
onBeforeUnmount(() => document.removeEventListener('keydown', onGlobalKey))
const activePlans = computed(() => planList.plans.filter((p) => p.status === 'ACTIVE'))
const themeIcon = computed(() => (mode.value === 'dark' ? Moon : mode.value === 'light' ? Sun : Monitor))

function isActive(item: { to: string; exact: boolean }) {
  return item.exact ? route.path === item.to : route.path.startsWith(item.to) && !(item.to === '/plans' && route.params.id)
}

watch(
  () => route.fullPath,
  () => {
    mobileOpen.value = false
    void dueReviews.refresh() // reviews become due at midnight and change as you tick topics
  },
)
onMounted(refresh)
</script>

<template>
  <div class="min-h-screen md:flex">
    <!-- mobile top bar -->
    <header class="sticky top-0 z-30 flex items-center gap-2 border-b border-slate-200 bg-white/90 px-4 py-2 backdrop-blur md:hidden dark:border-slate-800 dark:bg-slate-900/90">
      <button class="icon-btn" aria-label="Open menu" @click="mobileOpen = true"><Menu class="size-5" /></button>
      <GraduationCap class="size-5 text-emerald-600" />
      <span class="font-semibold">Learning Tracker</span>
    </header>

    <div v-if="mobileOpen" class="fixed inset-0 z-40 bg-slate-900/50 md:hidden" @click="mobileOpen = false" />

    <!-- sidebar -->
    <aside
      class="fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r border-slate-200 bg-white transition-transform md:sticky md:top-0 md:h-screen md:translate-x-0 dark:border-slate-800 dark:bg-slate-900"
      :class="mobileOpen ? 'translate-x-0' : '-translate-x-full'"
    >
      <div class="flex items-center gap-2 px-4 py-4">
        <GraduationCap class="size-6 text-emerald-600" />
        <span class="flex-1 font-semibold">Learning Tracker</span>
        <button class="icon-btn md:hidden" aria-label="Close menu" @click="mobileOpen = false"><X class="size-4" /></button>
      </div>

      <nav class="space-y-0.5 px-2" aria-label="Main">
        <RouterLink
          v-for="item in nav"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-2.5 rounded-lg px-3 py-2 text-sm font-medium"
          :class="isActive(item)
            ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300'
            : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'"
        >
          <component :is="item.icon" class="size-4" /> <span class="flex-1">{{ item.label }}</span>
          <span
            v-if="item.to === '/today' && dueReviews.count.value > 0"
            class="rounded-full bg-violet-600 px-1.5 text-[11px] font-semibold text-white tabular-nums"
            :title="`${dueReviews.count.value} review(s) due`"
            >{{ dueReviews.count.value }}</span
          >
        </RouterLink>
      </nav>

      <!-- running Pomodoro -->
      <div v-if="pomodoro.running.value" class="mx-2 mt-4 rounded-lg border border-emerald-300 bg-emerald-50 p-2.5 text-sm dark:border-emerald-800 dark:bg-emerald-500/10">
        <div class="flex items-center gap-2">
          <Timer class="size-4 shrink-0 text-emerald-600" />
          <span class="flex-1 font-mono text-lg font-semibold tabular-nums">{{ pomodoro.remainingLabel.value }}</span>
          <button class="icon-btn" title="Finish now and log the full time" aria-label="Finish now" @click="pomodoro.finishNow()"><CheckCheck class="size-4" /></button>
          <button class="icon-btn" title="Stop without logging" aria-label="Stop the Pomodoro" @click="pomodoro.cancel()"><Square class="size-4" /></button>
        </div>
        <RouterLink
          :to="{ path: `/plans/${pomodoro.running.value.planId}`, query: { item: pomodoro.running.value.itemId } }"
          class="muted block truncate text-xs hover:underline"
          :title="pomodoro.running.value.itemName"
          >{{ pomodoro.running.value.itemName }}</RouterLink
        >
      </div>

      <div class="mt-6 min-h-0 flex-1 overflow-y-auto px-2">
        <p class="muted px-3 pb-1 text-[11px] font-semibold tracking-wide uppercase">Active plans</p>
        <p v-if="planList.loaded && activePlans.length === 0" class="muted px-3 text-xs">None yet</p>
        <RouterLink
          v-for="p in activePlans"
          :key="p.id"
          :to="`/plans/${p.id}`"
          class="flex items-center gap-2 rounded-lg px-3 py-1.5 text-sm"
          :class="route.params.id === String(p.id)
            ? 'bg-slate-100 font-medium dark:bg-slate-800'
            : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'"
        >
          <span class="flex-1 truncate">{{ p.name }}</span>
          <span class="muted shrink-0 text-xs tabular-nums">{{ percentLabel(p.progress) }}</span>
        </RouterLink>
      </div>

      <div class="border-t border-slate-200 p-2 dark:border-slate-800">
        <button class="btn-ghost w-full justify-start" :title="`Theme: ${mode} (click to change)`" @click="cycle">
          <component :is="themeIcon" class="size-4" /> <span class="capitalize">{{ mode }} theme</span>
        </button>
        <button class="btn-ghost w-full justify-start" title="Keyboard shortcuts (?)" @click="shortcutsOpen = true">
          <Keyboard class="size-4" /> Shortcuts <kbd class="muted ml-auto text-xs">?</kbd>
        </button>
      </div>
    </aside>

    <main class="min-w-0 flex-1 px-4 py-6 sm:px-6 lg:px-10">
      <div class="mx-auto max-w-6xl">
        <RouterView />
      </div>
    </main>
  </div>

  <ToastHost />
  <ConfirmDialog />
  <ShortcutsDialog :open="shortcutsOpen" @close="shortcutsOpen = false" />
</template>
