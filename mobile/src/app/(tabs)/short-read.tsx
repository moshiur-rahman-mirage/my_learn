import { useEffect, useRef, useState } from 'react'
import { Pressable, ScrollView, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { loadUnreadShortReads } from '@/data/shortRead'
import { recommendRandomShortReads, type ShortReadCandidate } from '@/domain/shortRead'
import { useQuery } from '@/state/useQuery'
import { Button, Card, EmptyState, ErrorText, Loading } from '@/ui/components'
import { radius, space, useColors } from '@/ui/theme'

const openReader = (id: number) => router.push({ pathname: '/read/[id]', params: { id: String(id) } })

const TIME_CHOICES = [
  { minutes: 5, label: 'Quick read' },
  { minutes: 10, label: 'Focused read' },
  { minutes: 15, label: 'Deep read' },
] as const

export default function ShortReadScreen() {
  const c = useColors()
  const [minutes, setMinutes] = useState(10)
  const [refreshVersion, setRefreshVersion] = useState(0)
  const [recommendations, setRecommendations] = useState<ShortReadCandidate[]>([])
  const firstItemId = useRef<number | null>(null)
  const { data, error } = useQuery(loadUnreadShortReads, 'short-read')

  useEffect(() => {
    if (!data) return
    const next = recommendRandomShortReads(data, minutes, firstItemId.current)
    firstItemId.current = next[0]?.itemId ?? null
    setRecommendations(next)
  }, [data, minutes, refreshVersion])

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  const totalMinutes = recommendations.reduce((total, item) => total + item.minutes, 0)

  return (
    <ScrollView
      style={{ flex: 1, backgroundColor: c.bg }}
      contentContainerStyle={{ padding: space.lg, gap: space.lg, paddingBottom: space.xl * 2 }}
    >
      <View style={{ gap: space.xs }}>
        <Text style={{ color: c.text, fontSize: 22, fontWeight: '800' }}>I have some free time...</Text>
        <Text style={{ color: c.muted, fontSize: 14 }}>Choose a time and get a random set of unread topics that fits.</Text>
      </View>

      <View style={{ flexDirection: 'row', gap: space.sm }} accessibilityRole="radiogroup" accessibilityLabel="Reading time">
        {TIME_CHOICES.map((choice) => {
          const selected = minutes === choice.minutes
          return (
            <Pressable key={choice.minutes} onPress={() => setMinutes(choice.minutes)} accessibilityRole="radio"
              accessibilityState={{ checked: selected }}
              style={{
                flex: 1, minHeight: 76, alignItems: 'center', justifyContent: 'center', gap: 2,
                borderRadius: radius.md, borderWidth: 1, borderColor: selected ? c.primary : c.border,
                backgroundColor: selected ? c.chipOn : c.card,
              }}>
              <Text style={{ color: selected ? c.primary : c.text, fontSize: 20, fontWeight: '900' }}>{choice.minutes} min</Text>
              <Text style={{ color: c.muted, fontSize: 10, textAlign: 'center' }}>{choice.label}</Text>
            </Pressable>
          )
        })}
      </View>

      <View style={{ gap: space.md }}>
        <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
          <Text style={{ color: c.text, fontSize: 18, fontWeight: '800' }}>Recommended for {minutes} minutes</Text>
          <Pressable onPress={() => setRefreshVersion((version) => version + 1)} disabled={data.length === 0}
            accessibilityRole="button" accessibilityLabel="Refresh recommendations"
            style={{ flexDirection: 'row', alignItems: 'center', gap: space.xs, padding: space.sm, opacity: data.length === 0 ? 0.45 : 1 }}>
            <Ionicons name="refresh" size={18} color={c.primary} />
            <Text style={{ color: c.primary, fontWeight: '700' }}>Refresh</Text>
          </Pressable>
        </View>

        {recommendations.length === 0 ? (
          <Card style={{ padding: space.lg }}>
            <EmptyState
              title={data.length === 0 ? 'No unread notes in included plans' : `No note fits in ${minutes} minutes`}
              message={data.length === 0
                ? 'Mark a note as unread by editing it, or enable Short Read for another plan.'
                : 'Try another time option or choose a plan with shorter notes.'}
              action={data.length === 0 ? <Button title="Browse plans" kind="secondary" onPress={() => router.push('/plans')} /> : undefined}
            />
          </Card>
        ) : (
          <>
            <Text style={{ color: c.muted, fontSize: 13 }}>{totalMinutes} of {minutes} minutes planned</Text>
            {recommendations.map((item) => (
              <Card key={item.itemId} style={{ gap: space.sm, padding: space.md }}>
                <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Text style={{ color: c.primary, fontSize: 11, fontWeight: '800', flex: 1 }} numberOfLines={1}>
                    {[item.planName, item.parentName].filter(Boolean).join(' › ')}
                  </Text>
                  <Text style={{ color: c.muted, fontSize: 11, fontWeight: '700' }}>UNREAD</Text>
                </View>
                <Text style={{ color: c.text, fontSize: 16, fontWeight: '800' }} numberOfLines={2}>{item.name}</Text>
                <Text style={{ color: c.muted, fontSize: 12 }}>
                  ~{item.minutes} min read · {item.wordCount.toLocaleString()} words
                </Text>
                <Button title="Start reading" kind="secondary" onPress={() => openReader(item.itemId)}
                  icon={<Ionicons name="arrow-forward" size={16} color={c.text} />} />
              </Card>
            ))}
          </>
        )}
      </View>
      <ErrorText message={error} />
    </ScrollView>
  )
}
