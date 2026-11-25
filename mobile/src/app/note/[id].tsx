import { useCallback, useEffect, useRef, useState } from 'react'
import { KeyboardAvoidingView, Platform, Pressable, ScrollView, Text, TextInput, View } from 'react-native'
import { Stack, useLocalSearchParams } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { getItemDetail } from '@/data/itemDetail'
import { saveNote } from '@/data/notes'
import { NoteView, type NoteViewHandle } from '@/note/NoteView'
import { useDataVersion } from '@/state/stores'
import { Button, Chip, ErrorText, Loading } from '@/ui/components'
import { space, useColors } from '@/ui/theme'

/** Snippets for things that are fiddly to type on a phone keyboard; `|` marks where the cursor goes. */
const SNIPPETS: { label: string; text: string }[] = [
  { label: 'Q/A', text: '\nQ: |\nA: \n' },
  { label: '[[ ]]', text: '[[|]]' },
  { label: 'B', text: '**|**' },
  { label: '` `', text: '`|`' },
  { label: '☐', text: '\n- [ ] |' },
  { label: '•', text: '\n- |' },
  { label: '#', text: '\n## |' },
  { label: '```', text: '\n```\n|\n```\n' },
  { label: '$x$', text: '$|$' },
]

const AUTOSAVE_MS = 1200

export default function NoteEditor() {
  const { id } = useLocalSearchParams<{ id: string }>()
  const itemId = Number(id)
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const viewer = useRef<NoteViewHandle>(null)

  const [loaded, setLoaded] = useState<Awaited<ReturnType<typeof getItemDetail>> | undefined>()
  const [text, setText] = useState('')
  const [htmlSource, setHtmlSource] = useState<string | null>(null) // a rich-text note waiting to be converted
  const [mode, setMode] = useState<'write' | 'preview'>('write')
  const [status, setStatus] = useState<'saved' | 'dirty' | 'saving'>('saved')
  const [error, setError] = useState<string | null>(null)
  const [selection, setSelection] = useState({ start: 0, end: 0 })
  const saved = useRef('')
  const latest = useRef('')
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null)

  useEffect(() => {
    getItemDetail(db, itemId).then((d) => {
      setLoaded(d)
      if (!d) return
      if (d.note.format === 'HTML' && d.note.content) setHtmlSource(d.note.content)
      else {
        setText(d.note.content ?? '')
        saved.current = latest.current = d.note.content ?? ''
      }
    })
  }, [db, itemId])

  const flush = useCallback(async () => {
    if (timer.current) clearTimeout(timer.current)
    timer.current = null
    const content = latest.current
    if (content === saved.current) return
    setStatus('saving')
    try {
      await saveNote(db, itemId, content, 'MARKDOWN')
      saved.current = content
      setStatus(latest.current === content ? 'saved' : 'dirty')
      setError(null)
      bump()
    } catch (e) {
      setStatus('dirty')
      setError(e instanceof Error ? e.message : String(e))
    }
  }, [db, itemId, bump])

  // save when leaving the screen
  useEffect(() => () => void flush(), [flush])

  const onChange = (value: string) => {
    setText(value)
    latest.current = value
    setStatus('dirty')
    if (timer.current) clearTimeout(timer.current)
    timer.current = setTimeout(() => void flush(), AUTOSAVE_MS)
  }

  const insert = (snippet: string) => {
    const caret = snippet.indexOf('|')
    const clean = snippet.replace('|', '')
    const next = text.slice(0, selection.start) + clean + text.slice(selection.end)
    onChange(next)
    const at = selection.start + (caret < 0 ? clean.length : caret)
    setSelection({ start: at, end: at })
  }

  const convert = async () => {
    if (!htmlSource || !viewer.current) return
    const md = await viewer.current.toMarkdown(htmlSource)
    setHtmlSource(null)
    onChange(md)
  }

  if (loaded === undefined) return <Loading />
  if (loaded === null) return <ErrorText message="This topic no longer exists." />

  const statusLabel = status === 'saving' ? 'Saving…' : status === 'dirty' ? 'Unsaved' : 'Saved'

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
      <Stack.Screen
        options={{
          title: loaded.item.name,
          headerRight: () => <Text style={{ color: status === 'saved' ? c.muted : c.warn, fontSize: 13 }}>{statusLabel}</Text>,
        }}
      />

      {htmlSource ? (
        <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg }}>
          <Text style={{ color: c.text }}>
            This note was written with the rich-text editor on your computer. On the phone, notes are edited as Markdown: converting keeps
            headings, lists, code, links and highlights. The desktop shows the converted note in its Markdown editor.
          </Text>
          <Button title="Convert to Markdown and edit" onPress={convert} />
          <Text style={{ color: c.muted, fontSize: 13 }}>Nothing changes until you edit and the note saves.</Text>
          <NoteView ref={viewer} content={htmlSource} format="HTML" names={loaded.planNames} />
        </ScrollView>
      ) : (
        <View style={{ flex: 1 }}>
          <View style={{ flexDirection: 'row', gap: space.sm, paddingHorizontal: space.lg, paddingVertical: space.sm }} accessibilityRole="radiogroup">
            <Chip label="Write" on={mode === 'write'} onPress={() => setMode('write')} />
            <Chip label="Preview" on={mode === 'preview'} onPress={() => setMode('preview')} />
          </View>
          <ErrorText message={error} />
          {mode === 'write' ? (
            <>
              <TextInput
                value={text}
                onChangeText={onChange}
                selection={selection}
                onSelectionChange={(e) => setSelection(e.nativeEvent.selection)}
                multiline
                textAlignVertical="top"
                placeholder={'What did you learn?\n\nQ: A question\nA: Its answer   ← becomes a flashcard\n\n[[Other topic]] links a note'}
                placeholderTextColor={c.faint}
                style={{
                  flex: 1, color: c.text, backgroundColor: c.card, paddingHorizontal: space.lg, paddingVertical: space.md,
                  fontSize: 15, lineHeight: 22, fontFamily: Platform.OS === 'android' ? 'monospace' : 'Menlo',
                }}
                accessibilityLabel="Note text (Markdown)"
                onBlur={() => void flush()}
              />
              <ScrollView
                horizontal
                keyboardShouldPersistTaps="always"
                style={{ flexGrow: 0, borderTopColor: c.border, borderTopWidth: 1, backgroundColor: c.card }}
                contentContainerStyle={{ gap: space.xs, padding: space.xs }}
              >
                {SNIPPETS.map((s) => (
                  <Pressable
                    key={s.label}
                    onPress={() => insert(s.text)}
                    accessibilityRole="button"
                    accessibilityLabel={`Insert ${s.label}`}
                    style={{ minWidth: 44, minHeight: 40, paddingHorizontal: space.sm, alignItems: 'center', justifyContent: 'center', borderRadius: 8, borderWidth: 1, borderColor: c.border }}
                  >
                    <Text style={{ color: c.text, fontWeight: '600' }}>{s.label}</Text>
                  </Pressable>
                ))}
              </ScrollView>
            </>
          ) : (
            <ScrollView contentContainerStyle={{ padding: space.lg }}>
              {text.trim() ? (
                <NoteView content={text} format="MARKDOWN" names={loaded.planNames} />
              ) : (
                <Text style={{ color: c.muted }}>Nothing to preview yet.</Text>
              )}
            </ScrollView>
          )}
        </View>
      )}
    </KeyboardAvoidingView>
  )
}
