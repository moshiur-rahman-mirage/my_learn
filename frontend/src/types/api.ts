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

export type PaceStatus = 'DONE' | 'ON_TRACK' | 'BEHIND' | 'OVERDUE' | 'NO_TOPICS'

/** How a plan with a target date is doing (null when it has none). */
export interface Pace {
  targetDate: string
  /** days to the target day, counting it (0 = overdue) */
  daysLeft: number
  remaining: number
  perWeekNeeded: number | null
  /** topics per week you completed over the last 28 days */
  perWeekRecent: number
  projectedFinish: string | null
  status: PaceStatus
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
  /** yyyy-MM-dd, or null */
  targetDate: string | null
  pace: Pace | null
}

export interface Link {
  id: number
  url: string
  title: string | null
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
  links: Link[]
  tags: string[]
  /** time logged on this item itself */
  minutes: number
  /** time logged on this item and everything below it */
  totalMinutes: number
  /** next spaced-repetition review (yyyy-MM-dd), null when not being reviewed */
  reviewDueOn: string | null
  /** 1 (shaky) – 5 (solid); null = not rated */
  confidence: number | null
  /** flashcards in the item's note */
  flashcards: number
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

export type ColumnRole =
  | 'LEVEL'
  | 'DEPTH'
  | 'TYPE'
  | 'ID'
  | 'DESCRIPTION'
  | 'PRIORITY'
  | 'COMPLETED'
  | 'NOTE'
  | 'NOTE_FORMAT'
  | 'IGNORE'

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
  hasNote: boolean
  children: PreviewNode[]
}

export interface ImportPreview {
  fileName: string
  sheets: string[]
  sheet: string
  headerRow: number
  columns: ImportColumn[]
  tree: PreviewNode[]
  stats: { modules: number; topics: number; leafTopics: number; completed: number; notes: number }
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
  /** null = never checked (made by an older version) */
  check: BackupCheck | null
}

export interface BackupCheck {
  ok: boolean
  message: string
  plans: number
  items: number
  notes: number
  checkedAt: string
}

export interface BackupStatus {
  lastBackupAt: string | null
  lastCheck: BackupCheck | null
  /** something needing attention; null = all is well */
  warning: string | null
}

export interface Retention {
  recent: number
  daily: number
  weekly: number
  monthly: number
}

export interface BackupSettings {
  directory: string
  defaultDirectory: string
  custom: boolean
  automatic: boolean
  keep: number
  retention: Retention
  /** second folder every backup is also copied to (null = none) */
  mirrorDirectory: string | null
  /** why the last copy failed (null = fine) */
  mirrorError: string | null
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
/** HTML = rich-text editor; MARKDOWN = Markdown source, shown formatted on demand. */
export type NoteFormat = 'HTML' | 'MARKDOWN'

export interface Note {
  itemId: number
  content: string | null
  /** null when the item has no note */
  format: NoteFormat | null
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

// --- today, reviews, time, tags, search, sync -------------------------------

export interface DueReview {
  itemId: number
  name: string
  planId: number
  planName: string
  parentName: string | null
  /** which review this is (1 = first) of `of` */
  review: number
  of: number
  dueOn: string
}

export interface ReviewResult {
  itemId: number
  finished: boolean
  nextDue: string | null
}

export interface NextTopic {
  itemId: number
  name: string
  priority: Priority | null
  path: string[]
}

export interface Today {
  dueReviews: DueReview[]
  nextUp: { planId: number; planName: string; topics: NextTopic[] }[]
  minutesToday: number
  minutesLast7Days: number
}

export interface TimeEntry {
  id: number
  minutes: number
  loggedAt: string
}

export interface ItemTime {
  itemId: number
  totalMinutes: number
  entries: TimeEntry[]
}

export interface TagCount {
  name: string
  items: number
}

export interface SearchHit {
  itemId: number
  name: string
  type: ItemType
  checkable: boolean
  completed: boolean
  planId: number
  planName: string
  planArchived: boolean
  path: string[]
  matchedIn: 'name' | 'description' | 'note' | null
  snippet: { text: string; hit: boolean }[]
}

export interface SearchResult {
  hits: SearchHit[]
  more: boolean
}

export type SyncChangeKind = 'ADD' | 'UPDATE' | 'MOVE' | 'DELETE' | 'ORDER'

export interface SyncChange {
  key: string
  kind: SyncChangeKind
  name: string | null
  path: string | null
  fields: { field: string; from: string | null; to: string | null }[]
  conflict: boolean
  defaultSelected: boolean
  row: number | null
  /** key of a new parent that must be added too */
  dependsOn: string | null
  detail: string | null
}

export interface SyncPreview {
  fileName: string
  errors: string[]
  warnings: string[]
  changes: SyncChange[]
  versionColumn: boolean
  canApply: boolean
}

export interface SyncResult {
  added: number
  updated: number
  moved: number
  deleted: number
  skipped: string[]
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

export interface DueCard {
  id: number
  itemId: number
  itemName: string
  planId: number
  planName: string
  question: string
  answer: string
  format: NoteFormat
  stage: number
  dueOn: string
}

export interface ItemCard {
  id: number
  question: string
  stage: number
  dueOn: string
}

export interface CardReviewOutcome {
  id: number
  stage: number
  nextDue: string
}

export interface Backlink {
  itemId: number
  name: string
  type: ItemType
}

export interface ShakyItem {
  itemId: number
  name: string
  planId: number
  planName: string
  level: number
  ratedAt: string
}

export interface ConfidenceRated {
  itemId: number
  level: number | null
  /** set when the rating brought the topic's review forward to this day */
  reviewOn: string | null
}

export interface PlanBackupPreview {
  fileName: string
  planName: string
  suggestedName: string
  nameTaken: boolean
  exportedAt: string | null
  modules: number
  topics: number
  completed: number
  notes: number
  tags: number
  links: number
  timeEntries: number
  minutes: number
  reviews: number
  ratings: number
  flashcards: number
}
