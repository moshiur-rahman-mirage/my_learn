import { ref } from 'vue'
import { reviewsApi } from '../api'

const count = ref(0)

/** Number of reviews due, for the badge on "Today" in the sidebar. */
export function useDueReviews() {
  async function refresh() {
    try {
      count.value = (await reviewsApi.due()).length
    } catch {
      /* the badge is non-essential */
    }
  }
  return { count, refresh, set: (n: number) => (count.value = n) }
}
