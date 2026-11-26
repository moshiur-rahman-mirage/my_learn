import { useState } from 'react'
import { Alert, Linking, Pressable, Text, TextInput, View } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import { useDb } from '@/db/DbProvider'
import { addLink, deleteLink, logTime, rateConfidence, setTags } from '@/data/extras'
import type { Link } from '@/domain/types'
import { usePomodoro } from '@/state/pomodoro'
import { useDataVersion } from '@/state/stores'
import { Card } from './components'
import { radius, space, useColors } from './theme'

const fail = (e: unknown) => Alert.alert('Couldn’t save', e instanceof Error ? e.message : String(e))

function Title({ icon, text, right }: { icon: keyof typeof Ionicons.glyphMap; text: string; right?: string }) {
  const c = useColors()
  return (
    <View style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm }}>
      <Ionicons name={icon} size={18} color={c.primary} />
      <Text style={{ color: c.text, fontWeight: '800', fontSize: 15, flex: 1 }}>{text}</Text>
      {right ? <Text style={{ color: c.muted, fontSize: 13 }}>{right}</Text> : null}
    </View>
  )
}

const LEVELS = ['Shaky', 'Unsure', 'OK', 'Good', 'Solid']

/** How well you know it, 1–5; a low rating on a finished topic brings its review forward. */
export function ConfidenceCard({ itemId, level }: { itemId: number; level: number | null }) {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const rate = async (n: number) => {
    try {
      const reviewOn = await rateConfidence(db, itemId, n === level ? null : n)
      bump()
      if (reviewOn) Alert.alert('Review moved up', 'It comes back for review tomorrow.')
    } catch (e) {
      fail(e)
    }
  }
  return (
    <Card style={{ gap: space.md }}>
      <Title icon="pulse" text="How well do you know it?" right={level ? LEVELS[level - 1] : undefined} />
      <View style={{ flexDirection: 'row', gap: space.xs }} accessibilityRole="radiogroup">
        {LEVELS.map((label, i) => {
          const n = i + 1
          const on = level !== null && n <= level
          return (
            <Pressable key={n} onPress={() => void rate(n)} accessibilityRole="radio" accessibilityState={{ checked: level === n }}
              accessibilityLabel={`${n}: ${label}`}
              style={{ flex: 1, height: 40, borderRadius: radius.sm, alignItems: 'center', justifyContent: 'center', backgroundColor: on ? c.primary : c.track }}>
              <Text style={{ color: on ? c.primaryText : c.muted, fontWeight: '800' }}>{n}</Text>
            </Pressable>
          )
        })}
      </View>
    </Card>
  )
}

/** Time spent: quick logging and a focus (Pomodoro) timer. */
export function TimeCard({ itemId, itemName, minutes }: { itemId: number; itemName: string; minutes: number }) {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const running = usePomodoro((s) => s.running)
  const start = usePomodoro((s) => s.start)
  const add = async (m: number) => {
    try {
      await logTime(db, itemId, m)
      bump()
    } catch (e) {
      fail(e)
    }
  }
  const total = minutes < 60 ? `${minutes} min` : `${Math.floor(minutes / 60)} h ${minutes % 60 ? `${minutes % 60} min` : ''}`.trim()
  return (
    <Card style={{ gap: space.md }}>
      <Title icon="time" text="Time spent" right={minutes ? total : 'none yet'} />
      <View style={{ flexDirection: 'row', gap: space.sm }}>
        {[15, 25, 50].map((m) => (
          <Pressable key={m} onPress={() => void add(m)} accessibilityRole="button" accessibilityLabel={`Log ${m} minutes`}
            style={{ flex: 1, minHeight: 44, borderRadius: radius.pill, borderWidth: 1, borderColor: c.border, alignItems: 'center', justifyContent: 'center' }}>
            <Text style={{ color: c.text, fontWeight: '700' }}>+{m}′</Text>
          </Pressable>
        ))}
      </View>
      <Pressable
        onPress={() => (running ? undefined : void start(db, itemId, itemName, 25))}
        disabled={!!running}
        accessibilityRole="button"
        style={{ minHeight: 48, borderRadius: radius.md, backgroundColor: running ? c.track : c.chipOn, flexDirection: 'row', gap: space.sm, alignItems: 'center', justifyContent: 'center' }}
      >
        <Ionicons name="timer-outline" size={20} color={c.primary} />
        <Text style={{ color: c.primary, fontWeight: '800' }}>
          {running ? (running.itemId === itemId ? 'Focus timer running…' : `Timer running on “${running.itemName}”`) : 'Start a 25-minute focus session'}
        </Text>
      </Pressable>
    </Card>
  )
}

/** Tags as removable chips, an add field, and suggestions from tags already in use. */
export function TagsCard({ itemId, tags, known }: { itemId: number; tags: string[]; known: string[] }) {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const [text, setText] = useState('')
  const save = async (next: string[]) => {
    try {
      await setTags(db, itemId, next)
      setText('')
      bump()
    } catch (e) {
      fail(e)
    }
  }
  const lower = new Set(tags.map((t) => t.toLowerCase()))
  const suggestions = known.filter((k) => !lower.has(k.toLowerCase()) && (!text || k.toLowerCase().startsWith(text.trim().toLowerCase().replace(/^#/, '')))).slice(0, 6)
  return (
    <Card style={{ gap: space.md }}>
      <Title icon="pricetags" text="Tags" />
      {tags.length ? (
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.xs }}>
          {tags.map((t) => (
            <Pressable key={t} onPress={() => void save(tags.filter((x) => x !== t))} accessibilityRole="button" accessibilityLabel={`Remove tag ${t}`}
              style={{ flexDirection: 'row', alignItems: 'center', gap: 4, backgroundColor: c.chipOn, borderRadius: radius.pill, paddingLeft: 12, paddingRight: 8, paddingVertical: 6 }}>
              <Text style={{ color: c.primary, fontWeight: '700' }}>#{t}</Text>
              <Ionicons name="close" size={14} color={c.primary} />
            </Pressable>
          ))}
        </View>
      ) : null}
      <TextInput
        value={text}
        onChangeText={setText}
        onSubmitEditing={() => text.trim() && void save([...tags, text])}
        placeholder="Add a tag, e.g. interview"
        placeholderTextColor={c.faint}
        returnKeyType="done"
        autoCapitalize="none"
        style={{ borderWidth: 1, borderColor: c.border, borderRadius: radius.pill, paddingHorizontal: space.lg, paddingVertical: 10, color: c.text, backgroundColor: c.bg }}
        accessibilityLabel="Add a tag"
      />
      {suggestions.length ? (
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: space.xs }}>
          {suggestions.map((s) => (
            <Pressable key={s} onPress={() => void save([...tags, s])} accessibilityRole="button" accessibilityLabel={`Add tag ${s}`}
              style={{ borderWidth: 1, borderColor: c.border, borderRadius: radius.pill, paddingHorizontal: 10, paddingVertical: 4 }}>
              <Text style={{ color: c.muted }}>+ {s}</Text>
            </Pressable>
          ))}
        </View>
      ) : null}
    </Card>
  )
}

/** Web links: open, add, long-press to delete. */
export function LinksCard({ itemId, links }: { itemId: number; links: Link[] }) {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const [url, setUrl] = useState('')
  const [title, setTitle] = useState('')
  const add = async () => {
    try {
      await addLink(db, itemId, url, title)
      setUrl('')
      setTitle('')
      bump()
    } catch (e) {
      fail(e)
    }
  }
  const remove = (l: Link) =>
    Alert.alert('Remove link?', l.title || l.url, [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Remove', style: 'destructive', onPress: () => void deleteLink(db, l.id).then(bump, fail) },
    ])
  const input = { borderWidth: 1, borderColor: c.border, borderRadius: radius.md, paddingHorizontal: space.md, paddingVertical: 10, color: c.text, backgroundColor: c.bg }
  return (
    <Card style={{ gap: space.md }}>
      <Title icon="link" text="Links" right={links.length ? `${links.length}` : undefined} />
      {links.map((l) => (
        <Pressable key={l.id} onPress={() => Linking.openURL(l.url)} onLongPress={() => remove(l)} accessibilityRole="link"
          accessibilityHint="Long-press to remove" style={{ flexDirection: 'row', alignItems: 'center', gap: space.sm, minHeight: 40 }}>
          <Ionicons name="open-outline" size={16} color={c.link} />
          <Text style={{ color: c.link, flex: 1 }} numberOfLines={1}>{l.title || l.url}</Text>
        </Pressable>
      ))}
      <TextInput value={url} onChangeText={setUrl} placeholder="https://…" placeholderTextColor={c.faint} autoCapitalize="none"
        keyboardType="url" autoCorrect={false} style={input} accessibilityLabel="Link address" />
      {url.trim() ? (
        <View style={{ flexDirection: 'row', gap: space.sm }}>
          <TextInput value={title} onChangeText={setTitle} placeholder="Title (optional)" placeholderTextColor={c.faint}
            style={[input, { flex: 1 }]} accessibilityLabel="Link title" />
          <Pressable onPress={() => void add()} accessibilityRole="button" accessibilityLabel="Add link"
            style={{ paddingHorizontal: space.lg, borderRadius: radius.md, backgroundColor: c.primary, alignItems: 'center', justifyContent: 'center' }}>
            <Text style={{ color: c.primaryText, fontWeight: '800' }}>Add</Text>
          </Pressable>
        </View>
      ) : null}
    </Card>
  )
}
