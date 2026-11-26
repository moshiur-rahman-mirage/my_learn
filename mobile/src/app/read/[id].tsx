import { useEffect, useState } from 'react'
import { Alert, Pressable, StatusBar, Text, View, useColorScheme } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { router, useLocalSearchParams } from 'expo-router'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { useDb } from '@/db/DbProvider'
import { getItemDetail } from '@/data/itemDetail'
import { RuleError, setCompletion } from '@/data/items'
import { resolveLink } from '@/data/notes'
import { readingNav, rememberReading } from '@/data/reading'
import { isShortReadRead, markShortRead } from '@/data/shortRead'
import { NoteView } from '@/note/NoteView'
import { TEXT_SCALES, usePrefs, type Accent, type ReadingTheme } from '@/state/prefs'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { EmptyState, ErrorText, Loading } from '@/ui/components'
import { colorsFor, radius, space } from '@/ui/theme'

/** Colours of the reader's controls on each reading background; the progress bar follows the accent. */
function chromeFor(accent: Accent): Record<Exclude<ReadingTheme, 'auto'>, { bg: string; fg: string; muted: string; bar: string }> {
  return {
    paper: { bg: '#ffffff', fg: '#1c2321', muted: '#5f6b67', bar: colorsFor(false, accent).primary },
    sepia: { bg: '#f6ecd9', fg: '#433422', muted: '#7a6650', bar: '#a8742a' },
    night: { bg: '#15181b', fg: '#e6e9eb', muted: '#9ba4aa', bar: colorsFor(true, accent).primary },
  }
}
const NEXT_THEME: Record<Exclude<ReadingTheme, 'auto'>, ReadingTheme> = { paper: 'sepia', sepia: 'night', night: 'paper' }

interface IconButtonProps {
  name: keyof typeof Ionicons.glyphMap
  label: string
  onPress: () => void
  disabled?: boolean
  color: string
}

function IconButton({ name, label, onPress, disabled, color }: IconButtonProps) {
  return (
    <Pressable onPress={onPress} disabled={disabled} hitSlop={8} accessibilityRole="button" accessibilityLabel={label}
      style={{ width: 44, height: 44, borderRadius: 22, alignItems: 'center', justifyContent: 'center', opacity: disabled ? 0.35 : 1 }}>
      <Ionicons name={name} size={22} color={color} />
    </Pressable>
  )
}

/**
 * Reading mode: a topic's note on a full, distraction-free page, in the reading font and
 * background from Settings. Tap the page to show or hide the controls.
 */
export default function ReaderScreen() {
  const { id } = useLocalSearchParams<{ id: string }>()
  const itemId = Number(id)
  const db = useDb()
  const scheme = useColorScheme()
  const insets = useSafeAreaInsets()
  const bump = useDataVersion((s) => s.bump)
  const prefs = usePrefs()
  const [read, setRead] = useState({ itemId, fraction: 0 })
  const progress = read.itemId === itemId ? read.fraction : 0
  const setProgress = (fraction: number) => setRead({ itemId, fraction })
  const [chrome, setChrome] = useState(true)
  const { data, error } = useQuery(async (d) => ({
    detail: await getItemDetail(d, itemId),
    nav: await readingNav(d, itemId),
    shortReadRead: await isShortReadRead(d, itemId),
  }), `read:${itemId}`)

  useEffect(() => {
    void rememberReading(db, itemId)
  }, [db, itemId])

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  const { detail, nav } = data
  if (!detail) return <EmptyState title="This topic is gone" />

  const theme = prefs.readingTheme === 'auto' ? (scheme === 'dark' ? 'night' : 'paper') : prefs.readingTheme
  const ui = chromeFor(prefs.accent)[theme]
  const { item } = detail

  const go = (target: number | null) => {
    if (target !== null) router.replace({ pathname: '/read/[id]', params: { id: String(target) } })
  }
  const openWiki = async (target: string) => {
    const found = await resolveLink(db, item.planId, target)
    if (found) router.push({ pathname: '/read/[id]', params: { id: String(found) } })
    else Alert.alert('No such topic', `There’s no item named “${target}” in this plan.`)
  }
  const sizeStep = (delta: number) => {
    const i = TEXT_SCALES.indexOf(prefs.textScale)
    const next = TEXT_SCALES[Math.min(TEXT_SCALES.length - 1, Math.max(0, (i < 0 ? 1 : i) + delta))]
    void prefs.set(db, 'textScale', next)
  }
  const markDone = async () => {
    try {
      await setCompletion(db, item.id, !item.completed)
      bump()
    } catch (e) {
      Alert.alert('Can’t tick this', e instanceof RuleError ? e.message : String(e))
    }
  }
  const markAsRead = async () => {
    try {
      await markShortRead(db, item.id)
      bump()
    } catch (e) {
      Alert.alert('Can’t mark as read', e instanceof Error ? e.message : String(e))
    }
  }

  return (
    <View style={{ flex: 1, backgroundColor: ui.bg }}>
      <StatusBar barStyle={theme === 'night' ? 'light-content' : 'dark-content'} hidden={!chrome} />

      {/* reading progress */}
      <View style={{ height: 3, backgroundColor: 'transparent', marginTop: chrome ? insets.top : 0 }}>
        <View style={{ height: 3, width: `${Math.round(progress * 100)}%`, backgroundColor: ui.bar }} />
      </View>

      {chrome ? (
        <View style={{ flexDirection: 'row', alignItems: 'center', paddingHorizontal: space.sm, gap: space.xs }}>
          <IconButton color={ui.fg} name="arrow-back" label="Back" onPress={() => router.back()} />
          <View style={{ flex: 1 }}>
            <Text style={{ color: ui.muted, fontSize: 11 }} numberOfLines={1}>
              {[detail.planName, ...detail.path].join(' › ')}
            </Text>
            <Text style={{ color: ui.fg, fontWeight: '700', fontSize: 15 }} numberOfLines={1}>{item.name}</Text>
          </View>
          <IconButton color={ui.fg} name="remove" label="Smaller text" onPress={() => sizeStep(-1)} />
          <IconButton color={ui.fg} name="add" label="Larger text" onPress={() => sizeStep(1)} />
          <IconButton color={ui.fg} name={theme === 'night' ? 'moon' : theme === 'sepia' ? 'cafe-outline' : 'sunny-outline'} label="Change background"
            onPress={() => void prefs.set(db, 'readingTheme', NEXT_THEME[theme])} />
        </View>
      ) : null}

      <View style={{ flex: 1 }}>
        {detail.note.content && detail.note.format ? (
          <NoteView content={detail.note.content} format={detail.note.format} names={detail.planNames} mode="page"
            onWikiLink={openWiki} onProgress={setProgress} onTap={() => setChrome((v) => !v)} />
        ) : (
          <EmptyState title="No notes for this topic yet" message="Write one from the topic screen; it appears here as a full page." />
        )}
      </View>

      {chrome ? (
        <View style={{
          gap: space.xs, paddingHorizontal: space.md, paddingTop: space.sm,
          paddingBottom: insets.bottom + space.sm, borderTopWidth: 1, borderTopColor: theme === 'night' ? '#2a2638' : '#00000012',
        }}>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm }}>
            <IconButton color={ui.fg} name="chevron-back" label="Previous note" onPress={() => go(nav?.prevId ?? null)} disabled={!nav?.prevId} />
            <Text style={{ color: ui.muted, fontSize: 12, minWidth: 56, textAlign: 'center' }}>
              {nav && nav.index > 0 ? `${nav.index} / ${nav.count}` : ''}
            </Text>
            <IconButton color={ui.fg} name="chevron-forward" label="Next note" onPress={() => go(nav?.nextId ?? null)} disabled={!nav?.nextId} />
            <View style={{ flex: 1 }} />
          </View>
          {detail.note.content ? (
            <View style={{ flexDirection: 'row', gap: space.sm }}>
              <Pressable onPress={() => void markAsRead()} disabled={data.shortReadRead} accessibilityRole="button"
                accessibilityState={{ disabled: data.shortReadRead }}
                accessibilityLabel={data.shortReadRead ? 'Note marked as read' : 'Mark note as read'}
                style={{
                  flex: 1, minHeight: 44, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 6,
                  paddingHorizontal: space.md, borderRadius: radius.pill, borderWidth: 1, borderColor: ui.bar,
                  opacity: data.shortReadRead ? 0.75 : 1,
                }}>
                <Ionicons name={data.shortReadRead ? 'checkmark-circle' : 'book-outline'} size={18} color={ui.bar} />
                <Text style={{ color: ui.bar, fontWeight: '700' }}>{data.shortReadRead ? 'Read' : 'Mark as read'}</Text>
              </Pressable>
              {detail.checkable ? (
                <Pressable onPress={markDone} accessibilityRole="checkbox" accessibilityState={{ checked: item.completed }}
                  style={{
                    flex: 1, minHeight: 44, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 6,
                    paddingHorizontal: space.md, borderRadius: radius.pill, backgroundColor: item.completed ? 'transparent' : ui.bar,
                    borderWidth: item.completed ? 1 : 0, borderColor: ui.bar,
                  }}>
                  <Ionicons name={item.completed ? 'checkmark-circle' : 'checkmark'} size={18} color={item.completed ? ui.bar : ui.bg} />
                  <Text style={{ color: item.completed ? ui.bar : ui.bg, fontWeight: '700' }}>{item.completed ? 'Done' : 'Mark as done'}</Text>
                </Pressable>
              ) : null}
            </View>
          ) : detail.checkable ? (
            <Pressable onPress={markDone} accessibilityRole="checkbox" accessibilityState={{ checked: item.completed }}
              style={{
                alignSelf: 'flex-end', flexDirection: 'row', alignItems: 'center', gap: 6, paddingHorizontal: space.lg,
                minHeight: 44, borderRadius: radius.pill, backgroundColor: item.completed ? 'transparent' : ui.bar,
                borderWidth: item.completed ? 1 : 0, borderColor: ui.bar,
              }}>
              <Ionicons name={item.completed ? 'checkmark-circle' : 'checkmark'} size={18} color={item.completed ? ui.bar : ui.bg} />
              <Text style={{ color: item.completed ? ui.bar : ui.bg, fontWeight: '700' }}>{item.completed ? 'Done' : 'Mark as done'}</Text>
            </Pressable>
          ) : null}
        </View>
      ) : null}
    </View>
  )
}
