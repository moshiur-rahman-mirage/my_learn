import '@/platform/crypto'
import { Stack } from 'expo-router'
import { StatusBar } from 'expo-status-bar'
import { DbProvider } from '@/db/DbProvider'
import { useColors } from '@/ui/theme'

export default function RootLayout() {
  const c = useColors()
  return (
    <DbProvider>
      <StatusBar style="auto" />
      <Stack
        screenOptions={{
          headerStyle: { backgroundColor: c.card },
          headerTintColor: c.text,
          headerTitleStyle: { fontWeight: '700' },
          contentStyle: { backgroundColor: c.bg },
        }}
      >
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="plan/[id]" options={{ title: 'Plan' }} />
        <Stack.Screen name="item/[id]" options={{ title: 'Topic' }} />
        <Stack.Screen name="note/[id]" options={{ title: 'Note' }} />
        <Stack.Screen name="item-form" options={{ title: 'Item', presentation: 'modal' }} />
        <Stack.Screen name="move/[id]" options={{ title: 'Move', presentation: 'modal' }} />
        <Stack.Screen name="plan-form" options={{ title: 'Plan', presentation: 'modal' }} />
        <Stack.Screen name="plan-delete/[id]" options={{ title: 'Delete plan', presentation: 'modal' }} />
        <Stack.Screen name="pair" options={{ title: 'Pair with computer', presentation: 'modal' }} />
      </Stack>
    </DbProvider>
  )
}
