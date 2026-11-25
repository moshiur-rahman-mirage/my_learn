import type { TreeNode } from './types'

export interface VisibleRow {
  node: TreeNode
  depth: number
}

/** A node whose whole subtree is done: a ticked topic, or a parent at 100%. */
export function isDone(n: TreeNode): boolean {
  return n.checkable ? n.completed : n.progress.total > 0 && n.progress.completed === n.progress.total
}

/**
 * The rows a list should show: depth-first, descending only into expanded nodes. With
 * `hideDone`, finished topics and finished branches are left out (to see what's left).
 */
export function visibleRows(tree: TreeNode[], expanded: ReadonlySet<number>, hideDone = false): VisibleRow[] {
  const out: VisibleRow[] = []
  const visit = (nodes: TreeNode[], depth: number) => {
    for (const n of nodes) {
      if (hideDone && isDone(n)) continue
      out.push({ node: n, depth })
      if (!n.leaf && expanded.has(n.id)) visit(n.children, depth + 1)
    }
  }
  visit(tree, 0)
  return out
}

/** Ids of every node that has children (for "expand all"). */
export function parentIds(tree: TreeNode[]): number[] {
  const ids: number[] = []
  const visit = (nodes: TreeNode[]) => {
    for (const n of nodes) {
      if (!n.leaf) {
        ids.push(n.id)
        visit(n.children)
      }
    }
  }
  visit(tree)
  return ids
}
