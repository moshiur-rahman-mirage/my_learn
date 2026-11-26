import { useState } from 'react'
import { Pressable, RefreshControl, ScrollView, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { useDb } from '@/db/DbProvider'
import { getItemDetail } from '@/data/itemDetail'
import { setCompletion } from '@/data/items'
import { lastReadItem } from '@/data/reading'
import { reviewTopic } from '@/data/reviews'
import { loadStats } from '@/data/stats'
import { loadToday } from '@/data/today'
import { encouragement } from '@/domain/gamification'
import { readConfig } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { usePrefs } from '@/state/prefs'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { Button, Card, ErrorText, Loading, PriorityBadge } from '@/ui/components'
import { BarChart, GoalRing, LevelBar, StatTile, WeekRow, StatusBarShade } from '@/ui/dashboard'
import { SyncBanner } from '@/ui/SyncBanner'
import { radius, space, useColors } from '@/ui/theme'

const openItem = (id: number) => router.push({ pathname: '/item/[id]', params: { id: String(id) } })
const openReader = (id: number) => router.push({ pathname: '/read/[id]', params: { id: String(id) } })

function greeting(now = new Date()): string {
  const h = now.getHours()
  return h < 5 ? 'Up late' : h < 12 ? 'Good morning' : h < 18 ? 'Good afternoon' : 'Good evening'
}

function SectionTitle({ title, action }: { title: string; action?: { label: string; onPress: () => void } }) {
  const c = useColors()
  return (
    <View style={{ flexDirection: 'row', alignItems: 'baseline', justifyContent: 'space-between' }}>
      <Text style={{ color: c.text, fontSize: 18, fontWeight: '800' }}>{title}</Text>
      {action ? (
        <Pressable onPress={action.onPress} hitSlop={8} accessibilityRole="button">
          <Text style={{ color: c.primary, fontWeight: '700' }}>{action.label}</Text>
        </Pressable>
      ) : null}
    </View>
  )
}

/** Home: daily progress, what to continue, what's due, and the week at a glance. */
export default function HomeScreen() {
  const db = useDb()
  const c = useColors()
  const insets = useSafeAreaInsets()
  const bump = useDataVersion((s) => s.bump)
  const goal = usePrefs((s) => s.dailyGoal)
  const syncing = useSync((s) => s.running)
  const runSync = useSync((s) => s.run)
  const [busy, setBusy] = useState<number | null>(null)
  const [allReviews, setAllReviews] = useState(false)
  const { data, error, reload } = useQuery(async (d) => {
    const lastId = await lastReadItem(d)
    return {
      today: await loadToday(d),
      stats: await loadStats(d, goal),
      last: lastId ? await getItemDetail(d, lastId) : null,
      paired: !!(await readConfig(d)).token,
    }
  }, `home:${goal}`)

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  const { today: t, stats: s, last } = data
  const cheer = encouragement(s.actionsToday, s.goal, s.totals.currentStreak)
  const pct = Math.min(100, Math.round((s.actionsToday / Math.max(1, s.goal)) * 100))
  const firstNext = t.nextUp[0]?.topics[0]
  const empty = t.nextUp.length === 0 && t.dueReviews.length === 0 && t.dueCards === 0 && !last

  const act = async (id: number, f: () => Promise<unknown>) => {
    setBusy(id)
    try {
      await f()
      bump()
    } finally {
      setBusy(null)
    }
  }
  const startStudying = () => {
    if (t.dueCards > 0) router.push('/cards')
    else if (last?.note.content) openReader(last.item.id)
    else if (firstNext) openItem(firstNext.itemId)
    else router.push('/plans')
  }
  const reviews = allReviews ? t.dueReviews : t.dueReviews.slice(0, 3)

  return (
    <View style={{ flex: 1 }}>
      <ScrollView
        style={{ backgroundColor: c.bg }}
        contentContainerStyle={{ padding: space.lg, paddingTop: insets.top + space.md, gap: space.lg, paddingBottom: space.xl * 2 }}
        refreshControl={<RefreshControl refreshing={syncing} onRefresh={() => void (data.paired ? runSync(db) : reload())} colors={[c.primary]} />}
      >
        {/* header */}
        <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.md }}>
          <View style={{ flex: 1 }}>
            <Text style={{ color: c.muted, fontSize: 14 }}>{new Date().toLocaleDateString(undefined, { weekday: 'long', day: 'numeric', month: 'long' })}</Text>
            <Text style={{ color: c.text, fontSize: 28, fontWeight: '800' }}>{greeting()}</Text>
          </View>
          <Pressable onPress={() => router.push('/profile')} hitSlop={8} accessibilityRole="button" accessibilityLabel={`Level ${s.level.level}, open your profile`}
            style={{ width: 46, height: 46, borderRadius: 23, backgroundColor: c.primary, alignItems: 'center', justifyContent: 'center' }}>
            <Text style={{ color: c.primaryText, fontWeight: '900', fontSize: 16 }}>{s.level.level}</Text>
          </Pressable>
          <Pressable onPress={() => router.push('/settings')} hitSlop={8} accessibilityRole="button" accessibilityLabel="Settings"
            style={{ width: 46, height: 46, borderRadius: 23, backgroundColor: c.card, borderWidth: 1, borderColor: c.border, alignItems: 'center', justifyContent: 'center' }}>
            <Ionicons name="settings-outline" size={20} color={c.text} />
          </Pressable>
        </View>

        <SyncBanner />

        {/* encouragement */}
        <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.md, padding: space.lg, borderRadius: radius.card, backgroundColor: c.chipOn, borderWidth: 1, borderColor: c.primary + '55' }}>
          <View style={{ width: 48, height: 48, borderRadius: 16, backgroundColor: c.primary, alignItems: 'center', justifyContent: 'center' }}>
            <Ionicons name={s.actionsToday >= s.goal ? 'trophy' : s.totals.currentStreak > 0 ? 'flame' : 'sparkles'} size={24} color={c.primaryText} />
          </View>
          <View style={{ flex: 1 }}>
            <Text style={{ color: c.text, fontWeight: '800', fontSize: 16 }}>{cheer.title}</Text>
            <Text style={{ color: c.muted, fontSize: 13 }}>{cheer.message}</Text>
          </View>
        </View>

        {/* continue reading */}
        {last ? (
          <Pressable onPress={() => (last.note.content ? openReader(last.item.id) : openItem(last.item.id))} accessibilityRole="button"
            style={({ pressed }) => ({ borderRadius: radius.card, padding: space.lg, gap: space.sm, backgroundColor: c.hero, opacity: pressed ? 0.9 : 1 })}>
            <Text style={{ color: c.heroAccent, fontSize: 12, fontWeight: '800', letterSpacing: 0.6 }}>CONTINUE READING</Text>
            <Text style={{ color: c.heroText, fontSize: 20, fontWeight: '800' }} numberOfLines={2}>{last.item.name}</Text>
            <Text style={{ color: c.heroMuted, fontSize: 13 }} numberOfLines={1}>{[last.planName, ...last.path].join(' › ')}</Text>
            <View style={{ flexDirection: 'row', marginTop: space.xs }}>
              <View style={{ flexDirection: 'row', alignItems: 'center', gap: 6, backgroundColor: c.heroAccent, borderRadius: radius.pill, paddingHorizontal: space.lg, paddingVertical: 10 }}>
                <Ionicons name="book" size={16} color={c.hero} />
                <Text style={{ color: c.hero, fontWeight: '800' }}>{last.note.content ? 'Continue reading' : 'Open topic'}</Text>
              </View>
            </View>
          </Pressable>
        ) : null}

        {/* due today */}
        {t.dueCards > 0 || t.dueReviews.length > 0 ? (
          <View style={{ flexDirection: 'row', gap: space.md }}>
            <Pressable onPress={() => router.push('/cards')} accessibilityRole="button" style={{ flex: 1 }}>
              <Card style={{ gap: 4 }}>
                <Ionicons name="albums" size={22} color={c.primary} />
                <Text style={{ color: c.text, fontSize: 24, fontWeight: '900' }}>{t.dueCards}</Text>
                <Text style={{ color: c.muted, fontSize: 13 }}>flashcards due</Text>
              </Card>
            </Pressable>
            <View style={{ flex: 1 }}>
              <Card style={{ gap: 4 }}>
                <Ionicons name="refresh-circle" size={22} color={c.primary} />
                <Text style={{ color: c.text, fontSize: 24, fontWeight: '900' }}>{t.dueReviews.length}</Text>
                <Text style={{ color: c.muted, fontSize: 13 }}>topics to review</Text>
              </Card>
            </View>
          </View>
        ) : null}

        {t.dueReviews.length > 0 ? (
          <View style={{ gap: space.sm }}>
            <SectionTitle title="Do you still remember?" action={t.dueReviews.length > 3 ? { label: allReviews ? 'Show less' : `All ${t.dueReviews.length}`, onPress: () => setAllReviews((v) => !v) } : undefined} />
            {reviews.map((r) => (
              <Card key={r.itemId} style={{ gap: space.sm, padding: space.md }}>
                <Pressable onPress={() => openItem(r.itemId)} accessibilityRole="button">
                  <Text style={{ color: c.text, fontWeight: '700', fontSize: 15 }}>{r.name}</Text>
                  <Text style={{ color: c.muted, fontSize: 12 }}>{r.planName}{r.parentName ? ` › ${r.parentName}` : ''} · review {r.review} of {r.of}</Text>
                </Pressable>
                <View style={{ flexDirection: 'row', gap: space.sm }}>
                  <Button style={{ flex: 1, minHeight: 44 }} kind="secondary" title="Forgot" busy={busy === r.itemId} onPress={() => act(r.itemId, () => reviewTopic(db, r.itemId, false))} />
                  <Button style={{ flex: 1, minHeight: 44 }} title="Remembered" busy={busy === r.itemId} onPress={() => act(r.itemId, () => reviewTopic(db, r.itemId, true))} />
                </View>
              </Card>
            ))}
          </View>
        ) : null}

        {/* daily goal */}
        <Card style={{ gap: space.lg }}>
          <WeekRow week={s.week} goal={s.goal} />
          <View style={{ alignItems: 'center' }}>
            <GoalRing value={s.actionsToday} goal={s.goal}>
              <Text style={{ color: c.text, fontSize: 36, fontWeight: '900' }}>{pct}%</Text>
              <Text style={{ color: c.muted, fontSize: 12 }}>{s.actionsToday} of {s.goal} today</Text>
            </GoalRing>
          </View>
          <View style={{ flexDirection: 'row', gap: space.sm }}>
            <StatTile icon="flame" value={`${s.totals.currentStreak}`} label={s.totals.currentStreak === 1 ? 'day streak' : 'days streak'} tint="#f97316" />
            <StatTile icon="time-outline" value={`${t.minutesToday}′`} label="studied today" />
            <StatTile icon="star" value={`${s.xp}`} label="total XP" tint="#eab308" />
          </View>
          <LevelBar level={s.level.level} title={s.level.title} into={s.level.into} span={s.level.span} />
          <Button title={t.dueCards > 0 ? `Start studying · ${t.dueCards} cards` : 'Start studying'} onPress={startStudying}
            icon={<Ionicons name="play" size={18} color={c.primaryText} />} />
        </Card>

        {/* the week */}
        <Card style={{ gap: space.md }}>
          <SectionTitle title="Topics this week" />
          <BarChart data={s.topicsWeek} />
        </Card>

        {/* next up */}
        {t.nextUp.length > 0 ? (
          <View style={{ gap: space.sm }}>
            <SectionTitle title="Up next" action={{ label: 'All plans', onPress: () => router.push('/plans') }} />
            {t.nextUp.slice(0, 4).map((p) => {
              const topic = p.topics[0]
              return (
                <Card key={p.planId} style={{ flexDirection: 'row', alignItems: 'center', gap: space.md, padding: space.md }}>
                  <Pressable onPress={() => act(topic.itemId, () => setCompletion(db, topic.itemId, true))} disabled={busy === topic.itemId}
                    accessibilityRole="checkbox" accessibilityState={{ checked: false }} accessibilityLabel={`Mark ${topic.name} as done`}
                    style={{ width: 40, height: 40, borderRadius: 12, borderWidth: 2, borderColor: c.border, alignItems: 'center', justifyContent: 'center' }}>
                    <Ionicons name="checkmark" size={20} color={c.faint} />
                  </Pressable>
                  <Pressable onPress={() => openItem(topic.itemId)} style={{ flex: 1 }} accessibilityRole="button">
                    <Text style={{ color: c.primary, fontSize: 11, fontWeight: '800' }} numberOfLines={1}>{p.planName.toUpperCase()}</Text>
                    <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.xs, flexWrap: 'wrap' }}>
                      <Text style={{ color: c.text, fontSize: 15, fontWeight: '600', flexShrink: 1 }}>{topic.name}</Text>
                      <PriorityBadge priority={topic.priority} />
                    </View>
                  </Pressable>
                  <Ionicons name="chevron-forward" size={18} color={c.faint} />
                </Card>
              )
            })}
          </View>
        ) : null}

        {empty ? (
          <Card style={{ alignItems: 'center', gap: space.sm, padding: space.xl }}>
            <Ionicons name="cloud-download-outline" size={36} color={c.primary} />
            <Text style={{ color: c.text, fontSize: 17, fontWeight: '800' }}>{data.paired ? 'All caught up' : 'Bring your plans here'}</Text>
            <Text style={{ color: c.muted, textAlign: 'center' }}>
              {data.paired ? 'Nothing due. Pull down to sync, or open a plan.' : 'Pair with the Learning Tracker on your computer, then study offline anywhere.'}
            </Text>
            {!data.paired ? <Button title="Pair with computer" onPress={() => router.push('/pair')} /> : null}
          </Card>
        ) : null}
      </ScrollView>
      <StatusBarShade />
    </View>
  )
}
