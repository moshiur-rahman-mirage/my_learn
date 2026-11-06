import type { ItemType, Priority, Progress, TreeNode } from '../types/api'

/** Same rule as the backend: percent is null (N/A) when there are no leaves. */
export function progressOf(completed: number, total: number): Progress {
  return { completed, total, percent: total === 0 ? null : Math.round((completed * 1000) / total) / 10 }
}

/**
 * Recomputes leaf/checkable/progress/descendantCount for a (mutated) tree in place.
 * Used for instant feedback after a checkbox click, before the server answers.
 */
export function rollup(nodes: TreeNode[]): void {
  for (const node of nodes) {
    rollup(node.children)
    node.leaf = node.children.length === 0
    node.checkable = node.leaf && node.type === 'TOPIC'
    if (node.leaf) {
      node.progress = node.checkable ? progressOf(node.completed ? 1 : 0, 1) : progressOf(0, 0)
      node.descendantCount = 0
    } else {
      let done = 0
      let total = 0
      let count = 0
      for (const c of node.children) {
        done += c.progress.completed
        total += c.progress.total
        count += c.descendantCount + 1
      }
      node.progress = progressOf(done, total)
      node.descendantCount = count
    }
  }
}

export function totalProgress(nodes: TreeNode[]): Progress {
  let done = 0
  let total = 0
  for (const n of nodes) {
    done += n.progress.completed
    total += n.progress.total
  }
  return progressOf(done, total)
}

export function findNode(nodes: TreeNode[], id: number): TreeNode | null {
  for (const n of nodes) {
    if (n.id === id) return n
    const found = findNode(n.children, id)
    if (found) return found
  }
  return null
}

/** The sibling list that contains `id` (the roots or a parent's children) and its index there. */
export function locate(nodes: TreeNode[], id: number): { list: TreeNode[]; index: number } | null {
  const index = nodes.findIndex((n) => n.id === id)
  if (index >= 0) return { list: nodes, index }
  for (const n of nodes) {
    const found = locate(n.children, id)
    if (found) return found
  }
  return null
}

export function isSelfOrDescendant(node: TreeNode, id: number): boolean {
  return node.id === id || node.children.some((c) => isSelfOrDescendant(c, id))
}

export function allIds(nodes: TreeNode[], out: number[] = []): number[] {
  for (const n of nodes) {
    out.push(n.id)
    allIds(n.children, out)
  }
  return out
}

export interface FlatOption {
  id: number
  name: string
  depth: number
  type: ItemType
}

/** Depth-first list for parent pickers, optionally excluding a subtree (an item can't move into itself). */
export function flatten(nodes: TreeNode[], excludeId?: number, depth = 0, out: FlatOption[] = []): FlatOption[] {
  for (const n of nodes) {
    if (n.id === excludeId) continue
    out.push({ id: n.id, name: n.name, depth, type: n.type })
    flatten(n.children, excludeId, depth + 1, out)
  }
  return out
}

// --- filtering ----------------------------------------------------------------

export interface TreeFilter {
  text: string
  priorities: Priority[]
  status: 'all' | 'completed' | 'pending'
  types: ItemType[]
  /** items carrying any of these tags */
  tags: string[]
}

export function isFilterActive(f: TreeFilter): boolean {
  return f.text.trim() !== '' || f.priorities.length > 0 || f.status !== 'all' || f.types.length > 0 || f.tags.length > 0
}

/** Every tag used in the tree, alphabetical. */
export function tagsIn(nodes: TreeNode[], out = new Set<string>()): string[] {
  for (const n of nodes) {
    n.tags.forEach((t) => out.add(t))
    tagsIn(n.children, out)
  }
  return [...out].sort((a, b) => a.localeCompare(b, undefined, { sensitivity: 'base' }))
}

function matches(n: TreeNode, f: TreeFilter, text: string): boolean {
  if (text && !n.name.toLowerCase().includes(text) && !(n.description ?? '').toLowerCase().includes(text)) return false
  if (f.priorities.length && (!n.priority || !f.priorities.includes(n.priority))) return false
  if (f.types.length && !f.types.includes(n.type)) return false
  if (f.tags.length && !n.tags.some((t) => f.tags.includes(t))) return false
  if (f.status === 'completed' && !(n.checkable && n.completed)) return false
  if (f.status === 'pending' && !(n.checkable && !n.completed)) return false
  return true
}

/**
 * Returns the ids of matching nodes and of every node that must stay visible because a
 * descendant matches (its ancestors).
 */
export function filterTree(nodes: TreeNode[], f: TreeFilter): { visible: Set<number>; matched: Set<number> } {
  const visible = new Set<number>()
  const matched = new Set<number>()
  const text = f.text.trim().toLowerCase()
  const walk = (list: TreeNode[]): boolean => {
    let any = false
    for (const n of list) {
      const childVisible = walk(n.children)
      const self = matches(n, f, text)
      if (self) matched.add(n.id)
      if (self || childVisible) {
        visible.add(n.id)
        any = true
      }
    }
    return any
  }
  walk(nodes)
  return { visible, matched }
}
