import type { ReactNode } from 'react'
import { Alert, Pressable, ScrollView, Text, View, useColorScheme } from 'react-native'
import Ionicons from '@expo/vector-icons/Ionicons'
import Constants from 'expo-constants'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { NoteView } from '@/note/NoteView'
import { sharePhoneBackup } from '@/platform/share'
import { DAILY_GOALS, LINE_HEIGHTS, TEXT_SCALES, usePrefs, type Accent, type ReadingFont, type ReadingTheme, type ThemeMode } from '@/state/prefs'
import { ACCENTS, radius, space, useColors } from '@/ui/theme'

const SAMPLE = `## Why practice beats rereading

Retrieval practice strengthens memory: each time you **recall** an idea, it becomes easier to recall again.

> Read a section, close it, then explain it in your own words.`

function Section({ title, children }: { title: string; children: ReactNode }) {
  const c = useColors()
  return (
    <View style={{ gap: space.sm }}>
      <Text style={{ color: c.muted, fontSize: 13, fontWeight: '700', letterSpacing: 0.5, textTransform: 'uppercase' }}>{title}</Text>
      <View style={{ backgroundColor: c.card, borderRadius: radius.card, borderWidth: 1, borderColor: c.border, padding: space.lg, gap: space.lg }}>
        {children}
      </View>
    </View>
  )
}

/** A row of equal-width choices (segmented control). */
function Segmented<T extends string | number>({ label, value, options, onChange }: {
  label?: string
  value: T
  options: { value: T; label: string; icon?: keyof typeof Ionicons.glyphMap }[]
  onChange: (v: T) => void
}) {
  const c = useColors()
  return (
    <View style={{ gap: space.xs }}>
      {label ? <Text style={{ color: c.text, fontWeight: '600' }}>{label}</Text> : null}
      <View style={{ flexDirection: 'row', backgroundColor: c.track, borderRadius: radius.md, padding: 4, gap: 4 }} accessibilityRole="radiogroup" accessibilityLabel={label}>
        {options.map((o) => {
          const on = o.value === value
          return (
            <Pressable
              key={String(o.value)}
              onPress={() => onChange(o.value)}
              accessibilityRole="radio"
              accessibilityState={{ checked: on }}
              style={{
                flex: 1, minHeight: 44, borderRadius: radius.sm, alignItems: 'center', justifyContent: 'center', flexDirection: 'row', gap: 6,
                backgroundColor: on ? c.card : 'transparent', elevation: on ? 2 : 0,
              }}
            >
              {o.icon ? <Ionicons name={o.icon} size={16} color={on ? c.primary : c.muted} /> : null}
              <Text style={{ color: on ? c.text : c.muted, fontWeight: on ? '700' : '500' }}>{o.label}</Text>
            </Pressable>
          )
        })}
      </View>
    </View>
  )
}

const BACKGROUNDS: { value: ReadingTheme; label: string; swatch: string; ink: string }[] = [
  { value: 'auto', label: 'Auto', swatch: 'transparent', ink: '#888' },
  { value: 'paper', label: 'Paper', swatch: '#ffffff', ink: '#222' },
  { value: 'sepia', label: 'Sepia', swatch: '#f6ecd9', ink: '#433422' },
  { value: 'night', label: 'Night', swatch: '#15181b', ink: '#e6e9eb' },
]

export default function SettingsScreen() {
  const db = useDb()
  const c = useColors()
  const p = usePrefs()
  const scheme = useColorScheme()
  const backup = async () => {
    try {
      await sharePhoneBackup(db)
    } catch (e) {
      Alert.alert('Backup failed', e instanceof Error ? e.message : String(e))
    }
  }

  return (
    <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.xl, paddingBottom: space.xl * 2 }}>
      <Section title="Appearance">
        <Segmented<ThemeMode>
          label="Theme"
          value={p.theme}
          onChange={(v) => p.set(db, 'theme', v)}
          options={[
            { value: 'system', label: 'System', icon: 'phone-portrait-outline' },
            { value: 'light', label: 'Light', icon: 'sunny-outline' },
            { value: 'dark', label: 'Dark', icon: 'moon-outline' },
          ]}
        />
        <View style={{ gap: space.xs }}>
          <Text style={{ color: c.text, fontWeight: '600' }}>Accent colour</Text>
          <View style={{ flexDirection: 'row', justifyContent: 'space-between' }} accessibilityRole="radiogroup">
            {(Object.keys(ACCENTS) as Accent[]).map((a) => {
              const on = p.accent === a
              const swatch = ACCENTS[a][scheme === 'dark' ? 'dark' : 'light'].primary
              return (
                <Pressable key={a} onPress={() => p.set(db, 'accent', a)} accessibilityRole="radio" accessibilityState={{ checked: on }}
                  accessibilityLabel={`${ACCENTS[a].label} accent`} style={{ alignItems: 'center', gap: 4, minWidth: 52 }}>
                  <View style={{ width: 44, height: 44, borderRadius: 22, backgroundColor: swatch, borderWidth: on ? 3 : 0, borderColor: c.text,
                    alignItems: 'center', justifyContent: 'center' }}>
                    {on ? <Ionicons name="checkmark" size={20} color={ACCENTS[a][scheme === 'dark' ? 'dark' : 'light'].primaryText} /> : null}
                  </View>
                  <Text style={{ color: on ? c.text : c.muted, fontSize: 12, fontWeight: on ? '700' : '400' }}>{ACCENTS[a].label}</Text>
                </Pressable>
              )
            })}
          </View>
        </View>
      </Section>

      <Section title="Reading">
        <Segmented<ReadingFont>
          label="Font"
          value={p.readingFont}
          onChange={(v) => p.set(db, 'readingFont', v)}
          options={[
            { value: 'sans', label: 'Sans' },
            { value: 'serif', label: 'Serif' },
            { value: 'mono', label: 'Mono' },
          ]}
        />
        <Segmented<number>
          label="Text size"
          value={p.textScale}
          onChange={(v) => p.set(db, 'textScale', v)}
          options={TEXT_SCALES.map((s, i) => ({ value: s, label: ['A−', 'A', 'A+', 'A++', 'A+++'][i] }))}
        />
        <Segmented<number>
          label="Line spacing"
          value={p.lineHeight}
          onChange={(v) => p.set(db, 'lineHeight', v)}
          options={LINE_HEIGHTS.map((h, i) => ({ value: h, label: ['Compact', 'Comfortable', 'Airy'][i] }))}
        />
        <View style={{ gap: space.xs }}>
          <Text style={{ color: c.text, fontWeight: '600' }}>Reading background</Text>
          <View style={{ flexDirection: 'row', gap: space.md }} accessibilityRole="radiogroup">
            {BACKGROUNDS.map((b) => {
              const on = p.readingTheme === b.value
              return (
                <Pressable key={b.value} onPress={() => p.set(db, 'readingTheme', b.value)} accessibilityRole="radio"
                  accessibilityState={{ checked: on }} accessibilityLabel={`${b.label} background`} style={{ alignItems: 'center', gap: 4, flex: 1 }}>
                  <View style={{
                    width: 52, height: 52, borderRadius: 26, borderWidth: on ? 3 : 1, borderColor: on ? c.primary : c.border,
                    backgroundColor: b.value === 'auto' ? c.bg : b.swatch, alignItems: 'center', justifyContent: 'center',
                  }}>
                    <Text style={{ color: b.value === 'auto' ? c.text : b.ink, fontWeight: '700' }}>{b.value === 'auto' ? 'A' : 'Aa'}</Text>
                  </View>
                  <Text style={{ color: on ? c.text : c.muted, fontSize: 12, fontWeight: on ? '700' : '400' }}>{b.label}</Text>
                </Pressable>
              )
            })}
          </View>
        </View>
        <View style={{ height: 230, borderRadius: radius.md, overflow: 'hidden', borderWidth: 1, borderColor: c.border }}>
          <NoteView content={SAMPLE} format="MARKDOWN" names={[]} mode="page" />
        </View>
      </Section>

      <Section title="Daily goal">
        <Segmented<number>
          label="Learning actions a day"
          value={p.dailyGoal}
          onChange={(v) => p.set(db, 'dailyGoal', v)}
          options={DAILY_GOALS.map((g) => ({ value: g, label: String(g) }))}
        />
        <Text style={{ color: c.muted, fontSize: 13 }}>Each ticked topic, review and flashcard counts as one action.</Text>
      </Section>

      <Section title="Data">
        <Pressable onPress={() => void backup()} accessibilityRole="button"
          style={{ flexDirection: 'row', alignItems: 'center', gap: space.md, minHeight: 44 }}>
          <Ionicons name="archive-outline" size={20} color={c.primary} />
          <View style={{ flex: 1 }}>
            <Text style={{ color: c.text, fontWeight: '600' }}>Back up this phone</Text>
            <Text style={{ color: c.muted, fontSize: 12 }}>A complete copy of your plans, notes and progress, to keep in Drive or Files</Text>
          </View>
          <Ionicons name="share-outline" size={18} color={c.faint} />
        </Pressable>
        <Text style={{ color: c.muted, fontSize: 12 }}>To export one plan as CSV (for the desktop or Excel), open the plan → ⋯ → Export as CSV.</Text>
      </Section>

      <Section title="Sync">
        <Pressable onPress={() => router.push('/sync')} accessibilityRole="button"
          style={{ flexDirection: 'row', alignItems: 'center', gap: space.md, minHeight: 44 }}>
          <Ionicons name="sync" size={20} color={c.primary} />
          <Text style={{ color: c.text, flex: 1, fontWeight: '600' }}>Phone ⇄ computer sync</Text>
          <Ionicons name="chevron-forward" size={18} color={c.faint} />
        </Pressable>
      </Section>

      <Text style={{ color: c.faint, textAlign: 'center', fontSize: 12 }}>
        Learning Tracker {Constants.expoConfig?.version ?? ''} · your data stays on this phone and your computer
      </Text>
    </ScrollView>
  )
}
