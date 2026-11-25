import { DatabaseSync } from 'node:sqlite'
import { configure, migrate } from '../src/db/migrate'
import { TxQueue, type Db, type SqlValue } from '../src/db/types'

/** {@link Db} on Node's built-in SQLite, for unit tests of app code. */
export function nodeDb(path = ':memory:', opts: { readOnly?: boolean } = {}): Db & { raw: DatabaseSync } {
  const raw = new DatabaseSync(path, { readOnly: opts.readOnly ?? false })
  const exec = async (sql: string) => {
    raw.exec(sql)
  }
  const queue = new TxQueue(exec)
  const plain = <T>(row: unknown) => (row == null ? null : ({ ...(row as object) } as T))
  return {
    raw,
    exec,
    run: async (sql, params: SqlValue[] = []) => {
      const r = raw.prepare(sql).run(...params)
      return { changes: Number(r.changes), lastInsertRowId: Number(r.lastInsertRowid) }
    },
    all: async <T>(sql: string, params: SqlValue[] = []) => raw.prepare(sql).all(...params).map((r) => plain<T>(r) as T),
    first: async <T>(sql: string, params: SqlValue[] = []) => plain<T>(raw.prepare(sql).get(...params)),
    transaction: (fn) => queue.run(fn),
  }
}

/** A fresh in-memory database with the full app schema. */
export async function migratedDb() {
  const db = nodeDb()
  await configure(db)
  await migrate(db)
  return db
}
