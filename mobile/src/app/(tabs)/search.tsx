import { useEffect, useState } from 'react'
import { FlatList, Pressable, Text, TextInput, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { search, type SearchHit, type SearchResult } from '@/data/search'
import { EmptyState, ErrorText } from '@/ui/components'
import { PlanCover } from '@/ui/PlanCover'
import { radius, space, useColors } from '@/ui/theme'

/** Search across all plans: names, descriptions and notes; each word matches as a prefix ("sprin boo"). */
export default function SearchScreen() {
  const db = useDb()
  const c = useColors()
  const [text, setText] = useState('')
  const [result, setResult] = useState<{ query: string; value: SearchResult } | null>(null)
  const [error, setError] = useState<{ query: string; message: string } | null>(null)

  useEffect(() => {
    if (!text.trim()) return
    let alive = true
    const timer = setTimeout(() => {
      search(db, text)
        .then((value) => alive && (setResult({ query: text.trim(), value }), setError(null)))
        .catch((e: unknown) => alive && setError({
          query: text.trim(),
          message: e instanceof Error ? e.message : String(e),
        }))
    }, 150)
    return () => {
      alive = false
      clearTimeout(timer)
    }
  }, [db, text])

  const query = text.trim()
  const currentResult = result?.query === query ? result.value : null
  const currentError = error?.query === query ? error.message : null
  return (
    <View style={{ flex: 1 }}>
      <View style={{ padding: space.lg, paddingBottom: space.sm }}>
        <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm, borderWidth: 1, borderColor: c.border, borderRadius: radius.md, backgroundColor: c.card, paddingHorizontal: space.md }}>
          <Ionicons name="search" size={18} color={c.muted} />
          <TextInput
            value={text}
            onChangeText={setText}
            placeholder="Search topics and notes"
            placeholderTextColor={c.faint}
            autoCorrect={false}
            autoCapitalize="none"
            returnKeyType="search"
            style={{ flex: 1, color: c.text, fontSize: 16, paddingVertical: space.md }}
            accessibilityLabel="Search"
          />
          {text ? (
            <Pressable onPress={() => setText('')} hitSlop={10} accessibilityRole="button" accessibilityLabel="Clear search">
              <Ionicons name="close-circle" size={18} color={c.muted} />
            </Pressable>
          ) : null}
        </View>
        <ErrorText message={currentError} />
      </View>
      <FlatList
        data={currentResult?.hits ?? []}
        keyExtractor={(h) => String(h.itemId)}
        keyboardShouldPersistTaps="handled"
        contentContainerStyle={{ paddingHorizontal: space.lg, paddingBottom: space.xl, gap: space.sm }}
        ListEmptyComponent={
          !text.trim() ? (
            <EmptyState title="Search your learning library" message="Find topics by name or search words inside your notes. Try “Java” or “hash map”." />
          ) : currentError ? undefined : !currentResult ? (
            <EmptyState title="Searching…" />
          ) : (
            <EmptyState title="Nothing found" message="Words match as prefixes: “sprin boo” finds “Spring Boot”." />
          )
        }
        ListFooterComponent={
          currentResult?.more ? <Text style={{ color: c.muted, textAlign: 'center', padding: space.md }}>More matches: type more words to narrow it down.</Text> : undefined
        }
        renderItem={({ item }) => <Hit hit={item} />}
      />
    </View>
  )
}

function Hit({ hit }: { hit: SearchHit }) {
  const c = useColors()
  return (
    <Pressable
      onPress={() => router.push({ pathname: '/item/[id]', params: { id: String(hit.itemId) } })}
      accessibilityRole="button"
      style={({ pressed }) => ({ flexDirection: 'row', gap: space.md, backgroundColor: c.card, borderColor: c.border, borderWidth: 1, borderRadius: radius.md, padding: space.md, opacity: pressed ? 0.8 : 1 })}
    >
      <PlanCover name={hit.planName} width={34} height={46} />
      <View style={{ flex: 1, gap: 4 }}>
      <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.xs }}>
        {hit.checkable ? <Ionicons name={hit.completed ? 'checkbox' : 'square-outline'} size={16} color={hit.completed ? c.done : c.faint} /> : null}
        <Text style={{ color: c.text, fontWeight: '600', fontSize: 15, flexShrink: 1 }}>{hit.name}</Text>
      </View>
      <Text style={{ color: c.muted, fontSize: 12 }} numberOfLines={1}>
        {[hit.planName, ...hit.path].join(' › ')}{hit.planArchived ? ' · archived' : ''}
      </Text>
      {hit.snippet.length ? (
        <Text style={{ color: c.muted, fontSize: 13 }} numberOfLines={3}>
          {hit.matchedIn === 'note' ? <Text style={{ fontStyle: 'italic' }}>note: </Text> : null}
          {hit.snippet.map((s, i) => (
            <Text key={i} style={s.hit ? { color: c.text, fontWeight: '700', backgroundColor: c.chipOn } : undefined}>{s.text}</Text>
          ))}
        </Text>
      ) : null}
      </View>
    </Pressable>
  )
}
