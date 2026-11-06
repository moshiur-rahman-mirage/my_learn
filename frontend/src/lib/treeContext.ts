import type { InjectionKey, Ref } from 'vue'
import type { TreeNode } from '../types/api'

export type DropZone = 'before' | 'inside' | 'after'

/** Shared state and callbacks for every recursive TreeRow (provided by PlanDetailView). */
export interface TreeContext {
  isExpanded: (id: number) => boolean
  toggle: (id: number) => void
  /** When a filter is active: ids to show (null = show all) and ids that matched themselves. */
  visible: Ref<Set<number> | null>
  matched: Ref<Set<number> | null>
  pending: Ref<Set<number>>
  /** Item to draw attention to (opened from search or Today). */
  highlightId: Ref<number | null>
  onToggleComplete: (node: TreeNode) => void
  onAdd: (parent: TreeNode, type: 'MODULE' | 'TOPIC') => void
  onEdit: (node: TreeNode) => void
  /** Open the note (summary) of a module or topic. */
  onNotes: (node: TreeNode) => void
  onMove: (node: TreeNode) => void
  onShift: (node: TreeNode, delta: -1 | 1) => void
  onDelete: (node: TreeNode) => void
  onExport: (node: TreeNode) => void
  // drag & drop
  dragId: Ref<number | null>
  dropTarget: Ref<{ id: number; zone: DropZone } | null>
  canDrop: (target: TreeNode) => boolean
  onDrop: (target: TreeNode, zone: DropZone) => void
}

export const TREE_CONTEXT: InjectionKey<TreeContext> = Symbol('tree-context')
