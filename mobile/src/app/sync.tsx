import { Alert, ScrollView, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { pendingCount, readConfig, requestRebuild, unpair } from '@/sync/client'
import { useSync } from '@/sync/useSync'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { Button, Card, ErrorText, Loading } from '@/ui/components'
import { radius, space, useColors } from '@/ui/theme'

function ago(iso: string | null): string {
  if (!iso) return 'never'
  const s = Math.round((Date.now() - new Date(iso).getTime()) / 1000)
  if (s < 60) return 'just now'
  if (s < 3600) return `${Math.round(s / 60)} min ago`
  if (s < 86400) return `${Math.round(s / 3600)} h ago`
  return new Date(iso).toLocaleDateString()
}

export default function SyncScreen() {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const { running, lastReport, lastError, errorKind, run } = useSync()
  const { data } = useQuery(async (d) => ({ cfg: await readConfig(d), pending: await pendingCount(d) }), 'sync')

  if (!data) return <Loading />
  const paired = !!data.cfg.token

  /** Replaces this phone's plans with a fresh copy from the computer (fixes a phone that got out of step). */
  const confirmRebuild = () => {
    const go = async (discard: boolean) => {
      await requestRebuild(db, discard)
      void run(db)
    }
    Alert.alert(
      'Download everything again?',
      data.pending > 0
        ? `This phone's plans are replaced by your computer's. ${data.pending} change(s) on this phone haven't been sent yet and would be lost. Sync first to keep them.`
        : 'This phone’s plans, notes and progress are replaced by a fresh copy from your computer. Nothing on your computer changes.',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: data.pending > 0 ? 'Discard and download' : 'Download', style: data.pending > 0 ? 'destructive' : 'default', onPress: () => void go(data.pending > 0) },
      ],
    )
  }

  const confirmUnpair = () =>
    Alert.alert('Unpair this phone?', 'Your plans stay on this phone. Changes you make won’t reach the computer until you pair again.', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Unpair', style: 'destructive', onPress: async () => { await unpair(db); bump() } },
    ])

  return (
    <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }}>
      <Card style={{ gap: space.md }}>
        <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm }}>
          <View style={{ width: 48, height: 48, alignItems: 'center', justifyContent: 'center', borderRadius: radius.md, backgroundColor: c.chipOn }}>
            <Ionicons name={paired ? 'desktop-outline' : 'link-outline'} size={23} color={c.primary} />
          </View>
          <View style={{ flex: 1, gap: 3 }}>
            <Text style={{ color: c.text, fontSize: 17, fontWeight: '700' }}>{paired ? 'Paired with your computer' : 'Not paired yet'}</Text>
            <Text style={{ color: c.muted, fontSize: 13 }}>{paired ? 'Your learning stays up to date.' : 'Connect your phone to your computer.'}</Text>
          </View>
        </View>
        {paired ? (
          <>
            <View style={{ gap: space.xs, padding: space.md, borderRadius: radius.md, backgroundColor: c.chipOn }}>
              <Text selectable style={{ color: c.muted, fontSize: 12 }}>{data.cfg.url}</Text>
              <Text style={{ color: c.text, fontWeight: '600' }}>Last sync: {ago(data.cfg.lastSyncAt)}</Text>
              <Text style={{ color: c.muted, fontSize: 13 }}>
                {data.pending === 0 ? 'Everything on this phone has been sent.' : `${data.pending} change(s) waiting to be sent.`}
              </Text>
            </View>
            <Button
              title={running ? 'Syncing…' : 'Sync now'}
              busy={running}
              onPress={() => run(db)}
              icon={<Ionicons name="sync" size={18} color={c.primaryText} />}
            />
          </>
        ) : (
          <>
            <Text style={{ color: c.muted }}>
              Your plans, notes and flashcards live on this phone and work offline. Pair once with the Learning Tracker on your computer;
              after that, tap Sync whenever both are on the same Wi-Fi. Changes go both ways.
            </Text>
            <Button title="Pair with computer" onPress={() => router.push('/pair')} icon={<Ionicons name="qr-code-outline" size={18} color={c.primaryText} />} />
          </>
        )}
      </Card>

      {lastError ? (
        <Card style={{ gap: space.sm, borderColor: c.danger }}>
          <Text style={{ color: c.danger, fontWeight: '600' }}>Sync didn’t finish</Text>
          <ErrorText message={lastError} />
          {errorKind === 'not-paired' ? <Button kind="secondary" title="Pair again" onPress={() => router.push('/pair')} /> : null}
          <Text style={{ color: c.muted, fontSize: 12 }}>Nothing was lost: the next sync picks up where this one stopped.</Text>
        </Card>
      ) : null}

      {lastReport && !lastError ? (
        <Card style={{ gap: space.xs }}>
          <Text style={{ color: c.text, fontWeight: '700', fontSize: 16 }}>Last sync</Text>
          {lastReport.reset ? (
            <Text style={{ color: c.warn }}>Your computer’s data was restored from a backup, so this phone was rebuilt from it.</Text>
          ) : lastReport.rebuilt ? (
            <Text style={{ color: c.text }}>This phone now holds a fresh copy of your computer’s plans.</Text>
          ) : null}
          <Text style={{ color: c.text }}>Sent {lastReport.pushed} · received {lastReport.pulled}</Text>
          {lastReport.conflicts.length > 0 ? (
            <Text style={{ color: c.text }}>
              {lastReport.conflicts.length} note(s) were edited on both devices. Both versions were kept in the note, marked
              “Conflicting copy”.
            </Text>
          ) : null}
          {lastReport.skipped.length > 0 ? (
            <Text style={{ color: c.muted }}>
              {lastReport.skipped.length} change(s) couldn’t be applied (for example, a topic whose parent was deleted on the other device).
            </Text>
          ) : null}
        </Card>
      ) : null}

      {paired ? (
        <View style={{ gap: space.sm }}>
          <Button kind="secondary" title="Download everything again" onPress={confirmRebuild} />
          <Button kind="secondary" title="Pair with a different computer" onPress={() => router.push('/pair')} />
          <Button kind="danger" title="Unpair" onPress={confirmUnpair} />
        </View>
      ) : null}
    </ScrollView>
  )
}
