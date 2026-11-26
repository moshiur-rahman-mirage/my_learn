import { htmlToPlainText } from './text'
import type { NoteFormat } from './types'

const WORD = /[\p{L}\p{N}]+(?:['’][\p{L}\p{N}]+)*/gu
const WORDS_PER_MINUTE = 180

export interface ReadingEstimate {
  wordCount: number
  minutes: number
}

function countWords(text: string): number {
  return [...text.matchAll(WORD)].length
}

function markdownText(text: string): string {
  return text
    .replace(/!\[([^\]]*)\]\([^)]+\)/g, '$1')
    .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1')
    .replace(/<\/?[^>]+>/g, ' ')
    .replace(/[`*_~#>]/g, ' ')
}

function estimateMarkdown(content: string): { words: number; weightedWords: number; imageSeconds: number } {
  let codeWords = 0
  let text = content.replace(/(```|~~~)[^\n]*\n([\s\S]*?)\1/g, (_block, _fence: string, code: string) => {
    codeWords += countWords(code)
    return ' '
  })

  let tableWords = 0
  text = text.split('\n').map((line) => {
    if (!line.includes('|')) return line
    if (!/^\s*\|?[\s:|-]+\|?\s*$/.test(line)) tableWords += countWords(line)
    return ' '
  }).join('\n')

  const imageCount = [...text.matchAll(/!\[[^\]]*\]\([^)]+\)/g)].length
  text = text.replace(/!\[[^\]]*\]\([^)]+\)/g, ' ')
  const normalWords = countWords(markdownText(text))
  return {
    words: normalWords + codeWords + tableWords,
    weightedWords: normalWords + codeWords * 1.5 + tableWords * 1.3,
    imageSeconds: imageCount * 30,
  }
}

function estimateHtml(content: string): { words: number; weightedWords: number; imageSeconds: number } {
  let codeWords = 0
  let tableWords = 0
  let text = content.replace(/<pre\b[^>]*>([\s\S]*?)<\/pre\s*>/gi, (_block, code: string) => {
    codeWords += countWords(htmlToPlainText(code))
    return ' '
  })
  text = text.replace(/<code\b[^>]*>([\s\S]*?)<\/code\s*>/gi, (_block, code: string) => {
    codeWords += countWords(htmlToPlainText(code))
    return ' '
  })
  text = text.replace(/<table\b[^>]*>([\s\S]*?)<\/table\s*>/gi, (_block, table: string) => {
    tableWords += countWords(htmlToPlainText(table))
    return ' '
  })
  const imageCount = [...text.matchAll(/<img\b/gi)].length
  const normalWords = countWords(htmlToPlainText(text))
  return {
    words: normalWords + codeWords + tableWords,
    weightedWords: normalWords + codeWords * 1.5 + tableWords * 1.3,
    imageSeconds: imageCount * 30,
  }
}

/** Estimates note reading time at 180 words per minute, weighting code and tables. */
export function estimateReadingTime(content: string, format: NoteFormat): ReadingEstimate {
  const { words, weightedWords, imageSeconds } = format === 'HTML'
    ? estimateHtml(content)
    : estimateMarkdown(content)
  return {
    wordCount: words,
    minutes: words === 0 ? 0 : Math.max(1, Math.ceil((weightedWords + imageSeconds * WORDS_PER_MINUTE / 60) / WORDS_PER_MINUTE)),
  }
}

export interface ShortReadCandidate extends ReadingEstimate {
  itemId: number
  name: string
  planName: string
  parentName: string | null
}

/** Chooses the maximum number of minutes of unread notes that fit the budget. */
export function recommendShortReads(candidates: ShortReadCandidate[], budget: number): ShortReadCandidate[] {
  if (!Number.isInteger(budget) || budget <= 0) return []
  const reachable: (ShortReadCandidate[] | undefined)[] = Array(budget + 1)
  reachable[0] = []
  for (const candidate of candidates) {
    const cost = candidate.minutes
    if (!Number.isInteger(cost) || cost <= 0 || cost > budget) continue
    for (let total = budget - cost; total >= 0; total--) {
      const current = reachable[total]
      if (current && !reachable[total + cost]) reachable[total + cost] = [...current, candidate]
    }
  }
  for (let total = budget; total > 0; total--) {
    if (reachable[total]) return reachable[total]!
  }
  return []
}

/** Makes a randomized, budget-fitting suggestion and changes its lead topic when possible. */
export function recommendRandomShortReads(
  candidates: ShortReadCandidate[],
  budget: number,
  previousFirstId: number | null = null,
  random: () => number = Math.random,
): ShortReadCandidate[] {
  if (!Number.isInteger(budget) || budget <= 0) return []
  const shuffled = candidates.filter((candidate) =>
    Number.isInteger(candidate.minutes) && candidate.minutes > 0 && candidate.minutes <= budget,
  )
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1))
    const item = shuffled[i]
    shuffled[i] = shuffled[j]
    shuffled[j] = item
  }
  if (shuffled.length === 0) return []

  const first = shuffled.find((candidate) => candidate.itemId !== previousFirstId) ?? shuffled[0]
  const remaining = shuffled.filter((candidate) => candidate.itemId !== first.itemId)
  return [first, ...recommendShortReads(remaining, budget - first.minutes)]
}
