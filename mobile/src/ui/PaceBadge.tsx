import Ionicons from '@expo/vector-icons/Ionicons'
import { Text, View, useColorScheme } from 'react-native'
import type { Pace } from '@/domain/pace'
import { space, useColors } from './theme'

/** "11 Dec 2026" from yyyy-MM-dd, in the phone's language. */
function dayLabel(day: string): string {
  const [y, m, d] = day.split('-').map(Number)
  return new Date(y, m - 1, d).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' })
}

/** Target-date status of a plan (port of the desktop's PaceBadge): a pill, plus the numbers when `detailed`. */
export function PaceBadge({ pace, detailed }: { pace: Pace | null; detailed?: boolean }) {
  const c = useColors()
  const dark = useColorScheme() === 'dark'
  if (!pace) return null
  const look = {
    ON_TRACK: { text: 'On track', bg: dark ? '#064e3b' : '#d1fae5', fg: dark ? '#6ee7b7' : '#065f46' },
    DONE: { text: 'Done', bg: dark ? '#064e3b' : '#d1fae5', fg: dark ? '#6ee7b7' : '#065f46' },
    BEHIND: { text: 'Behind', bg: dark ? '#78350f' : '#fef3c7', fg: dark ? '#fcd34d' : '#92400e' },
    OVERDUE: { text: 'Overdue', bg: dark ? '#7f1d1d' : '#fee2e2', fg: dark ? '#fca5a5' : '#991b1b' },
    NO_TOPICS: { text: 'No topics', bg: c.track, fg: c.text },
  }[pace.status]
  const days =
    pace.status === 'DONE' ? `target ${dayLabel(pace.targetDate)}`
      : pace.daysLeft === 0 ? `was due ${dayLabel(pace.targetDate)}`
        : pace.daysLeft === 1 ? 'due today' : `${pace.daysLeft} days left`
  const details: string[] = []
  if (detailed && pace.status !== 'DONE' && pace.status !== 'NO_TOPICS') {
    if (pace.perWeekNeeded !== null) details.push(`need ${pace.perWeekNeeded}/week`)
    details.push(`your pace ${pace.perWeekRecent}/week (last 4 weeks)`)
    if (pace.projectedFinish) details.push(`at this pace done by ${dayLabel(pace.projectedFinish)}`)
  }
  return (
    <View style={{ flexDirection: 'row', flexWrap: 'wrap', alignItems: 'center', gap: space.xs }} accessibilityLabel={`${look.text}, ${days}`}>
      <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4, backgroundColor: look.bg, borderRadius: 999, paddingHorizontal: 8, paddingVertical: 2 }}>
        <Ionicons name="calendar-outline" size={12} color={look.fg} />
        <Text style={{ color: look.fg, fontSize: 12, fontWeight: '600' }}>{look.text}</Text>
      </View>
      <Text style={{ color: c.muted, fontSize: 12 }}>{days}</Text>
      {details.map((d) => (
        <Text key={d} style={{ color: c.muted, fontSize: 12 }}>· {d}</Text>
      ))}
    </View>
  )
}
