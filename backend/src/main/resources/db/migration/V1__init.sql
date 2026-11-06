-- Initial schema for the Personal Learning Tracker.
-- Timestamps are ISO-8601 UTC text with fixed millisecond precision ("2026-11-06T07:47:28.123Z"),
-- so they sort correctly as plain strings.

CREATE TABLE learning_plan (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL CHECK (length(trim(name)) > 0),
    description TEXT,
    status      TEXT    NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'ARCHIVED')),
    version     INTEGER NOT NULL DEFAULT 0,
    created_at  TEXT    NOT NULL,
    updated_at  TEXT    NOT NULL
);

-- Plan names are unique regardless of case ("Core Java" == "core java").
CREATE UNIQUE INDEX ux_plan_name ON learning_plan (name COLLATE NOCASE);

CREATE TABLE learning_item (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    external_id   TEXT    NOT NULL UNIQUE,   -- stable UUID used by CSV export/sync
    plan_id       INTEGER NOT NULL REFERENCES learning_plan (id) ON DELETE CASCADE,
    parent_id     INTEGER,                   -- NULL = root item of the plan
    item_type     TEXT    NOT NULL CHECK (item_type IN ('MODULE', 'TOPIC')),
    name          TEXT    NOT NULL CHECK (length(trim(name)) > 0),
    description   TEXT,
    priority      TEXT    CHECK (priority IN ('MUST', 'SHOULD', 'NICE')),  -- NULL = no priority
    is_completed  INTEGER NOT NULL DEFAULT 0 CHECK (is_completed IN (0, 1)),
    completed_at  TEXT,
    display_order INTEGER NOT NULL CHECK (display_order >= 0),
    version       INTEGER NOT NULL DEFAULT 0,
    created_at    TEXT    NOT NULL,
    updated_at    TEXT    NOT NULL,

    -- Target for the composite foreign key below.
    UNIQUE (id, plan_id),
    -- A parent must exist AND belong to the same plan. Deleting a parent deletes its whole subtree.
    FOREIGN KEY (parent_id, plan_id) REFERENCES learning_item (id, plan_id) ON DELETE CASCADE,
    -- An item can never be its own parent (deeper cycles are prevented in the service layer).
    CHECK (parent_id IS NULL OR parent_id <> id)
);

-- Children of a parent in display order (also serves plan-wide lookups and the plan cascade).
CREATE INDEX ix_item_plan_parent_order ON learning_item (plan_id, parent_id, display_order);
-- Child lookups by parent (the subtree cascade and the "is this a leaf?" checks).
CREATE INDEX ix_item_parent ON learning_item (parent_id);

-- One row per confirmed import; used to warn about importing the same content twice (Phase 6).
CREATE TABLE import_batch (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    plan_id        INTEGER NOT NULL REFERENCES learning_plan (id) ON DELETE CASCADE,
    file_name      TEXT    NOT NULL,
    sheet_name     TEXT,
    content_sha256 TEXT    NOT NULL,
    item_count     INTEGER NOT NULL,
    imported_at    TEXT    NOT NULL
);

CREATE INDEX ix_import_batch_sha ON import_batch (content_sha256);
