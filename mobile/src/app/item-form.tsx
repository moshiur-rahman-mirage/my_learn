import { useEffect, useState } from 'react'
import { Alert, KeyboardAvoidingView, Platform, ScrollView, Text, TextInput, View } from 'react-native'
import { router, Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { RuleError } from '@/data/items'
import { createItem, StaleError, updateItem } from '@/data/structure'
import { getItem } from '@/data/tree'
import type { ItemType, Priority } from '@/domain/types'
import { useDataVersion } from '@/state/stores'
import { Button, Chip, ErrorText, Loading } from '@/ui/components'
import { radius, space, useColors } from '@/ui/theme'

/**
 * Add or edit an item (the desktop's ItemFormDialog).
 * Params: `id` to edit; otherwise `planId`, `parentId` ('' = top level) and optional `position`.
 */
export default function ItemForm() {
  const params = useLocalSearchParams<{ id?: string; planId?: string; parentId?: string; position?: string; parentName?: string }>()
  const editing = !!params.id
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const [loaded, setLoaded] = useState(!editing)
  const [name, setName] = useState('')
  const [type, setType] = useState<ItemType>('TOPIC')
  const [priority, setPriority] = useState<Priority | null>(null)
  const [description, setDescription] = useState('')
  const [version, setVersion] = useState(0)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!editing) return
    getItem(db, Number(params.id)).then((item) => {
      if (!item) {
        setError('This item no longer exists.')
        return
      }
      setName(item.name)
      setType(item.type)
      setPriority(item.priority)
      setDescription(item.description ?? '')
      setVersion(item.version)
      setLoaded(true)
    })
  }, [db, editing, params.id])

  const save = async () => {
    setBusy(true)
    setError(null)
    try {
      if (editing) {
        await updateItem(db, Number(params.id), { name, type, priority, description, version })
      } else {
        await createItem(db, Number(params.planId), {
          name, type, priority, description,
          parentId: params.parentId ? Number(params.parentId) : null,
          position: params.position !== undefined && params.position !== '' ? Number(params.position) : null,
        })
      }
      bump()
      router.back()
    } catch (e) {
      if (e instanceof StaleError) Alert.alert('Changed meanwhile', e.message)
      else setError(e instanceof RuleError || e instanceof Error ? e.message : String(e))
    } finally {
      setBusy(false)
    }
  }

  if (!loaded) return error ? <ErrorText message={error} /> : <Loading />

  const input = { borderWidth: 1, borderColor: c.border, borderRadius: radius.md, padding: space.md, color: c.text, backgroundColor: c.card, fontSize: 16 }
  const label = { color: c.muted, fontSize: 13 }

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <Stack.Screen options={{ title: editing ? 'Edit item' : 'New item' }} />
      <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }} keyboardShouldPersistTaps="handled">
        {!editing && params.parentName ? <Text style={label}>Inside “{params.parentName}”</Text> : null}
        <View style={{ gap: space.xs }}>
          <Text style={label}>Name</Text>
          <TextInput value={name} onChangeText={setName} autoFocus={!editing} maxLength={300} style={input} accessibilityLabel="Name"
            returnKeyType="done" placeholder="e.g. Dependency Injection" placeholderTextColor={c.faint} />
        </View>
        <View style={{ gap: space.xs }}>
          <Text style={label}>Type</Text>
          <View style={{ flexDirection: 'row', gap: space.sm }} accessibilityRole="radiogroup">
            <Chip label="Topic" on={type === 'TOPIC'} onPress={() => setType('TOPIC')} />
            <Chip label="Module" on={type === 'MODULE'} onPress={() => setType('MODULE')} />
          </View>
          <Text style={{ color: c.faint, fontSize: 12 }}>
            {type === 'TOPIC' ? 'Something to learn. Without sub-items it gets a checkbox.' : 'A container for topics; its progress comes from them.'}
          </Text>
        </View>
        <View style={{ gap: space.xs }}>
          <Text style={label}>Priority</Text>
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.sm }} accessibilityRole="radiogroup">
            {([null, 'MUST', 'SHOULD', 'NICE'] as const).map((p) => (
              <Chip key={p ?? 'none'} label={p === null ? 'None' : p === 'MUST' ? 'Must' : p === 'SHOULD' ? 'Should' : 'Nice'}
                on={priority === p} onPress={() => setPriority(p)} />
            ))}
          </View>
        </View>
        <View style={{ gap: space.xs }}>
          <Text style={label}>Description (optional)</Text>
          <TextInput value={description} onChangeText={setDescription} multiline maxLength={4000} textAlignVertical="top"
            style={[input, { minHeight: 96 }]} accessibilityLabel="Description" />
        </View>
        <ErrorText message={error} />
        <Button title={editing ? 'Save' : 'Add'} onPress={save} busy={busy} disabled={!name.trim()} />
      </ScrollView>
    </KeyboardAvoidingView>
  )
}
