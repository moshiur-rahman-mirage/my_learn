import type { ReactNode } from 'react'
import { ActivityIndicator, Pressable, Text, View, type StyleProp, type ViewStyle } from 'react-native'
import { percentLabel } from '@/domain/progress'
import type { Priority, Progress } from '@/domain/types'
import { radius, space, useColors } from './theme'

/** Thin progress bar; an N/A subtree shows a dashed-looking empty track, like the desktop. */
export function ProgressBar({ progress, height = 6 }: { progress: Progress; height?: number }) {
  const c = useColors()
  const pct = progress.percent ?? 0
  return (
    <View
      accessibilityRole="progressbar"
      accessibilityValue={{ min: 0, max: 100, now: progress.percent ?? undefined, text: percentLabel(progress) }}
      style={{ height, borderRadius: height / 2, backgroundColor: c.track, overflow: 'hidden', opacity: progress.percent === null ? 0.5 : 1 }}
    >
      <View style={{ width: `${pct}%`, height: '100%', backgroundColor: c.done }} />
    </View>
  )
}

export function PriorityBadge({ priority }: { priority: Priority | null }) {
  const c = useColors()
  if (!priority) return null
  const color = priority === 'MUST' ? c.must : priority === 'SHOULD' ? c.should : c.nice
  return (
    <Text style={{ fontSize: 10, fontWeight: '700', color, backgroundColor: c.chipOn, borderRadius: radius.pill, paddingHorizontal: 8, paddingVertical: 3 }}>
      {priority === 'MUST' ? 'Must' : priority === 'SHOULD' ? 'Should' : 'Nice'}
    </Text>
  )
}

export function Button({
  title, onPress, kind = 'primary', disabled, busy, icon, style,
}: {
  title: string
  onPress: () => void
  kind?: 'primary' | 'secondary' | 'danger'
  disabled?: boolean
  busy?: boolean
  icon?: ReactNode
  style?: StyleProp<ViewStyle>
}) {
  const c = useColors()
  const bg = kind === 'primary' ? c.primary : 'transparent'
  const fg = kind === 'primary' ? c.primaryText : kind === 'danger' ? c.danger : c.text
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityState={{ disabled: disabled || busy, busy }}
      disabled={disabled || busy}
      onPress={onPress}
      style={({ pressed }) => [
        {
          flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: space.sm,
          paddingVertical: space.md, paddingHorizontal: space.lg, borderRadius: radius.md, minHeight: 52,
          backgroundColor: bg, borderWidth: kind === 'primary' ? 0 : 1, borderColor: kind === 'danger' ? c.danger : c.border,
          opacity: disabled ? 0.5 : pressed ? 0.8 : 1,
        },
        style,
      ]}
    >
      {busy ? <ActivityIndicator color={fg} /> : icon}
      <Text style={{ color: fg, fontWeight: '600', fontSize: 16 }}>{title}</Text>
    </Pressable>
  )
}

export function Chip({ label, on, onPress }: { label: string; on: boolean; onPress: () => void }) {
  const c = useColors()
  return (
    <Pressable
      accessibilityRole="radio"
      accessibilityState={{ checked: on }}
      onPress={onPress}
      hitSlop={6}
      style={{
        paddingVertical: 8, paddingHorizontal: 14, borderRadius: radius.pill, borderWidth: 1,
        borderColor: on ? c.primary : c.border, backgroundColor: on ? c.chipOn : 'transparent',
      }}
    >
      <Text style={{ color: c.text, fontSize: 13, fontWeight: on ? '600' : '400' }}>{label}</Text>
    </Pressable>
  )
}

export function Card({ children, style }: { children: ReactNode; style?: StyleProp<ViewStyle> }) {
  const c = useColors()
  return (
    <View style={[{
      backgroundColor: c.card,
      borderColor: c.border,
      borderWidth: 1,
      borderRadius: radius.card,
      padding: space.lg,
      shadowColor: '#29213d',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.06,
      shadowRadius: 12,
      elevation: 2,
    }, style]}>
      {children}
    </View>
  )
}

export function EmptyState({ title, message, action }: { title: string; message?: string; action?: ReactNode }) {
  const c = useColors()
  return (
    <View style={{ alignItems: 'center', justifyContent: 'center', padding: space.xl, gap: space.sm }}>
      <Text style={{ color: c.text, fontSize: 17, fontWeight: '600', textAlign: 'center' }}>{title}</Text>
      {message ? <Text style={{ color: c.muted, textAlign: 'center' }}>{message}</Text> : null}
      {action}
    </View>
  )
}

export function Loading() {
  const c = useColors()
  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <ActivityIndicator color={c.primary} />
    </View>
  )
}

export function ErrorText({ message }: { message: string | null }) {
  const c = useColors()
  if (!message) return null
  return <Text selectable style={{ color: c.danger }}>{message}</Text>
}
