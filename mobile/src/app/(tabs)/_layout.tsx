import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { Tabs } from 'expo-router/js-tabs'
import { Pressable, type ColorValue } from 'react-native'
import { useSync } from '@/sync/useSync'
import { useColors } from '@/ui/theme'

type IconName = keyof typeof Ionicons.glyphMap

function icon(name: IconName) {
  function TabIcon({ color, size }: { color: ColorValue; size: number }) {
    return <Ionicons name={name} color={color as string} size={size} />
  }
  return TabIcon
}

export default function TabsLayout() {
  const c = useColors()
  const syncError = useSync((s) => s.lastError)
  return (
    <Tabs
      screenOptions={{
        headerStyle: { backgroundColor: c.card },
        headerTintColor: c.text,
        tabBarStyle: { backgroundColor: c.card, borderTopColor: c.border, elevation: 8 },
        tabBarActiveTintColor: c.primary,
        tabBarInactiveTintColor: c.muted,
        sceneStyle: { backgroundColor: c.bg },
      }}
    >
      <Tabs.Screen name="index" options={{ title: 'Today', tabBarAccessibilityLabel: 'Today', tabBarIcon: icon('today-outline') }} />
      <Tabs.Screen
        name="plans"
        options={{
          title: 'Plans',
          tabBarAccessibilityLabel: 'Plans',
          tabBarIcon: icon('list'),
          headerRight: () => (
            <Pressable onPress={() => router.push('/plan-form')} hitSlop={12} style={{ marginRight: 16 }} accessibilityRole="button" accessibilityLabel="New plan">
              <Ionicons name="add" size={26} color={c.text} />
            </Pressable>
          ),
        }}
      />
      <Tabs.Screen name="search" options={{ title: 'Search', tabBarAccessibilityLabel: 'Search', tabBarIcon: icon('search') }} />
      <Tabs.Screen name="cards" options={{ title: 'Cards', tabBarAccessibilityLabel: 'Cards', tabBarIcon: icon('albums-outline') }} />
      <Tabs.Screen
        name="sync"
        options={{
          title: 'Sync',
          tabBarAccessibilityLabel: syncError ? 'Sync, last sync failed' : 'Sync',
          tabBarIcon: icon('sync'),
          tabBarBadge: syncError ? '!' : undefined,
        }}
      />
    </Tabs>
  )
}
