package com.mylearn.tracker.sync;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the current row behind a change line as a snapshot with external keys
 * (docs/SYNC_PROTOCOL.md §2, "Row snapshots"). Returns null when the row no longer exists.
 */
@Component
class SyncSnapshots {

    private final JdbcTemplate jdbc;

    SyncSnapshots(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    Map<String, Object> read(String entity, String key) {
        return switch (entity) {
            case "plan" -> one("""
                    SELECT name, description, status, target_date AS targetDate, created_at AS createdAt, updated_at AS updatedAt
                    FROM learning_plan WHERE external_id = ?""", key);
            case "item" -> {
                Map<String, Object> row = one("""
                        SELECT p.external_id AS planKey, pa.external_id AS parentKey, i.item_type AS type, i.name, i.description,
                               i.priority, i.is_completed AS completed, i.completed_at AS completedAt,
                               i.display_order AS displayOrder, i.created_at AS createdAt, i.updated_at AS updatedAt
                        FROM learning_item i
                        JOIN learning_plan p ON p.id = i.plan_id
                        LEFT JOIN learning_item pa ON pa.id = i.parent_id
                        WHERE i.external_id = ?""", key);
                if (row != null) {
                    row.put("completed", ((Number) row.get("completed")).intValue() == 1);
                }
                yield row;
            }
            case "note" -> one("""
                    SELECT i.external_id AS itemKey, n.content, n.format, n.updated_at AS updatedAt
                    FROM item_note n JOIN learning_item i ON i.id = n.item_id WHERE i.external_id = ?""", key);
            case "link" -> one("""
                    SELECT i.external_id AS itemKey, l.url, l.title, l.display_order AS displayOrder, l.created_at AS createdAt
                    FROM item_link l JOIN learning_item i ON i.id = l.item_id WHERE l.external_id = ?""", key);
            case "timeLog" -> one("""
                    SELECT i.external_id AS itemKey, t.minutes, t.logged_at AS loggedAt
                    FROM time_log t JOIN learning_item i ON i.id = t.item_id WHERE t.external_id = ?""", key);
            case "itemTag" -> {
                String[] parts = SyncKeys.split(key);
                yield one("""
                        SELECT i.external_id AS itemKey, t.name AS tagName
                        FROM item_tag it JOIN learning_item i ON i.id = it.item_id JOIN tag t ON t.id = it.tag_id
                        WHERE i.external_id = ? AND lower(t.name) = ?""", parts[0], parts[1]);
            }
            case "review" -> one("""
                    SELECT i.external_id AS itemKey, r.stage, r.due_on AS dueOn, r.last_reviewed_at AS lastReviewedAt
                    FROM item_review r JOIN learning_item i ON i.id = r.item_id WHERE i.external_id = ?""", key);
            case "confidence" -> one("""
                    SELECT i.external_id AS itemKey, c.level, c.rated_at AS ratedAt
                    FROM item_confidence c JOIN learning_item i ON i.id = c.item_id WHERE i.external_id = ?""", key);
            case "flashcard" -> {
                String[] parts = SyncKeys.split(key);
                yield one("""
                        SELECT i.external_id AS itemKey, f.card_key AS cardKey, f.stage, f.due_on AS dueOn,
                               f.last_reviewed_at AS lastReviewedAt
                        FROM flashcard f JOIN learning_item i ON i.id = f.item_id
                        WHERE i.external_id = ? AND f.card_key = ?""", parts[0], parts[1]);
            }
            default -> null;
        };
    }

    private Map<String, Object> one(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbc.queryForList(sql, args);
        // queryForList gives a case-insensitive map; copy into a plain one so JSON keeps our camelCase
        return rows.isEmpty() ? null : new LinkedHashMap<>(rows.getFirst());
    }
}
