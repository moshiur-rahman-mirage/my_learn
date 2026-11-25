import { describe, expect, it } from 'vitest'
import { migrate, SCHEMA_VERSION } from '../src/db/migrate'
import { MIGRATIONS } from '../src/db/migrations.generated'
import { migratedDb, nodeDb } from './nodeDb'

describe('migrations (the desktop Flyway scripts, run on the phone)', () => {
  it('creates the full desktop schema', async () => {
    const db = await migratedDb()
    const tables = (await db.all<{ name: string }>("SELECT name FROM sqlite_master WHERE type IN ('table')")).map((t) => t.name)
    for (const t of ['learning_plan', 'learning_item', 'item_note', 'item_link', 'time_log', 'tag', 'item_tag', 'item_review',
      'item_confidence', 'flashcard', 'note_ref', 'app_meta', 'sync_change', 'sync_device', 'search_index']) {
      expect(tables).toContain(t)
    }
    expect((await db.first<{ user_version: number }>('PRAGMA user_version'))?.user_version).toBe(SCHEMA_VERSION)
    expect(SCHEMA_VERSION).toBe(MIGRATIONS.length)
  })

  it('is idempotent', async () => {
    const db = await migratedDb()
    await migrate(db)
    expect((await db.first<{ user_version: number }>('PRAGMA user_version'))?.user_version).toBe(SCHEMA_VERSION)
  })

  it('refuses a database from a newer app', async () => {
    const db = nodeDb()
    await db.exec(`PRAGMA user_version = ${SCHEMA_VERSION + 1}`)
    await expect(migrate(db)).rejects.toThrow(/newer than this app/)
  })

  it('has the sync triggers: a new plan gets a UUID and a change line', async () => {
    const db = await migratedDb()
    const now = '2026-11-25T10:00:00.000Z'
    await db.run('INSERT INTO learning_plan (name, created_at, updated_at) VALUES (?, ?, ?)', ['P', now, now])
    const plan = await db.first<{ external_id: string }>('SELECT external_id FROM learning_plan')
    expect(plan?.external_id).toMatch(/^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/)
    const line = await db.first<{ entity: string; origin: string | null }>('SELECT entity, origin FROM sync_change WHERE key = ?',
      [plan!.external_id])
    expect(line).toEqual({ entity: 'plan', origin: null })
  })

  it('enforces foreign keys and cascades', async () => {
    const db = await migratedDb()
    const now = '2026-11-25T10:00:00.000Z'
    const { lastInsertRowId: planId } = await db.run('INSERT INTO learning_plan (name, created_at, updated_at) VALUES (?, ?, ?)',
      ['P', now, now])
    await db.run(`INSERT INTO learning_item (external_id, plan_id, item_type, name, display_order, created_at, updated_at)
                  VALUES ('i1', ?, 'TOPIC', 'T', 0, ?, ?)`, [planId, now, now])
    await db.run('DELETE FROM learning_plan WHERE id = ?', [planId])
    expect((await db.all('SELECT * FROM learning_item'))).toHaveLength(0)
    expect(await db.first<{ deleted: number }>("SELECT deleted FROM sync_change WHERE entity = 'item' AND key = 'i1'"))
      .toEqual({ deleted: 1 })
  })

  it('rolls back a failed transaction', async () => {
    const db = await migratedDb()
    await expect(db.transaction(async () => {
      await db.run("INSERT INTO app_meta (key, value) VALUES ('a', '1')")
      throw new Error('boom')
    })).rejects.toThrow('boom')
    expect(await db.first("SELECT * FROM app_meta WHERE key = 'a'")).toBeNull()
  })
})
