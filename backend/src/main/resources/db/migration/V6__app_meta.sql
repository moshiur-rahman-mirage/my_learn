-- Small key/value store for app bookkeeping.
-- "note_index_fingerprint" records which parser version and schema version the data derived
-- from notes (flashcards, [[link]] references, search text) was last fully rebuilt with, so
-- start-up can skip the rebuild when nothing has changed (NoteIndexes). There is no row yet, so
-- the first start after this migration rebuilds once.
CREATE TABLE app_meta (
    key   TEXT PRIMARY KEY,
    value TEXT NOT NULL
);
