import type { ReactNode } from 'react'
import { Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import Svg, { Circle } from 'react-native-svg'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import type { DayActivity } from '@/domain/gamification'
import { radius, space, useColors } from './theme'

/** The daily goal as a ring, with the percentage in the middle. */
export function GoalRing({ value, goal, size = 184, children }: { value: number; goal: number; size?: number; children?: ReactNode }) {
  const c = useColors()
  const stroke = 16
  const r = (size - stroke) / 2
  const circumference = 2 * Math.PI * r
  const fraction = goal > 0 ? Math.min(1, value / goal) : 0
  return (
    <View
      style={{ width: size, height: size, alignItems: 'center', justifyContent: 'center' }}
      accessibilityRole="progressbar"
      accessibilityLabel="Daily goal"
      accessibilityValue={{ min: 0, max: goal, now: Math.min(value, goal), text: `${value} of ${goal} learning actions today` }}
    >
      <Svg width={size} height={size} style={{ position: 'absolute' }}>
        <Circle cx={size / 2} cy={size / 2} r={r} stroke={c.track} strokeWidth={stroke} fill="none" />
        {/* nothing drawn at 0%: a round line cap would still paint a dot */}
        {fraction > 0 ? <Circle
          cx={size / 2} cy={size / 2} r={r} stroke={c.primary} strokeWidth={stroke} fill="none" strokeLinecap="round"
          strokeDasharray={`${circumference} ${circumference}`} strokeDashoffset={circumference * (1 - fraction)}
          transform={`rotate(-90 ${size / 2} ${size / 2})`}
        /> : null}
      </Svg>
      {children}
    </View>
  )
}

const WEEKDAY = (day: string) => new Date(`${day}T12:00:00`).toLocaleDateString(undefined, { weekday: 'short' }).slice(0, 3)

/** The last seven days: a filled circle when the goal was reached, a partial fill otherwise. */
export function WeekRow({ week, goal }: { week: DayActivity[]; goal: number }) {
  const c = useColors()
  return (
    <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
      {week.map((d, i) => {
        const isToday = i === week.length - 1
        const reached = d.actions >= goal
        const some = d.actions > 0
        return (
          <View key={d.day} style={{ alignItems: 'center', gap: 6 }} accessible accessibilityLabel={`${WEEKDAY(d.day)}: ${d.actions} actions${reached ? ', goal reached' : ''}`}>
            <View style={{
              width: 38, height: 38, borderRadius: 12, alignItems: 'center', justifyContent: 'center',
              backgroundColor: reached ? c.primary : some ? c.chipOn : 'transparent',
              borderWidth: isToday && !reached ? 2 : reached || some ? 0 : 1, borderColor: isToday ? c.primary : c.border,
            }}>
              {reached ? <Ionicons name="checkmark" size={20} color={c.primaryText} />
                : some ? <Text style={{ color: c.primary, fontWeight: '800', fontSize: 13 }}>{d.actions}</Text> : null}
            </View>
            <Text style={{ color: isToday ? c.primary : c.muted, fontSize: 11, fontWeight: isToday ? '800' : '500' }}>{WEEKDAY(d.day)}</Text>
          </View>
        )
      })}
    </View>
  )
}

/** Simple vertical bars for a week of counts. */
export function BarChart({ data, height = 120 }: { data: DayActivity[]; height?: number }) {
  const c = useColors()
  const max = Math.max(1, ...data.map((d) => d.actions))
  return (
    <View style={{ gap: space.xs }}>
      <View style={{ height, flexDirection: 'row', alignItems: 'flex-end', gap: space.sm }}>
        {data.map((d, i) => (
          <View key={d.day} style={{ flex: 1, alignItems: 'center', justifyContent: 'flex-end', height }}
            accessible accessibilityLabel={`${WEEKDAY(d.day)}: ${d.actions}`}>
            {d.actions > 0 ? <Text style={{ color: c.muted, fontSize: 10, marginBottom: 2 }}>{d.actions}</Text> : null}
            <View style={{
              width: '62%', height: Math.max(4, (d.actions / max) * (height - 18)), borderRadius: 8,
              backgroundColor: i === data.length - 1 ? c.primary : d.actions > 0 ? c.done : c.track, opacity: i === data.length - 1 ? 1 : 0.55,
            }} />
          </View>
        ))}
      </View>
      <View style={{ flexDirection: 'row', gap: space.sm }}>
        {data.map((d) => (
          <Text key={d.day} style={{ flex: 1, textAlign: 'center', color: c.faint, fontSize: 11 }}>{WEEKDAY(d.day)}</Text>
        ))}
      </View>
    </View>
  )
}

export function StatTile({ icon, value, label, tint }: { icon: keyof typeof Ionicons.glyphMap; value: string; label: string; tint?: string }) {
  const c = useColors()
  return (
    <View style={{ flex: 1, alignItems: 'center', gap: 6, paddingVertical: space.md, borderRadius: radius.md, backgroundColor: c.bg }}
      accessible accessibilityLabel={`${value} ${label}`}>
      <View style={{ width: 36, height: 36, borderRadius: 18, backgroundColor: c.chipOn, alignItems: 'center', justifyContent: 'center' }}>
        <Ionicons name={icon} size={18} color={tint ?? c.primary} />
      </View>
      <Text style={{ color: c.text, fontWeight: '800', fontSize: 17 }}>{value}</Text>
      <Text style={{ color: c.muted, fontSize: 11, textAlign: 'center' }}>{label}</Text>
    </View>
  )
}

/** A level badge with its XP bar. */
export function LevelBar({ level, title, into, span }: { level: number; title: string; into: number; span: number }) {
  const c = useColors()
  return (
    <View style={{ gap: 6 }} accessible accessibilityLabel={`Level ${level}, ${title}. ${into} of ${span} XP to the next level`}>
      <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm }}>
        <View style={{ backgroundColor: c.primary, borderRadius: radius.pill, paddingHorizontal: 10, paddingVertical: 3 }}>
          <Text style={{ color: c.primaryText, fontWeight: '800', fontSize: 12 }}>LV {level}</Text>
        </View>
        <Text style={{ color: c.text, fontWeight: '700', flex: 1 }}>{title}</Text>
        <Text style={{ color: c.muted, fontSize: 12 }}>{into} / {span} XP</Text>
      </View>
      <View style={{ height: 8, borderRadius: 4, backgroundColor: c.track, overflow: 'hidden' }}>
        <View style={{ height: 8, width: `${Math.round((into / Math.max(1, span)) * 100)}%`, backgroundColor: c.primary, borderRadius: 4 }} />
      </View>
    </View>
  )
}

/** A solid strip behind the status bar, for screens that draw under it, so scrolled content doesn't run under the clock. */
export function StatusBarShade() {
  const c = useColors()
  const insets = useSafeAreaInsets()
  return <View pointerEvents="none" style={{ position: 'absolute', top: 0, left: 0, right: 0, height: insets.top, backgroundColor: c.bg }} />
}
