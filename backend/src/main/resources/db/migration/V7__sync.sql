-- Two-way sync with the phone app (docs/SYNC_PROTOCOL.md).
-- The phone runs this same migration, so both databases keep an identical schema.

-- 1. Stable cross-device keys --------------------------------------------------------------------
-- Database ids differ per device; rows are matched by UUIDs that never change. Items already
-- have external_id. The triggers below fill the new columns on insert, so no write path has to
-- set them.

ALTER TABLE learning_plan ADD COLUMN external_id TEXT;
ALTER TABLE item_link ADD COLUMN external_id TEXT;
ALTER TABLE time_log ADD COLUMN external_id TEXT;

UPDATE learning_plan SET external_id = lower(hex(randomblob(4)) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)), 2) || '-' || substr('89ab', 1 + (abs(random()) % 4), 1) || substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6)));
UPDATE item_link     SET external_id = lower(hex(randomblob(4)) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)), 2) || '-' || substr('89ab', 1 + (abs(random()) % 4), 1) || substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6)));
UPDATE time_log      SET external_id = lower(hex(randomblob(4)) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)), 2) || '-' || substr('89ab', 1 + (abs(random()) % 4), 1) || substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6)));

CREATE UNIQUE INDEX ux_plan_external_id ON learning_plan (external_id);
CREATE UNIQUE INDEX ux_item_link_external_id ON item_link (external_id);
CREATE UNIQUE INDEX ux_time_log_external_id ON time_log (external_id);

CREATE TRIGGER trg_plan_external_id AFTER INSERT ON learning_plan WHEN NEW.external_id IS NULL BEGIN
    UPDATE learning_plan SET external_id = lower(hex(randomblob(4)) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)), 2) || '-' || substr('89ab', 1 + (abs(random()) % 4), 1) || substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))) WHERE id = NEW.id;
END;
CREATE TRIGGER trg_link_external_id AFTER INSERT ON item_link WHEN NEW.external_id IS NULL BEGIN
    UPDATE item_link SET external_id = lower(hex(randomblob(4)) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)), 2) || '-' || substr('89ab', 1 + (abs(random()) % 4), 1) || substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))) WHERE id = NEW.id;
END;
CREATE TRIGGER trg_time_log_external_id AFTER INSERT ON time_log WHEN NEW.external_id IS NULL BEGIN
    UPDATE time_log SET external_id = lower(hex(randomblob(4)) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)), 2) || '-' || substr('89ab', 1 + (abs(random()) % 4), 1) || substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))) WHERE id = NEW.id;
END;

-- 2. Change log --------------------------------------------------------------------------------
-- One line per synced row (REPLACE keeps only the newest), seq = sync cursor, deleted = tombstone.
-- origin: NULL = changed on this device; otherwise the device (or 'server') the change came from.

CREATE TABLE sync_change (
    seq        INTEGER PRIMARY KEY AUTOINCREMENT,
    entity     TEXT    NOT NULL,
    key        TEXT    NOT NULL,
    changed_at TEXT    NOT NULL,
    deleted    INTEGER NOT NULL DEFAULT 0 CHECK (deleted IN (0, 1)),
    origin     TEXT,
    UNIQUE (entity, key)
);

-- Devices that have synced with this database (shown on the desktop Settings page).
CREATE TABLE sync_device (
    device_id    TEXT PRIMARY KEY,
    name         TEXT,
    last_sync_at TEXT NOT NULL
);

-- Changes from before this migration: every existing row is one line, so a first sync carries everything.
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'plan', external_id, updated_at FROM learning_plan;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'item', external_id, updated_at FROM learning_item;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'note', i.external_id, n.updated_at FROM item_note n JOIN learning_item i ON i.id = n.item_id;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'link', external_id, created_at FROM item_link;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'timeLog', external_id, logged_at FROM time_log;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'itemTag', i.external_id || '|' || lower(t.name), strftime('%Y-%m-%dT%H:%M:%fZ', 'now')
FROM item_tag it JOIN learning_item i ON i.id = it.item_id JOIN tag t ON t.id = it.tag_id;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'review', i.external_id, COALESCE(r.last_reviewed_at, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))
FROM item_review r JOIN learning_item i ON i.id = r.item_id;
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'confidence', i.external_id, c.rated_at FROM item_confidence c JOIN learning_item i ON i.id = c.item_id;
-- Flashcards: only schedules that differ from a fresh card; fresh cards are re-derived from notes.
INSERT INTO sync_change (entity, key, changed_at)
SELECT 'flashcard', i.external_id || '|' || f.card_key, COALESCE(f.last_reviewed_at, f.created_at)
FROM flashcard f JOIN learning_item i ON i.id = f.item_id
WHERE f.stage > 0 OR f.last_reviewed_at IS NOT NULL;

-- Which history this database belongs to; the desktop changes it after a restore, so the phone rebuilds.
INSERT OR REPLACE INTO app_meta (key, value)
VALUES ('sync_epoch', lower(hex(randomblob(16))));

-- 3. Triggers that write the change log ----------------------------------------------------------
-- Keys of child rows come from their item; when the item itself was deleted (cascade) the key is
-- NULL and nothing is logged: the item's own tombstone carries the cascade to the other device.

-- plan
CREATE TRIGGER trg_sync_plan_ins AFTER INSERT ON learning_plan BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'plan', p.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0
    FROM learning_plan p WHERE p.id = NEW.id AND p.external_id IS NOT NULL;
END;
CREATE TRIGGER trg_sync_plan_upd AFTER UPDATE ON learning_plan WHEN NEW.external_id IS NOT NULL BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('plan', NEW.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0);
END;
CREATE TRIGGER trg_sync_plan_del AFTER DELETE ON learning_plan WHEN OLD.external_id IS NOT NULL BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('plan', OLD.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1);
END;

-- item
CREATE TRIGGER trg_sync_item_ins AFTER INSERT ON learning_item BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('item', NEW.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0);
END;
CREATE TRIGGER trg_sync_item_upd AFTER UPDATE ON learning_item BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('item', NEW.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0);
END;
CREATE TRIGGER trg_sync_item_del AFTER DELETE ON learning_item BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('item', OLD.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1);
END;

-- note
CREATE TRIGGER trg_sync_note_ins AFTER INSERT ON item_note BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'note', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0 FROM learning_item i WHERE i.id = NEW.item_id;
END;
CREATE TRIGGER trg_sync_note_upd AFTER UPDATE ON item_note BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'note', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0 FROM learning_item i WHERE i.id = NEW.item_id;
END;
CREATE TRIGGER trg_sync_note_del AFTER DELETE ON item_note BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'note', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1 FROM learning_item i WHERE i.id = OLD.item_id;
END;

-- link
CREATE TRIGGER trg_sync_link_ins AFTER INSERT ON item_link BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'link', l.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0
    FROM item_link l WHERE l.id = NEW.id AND l.external_id IS NOT NULL;
END;
CREATE TRIGGER trg_sync_link_upd AFTER UPDATE ON item_link WHEN NEW.external_id IS NOT NULL BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('link', NEW.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0);
END;
CREATE TRIGGER trg_sync_link_del AFTER DELETE ON item_link
WHEN OLD.external_id IS NOT NULL AND EXISTS (SELECT 1 FROM learning_item WHERE id = OLD.item_id) BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('link', OLD.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1);
END;

-- time log
CREATE TRIGGER trg_sync_time_log_ins AFTER INSERT ON time_log BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'timeLog', t.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0
    FROM time_log t WHERE t.id = NEW.id AND t.external_id IS NOT NULL;
END;
CREATE TRIGGER trg_sync_time_log_upd AFTER UPDATE ON time_log WHEN NEW.external_id IS NOT NULL BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('timeLog', NEW.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0);
END;
CREATE TRIGGER trg_sync_time_log_del AFTER DELETE ON time_log
WHEN OLD.external_id IS NOT NULL AND EXISTS (SELECT 1 FROM learning_item WHERE id = OLD.item_id) BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    VALUES ('timeLog', OLD.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1);
END;

-- item tag (rows are only inserted and deleted)
CREATE TRIGGER trg_sync_item_tag_ins AFTER INSERT ON item_tag BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'itemTag', i.external_id || '|' || lower(t.name), strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0
    FROM learning_item i, tag t WHERE i.id = NEW.item_id AND t.id = NEW.tag_id;
END;
CREATE TRIGGER trg_sync_item_tag_del AFTER DELETE ON item_tag BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'itemTag', i.external_id || '|' || lower(t.name), strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1
    FROM learning_item i, tag t WHERE i.id = OLD.item_id AND t.id = OLD.tag_id;
END;

-- review
CREATE TRIGGER trg_sync_review_ins AFTER INSERT ON item_review BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'review', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0 FROM learning_item i WHERE i.id = NEW.item_id;
END;
CREATE TRIGGER trg_sync_review_upd AFTER UPDATE ON item_review BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'review', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0 FROM learning_item i WHERE i.id = NEW.item_id;
END;
CREATE TRIGGER trg_sync_review_del AFTER DELETE ON item_review BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'review', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1 FROM learning_item i WHERE i.id = OLD.item_id;
END;

-- confidence
CREATE TRIGGER trg_sync_confidence_ins AFTER INSERT ON item_confidence BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'confidence', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0 FROM learning_item i WHERE i.id = NEW.item_id;
END;
CREATE TRIGGER trg_sync_confidence_upd AFTER UPDATE ON item_confidence BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'confidence', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0 FROM learning_item i WHERE i.id = NEW.item_id;
END;
CREATE TRIGGER trg_sync_confidence_del AFTER DELETE ON item_confidence BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'confidence', i.external_id, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 1 FROM learning_item i WHERE i.id = OLD.item_id;
END;

-- flashcard schedule (cards themselves are derived from notes on each device)
CREATE TRIGGER trg_sync_flashcard_upd AFTER UPDATE OF stage, due_on, last_reviewed_at ON flashcard BEGIN
    INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)
    SELECT 'flashcard', i.external_id || '|' || NEW.card_key, strftime('%Y-%m-%dT%H:%M:%fZ', 'now'), 0
    FROM learning_item i WHERE i.id = NEW.item_id;
END;
