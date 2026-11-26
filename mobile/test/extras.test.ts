import { describe, expect, it } from 'vitest'
import { addLink, allTags, deleteLink, deleteTime, logTime, normalizeTags, normalizeUrl, rateConfidence, setTags, timeEntries } from '../src/data/extras'
import { setCompletion } from '../src/data/items'
import { reviewTopic } from '../src/data/reviews'
import { createItem, createPlan } from '../src/data/structure'
import { addDays, today } from '../src/domain/dates'
import { migratedDb } from './nodeDb'

async function setup() {
  const db = await migratedDb()
  const p = await createPlan(db, { name: 'P' })
  const a = await createItem(db, p, { name: 'A', type: 'TOPIC', parentId: null })
  const b = await createItem(db, p, { name: 'B', type: 'TOPIC', parentId: null })
  return { db, a, b }
}

describe('tags (TagService)', () => {
  it('normalizes: trims, drops #, unique ignoring case, limits', () => {
    expect(normalizeTags([' #Interview ', 'interview', 'Spring  Boot', ''])).toEqual(['Interview', 'Spring Boot'])
    expect(() => normalizeTags(['x'.repeat(41)])).toThrow(/longer than 40/)
    expect(() => normalizeTags(Array.from({ length: 21 }, (_, i) => `t${i}`))).toThrow(/at most 20/)
  })

  it('keeps the existing spelling, removes unused tags, is recorded for sync', async () => {
    const { db, a, b } = await setup()
    await setTags(db, a, ['Interview', 'revise'])
    expect(await setTags(db, b, ['INTERVIEW'])).toEqual(['Interview'])
    await setTags(db, a, [])
    expect(await allTags(db)).toEqual([{ name: 'Interview', items: 1 }])
    expect(await db.first("SELECT COUNT(*) AS n FROM sync_change WHERE entity = 'itemTag'")).toEqual({ n: 3 })
  })
})

describe('links (LinkService)', () => {
  it('adds https:// and refuses other schemes', () => {
    expect(normalizeUrl(' docs.oracle.com/java ')).toBe('https://docs.oracle.com/java')
    expect(() => normalizeUrl('ftp://x.org')).toThrow(/Only http/)
    expect(() => normalizeUrl('  ')).toThrow(/Enter a link/)
  })

  it('adds in order and deletes', async () => {
    const { db, a } = await setup()
    await addLink(db, a, 'a.dev', ' A ')
    await addLink(db, a, 'https://b.dev', '')
    const links = await db.all<{ id: number; url: string; title: string | null; display_order: number }>(
      'SELECT id, url, title, display_order FROM item_link WHERE item_id = ? ORDER BY display_order', [a])
    expect(links.map((l) => [l.url, l.title, l.display_order])).toEqual([['https://a.dev', 'A', 0], ['https://b.dev', null, 1]])
    await deleteLink(db, links[0].id)
    expect(await db.first('SELECT COUNT(*) AS n FROM item_link')).toEqual({ n: 1 })
  })
})

describe('time log (TimeLogService)', () => {
  it('logs 1..1440 minutes, lists newest first, deletes', async () => {
    const { db, a } = await setup()
    await logTime(db, a, 25)
    await logTime(db, a, 50)
    await expect(logTime(db, a, 0)).rejects.toThrow(/between 1 and 1440/)
    await expect(logTime(db, a, 1441)).rejects.toThrow()
    const entries = await timeEntries(db, a)
    expect(entries.map((e) => e.minutes).sort()).toEqual([25, 50])
    await deleteTime(db, entries[0].id)
    expect(await timeEntries(db, a)).toHaveLength(1)
  })
})

describe('confidence (ConfidenceService)', () => {
  it('a low rating on a completed topic brings the review forward to tomorrow', async () => {
    const { db, a } = await setup()
    await setCompletion(db, a, true)
    await reviewTopic(db, a, true) // next review in 7 days
    expect(await rateConfidence(db, a, 2)).toBe(addDays(today(), 1))
    expect(await db.first('SELECT stage, due_on FROM item_review WHERE item_id = ?', [a])).toEqual({ stage: 0, due_on: addDays(today(), 1) })
  })

  it('a good rating, or an unfinished topic, schedules nothing; null clears', async () => {
    const { db, a, b } = await setup()
    await setCompletion(db, a, true)
    expect(await rateConfidence(db, a, 5)).toBeNull()
    expect(await rateConfidence(db, b, 1)).toBeNull() // not completed
    await expect(rateConfidence(db, a, 6)).rejects.toThrow(/between 1 and 5/)
    await rateConfidence(db, a, null)
    expect(await db.first('SELECT * FROM item_confidence WHERE item_id = ?', [a])).toBeNull()
  })
})
