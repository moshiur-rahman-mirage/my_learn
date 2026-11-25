import { useState } from 'react'
import { ScrollView, Text, TextInput } from 'react-native'
import { router, Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { getPlan } from '@/data/plans'
import { deletePlan } from '@/data/structure'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { Button, ErrorText, Loading } from '@/ui/components'
import { radius, space, useColors } from '@/ui/theme'

/** Deleting a whole plan needs its name typed, like the desktop (it deletes every topic and note in it). */
export default function DeletePlanScreen() {
  const { id } = useLocalSearchParams<{ id: string }>()
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const [typed, setTyped] = useState('')
  const [busy, setBusy] = useState(false)
  const { data, error } = useQuery((d) => getPlan(d, Number(id)), `plan-delete:${id}`)

  if (data === undefined) return error ? <ErrorText message={error} /> : <Loading />
  if (data === null) return <ErrorText message="This plan no longer exists." />

  const remove = async () => {
    setBusy(true)
    await deletePlan(db, data.id)
    bump()
    router.dismissAll()
    router.replace('/plans')
  }

  return (
    <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }} keyboardShouldPersistTaps="handled">
      <Stack.Screen options={{ title: 'Delete plan' }} />
      <Text style={{ color: c.text, fontSize: 16 }}>
        This permanently deletes “{data.name}” with all {data.progress.total} topics, their notes, flashcards and history, on this phone
        and, after the next sync, on your computer.
      </Text>
      <Text style={{ color: c.muted }}>Archiving keeps everything but hides it. To delete, type the plan’s name:</Text>
      <TextInput
        value={typed}
        onChangeText={setTyped}
        placeholder={data.name}
        placeholderTextColor={c.faint}
        autoCapitalize="none"
        autoCorrect={false}
        style={{ borderWidth: 1, borderColor: c.border, borderRadius: radius.md, padding: space.md, color: c.text, backgroundColor: c.card, fontSize: 16 }}
        accessibilityLabel="Type the plan name to confirm"
      />
      <Button kind="danger" title="Delete plan" onPress={remove} busy={busy} disabled={typed.trim() !== data.name} />
    </ScrollView>
  )
}
