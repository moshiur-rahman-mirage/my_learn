import { request } from './client'
import type {
  Activity,
  BackupFile,
  BackupSettings,
  ColumnRole,
  DashboardSummary,
  FolderListing,
  ImportPreview,
  ImportResult,
  Item,
  ItemType,
  Note,
  Plan,
  PlanStatus,
  Priority,
  Progress,
  RestoreResult,
  TreeNode,
} from '../types/api'

export interface ItemInput {
  name: string
  type: ItemType
  description: string | null
  priority: Priority | null
}

export const plansApi = {
  list: (search?: string, status?: PlanStatus | null) => {
    const q = new URLSearchParams()
    if (search) q.set('search', search)
    if (status) q.set('status', status)
    const qs = q.toString()
    return request<Plan[]>('GET', '/api/plans' + (qs ? `?${qs}` : ''))
  },
  get: (id: number) => request<Plan>('GET', `/api/plans/${id}`),
  create: (name: string, description: string | null) => request<Plan>('POST', '/api/plans', { name, description }),
  update: (plan: Pick<Plan, 'id' | 'name' | 'description' | 'status' | 'version'>) =>
    request<Plan>('PUT', `/api/plans/${plan.id}`, {
      name: plan.name,
      description: plan.description,
      status: plan.status,
      version: plan.version,
    }),
  remove: (id: number) => request<void>('DELETE', `/api/plans/${id}`),
  progress: (id: number) => request<Progress>('GET', `/api/plans/${id}/progress`),
  exportUrl: (id: number) => `/api/plans/${id}/export`,
}

export const itemsApi = {
  tree: (planId: number) => request<TreeNode[]>('GET', `/api/plans/${planId}/items`),
  create: (planId: number, input: ItemInput & { parentId: number | null; position?: number | null }) =>
    request<Item>('POST', `/api/plans/${planId}/items`, input),
  update: (id: number, input: ItemInput & { version: number }) => request<Item>('PUT', `/api/items/${id}`, input),
  remove: (id: number) => request<void>('DELETE', `/api/items/${id}`),
  setCompleted: (id: number, completed: boolean) =>
    request<Item>('PATCH', `/api/items/${id}/completion`, { completed }),
  move: (id: number, parentId: number | null, position: number | null = null) =>
    request<Item>('PATCH', `/api/items/${id}/move`, { parentId, position }),
  reorder: (id: number, position: number) => request<Item>('PATCH', `/api/items/${id}/reorder`, { position }),
  exportUrl: (id: number) => `/api/items/${id}/export`,
}

export const notesApi = {
  get: (itemId: number) => request<Note>('GET', `/api/items/${itemId}/notes`),
  save: (itemId: number, content: string) => request<Note>('PUT', `/api/items/${itemId}/notes`, { content }),
}

export const dashboardApi = {
  summary: () => request<DashboardSummary>('GET', '/api/dashboard/summary'),
  /** days: 0 = all history. Days follow the browser's time zone. */
  activity: (days: number, planId: number | null) => {
    const q = new URLSearchParams({ days: String(days), tz: Intl.DateTimeFormat().resolvedOptions().timeZone })
    if (planId) q.set('planId', String(planId))
    return request<Activity>('GET', `/api/dashboard/activity?${q}`)
  },
}

export interface ImportParams {
  sheet?: string | null
  headerRow?: number | null
  columnRoles?: ColumnRole[] | null
  newPlanName?: string | null
  allSheets?: boolean
  allowDuplicate?: boolean
}

function importForm(file: File, p: ImportParams): FormData {
  const form = new FormData()
  form.append('file', file)
  if (p.sheet) form.append('sheet', p.sheet)
  if (p.headerRow) form.append('headerRow', String(p.headerRow))
  if (p.columnRoles?.length) form.append('columnRoles', p.columnRoles.join(','))
  if (p.newPlanName) form.append('newPlanName', p.newPlanName)
  if (p.allSheets) form.append('allSheets', 'true')
  if (p.allowDuplicate) form.append('allowDuplicate', 'true')
  return form
}

export const importApi = {
  preview: (planId: number | null, file: File, p: ImportParams) =>
    request<ImportPreview>('POST', planId ? `/api/plans/${planId}/import/preview` : '/api/import/preview', importForm(file, p)),
  confirm: (planId: number | null, file: File, p: ImportParams) =>
    request<ImportResult>('POST', planId ? `/api/plans/${planId}/import/confirm` : '/api/import/confirm', importForm(file, p)),
  /** Header row only, ready to fill in. */
  blankTemplateUrl: '/api/import/template',
  /** Same columns plus example rows showing nesting. */
  exampleTemplateUrl: '/api/import/template?examples=true',
}

export const adminApi = {
  backup: () => request<BackupFile>('POST', '/api/admin/backup'),
  backups: () => request<BackupFile[]>('GET', '/api/admin/backups'),
  backupSettings: () => request<BackupSettings>('GET', '/api/admin/backup-settings'),
  /** blank directory = back to the default folder */
  setBackupDirectory: (directory: string) => request<BackupSettings>('PUT', '/api/admin/backup-settings', { directory }),
  folders: (path: string | null) =>
    request<FolderListing>('GET', '/api/admin/folders' + (path ? `?path=${encodeURIComponent(path)}` : '')),
  restoreBackup: (fileName: string) =>
    request<RestoreResult>('POST', `/api/admin/backups/${encodeURIComponent(fileName)}/restore`),
  restoreFile: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return request<RestoreResult>('POST', '/api/admin/restore', form)
  },
}
