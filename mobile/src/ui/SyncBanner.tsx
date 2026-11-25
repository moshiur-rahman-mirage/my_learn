import { ActivityIndicator, Text, View } from 'react-native'
import { useSync } from '@/sync/useSync'
import { radius, space, useColors } from './theme'

/** "Syncing… 2,000 received" while a sync runs, so growing totals make sense. */
export function SyncBanner() {
  const c = useColors()
  const running = useSync((s) => s.running)
  const progress = useSync((s) => s.progress)
  if (!running) return null
  const received = progress?.received ?? 0
  return (
    <View
      accessibilityLiveRegion="polite"
      style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm, padding: space.md, borderRadius: radius.md, backgroundColor: c.chipOn }}
    >
      <ActivityIndicator color={c.primary} />
      <Text style={{ color: c.text, flex: 1 }}>
        {received === 0 ? 'Syncing with your computer…' : `Syncing… ${received.toLocaleString()} changes received${progress?.more ? ', more coming' : ''}`}
      </Text>
    </View>
  )
}
