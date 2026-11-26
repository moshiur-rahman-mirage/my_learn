import { describe, expect, it } from 'vitest'
import { csvField, exportPlanCsv, slug } from '../src/data/exportCsv'
import { setCompletion } from '../src/data/items'
import { saveNote } from '../src/data/notes'
import { createItem, createPlan } from '../src/data/structure'
import { migratedDb } from './nodeDb'

/** A strict RFC 4180 parser, to prove the output reads back exactly. */
function parseCsv(text: string): string[][] {
  const rows: string[][] = []
  let row: string[] = []
  let field = ''
  let quoted = false
  for (let i = 0; i < text.length; i++) {
    const ch = text[i]
    if (quoted) {
      if (ch === '"' && text[i + 1] === '"') { field += '"'; i++ } else if (ch === '"') quoted = false
      else field += ch
    } else if (ch === '"') quoted = true
    else if (ch === ',') { row.push(field); field = '' } else if (ch === '\r' && text[i + 1] === '\n') { row.push(field); rows.push(row); row = []; field = ''; i++ } else field += ch
  }
  return rows
}

describe('CSV export (desktop ExportService format)', () => {
  it('quotes only when needed', () => {
    expect(csvField('plain')).toBe('plain')
    expect(csvField('a, b')).toBe('"a, b"')
    expect(csvField('say "hi"')).toBe('"say ""hi"""')
    expect(csvField('two\nlines')).toBe('"two\nlines"')
    expect(csvField(' padded')).toBe('" padded"')
    expect(csvField(3)).toBe('3')
  })

  it('slugs names like the desktop', () => {
    expect(slug('Core Java — 2026!')).toBe('core-java-2026')
    expect(slug('বাংলা পরিকল্পনা')).toBe('বাংলা-পরিকল্পনা')
    expect(slug('***')).toBe('export')
  })

  it('writes the tree in order with levels, completion, version and notes; reads back exactly', async () => {
    const db = await migratedDb()
    const p = await createPlan(db, { name: 'Core Java' })
    const m = await createItem(db, p, { name: 'Collections', type: 'MODULE', parentId: null, priority: 'MUST' })
    const a = await createItem(db, p, { name: 'ArrayList', type: 'TOPIC', parentId: m, description: 'dynamic, "growable" array' })
    await createItem(db, p, { name: 'Empty module', type: 'MODULE', parentId: null })
    await setCompletion(db, a, true)
    await saveNote(db, a, 'Line 1, with comma\nQ: "Growth"?\nA: 1.5x')

    const { fileName, content } = await exportPlanCsv(db, p)
    expect(fileName).toBe('core-java.csv')
    expect(content.startsWith('﻿id,level,type,name,description,priority,completed,version,note_format,note\r\n')).toBe(true)
    const rows = parseCsv(content.slice(1))
    const ext = async (id: number) => (await db.first<{ external_id: string; version: number }>('SELECT external_id, version FROM learning_item WHERE id = ?', [id]))!
    const ea = await ext(a)
    expect(rows.map((r) => r.slice(1, 4))).toEqual([['level', 'type', 'name'], ['1', 'MODULE', 'Collections'], ['2', 'TOPIC', 'ArrayList'], ['1', 'MODULE', 'Empty module']])
    expect(rows[2]).toEqual([ea.external_id, '2', 'TOPIC', 'ArrayList', 'dynamic, "growable" array', '', 'TRUE', String(ea.version), 'MARKDOWN', 'Line 1, with comma\nQ: "Growth"?\nA: 1.5x'])
    expect(rows[1][6]).toBe('') // modules have no checkbox
  })
})
