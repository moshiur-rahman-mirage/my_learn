import { reactive } from 'vue'

export interface Toast {
  id: number
  kind: 'success' | 'error' | 'info'
  message: string
}

const toasts = reactive<Toast[]>([])
let nextId = 1

function push(kind: Toast['kind'], message: string) {
  const id = nextId++
  toasts.push({ id, kind, message })
  setTimeout(() => dismiss(id), kind === 'error' ? 7000 : 3500)
}

function dismiss(id: number) {
  const i = toasts.findIndex((t) => t.id === id)
  if (i >= 0) toasts.splice(i, 1)
}

/** App-wide notifications (rendered once by ToastHost in App.vue). */
export function useToast() {
  return {
    toasts,
    dismiss,
    success: (m: string) => push('success', m),
    error: (m: string) => push('error', m),
    info: (m: string) => push('info', m),
  }
}
