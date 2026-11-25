import { readFileSync } from 'node:fs'
import { join } from 'node:path'
import { describe, expect, it } from 'vitest'
import { cardsOf } from '../src/domain/flashcards'
import { htmlToLines, htmlToPlainText, wikiKey } from '../src/domain/text'
import type { NoteFormat } from '../src/domain/types'

/**
 * The desktop's own output for tricky notes (backend FlashcardParityFixtureTest generates and
 * checks the same file): the TypeScript ports must produce exactly the same cards, keys and text.
 */
interface Case {
  name: string
  format: NoteFormat
  content: string
  cards: { key: string; question: string; answer: string }[]
  lines?: string
  plainText?: string
  wikiKey: string
}

const fixture = JSON.parse(
  readFileSync(join(__dirname, '..', '..', 'backend', 'src', 'test', 'resources', 'parity', 'flashcard-fixtures.json'), 'utf8'),
) as { cases: Case[] }

describe('parity with the desktop (Java) implementation', () => {
  it('has cases', () => {
    expect(fixture.cases.length).toBeGreaterThan(10)
  })

  for (const c of fixture.cases) {
    it(c.name, async () => {
      const cards = await cardsOf(c.content, c.format)
      expect(cards.map((x) => ({ key: x.key, question: x.question, answer: x.answer }))).toEqual(
        c.cards.map((x) => ({ key: x.key, question: x.question, answer: x.answer })),
      )
      if (c.format === 'HTML') {
        expect(htmlToLines(c.content)).toBe(c.lines)
        expect(htmlToPlainText(c.content)).toBe(c.plainText)
      }
      expect(wikiKey(c.content)).toBe(c.wikiKey)
    })
  }
})
