-- Learning features: target dates, links, time spent, tags, review reminders, full-text search.

-- Optional "finish by" date of a plan (yyyy-MM-dd, the viewer's calendar day).
ALTER TABLE learning_plan ADD COLUMN target_date TEXT;

-- Web links of an item (video, docs page, course lesson...).
CREATE TABLE item_link (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id       INTEGER NOT NULL REFERENCES learning_item (id) ON DELETE CASCADE,
    url           TEXT    NOT NULL CHECK (length(trim(url)) > 0),
    title         TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at    TEXT    NOT NULL
);
CREATE INDEX ix_item_link_item ON item_link (item_id, display_order);

-- Time spent learning, one row per session ("+25 min" or a finished Pomodoro).
CREATE TABLE time_log (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id   INTEGER NOT NULL REFERENCES learning_item (id) ON DELETE CASCADE,
    minutes   INTEGER NOT NULL CHECK (minutes BETWEEN 1 AND 1440),
    logged_at TEXT    NOT NULL
);
CREATE INDEX ix_time_log_item ON time_log (item_id);
CREATE INDEX ix_time_log_logged ON time_log (logged_at);

-- Labels shared by all plans ("interview", "revise"...). Names are unique regardless of case.
CREATE TABLE tag (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL CHECK (length(trim(name)) > 0)
);
CREATE UNIQUE INDEX ux_tag_name ON tag (name COLLATE NOCASE);

CREATE TABLE item_tag (
    item_id INTEGER NOT NULL REFERENCES learning_item (id) ON DELETE CASCADE,
    tag_id  INTEGER NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (item_id, tag_id)
);
CREATE INDEX ix_item_tag_tag ON item_tag (tag_id);

-- Spaced-repetition reviews of completed topics: one row per topic still being reviewed.
-- stage = number of reviews done so far; due_on = yyyy-MM-dd when the next review is due.
CREATE TABLE item_review (
    item_id          INTEGER PRIMARY KEY REFERENCES learning_item (id) ON DELETE CASCADE,
    stage            INTEGER NOT NULL CHECK (stage >= 0),
    due_on           TEXT    NOT NULL,
    last_reviewed_at TEXT
);
CREATE INDEX ix_item_review_due ON item_review (due_on);

-- Full-text search over item names, descriptions and notes. Names and descriptions are kept up
-- to date by the triggers below, so every write path (UI, import, sync) is covered. The note
-- column holds the note as PLAIN TEXT (notes are stored as HTML, and indexing the markup would
-- make "strong" or "span" match every note): the app writes it when a note is saved and
-- rebuilds it at start-up and after a restore (SearchIndexer).
CREATE VIRTUAL TABLE search_index USING fts5(
    name, description, note,
    item_id UNINDEXED,
    tokenize = 'unicode61 remove_diacritics 2'
);

INSERT INTO search_index (rowid, name, description, note, item_id)
SELECT i.id, i.name, COALESCE(i.description, ''), '', i.id
FROM learning_item i;

CREATE TRIGGER trg_search_item_insert AFTER INSERT ON learning_item BEGIN
    INSERT INTO search_index (rowid, name, description, note, item_id)
    VALUES (NEW.id, NEW.name, COALESCE(NEW.description, ''), '', NEW.id);
END;

CREATE TRIGGER trg_search_item_update AFTER UPDATE OF name, description ON learning_item BEGIN
    UPDATE search_index SET name = NEW.name, description = COALESCE(NEW.description, '') WHERE rowid = NEW.id;
END;

CREATE TRIGGER trg_search_item_delete AFTER DELETE ON learning_item BEGIN
    DELETE FROM search_index WHERE rowid = OLD.id;
END;

CREATE TRIGGER trg_search_note_delete AFTER DELETE ON item_note BEGIN
    UPDATE search_index SET note = '' WHERE rowid = OLD.item_id;
END;
