import { reactive } from 'vue'
import { plansApi } from '../api'
import type { Plan } from '../types/api'

const state = reactive<{ plans: Plan[]; loaded: boolean }>({ plans: [], loaded: false })

/** Shared list of plans for the sidebar; views call refresh() after changing plans or progress. */
export function usePlanList() {
  async function refresh() {
    try {
      state.plans = await plansApi.list()
      state.loaded = true
    } catch {
      /* the sidebar is non-essential; views show their own errors */
    }
  }
  return { state, refresh }
}
