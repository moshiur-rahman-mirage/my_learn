import { Alert, FlatList, Pressable, Text } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router, Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { getItem } from '@/data/tree'
import { moveItem, moveTargets } from '@/data/structure'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { EmptyState, ErrorText, Loading } from '@/ui/components'
import { space, useColors } from '@/ui/theme'

/** "Move to…": pick a new parent in the same plan (the item's own subtree isn't offered). */
export default function MoveScreen() {
  const { id } = useLocalSearchParams<{ id: string }>()
  const itemId = Number(id)
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const { data, error } = useQuery(async (d) => ({ item: await getItem(d, itemId), targets: await moveTargets(d, itemId) }), `move:${itemId}`)

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  if (!data.item) return <EmptyState title="This item is gone" />

  const move = async (parentId: number | null) => {
    try {
      await moveItem(db, itemId, parentId) // appended as the last child
      bump()
      router.back()
    } catch (e) {
      Alert.alert('Can’t move it there', e instanceof Error ? e.message : String(e))
    }
  }

  return (
    <>
      <Stack.Screen options={{ title: `Move “${data.item.name}”` }} />
      <FlatList
        data={data.targets}
        keyExtractor={(t) => String(t.id)}
        ListHeaderComponent={<Text style={{ color: c.muted, padding: space.lg }}>Choose its new parent. It goes to the end, with everything below it.</Text>}
        ListEmptyComponent={<EmptyState title="Nowhere else to move it" message="This plan has no other items." />}
        renderItem={({ item: t }) => (
          <Pressable
            onPress={() => move(t.id)}
            accessibilityRole="button"
            style={({ pressed }) => ({
              flexDirection: 'row', alignItems: 'center', gap: space.sm, minHeight: 48,
              paddingLeft: space.lg + t.depth * 18, paddingRight: space.lg, borderBottomColor: c.border, borderBottomWidth: 0.5,
              opacity: pressed ? 0.6 : 1,
            })}
          >
            <Ionicons name={t.id === null ? 'home-outline' : 'folder-outline'} size={18} color={c.muted} />
            <Text style={{ color: c.text, fontSize: 15, flexShrink: 1, fontWeight: t.id === null ? '600' : '400' }}>{t.name}</Text>
          </Pressable>
        )}
      />
    </>
  )
}
