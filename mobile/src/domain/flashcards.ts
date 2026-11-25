import { sha256Hex } from './hash'
import { collapseJavaSpaces, htmlToLines, javaIsBlank, javaStrip, JDOT, JS } from './text'
import type { NoteFormat } from './types'

/**
 * Flashcards written in notes as "Q: … / A: …" (port of backend flashcard/FlashcardParser;
 * keep the two in step: card keys must match on both devices or schedules won't sync).
 */
export const MAX_CARDS = 300
export const MAX_QUESTION = 2_000
export const MAX_ANSWER = 20_000

/** Review intervals in days after each "Remembered" (FlashcardService.INTERVALS). */
export const CARD_INTERVALS = [1, 3, 7, 14, 30, 60, 120]

export interface ParsedCard {
  question: string
  answer: string
}

export interface Card extends ParsedCard {
  key: string
}

const MARKER = new RegExp(
  `^${JS}*(?:[-*+]${JS}+|[0-9]+[.)]${JS}+)?(?:\\*\\*|__)?([QqAa])(?:\\*\\*|__)?${JS}*[:：](?:\\*\\*|__)?${JS}?(${JDOT}*)$`,
)
const FENCE = new RegExp(`^${JS}*(\`\`\`|~~~)`)
const HEADING = new RegExp(`^${JS}{0,3}#{1,6}${JS}`)

/** The normalized question a card's key is the hash of: lower case, single spaces, stripped. */
export function normalizeQuestion(question: string): string {
  return javaStrip(collapseJavaSpaces(question.toLowerCase()))
}

/** Card key: the first 12 bytes of SHA-256 of the normalized question, as 24 hex characters. */
export async function cardKey(question: string): Promise<string> {
  return (await sha256Hex(normalizeQuestion(question))).substring(0, 24)
}

/** Parses the cards of a note, in order; the same question twice is one card. */
export function parseCards(content: string | null | undefined, format: NoteFormat): ParsedCard[] {
  if (content == null || javaIsBlank(content)) return []
  const text = format === 'HTML' ? htmlToLines(content) : content
  const lines = text.replaceAll('\r\n', '\n').replaceAll('\r', '\n').split('\n')
  const cards: ParsedCard[] = []
  const seen = new Set<string>()
  let question: string | null = null
  let answer: string | null = null
  let inFence = false

  const add = () => {
    if (question === null || answer === null || cards.length >= MAX_CARDS) return
    let q = javaStrip(question)
    let a = javaStrip(answer)
    if (q.length === 0 || a.length === 0) return
    if (q.length > MAX_QUESTION) q = q.substring(0, MAX_QUESTION)
    if (a.length > MAX_ANSWER) a = a.substring(0, MAX_ANSWER)
    const normal = normalizeQuestion(q)
    if (!seen.has(normal)) {
      seen.add(normal)
      cards.push({ question: q, answer: a })
    }
  }

  for (const line of lines) {
    if (FENCE.test(line)) {
      inFence = !inFence
      if (answer !== null) answer += line + '\n'
      continue
    }
    if (inFence) {
      if (answer !== null) answer += line + '\n'
      continue
    }
    const m = MARKER.exec(line)
    const isQ = m !== null && m[1].toLowerCase() === 'q'
    const isA = m !== null && m[1].toLowerCase() === 'a'
    if (isQ) {
      add()
      question = m![2]
      answer = null
    } else if (isA && question !== null && answer === null) {
      answer = m![2] + '\n'
    } else if (javaIsBlank(line) || HEADING.test(line)) {
      if (answer !== null) {
        add()
        question = null
        answer = null
      } else if (question !== null && HEADING.test(line)) {
        question = null // a question without an answer
      }
    } else if (answer !== null) {
      answer += line + '\n'
    } else if (question !== null) {
      question += '\n' + line
    }
    if (cards.length >= MAX_CARDS) return cards
  }
  add()
  return cards
}

/** Parses a note's cards and computes their keys. */
export async function cardsOf(content: string | null | undefined, format: NoteFormat): Promise<Card[]> {
  const parsed = parseCards(content, format)
  return Promise.all(parsed.map(async (c) => ({ ...c, key: await cardKey(c.question) })))
}
