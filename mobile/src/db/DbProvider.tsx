import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { ActivityIndicator, Pressable, Text, View } from 'react-native'
import { openAppDb } from './expoDb'
import type { Db } from './types'

const DbContext = createContext<Db | null>(null)

/** Opens and migrates the database before any screen renders. */
export function DbProvider({ children }: { children: ReactNode }) {
  const [db, setDb] = useState<Db | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [attempt, setAttempt] = useState(0)

  useEffect(() => {
    let alive = true
    openAppDb()
      .then((d) => alive && setDb(d))
      .catch((e: unknown) => alive && setError(e instanceof Error ? e.message : String(e)))
    return () => {
      alive = false
    }
  }, [attempt])

  if (error) {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24, gap: 12 }}>
        <Text style={{ fontSize: 16, fontWeight: '600' }}>Couldn’t open your data</Text>
        <Text selectable>{error}</Text>
        <Pressable
          onPress={() => {
            setError(null)
            setAttempt((a) => a + 1)
          }}
          accessibilityRole="button"
        >
          <Text style={{ color: '#2563eb', fontWeight: '600' }}>Try again</Text>
        </Pressable>
      </View>
    )
  }
  if (!db) {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
        <ActivityIndicator />
      </View>
    )
  }
  return <DbContext.Provider value={db}>{children}</DbContext.Provider>
}

export function useDb(): Db {
  const db = useContext(DbContext)
  if (!db) throw new Error('useDb must be used inside <DbProvider>')
  return db
}
