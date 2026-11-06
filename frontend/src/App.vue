<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { GraduationCap, LayoutDashboard, ListChecks, Menu, Monitor, Moon, Settings, Sun, Upload, X } from 'lucide-vue-next'
import { usePlanList } from './composables/usePlanList'
import { useTheme } from './composables/useTheme'
import { percentLabel } from './lib/format'
import ToastHost from './components/ToastHost.vue'
import ConfirmDialog from './components/ConfirmDialog.vue'

const route = useRoute()
const { state: planList, refresh } = usePlanList()
const { mode, cycle } = useTheme()
const mobileOpen = ref(false)

const nav = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, exact: true },
  { to: '/plans', label: 'Plans', icon: ListChecks, exact: false },
  { to: '/import', label: 'Import', icon: Upload, exact: false },
  { to: '/settings', label: 'Settings', icon: Settings, exact: false },
]
const activePlans = computed(() => planList.plans.filter((p) => p.status === 'ACTIVE'))
const themeIcon = computed(() => (mode.value === 'dark' ? Moon : mode.value === 'light' ? Sun : Monitor))

function isActive(item: { to: string; exact: boolean }) {
  return item.exact ? route.path === item.to : route.path.startsWith(item.to) && !(item.to === '/plans' && route.params.id)
}

watch(() => route.fullPath, () => (mobileOpen.value = false))
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
          <component :is="item.icon" class="size-4" /> {{ item.label }}
        </RouterLink>
      </nav>

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
</template>
