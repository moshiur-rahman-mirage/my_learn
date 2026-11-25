import { useState } from 'react'
import { Alert, Linking, Pressable, ScrollView, Text, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router, Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { getItemDetail } from '@/data/itemDetail'
import { RuleError, setCompletion } from '@/data/items'
import { resolveLink } from '@/data/notes'
import { NoteView } from '@/note/NoteView'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { ActionSheet } from '@/ui/ActionSheet'
import { Button, Card, EmptyState, ErrorText, Loading, PriorityBadge } from '@/ui/components'
import { itemActions } from '@/ui/itemActions'
import { space, useColors } from '@/ui/theme'

function minutesLabel(m: number): string {
  if (m < 60) return `${m} min`
  const h = Math.floor(m / 60)
  return m % 60 ? `${h} h ${m % 60} min` : `${h} h`
}

export default function ItemScreen() {
  const { id } = useLocalSearchParams<{ id: string }>()
  const itemId = Number(id)
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const { data, error } = useQuery((d) => getItemDetail(d, itemId), `item:${itemId}`)
  const [menu, setMenu] = useState(false)

  if (data === undefined) return error ? <ErrorText message={error} /> : <Loading />
  if (data === null) return <EmptyState title="This topic is gone" message="It was deleted, maybe on your computer." />
  const { item } = data

  const tick = async () => {
    try {
      await setCompletion(db, item.id, !item.completed)
      bump()
    } catch (e) {
      Alert.alert('Can’t tick this', e instanceof RuleError ? e.message : String(e))
    }
  }

  const openWiki = async (target: string) => {
    const found = await resolveLink(db, item.planId, target)
    if (found) router.push({ pathname: '/item/[id]', params: { id: String(found) } })
    else Alert.alert('No such topic', `There’s no item named “${target}” in this plan.`)
  }

  const meta: string[] = []
  if (data.minutes) meta.push(`${minutesLabel(data.minutes)} spent`)
  if (data.flashcards) meta.push(`${data.flashcards} flashcard${data.flashcards === 1 ? '' : 's'}`)
  if (data.reviewDueOn) meta.push(`review on ${data.reviewDueOn}`)
  if (data.confidence) meta.push(`confidence ${data.confidence}/5`)

  return (
    <>
      <Stack.Screen
        options={{
          title: item.type === 'MODULE' ? 'Module' : 'Topic',
          headerRight: () => (
            <Pressable onPress={() => setMenu(true)} hitSlop={12} accessibilityRole="button" accessibilityLabel="Item actions">
              <Ionicons name="ellipsis-vertical" size={22} color={c.text} />
            </Pressable>
          ),
        }}
      />
      <ActionSheet
        visible={menu}
        onClose={() => setMenu(false)}
        title={item.name}
        actions={itemActions(db, { id: item.id, name: item.name, planId: item.planId, parentId: item.parentId }, bump, () => router.back())}
      />
      <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }}>
        <View style={{ gap: space.xs }}>
          <Text style={{ color: c.muted, fontSize: 12 }} numberOfLines={2}>
            {[data.planName, ...data.path].join(' › ')}
          </Text>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm, flexWrap: 'wrap' }}>
            <Text selectable style={{ color: c.text, fontSize: 22, fontWeight: '700', flexShrink: 1 }}>{item.name}</Text>
            <PriorityBadge priority={item.priority} />
          </View>
          {meta.length ? <Text style={{ color: c.muted, fontSize: 13 }}>{meta.join(' · ')}</Text> : null}
        </View>

        {data.checkable ? (
          <Button
            kind={item.completed ? 'secondary' : 'primary'}
            title={item.completed ? 'Done ✓  (tap to undo)' : 'Mark as done'}
            onPress={tick}
            icon={<Ionicons name={item.completed ? 'checkbox' : 'square-outline'} size={20} color={item.completed ? c.done : c.primaryText} />}
          />
        ) : data.childCount > 0 ? (
          <Text style={{ color: c.muted, fontSize: 13 }}>
            Progress comes from its {data.childCount} sub-item{data.childCount === 1 ? '' : 's'}.
          </Text>
        ) : null}

        {item.description ? <Text selectable style={{ color: c.text, fontSize: 15 }}>{item.description}</Text> : null}

        {data.tags.length ? (
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.xs }}>
            {data.tags.map((t) => (
              <Text key={t} style={{ color: c.muted, borderColor: c.border, borderWidth: 1, borderRadius: 999, paddingHorizontal: 10, paddingVertical: 2, fontSize: 12 }}>
                #{t}
              </Text>
            ))}
          </View>
        ) : null}

        {data.links.length ? (
          <View style={{ gap: space.xs }}>
            {data.links.map((l) => (
              <Pressable key={l.id} onPress={() => Linking.openURL(l.url)} accessibilityRole="link" style={{ flexDirection: 'row', gap: space.sm, alignItems: 'center', minHeight: 36 }}>
                <Ionicons name="open-outline" size={16} color={c.link} />
                <Text style={{ color: c.link, flexShrink: 1 }} numberOfLines={1}>{l.title || l.url}</Text>
              </Pressable>
            ))}
          </View>
        ) : null}

        <Card style={{ gap: space.md }}>
          <View style={{ flexDirection: 'row', alignItems: 'center' }}>
            <Text style={{ color: c.text, fontWeight: '600', fontSize: 16, flex: 1 }}>Notes</Text>
            <Pressable
              onPress={() => router.push({ pathname: '/note/[id]', params: { id: String(item.id) } })}
              accessibilityRole="button"
              hitSlop={8}
              style={{ flexDirection: 'row', alignItems: 'center', gap: 4, minHeight: 36 }}
            >
              <Ionicons name="create-outline" size={18} color={c.primary} />
              <Text style={{ color: c.primary, fontWeight: '600' }}>{data.note.content ? 'Edit' : 'Write'}</Text>
            </Pressable>
          </View>
          {data.note.content && data.note.format ? (
            <NoteView content={data.note.content} format={data.note.format} names={data.planNames} onWikiLink={openWiki} />
          ) : (
            <Text style={{ color: c.muted }}>No notes yet. Write what you learned, as Markdown. “Q: … / A: …” lines become flashcards.</Text>
          )}
        </Card>

        {data.backlinks.length ? (
          <View style={{ gap: space.xs }}>
            <Text style={{ color: c.muted, fontSize: 13 }}>Linked from</Text>
            {data.backlinks.map((b) => (
              <Pressable key={b.itemId} onPress={() => router.push({ pathname: '/item/[id]', params: { id: String(b.itemId) } })} accessibilityRole="link" style={{ minHeight: 36, justifyContent: 'center' }}>
                <Text style={{ color: c.link }}>{b.name}</Text>
              </Pressable>
            ))}
          </View>
        ) : null}
      </ScrollView>
    </>
  )
}
