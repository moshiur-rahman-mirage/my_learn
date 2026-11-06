// Mirrors the backend DTOs (see backend/src/main/java/.../dto). Dates are ISO-8601 strings.

export type PlanStatus = 'ACTIVE' | 'ARCHIVED'
export type ItemType = 'MODULE' | 'TOPIC'
export type Priority = 'MUST' | 'SHOULD' | 'NICE'

/** Leaf-based progress; percent is null when there are no leaves (shown as N/A). */
export interface Progress {
  completed: number
  total: number
  percent: number | null
}

export interface Plan {
  id: number
  name: string
  description: string | null
  status: PlanStatus
  version: number
  createdAt: string
  updatedAt: string
  progress: Progress
}

export interface Item {
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
  createdAt: string
  updatedAt: string
}

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
  leaf: boolean
  checkable: boolean
  progress: Progress
  descendantCount: number
  /** The item has a note (loaded separately with notesApi.get). */
  hasNotes: boolean
  children: TreeNode[]
}

export interface RecentCompletion {
  itemId: number
  itemName: string
  completedAt: string
  planId: number
  planName: string
}

export interface DashboardSummary {
  totalPlans: number
  activePlans: number
  totalLeaves: number
  completedLeaves: number
  pendingLeaves: number
  overall: Progress
  plans: Plan[]
  recentlyUpdated: Plan[]
  recentlyCompleted: RecentCompletion[]
}

// --- import -----------------------------------------------------------------

export type ColumnRole = 'LEVEL' | 'DEPTH' | 'TYPE' | 'ID' | 'DESCRIPTION' | 'PRIORITY' | 'COMPLETED' | 'IGNORE'

export interface ImportColumn {
  index: number
  header: string
  role: ColumnRole
}

export interface ImportIssue {
  row: number
  message: string
}

export interface PreviewNode {
  row: number
  name: string
  type: ItemType
  description: string | null
  priority: Priority | null
  completed: boolean
  children: PreviewNode[]
}

export interface ImportPreview {
  fileName: string
  sheets: string[]
  sheet: string
  headerRow: number
  columns: ImportColumn[]
  tree: PreviewNode[]
  stats: { modules: number; topics: number; leafTopics: number; completed: number }
  errors: ImportIssue[]
  warnings: ImportIssue[]
  duplicateOf: { planId: number; planName: string; importedAt: string } | null
  canImport: boolean
}

export interface ImportResult {
  plans: { planId: number; planName: string; sheet: string; itemsCreated: number }[]
}

export interface BackupFile {
  fileName: string
  path: string
  sizeBytes: number
  createdAt: string
}

export interface BackupSettings {
  directory: string
  defaultDirectory: string
  custom: boolean
  automatic: boolean
  keep: number
}

export interface FolderListing {
  /** null = the starting points (drives and home) */
  path: string | null
  /** "" = back to the starting points */
  parent: string | null
  folders: { name: string; path: string }[]
}

export interface RestoreResult {
  restoredFrom: string
  safetyBackup: string
  plans: number
  items: number
}

// --- notes ------------------------------------------------------------------

/** content (sanitized HTML) and updatedAt are null when the item has no note. */
export interface Note {
  itemId: number
  content: string | null
  updatedAt: string | null
}

// --- activity (dashboard charts) --------------------------------------------

export interface ActivityDay {
  /** yyyy-MM-dd in the viewer's time zone */
  date: string
  completedByYou: number
  /** came in already ticked by an import that day */
  completedImported: number
  totalLeaves: number
  completedLeaves: number
  percent: number | null
}

export interface Activity {
  timezone: string
  days: ActivityDay[]
  completedToday: number
  completedLast7Days: number
  completedLast30Days: number
  currentStreak: number
  bestStreak: number
}
