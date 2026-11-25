import Ionicons from '@expo/vector-icons/Ionicons'
import { Modal, Pressable, Text, View } from 'react-native'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { space, useColors } from './theme'

export interface SheetAction {
  label: string
  icon: keyof typeof Ionicons.glyphMap
  onPress: () => void
  destructive?: boolean
  disabled?: boolean
}

/** A bottom sheet of actions (Android's Alert allows only three buttons). */
export function ActionSheet({ title, actions, visible, onClose }: {
  title?: string
  actions: SheetAction[]
  visible: boolean
  onClose: () => void
}) {
  const c = useColors()
  const insets = useSafeAreaInsets()
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose} statusBarTranslucent>
      <Pressable style={{ flex: 1, backgroundColor: 'rgba(0,0,0,0.4)' }} onPress={onClose} accessibilityLabel="Close menu" />
      <View
        style={{
          backgroundColor: c.card, borderTopLeftRadius: 16, borderTopRightRadius: 16,
          paddingBottom: insets.bottom + space.sm, paddingTop: space.sm,
        }}
      >
        {title ? (
          <Text style={{ color: c.muted, fontSize: 13, paddingHorizontal: space.lg, paddingVertical: space.sm }} numberOfLines={2}>
            {title}
          </Text>
        ) : null}
        {actions.map((a) => (
          <Pressable
            key={a.label}
            disabled={a.disabled}
            accessibilityRole="button"
            accessibilityState={{ disabled: a.disabled }}
            onPress={() => {
              onClose()
              a.onPress()
            }}
            style={({ pressed }) => ({
              flexDirection: 'row', alignItems: 'center', gap: space.md, minHeight: 52, paddingHorizontal: space.lg,
              opacity: a.disabled ? 0.4 : pressed ? 0.6 : 1,
            })}
          >
            <Ionicons name={a.icon} size={22} color={a.destructive ? c.danger : c.text} />
            <Text style={{ color: a.destructive ? c.danger : c.text, fontSize: 16 }}>{a.label}</Text>
          </Pressable>
        ))}
      </View>
    </Modal>
  )
}
