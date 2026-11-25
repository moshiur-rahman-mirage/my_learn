import type { Db } from '@/db/types'
import { splitKey, type Entity, type Snapshot } from './protocol'

/**
 * The current row behind a change line, as a snapshot with external keys (port of backend
 * sync/SyncSnapshots; the field names are the wire format). Null when the row no longer exists.
 */
export async function readSnapshot(db: Db, entity: Entity, key: string): Promise<Snapshot | null> {
  switch (entity) {
    case 'plan':
      return db.first<Snapshot>(
        `SELECT name, description, status, target_date AS targetDate, created_at AS createdAt, updated_at AS updatedAt
         FROM learning_plan WHERE external_id = ?`, [key])
    case 'item': {
      const row = await db.first<Snapshot>(
        `SELECT p.external_id AS planKey, pa.external_id AS parentKey, i.item_type AS type, i.name, i.description,
                i.priority, i.is_completed AS completed, i.completed_at AS completedAt,
                i.display_order AS displayOrder, i.created_at AS createdAt, i.updated_at AS updatedAt
         FROM learning_item i
         JOIN learning_plan p ON p.id = i.plan_id
         LEFT JOIN learning_item pa ON pa.id = i.parent_id
         WHERE i.external_id = ?`, [key])
      if (row) row.completed = row.completed === 1
      return row
    }
    case 'note':
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, n.content, n.format, n.updated_at AS updatedAt
         FROM item_note n JOIN learning_item i ON i.id = n.item_id WHERE i.external_id = ?`, [key])
    case 'link':
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, l.url, l.title, l.display_order AS displayOrder, l.created_at AS createdAt
         FROM item_link l JOIN learning_item i ON i.id = l.item_id WHERE l.external_id = ?`, [key])
    case 'timeLog':
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, t.minutes, t.logged_at AS loggedAt
         FROM time_log t JOIN learning_item i ON i.id = t.item_id WHERE t.external_id = ?`, [key])
    case 'itemTag': {
      const [itemKey, tag] = splitKey(key)
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, t.name AS tagName
         FROM item_tag it JOIN learning_item i ON i.id = it.item_id JOIN tag t ON t.id = it.tag_id
         WHERE i.external_id = ? AND lower(t.name) = ?`, [itemKey, tag])
    }
    case 'review':
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, r.stage, r.due_on AS dueOn, r.last_reviewed_at AS lastReviewedAt
         FROM item_review r JOIN learning_item i ON i.id = r.item_id WHERE i.external_id = ?`, [key])
    case 'confidence':
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, c.level, c.rated_at AS ratedAt
         FROM item_confidence c JOIN learning_item i ON i.id = c.item_id WHERE i.external_id = ?`, [key])
    case 'flashcard': {
      const [itemKey, cardKey] = splitKey(key)
      return db.first<Snapshot>(
        `SELECT i.external_id AS itemKey, f.card_key AS cardKey, f.stage, f.due_on AS dueOn, f.last_reviewed_at AS lastReviewedAt
         FROM flashcard f JOIN learning_item i ON i.id = f.item_id
         WHERE i.external_id = ? AND f.card_key = ?`, [itemKey, cardKey])
    }
    default:
      return null
  }
}
