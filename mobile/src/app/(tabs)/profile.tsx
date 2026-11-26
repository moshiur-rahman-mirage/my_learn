import { Pressable, ScrollView, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { loadStats } from '@/data/stats'
import type { Badge } from '@/domain/gamification'
import { readConfig } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { usePrefs } from '@/state/prefs'
import { useQuery } from '@/state/useQuery'
import { Card, ErrorText, Loading } from '@/ui/components'
import { LevelBar, StatusBarShade } from '@/ui/dashboard'
import { radius, space, useColors } from '@/ui/theme'

function BadgeTile({ badge }: { badge: Badge }) {
  const c = useColors()
  return (
    <View style={{ width: '31%', alignItems: 'center', gap: 6, paddingVertical: space.md, paddingHorizontal: 4, borderRadius: radius.md, backgroundColor: badge.earned ? c.chipOn : c.bg, opacity: badge.earned ? 1 : 0.75 }}
      accessible accessibilityLabel={`${badge.title}: ${badge.description}. ${badge.earned ? 'Earned' : `${Math.round(badge.progress * 100)} percent`}`}>
      <View style={{ width: 52, height: 52, borderRadius: 26, alignItems: 'center', justifyContent: 'center', backgroundColor: badge.earned ? c.primary : c.track }}>
        <Ionicons name={(badge.earned ? badge.icon : 'lock-closed-outline') as keyof typeof Ionicons.glyphMap} size={24} color={badge.earned ? c.primaryText : c.faint} />
      </View>
      <Text style={{ color: c.text, fontWeight: '700', fontSize: 12, textAlign: 'center' }} numberOfLines={2}>{badge.title}</Text>
      {badge.earned ? null : (
        <View style={{ width: '70%', height: 4, borderRadius: 2, backgroundColor: c.track, overflow: 'hidden' }}>
          <View style={{ width: `${Math.round(badge.progress * 100)}%`, height: 4, backgroundColor: c.primary }} />
        </View>
      )}
    </View>
  )
}

function Row({ icon, label, detail, onPress }: { icon: keyof typeof Ionicons.glyphMap; label: string; detail?: string; onPress: () => void }) {
  const c = useColors()
  return (
    <Pressable onPress={onPress} accessibilityRole="button" style={({ pressed }) => ({ flexDirection: 'row', alignItems: 'center', gap: space.md, minHeight: 52, opacity: pressed ? 0.6 : 1 })}>
      <View style={{ width: 36, height: 36, borderRadius: 12, backgroundColor: c.chipOn, alignItems: 'center', justifyContent: 'center' }}>
        <Ionicons name={icon} size={18} color={c.primary} />
      </View>
      <Text style={{ color: c.text, flex: 1, fontWeight: '600', fontSize: 15 }}>{label}</Text>
      {detail ? <Text style={{ color: c.muted, fontSize: 13 }}>{detail}</Text> : null}
      <Ionicons name="chevron-forward" size={18} color={c.faint} />
    </Pressable>
  )
}

/** Your level, lifetime numbers and badges; the way to Settings and Sync. */
export default function ProfileScreen() {
  const c = useColors()
  const insets = useSafeAreaInsets()
  const goal = usePrefs((s) => s.dailyGoal)
  const syncError = useSync((s) => s.lastError)
  const { data, error } = useQuery(async (d) => ({ stats: await loadStats(d, goal), cfg: await readConfig(d) }), `profile:${goal}`)

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  const { stats: s, cfg } = data
  const earned = s.badges.filter((b) => b.earned).length
  const numbers: { icon: keyof typeof Ionicons.glyphMap; value: number; label: string }[] = [
    { icon: 'checkmark-done', value: s.totals.topicsDone, label: 'topics done' },
    { icon: 'create', value: s.totals.notes, label: 'notes' },
    { icon: 'flame', value: s.totals.bestStreak, label: 'best streak' },
    { icon: 'albums', value: s.totals.cardStages, label: 'card stages' },
    { icon: 'refresh-circle', value: s.totals.reviewStages, label: 'reviews passed' },
    { icon: 'ribbon', value: s.totals.plansFinished, label: 'plans finished' },
  ]

  return (
    <View style={{ flex: 1 }}>
      <ScrollView contentContainerStyle={{ padding: space.lg, paddingTop: insets.top + space.md, gap: space.lg, paddingBottom: space.xl * 2 }}>
        <Text style={{ color: c.text, fontSize: 28, fontWeight: '800' }}>Profile</Text>

        <Card style={{ gap: space.lg, alignItems: 'stretch' }}>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.lg }}>
            <View style={{ width: 76, height: 76, borderRadius: 38, backgroundColor: c.primary, alignItems: 'center', justifyContent: 'center' }}>
              <Text style={{ color: c.primaryText, fontSize: 30, fontWeight: '900' }}>{s.level.level}</Text>
            </View>
            <View style={{ flex: 1, gap: 2 }}>
              <Text style={{ color: c.muted, fontSize: 12, fontWeight: '700' }}>LEVEL {s.level.level}</Text>
              <Text style={{ color: c.text, fontSize: 22, fontWeight: '800' }}>{s.level.title}</Text>
              <Text style={{ color: c.muted }}>{s.xp.toLocaleString()} XP · {earned}/{s.badges.length} badges</Text>
            </View>
          </View>
          <LevelBar level={s.level.level} title={`${s.level.nextAt - s.xp} XP to level ${s.level.level + 1}`} into={s.level.into} span={s.level.span} />
        </Card>

        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.sm }}>
          {numbers.map((n) => (
            <View key={n.label} style={{ width: '31.5%', backgroundColor: c.card, borderRadius: radius.md, borderWidth: 1, borderColor: c.border, padding: space.md, gap: 4 }}
              accessible accessibilityLabel={`${n.value} ${n.label}`}>
              <Ionicons name={n.icon} size={18} color={c.primary} />
              <Text style={{ color: c.text, fontSize: 20, fontWeight: '900' }}>{n.value.toLocaleString()}</Text>
              <Text style={{ color: c.muted, fontSize: 11 }}>{n.label}</Text>
            </View>
          ))}
        </View>

        <View style={{ gap: space.sm }}>
          <Text style={{ color: c.text, fontSize: 18, fontWeight: '800' }}>Badges</Text>
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.sm, justifyContent: 'space-between' }}>
            {s.badges.map((b) => <BadgeTile key={b.id} badge={b} />)}
          </View>
        </View>

        <Card style={{ paddingVertical: space.sm, gap: 0 }}>
          <Row icon="settings-outline" label="Settings" detail="Theme, reading font" onPress={() => router.push('/settings')} />
          <Row icon={syncError ? 'alert-circle-outline' : 'sync'} label="Sync with computer" detail={cfg.token ? (syncError ? 'Last sync failed' : 'Paired') : 'Not paired'}
            onPress={() => router.push('/sync')} />
        </Card>
        <Text style={{ color: c.faint, fontSize: 12, textAlign: 'center' }}>
          XP: 20 per topic you complete, 15 per note, 10 per review passed, 5 per flashcard stage.
        </Text>
      </ScrollView>
      <StatusBarShade />
    </View>
  )
}
