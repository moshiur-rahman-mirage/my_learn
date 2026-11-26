import { useEffect, useState } from 'react'
import { Alert, AppState, Pressable, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { useDb } from '@/db/DbProvider'
import { usePomodoro } from '@/state/pomodoro'
import { useDataVersion } from '@/state/stores'
import { radius, space, useColors } from './theme'

/** The running focus timer, floating above every screen; logs the time when it ends. */
export function PomodoroBar() {
  const db = useDb()
  const c = useColors()
  const insets = useSafeAreaInsets()
  const running = usePomodoro((s) => s.running)
  const load = usePomodoro((s) => s.load)
  const finishIfDue = usePomodoro((s) => s.finishIfDue)
  const cancel = usePomodoro((s) => s.cancel)
  const bump = useDataVersion((s) => s.bump)
  const [now, setNow] = useState(() => Date.now())

  useEffect(() => {
    void load(db)
  }, [db, load])

  useEffect(() => {
    if (!running) return
    const check = async () => {
      setNow(Date.now())
      const done = await finishIfDue(db)
      if (done) {
        bump()
        Alert.alert('Focus session done', `${done.minutes} minutes logged on “${done.itemName}”. Take a short break.`)
      }
    }
    const timer = setInterval(() => void check(), 1000)
    const sub = AppState.addEventListener('change', (s) => s === 'active' && void check())
    return () => {
      clearInterval(timer)
      sub.remove()
    }
  }, [db, running, finishIfDue, bump])

  if (!running) return null
  const left = Math.max(0, running.endsAt - now)
  const mm = String(Math.floor(left / 60_000)).padStart(2, '0')
  const ss = String(Math.floor((left % 60_000) / 1000)).padStart(2, '0')

  return (
    <View pointerEvents="box-none" style={{ position: 'absolute', left: space.lg, right: space.lg, bottom: insets.bottom + 76 }}>
      <Pressable
        onPress={() => router.push({ pathname: '/item/[id]', params: { id: String(running.itemId) } })}
        accessibilityRole="button"
        accessibilityLabel={`Focus timer: ${mm} minutes ${ss} seconds left on ${running.itemName}`}
        style={{ flexDirection: 'row', alignItems: 'center', gap: space.md, backgroundColor: c.hero, borderRadius: radius.pill, paddingVertical: 10, paddingHorizontal: space.lg, elevation: 8 }}
      >
        <Ionicons name="timer-outline" size={20} color={c.heroAccent} />
        <Text style={{ color: c.heroText, fontWeight: '900', fontSize: 16, fontVariant: ['tabular-nums'] }}>{mm}:{ss}</Text>
        <Text style={{ color: c.heroMuted, flex: 1 }} numberOfLines={1}>{running.itemName}</Text>
        <Pressable onPress={() => void cancel(db)} hitSlop={10} accessibilityRole="button" accessibilityLabel="Stop the focus timer">
          <Ionicons name="close-circle" size={22} color={c.heroMuted} />
        </Pressable>
      </Pressable>
    </View>
  )
}
