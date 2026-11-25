import { useEffect, useState } from 'react'
import { Alert, KeyboardAvoidingView, Platform, ScrollView, Text, TextInput, View } from 'react-native'
import { router, Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { getPlan } from '@/data/plans'
import { createPlan, StaleError, updatePlan } from '@/data/structure'
import { formatDay } from '@/domain/dates'
import type { PlanStatus } from '@/domain/types'
import { useDataVersion } from '@/state/stores'
import { Button, Chip, ErrorText, Loading } from '@/ui/components'
import { radius, space, useColors } from '@/ui/theme'

function monthsFromNow(n: number): string {
  const d = new Date()
  d.setMonth(d.getMonth() + n)
  return formatDay(d)
}

/** New plan, or edit name, description and target date (`id` param). */
export default function PlanForm() {
  const { id } = useLocalSearchParams<{ id?: string }>()
  const editing = !!id
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const [loaded, setLoaded] = useState(!editing)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [targetDate, setTargetDate] = useState('')
  const [status, setStatus] = useState<PlanStatus>('ACTIVE')
  const [version, setVersion] = useState(0)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!editing) return
    getPlan(db, Number(id)).then((p) => {
      if (!p) return setError('This plan no longer exists.')
      setName(p.name)
      setDescription(p.description ?? '')
      setTargetDate(p.targetDate ?? '')
      setStatus(p.status)
      setVersion(p.version)
      setLoaded(true)
    })
  }, [db, editing, id])

  const save = async () => {
    setBusy(true)
    setError(null)
    try {
      const input = { name, description, targetDate: targetDate.trim() || null }
      if (editing) {
        await updatePlan(db, Number(id), { ...input, status, version })
        bump()
        router.back()
      } else {
        const newId = await createPlan(db, input)
        bump()
        router.replace({ pathname: '/plan/[id]', params: { id: String(newId) } })
      }
    } catch (e) {
      if (e instanceof StaleError) Alert.alert('Changed meanwhile', e.message)
      else setError(e instanceof Error ? e.message : String(e))
    } finally {
      setBusy(false)
    }
  }

  if (!loaded) return error ? <ErrorText message={error} /> : <Loading />
  const input = { borderWidth: 1, borderColor: c.border, borderRadius: radius.md, padding: space.md, color: c.text, backgroundColor: c.card, fontSize: 16 }
  const label = { color: c.muted, fontSize: 13 }

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <Stack.Screen options={{ title: editing ? 'Edit plan' : 'New plan' }} />
      <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }} keyboardShouldPersistTaps="handled">
        <View style={{ gap: space.xs }}>
          <Text style={label}>Name</Text>
          <TextInput value={name} onChangeText={setName} autoFocus={!editing} maxLength={200} style={input} accessibilityLabel="Plan name"
            placeholder="e.g. Kubernetes" placeholderTextColor={c.faint} />
        </View>
        <View style={{ gap: space.xs }}>
          <Text style={label}>Description (optional)</Text>
          <TextInput value={description} onChangeText={setDescription} multiline maxLength={4000} textAlignVertical="top"
            style={[input, { minHeight: 80 }]} accessibilityLabel="Description" />
        </View>
        <View style={{ gap: space.xs }}>
          <Text style={label}>Finish by (optional)</Text>
          <TextInput value={targetDate} onChangeText={setTargetDate} placeholder="YYYY-MM-DD" placeholderTextColor={c.faint}
            keyboardType="numbers-and-punctuation" maxLength={10} style={input} accessibilityLabel="Target date, year-month-day" />
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.sm }}>
            {[1, 3, 6].map((m) => (
              <Chip key={m} label={`+${m} month${m === 1 ? '' : 's'}`} on={targetDate === monthsFromNow(m)} onPress={() => setTargetDate(monthsFromNow(m))} />
            ))}
            <Chip label="No date" on={!targetDate.trim()} onPress={() => setTargetDate('')} />
          </View>
          <Text style={{ color: c.faint, fontSize: 12 }}>With a date, the plan shows whether your pace will finish it on time.</Text>
        </View>
        <ErrorText message={error} />
        <Button title={editing ? 'Save' : 'Create plan'} onPress={save} busy={busy} disabled={!name.trim()} />
      </ScrollView>
    </KeyboardAvoidingView>
  )
}
