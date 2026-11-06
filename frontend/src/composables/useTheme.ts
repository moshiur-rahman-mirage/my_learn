import { ref, watch } from 'vue'

export type ThemeMode = 'system' | 'light' | 'dark'

const KEY = 'lt.theme'
const media = window.matchMedia('(prefers-color-scheme: dark)')

function load(): ThemeMode {
  try {
    const v = localStorage.getItem(KEY)
    return v === 'light' || v === 'dark' ? v : 'system'
  } catch {
    return 'system'
  }
}

const mode = ref<ThemeMode>(load())

function apply() {
  const dark = mode.value === 'dark' || (mode.value === 'system' && media.matches)
  document.documentElement.classList.toggle('dark', dark)
  document.documentElement.style.colorScheme = dark ? 'dark' : 'light'
}

watch(mode, (m) => {
  try {
    localStorage.setItem(KEY, m)
  } catch {
    /* storage unavailable: theme just isn't remembered */
  }
  apply()
})
media.addEventListener('change', apply)
apply()

export function useTheme() {
  const cycle = () => {
    mode.value = mode.value === 'system' ? 'light' : mode.value === 'light' ? 'dark' : 'system'
  }
  return { mode, cycle }
}
