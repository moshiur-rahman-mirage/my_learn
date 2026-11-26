import { useState } from 'react'
import { FlatList, Pressable, RefreshControl, Text, TextInput, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { listPlans, overallProgress } from '@/data/plans'
import { percentLabel, remaining } from '@/domain/progress'
import type { Plan, PlanStatus } from '@/domain/types'
import { readConfig } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { useQuery } from '@/state/useQuery'
import { Button, Chip, EmptyState, ErrorText, Loading, ProgressBar } from '@/ui/components'
import { PaceBadge } from '@/ui/PaceBadge'
import { PlanCover } from '@/ui/PlanCover'
import { SyncBanner } from '@/ui/SyncBanner'
import { radius, space, useColors } from '@/ui/theme'

const FILTERS: { label: string; value: PlanStatus | null }[] = [
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Archived', value: 'ARCHIVED' },
  { label: 'All', value: null },
]

/** The library: every plan as a "book" with its cover, progress and pace. */
export default function PlansScreen() {
  const db = useDb()
  const c = useColors()
  const [status, setStatus] = useState<PlanStatus | null>('ACTIVE')
  const [search, setSearch] = useState('')
  const { data, error, reload } = useQuery(
    async (d) => ({ plans: await listPlans(d, { status, search }), overall: await overallProgress(d), paired: !!(await readConfig(d)).token }),
    `status:${status}:${search}`,
  )
  const syncing = useSync((s) => s.running)
  const runSync = useSync((s) => s.run)

  if (!data) return error ? <ErrorText message={error} /> : <Loading />

  return (
    <FlatList
      data={data.plans}
      keyExtractor={(p) => String(p.id)}
      contentContainerStyle={{ padding: space.lg, paddingTop: space.sm, gap: space.md, flexGrow: 1, paddingBottom: space.xl * 2 }}
      keyboardShouldPersistTaps="handled"
      refreshControl={<RefreshControl refreshing={syncing} onRefresh={() => void (data.paired ? runSync(db) : reload())} colors={[c.primary]} />}
      ListHeaderComponent={
        <View style={{ gap: space.md, marginBottom: space.xs }}>
          <SyncBanner />
          {/* overall */}
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.lg, padding: space.lg, borderRadius: radius.card, backgroundColor: c.hero }}>
            <View style={{ flex: 1, gap: 6 }}>
              <Text style={{ color: c.heroAccent, fontSize: 12, fontWeight: '800', letterSpacing: 0.6 }}>ALL ACTIVE PLANS</Text>
              <Text style={{ color: c.heroText, fontSize: 22, fontWeight: '800' }}>{data.overall.completed} of {data.overall.total} topics</Text>
              <View style={{ height: 6, borderRadius: 3, backgroundColor: '#ffffff22', overflow: 'hidden' }}>
                <View style={{ height: 6, width: `${data.overall.percent ?? 0}%`, backgroundColor: c.heroAccent }} />
              </View>
            </View>
            <Text style={{ color: c.heroText, fontSize: 28, fontWeight: '900' }}>{percentLabel(data.overall)}</Text>
          </View>
          {/* search + filters */}
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm, backgroundColor: c.card, borderRadius: radius.pill, borderWidth: 1, borderColor: c.border, paddingHorizontal: space.lg }}>
            <Ionicons name="search" size={18} color={c.faint} />
            <TextInput value={search} onChangeText={setSearch} placeholder="Find a plan" placeholderTextColor={c.faint}
              style={{ flex: 1, color: c.text, paddingVertical: 12, fontSize: 15 }} accessibilityLabel="Find a plan" autoCorrect={false} />
            {search ? (
              <Pressable onPress={() => setSearch('')} hitSlop={10} accessibilityRole="button" accessibilityLabel="Clear">
                <Ionicons name="close-circle" size={18} color={c.faint} />
              </Pressable>
            ) : null}
          </View>
          <View style={{ flexDirection: 'row', gap: space.sm }} accessibilityRole="radiogroup">
            {FILTERS.map((f) => (
              <Chip key={f.label} label={f.label} on={status === f.value} onPress={() => setStatus(f.value)} />
            ))}
          </View>
        </View>
      }
      ListEmptyComponent={
        syncing ? (
          <EmptyState title="Downloading your plans…" message="They appear here as they arrive. The first sync can take a minute." />
        ) : search ? (
          <EmptyState title="No plan matches" message={`Nothing named or described like “${search}”.`} />
        ) : data.paired ? (
          <EmptyState title="No plans here" message="Pull down to sync with your computer, or tap + to start one." />
        ) : (
          <EmptyState
            title="No plans on this phone yet"
            message="Pair with the Learning Tracker on your computer to bring your plans and notes here, then read them offline."
            action={<Button title="Pair with computer" onPress={() => router.push('/pair')} />}
          />
        )
      }
      renderItem={({ item }) => <PlanRow plan={item} />}
    />
  )
}

function PlanRow({ plan }: { plan: Plan }) {
  const c = useColors()
  const left = remaining(plan.progress)
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${plan.name}, ${percentLabel(plan.progress)} done, ${left} topics left`}
      onPress={() => router.push({ pathname: '/plan/[id]', params: { id: String(plan.id) } })}
      style={({ pressed }) => ({
        flexDirection: 'row', gap: space.lg, alignItems: 'center', backgroundColor: c.card, borderColor: c.border, borderWidth: 1,
        borderRadius: radius.card, padding: space.md, opacity: pressed ? 0.85 : 1,
      })}
    >
      <PlanCover name={plan.name} />
      <View style={{ flex: 1, gap: 6 }}>
        <Text style={{ color: c.text, fontSize: 17, fontWeight: '700', fontFamily: 'serif' }} numberOfLines={2}>{plan.name}</Text>
        {plan.description ? <Text style={{ color: c.muted, fontSize: 12 }} numberOfLines={1}>{plan.description}</Text> : null}
        <ProgressBar progress={plan.progress} height={5} />
        <Text style={{ color: c.muted, fontSize: 12 }}>
          <Text style={{ color: c.primary, fontWeight: '800' }}>{percentLabel(plan.progress)}</Text>
          {plan.progress.total > 0 ? ` · ${left === 0 ? 'all done' : `${left} topic${left === 1 ? '' : 's'} left`}` : ' · no topics yet'}
          {plan.status === 'ARCHIVED' ? ' · archived' : ''}
        </Text>
        <PaceBadge pace={plan.pace} />
      </View>
    </Pressable>
  )
}
