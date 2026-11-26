import { File, Paths } from 'expo-file-system'
import * as Sharing from 'expo-sharing'
import type { Db } from '@/db/types'
import { formatDay } from '@/domain/dates'

/** Writes a text file to the cache and opens Android's share sheet for it (Drive, email, Files…). */
export async function shareTextFile(fileName: string, content: string, mimeType: string): Promise<void> {
  const file = new File(Paths.cache, fileName)
  file.create({ overwrite: true })
  await file.write(content)
  await Sharing.shareAsync(file.uri, { mimeType, dialogTitle: fileName })
}

/**
 * A complete copy of this phone's database (plans, notes, progress, schedules), made with
 * SQLite's VACUUM INTO like the desktop's backups, then shared. Keep it in Drive or Files as a
 * spare; your computer remains the main copy.
 */
export async function sharePhoneBackup(db: Db): Promise<void> {
  const stamp = `${formatDay(new Date())}-${new Date().toTimeString().slice(0, 8).replace(/:/g, '')}`
  const file = new File(Paths.cache, `learning-tracker-phone-${stamp}.db`)
  if (file.exists) file.delete()
  const path = decodeURIComponent(file.uri.replace(/^file:\/\//, ''))
  await db.exec(`VACUUM INTO '${path.replace(/'/g, "''")}'`)
  await Sharing.shareAsync(file.uri, { mimeType: 'application/vnd.sqlite3', dialogTitle: 'Back up Learning Tracker' })
}
