/** Domain types mirroring the desktop's DTOs (backend item/dto, frontend types/api.ts). */

export type ItemType = 'MODULE' | 'TOPIC'
export type Priority = 'MUST' | 'SHOULD' | 'NICE'
export type PlanStatus = 'ACTIVE' | 'ARCHIVED'
export type NoteFormat = 'HTML' | 'MARKDOWN'

/** completed of total checkable leaves; percent null = no leaves (shown as N/A). */
export interface Progress {
  completed: number
  total: number
  percent: number | null
}

/** A learning_item row as stored (snake_case columns mapped to camelCase). */
export interface ItemRow {
  id: number
  externalId: string
  planId: number
  parentId: number | null
  type: ItemType
  name: string
  description: string | null
  priority: Priority | null
  completed: boolean
  completedAt: string | null
  displayOrder: number
  version: number
}

export interface Link {
  id: number
  url: string
  title: string | null
}

/** Per-item data kept outside learning_item that the tree shows (backend item/TreeExtras). */
export interface TreeExtras {
  withNotes: Set<number>
  links: Map<number, Link[]>
  tags: Map<number, string[]>
  minutes: Map<number, number>
  reviewDue: Map<number, string>
  confidence: Map<number, number>
  flashcards: Map<number, number>
}

export const NO_EXTRAS: TreeExtras = {
  withNotes: new Set(),
  links: new Map(),
  tags: new Map(),
  minutes: new Map(),
  reviewDue: new Map(),
  confidence: new Map(),
  flashcards: new Map(),
}

/** One node of a plan's roadmap tree with derived state (backend item/dto/TreeNode). */
export interface TreeNode {
  id: number
  externalId: string
  parentId: number | null
  type: ItemType
  name: string
  description: string | null
  priority: Priority | null
  completed: boolean
  completedAt: string | null
  displayOrder: number
  version: number
  /** has no children */
  leaf: boolean
  /** a leaf TOPIC: the only kind of item that can be completed */
  checkable: boolean
  progress: Progress
  /** number of items below this one */
  descendantCount: number
  hasNotes: boolean
  links: Link[]
  tags: string[]
  minutes: number
  totalMinutes: number
  /** next review day (yyyy-MM-dd) of a completed topic still being reviewed */
  reviewDueOn: string | null
  confidence: number | null
  flashcards: number
  children: TreeNode[]
}

export interface Plan {
  id: number
  externalId: string
  name: string
  description: string | null
  status: PlanStatus
  targetDate: string | null
  version: number
  createdAt: string
  updatedAt: string
  progress: Progress
  /** pacing toward the target date; null without one */
  pace: import('./pace').Pace | null
}
