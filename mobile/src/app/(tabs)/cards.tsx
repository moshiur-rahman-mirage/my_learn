import { useState } from 'react'
import { Pressable, ScrollView, Text, View } from 'react-native'
import { router } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import { dueCards, reviewCard, type DueCard } from '@/data/flashcards'
import { NoteView } from '@/note/NoteView'
import { useDataVersion } from '@/state/stores'
import { useQuery } from '@/state/useQuery'
import { Button, Card, EmptyState, ErrorText, Loading } from '@/ui/components'
import { space, useColors } from '@/ui/theme'

/**
 * Flashcard study: due cards from every active plan, one at a time. Cards come from "Q: / A:"
 * lines in notes; Remembered spaces them out (1, 3, 7, 14, 30, 60, 120 days), Forgot brings
 * them back today.
 */
export default function CardsScreen() {
  const db = useDb()
  const c = useColors()
  const bump = useDataVersion((s) => s.bump)
  const { data, error } = useQuery((d) => dueCards(d), 'cards')
  const [shown, setShown] = useState(false)
  const [busy, setBusy] = useState(false)
  const [doneCount, setDoneCount] = useState(0)

  if (!data) return error ? <ErrorText message={error} /> : <Loading />
  const card: DueCard | undefined = data[0]

  const answer = async (remembered: boolean) => {
    if (!card) return
    setBusy(true)
    try {
      await reviewCard(db, card.id, remembered)
      setShown(false)
      setDoneCount((n) => n + 1)
      bump() // reloads: the next due card comes first (a forgotten one goes to the back of today's queue)
    } finally {
      setBusy(false)
    }
  }

  if (!card) {
    return (
      <EmptyState
        title={doneCount ? `Done: ${doneCount} card${doneCount === 1 ? '' : 's'} reviewed` : 'No cards due'}
        message="Write “Q: question” and “A: answer” lines in a note and they become flashcards. Due cards show up here."
      />
    )
  }

  return (
    <ScrollView contentContainerStyle={{ padding: space.lg, gap: space.lg, flexGrow: 1 }}>
      <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
        <Text style={{ color: c.text, fontSize: 24, fontWeight: '800' }}>Study cards</Text>
        <Text style={{ color: c.primary, fontSize: 13, fontWeight: '700', backgroundColor: c.chipOn, borderRadius: 999, paddingHorizontal: space.md, paddingVertical: space.xs }}>
          {data.length} due{doneCount ? ` · ${doneCount} done` : ''}
        </Text>
      </View>
      <Card style={{ gap: space.lg, padding: space.xl }}>
        <Pressable onPress={() => router.push({ pathname: '/item/[id]', params: { id: String(card.itemId) } })} accessibilityRole="link">
          <Text style={{ color: c.primary, fontSize: 12, fontWeight: '700' }}>
            {card.planName} › {card.itemName}
          </Text>
        </Pressable>
        <Text selectable style={{ color: c.text, fontSize: 22, lineHeight: 30, fontWeight: '700' }}>{card.question}</Text>
        {shown ? (
          <View style={{ borderTopColor: c.border, borderTopWidth: 1, paddingTop: space.md }}>
            {/* answers are Markdown in Markdown notes; HTML notes' answers are plain lines */}
            <NoteView content={card.answer} format="MARKDOWN" names={[]} />
          </View>
        ) : null}
      </Card>
      <View style={{ flex: 1 }} />
      {shown ? (
        <View style={{ flexDirection: 'row', gap: space.sm }}>
          <Button style={{ flex: 1 }} kind="secondary" title="Forgot" busy={busy} onPress={() => answer(false)} />
          <Button style={{ flex: 1 }} title="Remembered" busy={busy} onPress={() => answer(true)} />
        </View>
      ) : (
        <Button title="Show answer" onPress={() => setShown(true)} />
      )}
    </ScrollView>
  )
}
