import { useState } from 'react'
import { Pressable, RefreshControl, ScrollView, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { setCompletion } from '@/data/items'
import { reviewTopic } from '@/data/reviews'
import { loadToday } from '@/data/today'
import { readConfig } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { Button, Card, EmptyState, ErrorText, Loading, PriorityBadge } from '@/ui/components'
import { SyncBanner } from '@/ui/SyncBanner'
import { space, useColors } from '@/ui/theme'

function openItem(id: number) {
  router.push({ pathname: '/item/[id]', params: { id: String(id) } })
}

function Stat({ value, label }: { value: string; label: string }) {
  const c = useColors()
  return (
    <View style={{ flexBasis: '47%', flexGrow: 1, minHeight: 76, justifyContent: 'center', padding: space.md, gap: 4, backgroundColor: c.chipOn, borderRadius: 16 }}>
      <Text style={{ color: c.primary, fontSize: 23, fontWeight: '800' }}>{value}</Text>
      <Text style={{ color: c.muted, fontSize: 12 }}>{label}</Text>
    </View>
  )
}

export default function TodayScreen() {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const syncing = useSync((s) => s.running)
  const runSync = useSync((s) => s.run)
  const [busy, setBusy] = useState<number | null>(null)
  const { data, error, reload } = useQuery(async (d) => ({ today: await loadToday(d), paired: !!(await readConfig(d)).token }), 'today')

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  const t = data.today
  const empty = t.nextUp.length === 0 && t.dueReviews.length === 0 && t.dueCards === 0

  const review = async (itemId: number, remembered: boolean) => {
    setBusy(itemId)
    try {
      await reviewTopic(db, itemId, remembered)
      bump()
    } finally {
      setBusy(null)
    }
  }

  const tick = async (itemId: number) => {
    setBusy(itemId)
    try {
      await setCompletion(db, itemId, true)
      bump()
    } finally {
      setBusy(null)
    }
  }

  return (
    <ScrollView
      contentContainerStyle={{ padding: space.lg, gap: space.lg }}
      refreshControl={<RefreshControl refreshing={syncing} onRefresh={() => void (data.paired ? runSync(db) : reload())} colors={[c.primary]} />}
    >
      <View style={{ gap: space.xs, paddingHorizontal: space.xs }}>
        <Text style={{ color: c.text, fontSize: 27, fontWeight: '800', letterSpacing: -0.5 }}>Today</Text>
        <Text style={{ color: c.muted, fontSize: 14 }}>A little progress goes a long way.</Text>
      </View>
      <SyncBanner />
      <Card style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.sm, padding: space.md }}>
        <Stat value={`${t.currentStreak}`} label="day streak" />
        <Stat value={`${t.completedToday}`} label="done today" />
        <Stat value={`${t.completedLast7Days}`} label="last 7 days" />
        <Stat value={`${t.minutesToday}′`} label="studied today" />
      </Card>
      {t.bestStreak > t.currentStreak ? (
        <Text style={{ color: c.muted, fontSize: 12, marginTop: -space.sm, textAlign: 'center' }}>Best streak: {t.bestStreak} days</Text>
      ) : null}

      {t.dueCards > 0 ? (
        <Button
          title={`Study ${t.dueCards} flashcard${t.dueCards === 1 ? '' : 's'}`}
          onPress={() => router.push('/cards')}
          icon={<Ionicons name="albums-outline" size={18} color={c.primaryText} />}
        />
      ) : null}

      {t.dueReviews.length > 0 ? (
        <View style={{ gap: space.sm }}>
          <Text style={{ color: c.text, fontSize: 17, fontWeight: '600' }}>Review ({t.dueReviews.length})</Text>
          <Text style={{ color: c.muted, fontSize: 13 }}>Do you still remember these? Open one to reread your notes first.</Text>
          {t.dueReviews.map((r) => (
            <Card key={r.itemId} style={{ gap: space.sm, padding: space.md }}>
              <Pressable onPress={() => openItem(r.itemId)} accessibilityRole="button">
                <Text style={{ color: c.text, fontWeight: '600', fontSize: 15 }}>{r.name}</Text>
                <Text style={{ color: c.muted, fontSize: 12 }}>
                  {r.planName}{r.parentName ? ` › ${r.parentName}` : ''} · review {r.review} of {r.of}
                </Text>
              </Pressable>
              <View style={{ flexDirection: 'row', gap: space.sm }}>
                <Button style={{ flex: 1 }} kind="secondary" title="Forgot" busy={busy === r.itemId} onPress={() => review(r.itemId, false)} />
                <Button style={{ flex: 1 }} title="Remembered" busy={busy === r.itemId} onPress={() => review(r.itemId, true)} />
              </View>
            </Card>
          ))}
        </View>
      ) : null}

      {t.nextUp.length > 0 ? (
        <View style={{ gap: space.sm }}>
          <Text style={{ color: c.text, fontSize: 17, fontWeight: '600' }}>Next up</Text>
          {t.nextUp.map((p) => (
            <Card key={p.planId} style={{ padding: 0, overflow: 'hidden' }}>
              <Pressable
                onPress={() => router.push({ pathname: '/plan/[id]', params: { id: String(p.planId) } })}
                style={{ paddingHorizontal: space.md, paddingTop: space.md, paddingBottom: space.xs }}
                accessibilityRole="button"
              >
                <Text style={{ color: c.muted, fontSize: 12, fontWeight: '600' }}>{p.planName.toUpperCase()}</Text>
              </Pressable>
              {p.topics.map((topic) => (
                <View key={topic.itemId} style={{ flexDirection: 'row', alignItems: 'center', borderTopColor: c.border, borderTopWidth: 0.5 }}>
                  <Pressable
                    onPress={() => tick(topic.itemId)}
                    disabled={busy === topic.itemId}
                    accessibilityRole="checkbox"
                    accessibilityState={{ checked: false }}
                    accessibilityLabel={`Mark ${topic.name} as done`}
                    style={{ width: 48, height: 52, alignItems: 'center', justifyContent: 'center' }}
                  >
                    <Ionicons name="square-outline" size={24} color={c.faint} />
                  </Pressable>
                  <Pressable onPress={() => openItem(topic.itemId)} style={{ flex: 1, paddingVertical: space.sm, paddingRight: space.md }} accessibilityRole="button">
                    <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.xs, flexWrap: 'wrap' }}>
                      <Text style={{ color: c.text, fontSize: 15, flexShrink: 1 }}>{topic.name}</Text>
                      <PriorityBadge priority={topic.priority} />
                    </View>
                    {topic.path.length ? <Text style={{ color: c.muted, fontSize: 12 }} numberOfLines={1}>{topic.path.join(' › ')}</Text> : null}
                  </Pressable>
                </View>
              ))}
            </Card>
          ))}
        </View>
      ) : null}

      {empty && !syncing ? (
        data.paired ? (
          <EmptyState title="All caught up" message="No reviews or cards due, and every active plan is finished. Pull down to sync." />
        ) : (
          <EmptyState
            title="Welcome"
            message="Pair with the Learning Tracker on your computer to bring your plans here. Then study offline, anywhere."
            action={<Button title="Pair with computer" onPress={() => router.push('/pair')} />}
          />
        )
      ) : null}
    </ScrollView>
  )
}
