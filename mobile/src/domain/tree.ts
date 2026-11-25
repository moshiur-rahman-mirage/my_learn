import { NO_PROGRESS, plus, progressOf } from './progress'
import { NO_EXTRAS, type ItemRow, type Progress, type TreeExtras, type TreeNode } from './types'

/**
 * Builds a plan's tree from its flat item list and rolls progress up from the leaves, in O(n)
 * (port of backend item/TreeBuilder; keep the two in step).
 *
 * - Only a TOPIC without children is checkable and counts toward progress.
 * - An empty MODULE is N/A and doesn't drag its parent down.
 * - A stale completion on an item that has children is ignored.
 * - Siblings are ordered by displayOrder, then id.
 */
export function buildTree(items: ItemRow[], extras: TreeExtras = NO_EXTRAS): TreeNode[] {
  const childrenByParent = new Map<number | null, ItemRow[]>()
  for (const item of items) {
    const list = childrenByParent.get(item.parentId)
    if (list) list.push(item)
    else childrenByParent.set(item.parentId, [item])
  }
  for (const list of childrenByParent.values()) {
    list.sort((a, b) => a.displayOrder - b.displayOrder || a.id - b.id)
  }
  return toNodes(childrenByParent.get(null) ?? [], childrenByParent, extras)
}

function toNodes(items: ItemRow[], childrenByParent: Map<number | null, ItemRow[]>, extras: TreeExtras): TreeNode[] {
  return items.map((item) => {
    const children = toNodes(childrenByParent.get(item.id) ?? [], childrenByParent, extras)
    const ownMinutes = extras.minutes.get(item.id) ?? 0
    let totalMinutes = ownMinutes
    for (const child of children) totalMinutes += child.totalMinutes

    const leaf = children.length === 0
    const checkable = leaf && item.type === 'TOPIC'
    let progress: Progress
    let descendants = 0
    if (leaf) {
      progress = checkable ? progressOf(item.completed ? 1 : 0, 1) : NO_PROGRESS
    } else {
      progress = NO_PROGRESS
      for (const child of children) {
        progress = plus(progress, child.progress)
        descendants += child.descendantCount + 1
      }
    }
    const completed = checkable && item.completed
    return {
      id: item.id,
      externalId: item.externalId,
      parentId: item.parentId,
      type: item.type,
      name: item.name,
      description: item.description,
      priority: item.priority,
      completed,
      completedAt: checkable ? item.completedAt : null,
      displayOrder: item.displayOrder,
      version: item.version,
      leaf,
      checkable,
      progress,
      descendantCount: descendants,
      hasNotes: extras.withNotes.has(item.id),
      links: extras.links.get(item.id) ?? [],
      tags: extras.tags.get(item.id) ?? [],
      minutes: ownMinutes,
      totalMinutes,
      reviewDueOn: completed ? (extras.reviewDue.get(item.id) ?? null) : null,
      confidence: extras.confidence.get(item.id) ?? null,
      flashcards: extras.flashcards.get(item.id) ?? 0,
      children,
    }
  })
}

/** Depth-first walk, parents before children. */
export function* walk(nodes: TreeNode[]): Generator<TreeNode> {
  for (const n of nodes) {
    yield n
    yield* walk(n.children)
  }
}

/** The node with this id and the path of names above it ("breadcrumbs"), or null. */
export function findWithPath(nodes: TreeNode[], id: number, path: TreeNode[] = []): { node: TreeNode; path: TreeNode[] } | null {
  for (const n of nodes) {
    if (n.id === id) return { node: n, path }
    const found = findWithPath(n.children, id, [...path, n])
    if (found) return found
  }
  return null
}
