import Ionicons from '@expo/vector-icons/Ionicons'
import { router } from 'expo-router'
import { Tabs } from 'expo-router/js-tabs'
import { Pressable, type ColorValue } from 'react-native'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { useSync } from '@/sync/useSync'
import { useColors } from '@/ui/theme'

type IconName = keyof typeof Ionicons.glyphMap

/** Filled icon on the active tab, outline otherwise. */
function icon(active: IconName, inactive: IconName) {
  function TabIcon({ color, focused }: { color: ColorValue; size: number; focused: boolean }) {
    return <Ionicons name={focused ? active : inactive} color={color as string} size={24} />
  }
  return TabIcon
}

export default function TabsLayout() {
  const c = useColors()
  const insets = useSafeAreaInsets()
  const syncError = useSync((s) => s.lastError)
  return (
    <Tabs
      screenOptions={{
        headerStyle: { backgroundColor: c.bg },
        headerShadowVisible: false,
        headerTitleStyle: { fontWeight: '800', fontSize: 22 },
        headerTintColor: c.text,
        tabBarStyle: {
          backgroundColor: c.card, borderTopColor: c.border, elevation: 12,
          height: 64 + insets.bottom, paddingTop: 8, paddingBottom: insets.bottom + 6,
        },
        tabBarLabelStyle: { fontSize: 11, fontWeight: '700' },
        tabBarActiveTintColor: c.primary,
        tabBarInactiveTintColor: c.faint,
        sceneStyle: { backgroundColor: c.bg },
      }}
    >
      <Tabs.Screen name="index" options={{ title: 'Home', headerShown: false, tabBarAccessibilityLabel: 'Home', tabBarIcon: icon('home', 'home-outline') }} />
      <Tabs.Screen
        name="plans"
        options={{
          title: 'Plans',
          tabBarAccessibilityLabel: 'Plans',
          tabBarIcon: icon('library', 'library-outline'),
          headerRight: () => (
            <Pressable onPress={() => router.push('/plan-form')} hitSlop={12} style={{ marginRight: 16 }} accessibilityRole="button" accessibilityLabel="New plan">
              <Ionicons name="add-circle" size={30} color={c.primary} />
            </Pressable>
          ),
        }}
      />
      <Tabs.Screen
        name="short-read"
        options={{
          title: 'Short Read',
          tabBarAccessibilityLabel: 'Short Read',
          tabBarIcon: icon('book', 'book-outline'),
        }}
      />
      <Tabs.Screen name="search" options={{ title: 'Search', tabBarAccessibilityLabel: 'Search', tabBarIcon: icon('search', 'search-outline') }} />
      <Tabs.Screen name="cards" options={{ title: 'Flashcards', headerShown: false, tabBarAccessibilityLabel: 'Flashcards', tabBarIcon: icon('albums', 'albums-outline') }} />
      <Tabs.Screen
        name="profile"
        options={{
          title: 'Profile',
          headerShown: false,
          tabBarAccessibilityLabel: syncError ? 'Profile, last sync failed' : 'Profile',
          tabBarIcon: icon('person-circle', 'person-circle-outline'),
          tabBarBadge: syncError ? '!' : undefined,
        }}
      />
    </Tabs>
  )
}
