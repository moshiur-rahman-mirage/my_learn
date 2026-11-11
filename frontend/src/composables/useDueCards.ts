import { ref } from 'vue'
import { flashcardsApi } from '../api'

const count = ref(0)

/** Number of flashcards due, for the badge on "Flashcards" in the sidebar. */
export function useDueCards() {
  async function refresh() {
    try {
      count.value = (await flashcardsApi.due()).length
    } catch {
      /* the badge is non-essential */
    }
  }
  return { count, refresh, set: (n: number) => (count.value = n) }
}
