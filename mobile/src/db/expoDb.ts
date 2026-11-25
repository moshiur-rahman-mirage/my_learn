import * as SQLite from 'expo-sqlite'
import { configure, migrate } from './migrate'
import { TxQueue, type Db, type SqlValue } from './types'

export const DATABASE_NAME = 'learning-tracker.db'

/** {@link Db} on expo-sqlite: one connection for the whole app (foreign keys are per connection). */
export function wrapExpo(sqlite: SQLite.SQLiteDatabase): Db {
  const queue = new TxQueue((sql) => sqlite.execAsync(sql))
  return {
    exec: (sql) => sqlite.execAsync(sql),
    run: async (sql, params: SqlValue[] = []) => {
      const r = await sqlite.runAsync(sql, params)
      return { changes: r.changes, lastInsertRowId: r.lastInsertRowId }
    },
    all: (sql, params: SqlValue[] = []) => sqlite.getAllAsync(sql, params),
    first: (sql, params: SqlValue[] = []) => sqlite.getFirstAsync(sql, params),
    transaction: (fn) => queue.run(fn),
  }
}

let opened: Promise<Db> | null = null

/** The app's database, opened, configured and migrated once. */
export function openAppDb(): Promise<Db> {
  if (!opened) {
    opened = (async () => {
      const db = wrapExpo(await SQLite.openDatabaseAsync(DATABASE_NAME))
      await configure(db)
      await migrate(db)
      return db
    })()
    opened.catch(() => {
      opened = null // let a retry open it again
    })
  }
  return opened
}
