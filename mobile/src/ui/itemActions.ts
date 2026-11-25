import { Alert } from 'react-native'
import { router } from 'expo-router'
import type { Db } from '@/db/types'
import { deleteItem, descendantCount, reorderItem, siblingPosition } from '@/data/structure'
import type { SheetAction } from './ActionSheet'

export interface ActionTarget {
  id: number
  name: string
  planId: number
  parentId: number | null
}

/** Everything you can do to an item; shared by the item screen's menu and long-press in the tree. */
export function itemActions(db: Db, item: ActionTarget, done: () => void, onDeleted?: () => void): SheetAction[] {
  const fail = (title: string) => (e: unknown) => Alert.alert(title, e instanceof Error ? e.message : String(e))

  const shift = async (delta: number) => {
    const pos = await siblingPosition(db, item.id)
    if (!pos) return
    const to = pos.index + delta
    if (to < 0 || to >= pos.count) return // already first/last
    await reorderItem(db, item.id, to).then(done, fail('Can’t move it'))
  }

  const remove = async () => {
    const below = await descendantCount(db, item.id)
    Alert.alert(
      `Delete “${item.name}”?`,
      below > 0
        ? `This also deletes the ${below} item${below === 1 ? '' : 's'} inside it, with their notes and flashcards. The deletion syncs to your computer.`
        : 'Its note and flashcards are deleted too. The deletion syncs to your computer.',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () =>
            deleteItem(db, item.id).then(() => {
              done()
              onDeleted?.()
            }, fail('Can’t delete it')),
        },
      ],
    )
  }

  return [
    { label: 'Edit', icon: 'create-outline', onPress: () => router.push({ pathname: '/item-form', params: { id: String(item.id) } }) },
    {
      label: 'Add a sub-item',
      icon: 'add-circle-outline',
      onPress: () =>
        router.push({ pathname: '/item-form', params: { planId: String(item.planId), parentId: String(item.id), parentName: item.name } }),
    },
    {
      label: 'Add an item after this',
      icon: 'return-down-forward-outline',
      onPress: async () => {
        const pos = await siblingPosition(db, item.id)
        router.push({
          pathname: '/item-form',
          params: { planId: String(item.planId), parentId: item.parentId === null ? '' : String(item.parentId), position: String((pos?.index ?? 0) + 1) },
        })
      },
    },
    { label: 'Move up', icon: 'arrow-up-outline', onPress: () => void shift(-1) },
    { label: 'Move down', icon: 'arrow-down-outline', onPress: () => void shift(1) },
    { label: 'Move to…', icon: 'git-branch-outline', onPress: () => router.push({ pathname: '/move/[id]', params: { id: String(item.id) } }) },
    { label: 'Delete', icon: 'trash-outline', destructive: true, onPress: () => void remove() },
  ]
}
