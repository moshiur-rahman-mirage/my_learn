import { useState } from 'react'
import { FlatList, Pressable, RefreshControl, Text, View } from 'react-native'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { listPlans, overallProgress } from '@/data/plans'
import { percentLabel } from '@/domain/progress'
import type { Plan, PlanStatus } from '@/domain/types'
import { readConfig } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { useQuery } from '@/state/useQuery'
import { Button, Card, Chip, EmptyState, ErrorText, Loading, ProgressBar } from '@/ui/components'
import { SyncBanner } from '@/ui/SyncBanner'
import { PaceBadge } from '@/ui/PaceBadge'
import { space, useColors } from '@/ui/theme'

const FILTERS: { label: string; value: PlanStatus | null }[] = [
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Archived', value: 'ARCHIVED' },
  { label: 'All', value: null },
]

export default function PlansScreen() {
  const db = useDb()
  const c = useColors()
  const [status, setStatus] = useState<PlanStatus | null>('ACTIVE')
  const { data, error, reload } = useQuery(
    async (d) => ({ plans: await listPlans(d, { status }), overall: await overallProgress(d), paired: !!(await readConfig(d)).token }),
    `status:${status}`,
  )
  const syncing = useSync((s) => s.running)
  const runSync = useSync((s) => s.run)

  const refresh = async () => {
    if (data?.paired) await runSync(db)
    else await reload()
  }

  if (!data) return error ? <ErrorText message={error} /> : <Loading />

  return (
    <FlatList
      data={data.plans}
      keyExtractor={(p) => String(p.id)}
      contentContainerStyle={{ padding: space.lg, gap: space.md, flexGrow: 1 }}
      refreshControl={<RefreshControl refreshing={syncing} onRefresh={refresh} colors={[c.primary]} />}
      ListHeaderComponent={
        <View style={{ gap: space.md, marginBottom: space.sm }}>
          <SyncBanner />
          <Card style={{ gap: space.sm }}>
            <Text style={{ color: c.text, fontSize: 16, fontWeight: '700' }}>Overall (active plans)</Text>
            <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.md }}>
              <View style={{ flex: 1 }}>
                <ProgressBar progress={data.overall} height={8} />
              </View>
              <Text style={{ color: c.primary, fontWeight: '800' }}>{percentLabel(data.overall)}</Text>
            </View>
            <Text style={{ color: c.muted, fontSize: 12 }}>
              {data.overall.completed} of {data.overall.total} topics done
            </Text>
          </Card>
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
        ) : data.paired ? (
          <EmptyState title="No plans here" message="Pull down to sync with your computer." />
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
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${plan.name}, ${percentLabel(plan.progress)} done`}
      onPress={() => router.push({ pathname: '/plan/[id]', params: { id: String(plan.id) } })}
      style={({ pressed }) => ({
        backgroundColor: c.card, borderColor: c.border, borderWidth: 1, borderRadius: 20, padding: space.lg, gap: space.sm,
        shadowColor: '#29213d', shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.05, shadowRadius: 12, elevation: 2,
        opacity: pressed ? 0.8 : 1,
      })}
    >
      <View style={{ flexDirection: 'row', alignItems: 'baseline', gap: space.sm }}>
        <Text style={{ flex: 1, color: c.text, fontSize: 16, fontWeight: '600' }} numberOfLines={2}>
          {plan.name}
        </Text>
        <Text style={{ color: plan.progress.percent === null ? c.faint : c.text, fontWeight: '700' }}>{percentLabel(plan.progress)}</Text>
      </View>
      {plan.description ? (
        <Text style={{ color: c.muted, fontSize: 13 }} numberOfLines={2}>
          {plan.description}
        </Text>
      ) : null}
      <ProgressBar progress={plan.progress} />
      <PaceBadge pace={plan.pace} />
      <Text style={{ color: c.muted, fontSize: 12 }}>
        {plan.progress.completed}/{plan.progress.total} topics{plan.status === 'ARCHIVED' ? ' · archived' : ''}
      </Text>
    </Pressable>
  )
}
