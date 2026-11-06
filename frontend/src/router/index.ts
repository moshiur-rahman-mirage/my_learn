import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: DashboardView, meta: { title: 'Dashboard' } },
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
