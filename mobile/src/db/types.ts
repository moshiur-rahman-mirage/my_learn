/**
 * The small database interface all app code uses. The app runs it on expo-sqlite
 * (`expoDb.ts`); unit tests run the very same code on Node's built-in SQLite (`test/nodeDb.ts`).
 */
export type SqlValue = string | number | null

export interface RunResult {
  changes: number
  lastInsertRowId: number
}

export interface Db {
  /** Several statements, no parameters (migrations, PRAGMAs). */
  exec(sql: string): Promise<void>
  run(sql: string, params?: SqlValue[]): Promise<RunResult>
  all<T>(sql: string, params?: SqlValue[]): Promise<T[]>
  first<T>(sql: string, params?: SqlValue[]): Promise<T | null>
  /**
   * Runs `fn` in one transaction on the single connection: all or nothing. Transactions are
   * queued, never nested or interleaved, so a sync and a tap on a checkbox can't mix.
   */
  transaction<T>(fn: () => Promise<T>): Promise<T>
}

/** Serializes transactions on one connection (shared by both adapters). */
export class TxQueue {
  private tail: Promise<unknown> = Promise.resolve()
  private active = false

  constructor(private readonly exec: (sql: string) => Promise<void>) {}

  run<T>(fn: () => Promise<T>): Promise<T> {
    if (this.active) {
      // already inside a transaction (a helper that opens its own): join it
      return fn()
    }
    const next = this.tail.then(async () => {
      this.active = true
      await this.exec('BEGIN IMMEDIATE')
      try {
        const result = await fn()
        await this.exec('COMMIT')
        return result
      } catch (e) {
        await this.exec('ROLLBACK')
        throw e
      } finally {
        this.active = false
      }
    })
    this.tail = next.catch(() => undefined)
    return next
  }
}
