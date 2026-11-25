import type { Db } from './types'
import { MIGRATIONS, type Migration } from './migrations.generated'

/** Highest schema version this app knows (the desktop's latest Flyway migration). */
export const SCHEMA_VERSION = MIGRATIONS.length

/**
 * Brings the database up to {@link SCHEMA_VERSION} using the desktop's own migration scripts.
 * The applied version is kept in `PRAGMA user_version`; each migration runs in its own
 * transaction, so a failure leaves the previous version intact.
 */
export async function migrate(db: Db, migrations: Migration[] = MIGRATIONS): Promise<number> {
  const row = await db.first<{ user_version: number }>('PRAGMA user_version')
  const current = row?.user_version ?? 0
  if (current > migrations.length) {
    throw new Error(`This database has schema version ${current}, newer than this app (${migrations.length}). Update the app.`)
  }
  for (const m of migrations) {
    if (m.version <= current) continue
    await db.transaction(async () => {
      await db.exec(m.sql)
      await db.exec(`PRAGMA user_version = ${m.version}`)
    })
  }
  return migrations.length
}

/** Opens the connection the way the desktop does (FKs on every connection, WAL). */
export async function configure(db: Db): Promise<void> {
  await db.exec('PRAGMA foreign_keys = ON')
  await db.exec('PRAGMA journal_mode = WAL')
  await db.exec('PRAGMA busy_timeout = 5000')
}
