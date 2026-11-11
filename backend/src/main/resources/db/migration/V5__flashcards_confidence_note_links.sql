-- Flashcards, confidence ratings and [[links]] between notes.

-- How well you know an item, 1 (shaky) to 5 (solid). Kept outside learning_item so that rating
-- never bumps the item's optimistic-lock version.
CREATE TABLE item_confidence (
    item_id  INTEGER PRIMARY KEY REFERENCES learning_item (id) ON DELETE CASCADE,
    level    INTEGER NOT NULL CHECK (level BETWEEN 1 AND 5),
    rated_at TEXT    NOT NULL
);

-- Flashcards written in notes as "Q: … / A: …". The note is the source of truth: the cards are
-- re-derived whenever it is saved, and card_key (a hash of the question) keeps each card's
-- review schedule while its answer is edited or cards are reordered.
CREATE TABLE flashcard (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id          INTEGER NOT NULL REFERENCES learning_item (id) ON DELETE CASCADE,
    card_key         TEXT    NOT NULL,
    question         TEXT    NOT NULL,
    answer           TEXT    NOT NULL,
    position         INTEGER NOT NULL,
    stage            INTEGER NOT NULL DEFAULT 0 CHECK (stage >= 0),
    due_on           TEXT    NOT NULL,
    last_reviewed_at TEXT,
    created_at       TEXT    NOT NULL,
    UNIQUE (item_id, card_key)
);
CREATE INDEX ix_flashcard_due ON flashcard (due_on);

-- [[Name]] references in notes, by normalized target name (lower case, single spaces), so that
-- an item can list the notes that link to it ("Linked from").
CREATE TABLE note_ref (
    source_item_id INTEGER NOT NULL REFERENCES learning_item (id) ON DELETE CASCADE,
    target_name    TEXT    NOT NULL,
    PRIMARY KEY (source_item_id, target_name)
);
CREATE INDEX ix_note_ref_target ON note_ref (target_name);
