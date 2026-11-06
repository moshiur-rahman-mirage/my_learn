import { ref } from 'vue'

/**
 * Expanded/collapsed state of a plan's tree, remembered per plan in localStorage so it
 * survives reloads and navigation. `null` = never visited (caller decides the default).
 */
export function useExpanded(planId: number) {
  const key = `lt.expanded.${planId}`
  const expanded = ref<Set<number>>(new Set())
  let firstVisit = true

  try {
    const raw = localStorage.getItem(key)
    if (raw) {
      expanded.value = new Set(JSON.parse(raw) as number[])
      firstVisit = false
    }
  } catch {
    /* ignore unreadable storage */
  }

  function save() {
    try {
      localStorage.setItem(key, JSON.stringify([...expanded.value]))
    } catch {
      /* storage unavailable */
    }
  }

  function set(ids: Iterable<number>) {
    expanded.value = new Set(ids)
    save()
  }

  return {
    expanded,
    isFirstVisit: () => firstVisit,
    isExpanded: (id: number) => expanded.value.has(id),
    toggle(id: number) {
      const next = new Set(expanded.value)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      set(next)
    },
    expand(id: number) {
      if (!expanded.value.has(id)) set([...expanded.value, id])
    },
    set,
  }
}
