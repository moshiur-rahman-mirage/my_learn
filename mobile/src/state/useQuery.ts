import { useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react'
import { useFocusEffect } from 'expo-router'
import { useDb } from '@/db/DbProvider'
import type { Db } from '@/db/types'
import { useDataVersion } from './stores'

/**
 * Loads data from the local database, and loads it again when `key` changes, the screen regains
 * focus, or any write bumps the data version. Offline-first: the database is the only source a
 * screen reads.
 *
 * @param key whatever the loader depends on, as a string (e.g. `plan:${id}`)
 */
export function useQuery<T>(load: (db: Db) => Promise<T>, key: string): {
  data: T | undefined
  error: string | null
  reload: () => Promise<void>
} {
  const db = useDb()
  const version = useDataVersion((s) => s.version)
  const [data, setData] = useState<T>()
  const [error, setError] = useState<string | null>(null)
  const loadRef = useRef(load)
  const seq = useRef(0)

  useLayoutEffect(() => {
    loadRef.current = load
  })

  const reload = useCallback(async () => {
    const mine = ++seq.current
    try {
      const value = await loadRef.current(db)
      if (mine === seq.current) {
        setData(value)
        setError(null)
      }
    } catch (e) {
      if (mine === seq.current) setError(e instanceof Error ? e.message : String(e))
    }
  }, [db])

  useEffect(() => {
    void reload()
  }, [reload, version, key])

  useFocusEffect(
    useCallback(() => {
      void reload()
    }, [reload]),
  )

  return { data, error, reload }
}
