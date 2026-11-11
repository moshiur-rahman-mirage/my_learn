import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: DashboardView, meta: { title: 'Dashboard' } },
    { path: '/today', name: 'today', component: () => import('../views/TodayView.vue'), meta: { title: 'Today' } },
    { path: '/flashcards', name: 'flashcards', component: () => import('../views/FlashcardsView.vue'), meta: { title: 'Flashcards' } },
    { path: '/search', name: 'search', component: () => import('../views/SearchView.vue'), meta: { title: 'Search' } },
    { path: '/plans/:id(\\d+)/sync', name: 'sync', component: () => import('../views/SyncView.vue'), props: true, meta: { title: 'Sync' } },
    { path: '/plans', name: 'plans', component: () => import('../views/PlansView.vue'), meta: { title: 'Plans' } },
    { path: '/plans/:id(\\d+)', name: 'plan', component: () => import('../views/PlanDetailView.vue'), props: true, meta: { title: 'Plan' } },
    { path: '/import', name: 'import', component: () => import('../views/ImportView.vue'), meta: { title: 'Import' } },
    { path: '/settings', name: 'settings', component: () => import('../views/SettingsView.vue'), meta: { title: 'Settings' } },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.afterEach((to) => {
  document.title = `${(to.meta.title as string) ?? ''} · Learning Tracker`
})
