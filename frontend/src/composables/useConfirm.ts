import { reactive } from 'vue'

export interface ConfirmOptions {
  title: string
  message: string
  confirmLabel?: string
  danger?: boolean
  /** If set, the user must type this exact text to enable the confirm button. */
  requireText?: string
}

interface ConfirmState extends ConfirmOptions {
  open: boolean
  resolve: ((ok: boolean) => void) | null
}

const state = reactive<ConfirmState>({ open: false, title: '', message: '', resolve: null })

/** Promise-based confirmation dialog: `if (await confirm({...})) { ... }`. */
export function useConfirm() {
  function confirm(options: ConfirmOptions): Promise<boolean> {
    state.resolve?.(false)
    Object.assign(state, { confirmLabel: undefined, danger: false, requireText: undefined }, options, { open: true })
    return new Promise((resolve) => {
      state.resolve = resolve
    })
  }

  function close(ok: boolean) {
    state.open = false
    state.resolve?.(ok)
    state.resolve = null
  }

  return { state, confirm, close }
}
