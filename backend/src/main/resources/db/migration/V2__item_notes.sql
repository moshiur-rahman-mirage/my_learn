-- Optional formatted notes / summary per item (module or topic).
-- A separate table so loading a plan's tree never reads note bodies.
-- content is sanitized HTML produced by the editor; deleting an item deletes its note.
CREATE TABLE item_note (
    item_id    INTEGER PRIMARY KEY REFERENCES learning_item (id) ON DELETE CASCADE,
    content    TEXT    NOT NULL,
    updated_at TEXT    NOT NULL
);
