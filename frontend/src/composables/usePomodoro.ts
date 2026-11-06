import { computed, reactive } from 'vue'
import { timeApi } from '../api'
import { errorMessage } from '../api/client'
import { useToast } from './useToast'

/**
 * One app-wide Pomodoro timer. When it finishes, the minutes are logged on the item it was started
 * for. The end time is remembered in localStorage, so a page reload doesn't lose a running timer.
 */
interface Running {
  itemId: number
  itemName: string
  planId: number
  minutes: number
  /** epoch ms */
  endsAt: number
}

const STORE_KEY = 'lt.pomodoro'
const state = reactive<{ running: Running | null; now: number; listeners: Set<(itemId: number) => void> }>({
  running: read(),
  now: Date.now(),
  listeners: new Set(),
})
let ticker: ReturnType<typeof setInterval> | undefined

function read(): Running | null {
  try {
    const raw = localStorage.getItem(STORE_KEY)
    return raw ? (JSON.parse(raw) as Running) : null
  } catch {
    return null
  }
}

function write(r: Running | null) {
  try {
    if (r) localStorage.setItem(STORE_KEY, JSON.stringify(r))
    else localStorage.removeItem(STORE_KEY)
  } catch {
    /* private mode: the timer still works until the page is reloaded */
  }
}

function ensureTicker() {
  if (ticker || !state.running) return
  ticker = setInterval(() => {
    state.now = Date.now()
    if (state.running && state.now >= state.running.endsAt) void finish()
  }, 1000)
}

async function finish() {
  const r = state.running
  if (!r) return
  stopTicker()
  state.running = null
  write(null)
  const toast = useToast()
  try {
    await timeApi.log(r.itemId, r.minutes)
    toast.success(`Pomodoro done: ${r.minutes} min logged on “${r.itemName}”. Take a short break!`)
    state.listeners.forEach((fn) => fn(r.itemId))
  } catch (e) {
    toast.error(`Pomodoro finished, but the time wasn't saved: ${errorMessage(e)}`)
  }
  chime()
  notify(r)
}

function stopTicker() {
  clearInterval(ticker)
  ticker = undefined
}

function chime() {
  try {
    const ctx = new AudioContext()
    ;[0, 0.25, 0.5].forEach((t) => {
      const osc = ctx.createOscillator()
      const gain = ctx.createGain()
      osc.frequency.value = 880
      gain.gain.setValueAtTime(0.15, ctx.currentTime + t)
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + t + 0.2)
      osc.connect(gain).connect(ctx.destination)
      osc.start(ctx.currentTime + t)
      osc.stop(ctx.currentTime + t + 0.2)
    })
  } catch {
    /* no audio: the toast is enough */
  }
}

function notify(r: Running) {
  try {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('Pomodoro done', { body: `${r.minutes} min on “${r.itemName}” logged. Time for a break.` })
    }
  } catch {
    /* notifications unavailable */
  }
}

// a timer left running in an earlier session: finish it now if its time is up, else keep ticking
if (state.running) {
  if (Date.now() >= state.running.endsAt) void finish()
  else ensureTicker()
}

export function usePomodoro() {
  const remaining = computed(() => (state.running ? Math.max(0, state.running.endsAt - state.now) : 0))
  const remainingLabel = computed(() => {
    const s = Math.ceil(remaining.value / 1000)
    return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`
  })

  function start(item: { id: number; name: string }, planId: number, minutes = 25) {
    if ('Notification' in window && Notification.permission === 'default') {
      void Notification.requestPermission().catch(() => undefined)
    }
    state.now = Date.now()
    state.running = { itemId: item.id, itemName: item.name, planId, minutes, endsAt: state.now + minutes * 60_000 }
    write(state.running)
    ensureTicker()
  }

  /** Stops without logging time. */
  function cancel() {
    stopTicker()
    state.running = null
    write(null)
  }

  /** Called with the item id whenever a finished Pomodoro has been logged. */
  function onLogged(fn: (itemId: number) => void) {
    state.listeners.add(fn)
    return () => state.listeners.delete(fn)
  }

  return {
    running: computed(() => state.running),
    remaining,
    remainingLabel,
    start,
    cancel,
    finishNow: finish,
    onLogged,
  }
}
