# Phone ⇄ desktop sync protocol (v1)

Two-way sync between the Android app (`mobile/`) and the desktop server (`backend/`).
Both sides hold a full SQLite database with the **same schema** (migrations V1–V7).
The phone works fully offline. When it reaches the desktop over Wi-Fi it calls one endpoint, which pushes its own changes and pulls the desktop's.

> Analogy: each side keeps a **logbook** (`sync_change`) with one line per row it has touched. Each line records
> *what* changed and *when*. Syncing means swapping the logbook pages written since the last swap. If both sides
> wrote about the same row, the later entry wins. A note is never thrown away, though: the losing text is
> appended to the winning note.

## 1. Schema additions: `V7__sync.sql` (identical on both sides)

### 1.1 Stable cross-device keys
Database `id`s differ per device, so rows are matched by keys that never change:

| Entity (`entity`) | Table | `key` |
|---|---|---|
| `plan` | learning_plan | `learning_plan.external_id` **(new column, UUID)** |
| `item` | learning_item | `learning_item.external_id` (exists) |
| `note` | item_note | the item's external_id |
| `link` | item_link | `item_link.external_id` **(new column, UUID)** |
| `timeLog` | time_log | `time_log.external_id` **(new column, UUID)** |
| `itemTag` | item_tag (+ tag) | `<itemExtId>\|<lower(tagName)>` |
| `review` | item_review | the item's external_id |
| `confidence` | item_confidence | the item's external_id |
| `flashcard` | flashcard | `<itemExtId>\|<card_key>`; **schedule only** (`stage`, `due_on`, `last_reviewed_at`) |

**Not synced** (each side derives them locally from synced data): `search_index`, `note_ref`, `tag` rows
(created on demand, orphans removed), flashcard rows themselves and their question/answer/position
(re-derived from notes), `import_batch`, `app_meta`, `flyway_schema_history`.

**The new `external_id` columns** are added with `ALTER TABLE … ADD COLUMN` and filled for existing rows with SQL-generated
UUIDs. A unique index is added after that. Application code does **not** need to set them: an `AFTER INSERT … WHEN NEW.external_id IS NULL`
trigger fills them, so JPA inserts, JDBC inserts, imports and plan restores are all covered unchanged.

### 1.2 Change log
```sql
CREATE TABLE sync_change (
  seq        INTEGER PRIMARY KEY AUTOINCREMENT,  -- monotonically increasing cursor
  entity     TEXT NOT NULL,
  key        TEXT NOT NULL,
  changed_at TEXT NOT NULL,      -- ISO-8601 UTC, ms precision ("2026-11-25T10:00:00.123Z")
  deleted    INTEGER NOT NULL DEFAULT 0,
  origin     TEXT,               -- NULL = changed locally; otherwise who it came from (deviceId / 'server')
  UNIQUE (entity, key)
);
CREATE TABLE sync_device (       -- server only uses it; harmless on the phone
  device_id    TEXT PRIMARY KEY,
  name         TEXT,
  last_sync_at TEXT NOT NULL
);
```
- **Triggers** on every synced table run `INSERT OR REPLACE INTO sync_change (entity, key, changed_at, deleted)`.
  REPLACE drops the old line, so there is **one line per row, carrying the newest `seq`**. Deletes leave a line with
  `deleted = 1`, which serves as the tombstone.
- Child rows deleted by an item cascade compute their key from the (already deleted) item and get NULL. Those
  triggers insert nothing (`WHERE key IS NOT NULL`), because the item's own tombstone carries the cascade.
- `flashcard`: only `AFTER UPDATE OF stage, due_on, last_reviewed_at`. Inserts and deletes come from notes and are derived locally.
- `INSERT OR REPLACE` into `item_review`/`item_confidence` fires only the INSERT trigger (`recursive_triggers` is off), which is what we want.
- The migration **seeds** `sync_change` with one line for every existing row, so the first full pull carries everything.
- `app_meta('sync_epoch')` holds a random UUID. It is created by V7 and **regenerated after every database restore** on the
  desktop (`RestoreService`). A different epoch tells the phone that the server's history was replaced.

## 2. Endpoint

`POST /api/sync`, with headers `Authorization: Bearer <pairing token>` and `Content-Type: application/json`

### Request
```jsonc
{
  "protocol": 1,
  "deviceId": "6f1c…",                 // UUID chosen by the phone at install
  "deviceName": "Pixel 8",
  "epoch": "a2b9…",                    // last epoch the phone saw; null on first sync
  "since": 1234,                       // server cursor from the last response; 0 on first sync
  "clientNow": "2026-11-25T10:00:00.000Z",
  "changes": [ Change, … ]             // phone's local lines with origin IS NULL and seq > lastPushedSeq
}
```

### Change
```jsonc
{
  "entity": "item",
  "key": "0b6e…",
  "changedAt": "2026-11-25T09:58:12.345Z",
  "deleted": false,
  "data": { … }                        // row snapshot (below); null when deleted
}
```

### Row snapshots (`data`): all references use external keys
| entity | fields |
|---|---|
| plan | `name, description, status, targetDate, createdAt, updatedAt` |
| item | `planKey, parentKey (null = root), type, name, description, priority, completed, completedAt, displayOrder, createdAt, updatedAt` |
| note | `itemKey, content, format ('HTML'\|'MARKDOWN'), updatedAt` |
| link | `itemKey, url, title, displayOrder, createdAt` |
| timeLog | `itemKey, minutes, loggedAt` |
| itemTag | `itemKey, tagName` |
| review | `itemKey, stage, dueOn, lastReviewedAt` |
| confidence | `itemKey, level, ratedAt` |
| flashcard | `itemKey, cardKey, stage, dueOn, lastReviewedAt` |

### Response
```jsonc
{
  "protocol": 1,
  "epoch": "a2b9…",
  "reset": false,                      // true: epoch changed. The phone must wipe its data and rebuild from these pages
  "cursor": 1301,                      // pass as `since` next time
  "more": false,                       // true: call again with since=cursor and changes=[]
  "serverNow": "2026-11-25T10:00:00.120Z",
  "applied": 12,
  "skipped": [ { "entity": "item", "key": "…", "reason": "PARENT_DELETED" } ],
  "conflicts": [ { "entity": "note", "key": "…", "resolution": "MERGED" } ],
  "changes": [ Change, … ]             // server lines with seq > since, origin <> deviceId, ordered by seq, max 2000
}
```
Errors: `401` bad or missing token · `409 PROTOCOL_MISMATCH` · `413` too large. A 4xx/5xx means **nothing** was applied (one transaction).

## 3. Server algorithm (one transaction)

1. Check the token and protocol. Upsert `sync_device`. Compute `skew = serverNow − clientNow`. Every incoming `changedAt` is
   shifted by `skew`, so both clocks are compared in server time.
2. `reset = (request.epoch != null && request.epoch != serverEpoch)`. On reset, `since` is treated as 0.
3. `before = max(seq)`. Apply the incoming changes in **entity order**: plan → item → note → link → timeLog → itemTag →
   review → confidence → flashcard. Items are applied parents first, in repeated passes over the batch.
   For each change, take the local line `L = sync_change(entity, key)`:
   - `L` exists and `L.changed_at > shifted changedAt` → **the local row wins**. Skip the change, except for notes (step 4).
   - otherwise → **the incoming change wins**. Upsert or delete the row with plain JDBC, mapping keys to ids. Item updates do `version = version + 1`.
4. **Note conflict.** Both sides edited the note since the last sync: `L.seq > since`, `L.origin <> deviceId` and the contents differ.
   The winner by time keeps its content, and the loser's text is appended below a marker
   (`\n\n---\n**Conflicting copy from <device> (<time>)**\n\n…` for Markdown, `<hr><p><strong>…</strong></p>…` for HTML).
   The merged note is a new local change, so the phone pulls it back.
5. Integrity fixes after applying: renumber siblings `0..n-1` (by `display_order`, then `updated_at`) for every
   touched parent. A TOPIC that now has children loses its completion and review. A plan name that collides with another plan
   gets a ` (2)` suffix. Any note that was applied or merged has its derived data rebuilt: `SearchIndexer.indexNote`,
   `FlashcardService.syncFromNote`, then `NoteLinks.update`.
   Flashcard schedules are applied **after** their notes, so the card rows exist. Missing cards → `skipped: CARD_NOT_FOUND`.
6. Every line with `seq > before` gets `origin = deviceId`, so it is not echoed back. Lines of rows that the incoming change won get
   `changed_at = shifted changedAt`.
7. Pull: `SELECT … FROM sync_change WHERE seq > since AND (origin IS NULL OR origin <> deviceId) ORDER BY seq LIMIT 2001`,
   joining each line to its current row to build the snapshot. Set `more` if there are 2001 lines.
   Lines whose row has disappeared are sent as `deleted`.

Skip reasons: `PARENT_DELETED`, `PLAN_DELETED`, `ITEM_DELETED`, `CARD_NOT_FOUND`, `CYCLE` (a move under its own
descendant, e.g. both sides moved two items into each other), `INVALID`.

Ties: when both timestamps are equal, the incoming change wins. Implementation: `backend/.../sync/` (`PhoneSyncService`, `SyncApplier`, `SyncSnapshots`).

## 4. Phone algorithm

```
pushedUpTo = max(local seq)                         // snapshot before sending
send changes = lines with origin IS NULL AND seq > lastPushedSeq
for each response page (first page carries the push; later pages: since=cursor, changes=[]):
  BEGIN
    if reset and first page: delete all synced rows (plans cascade everything; tags, sync_change)
    apply changes in entity order:
       skip a change whose local line has origin IS NULL AND seq > pushedUpTo   // edited during the request; pushed next time
    the same integrity fixes and derived-data rebuild as the server (shared rules, ported to TS)
    mark lines with seq > beforeApply as origin = 'server'
  COMMIT
lastPushedSeq = pushedUpTo; since = cursor; epoch = response.epoch   (saved in app_meta after the last page)
```
A failure on any page leaves `since` and `epoch` unsaved. The next sync repeats safely, because applying the same snapshot twice is a no-op.

**Reset (desktop database restored).** Local unpushed changes are pushed first, in the same request, and merged by the server
with `since = 0`. Then the phone rebuilds from the server's full history.

## 5. Pairing and network safety

- **Token.** The desktop creates a random 32-byte token (base64url) on first use and keeps it in `settings.properties`, which survives
  database restores. Settings → *Phone sync* shows the server address(es), the token and a QR code of
  `{"url":"http://192.168.1.20:38427","token":"…"}`, and lets you **reset** the token, which unpairs every device.
- **Remote access filter.** A request from a non-loopback address may only call `POST /api/sync` and `GET /api/sync/ping`,
  and only with a valid token. Anything else from the LAN gets `403`.
  Docker (requests arrive through nginx) sets `app.remote-access=all` to keep its current behaviour.
- **Binding.** Sync needs the server to listen on the LAN. The installed Windows app's profile (`application-desktop.yml`)
  used to bind `127.0.0.1`; it now binds `0.0.0.0`, and the filter above keeps everything except sync this-PC-only.
  `install-autostart.ps1` binds `127.0.0.1` unless `-AllowPhoneSync` is given. Windows Firewall asks once: allow private networks.
- `GET /api/sync/ping` (token required) → `{ "app": "learning-tracker", "protocol": 1, "epoch": "…" }`, used by the pairing screen.

## 6. Shared rules that must match byte-for-byte (parity tests)

- **Flashcard `card_key`** = first 12 bytes of SHA-256(`lower(question)` with whitespace collapsed and stripped), as 24 hex chars.
  Java's `\s` and `strip()` are ASCII/`Character.isWhitespace` based, so the TS port must not use JS's Unicode `\s`.
  A fixture file `backend/src/test/resources/sync/flashcard-fixtures.json` of note → keys is generated from Java and
  checked by the mobile unit tests.
- **`HtmlText.toLines`** (HTML notes → lines) and **`wikiKey`** (`trim`, collapse spaces, lower case).
- **Timestamps**: `YYYY-MM-DDTHH:mm:ss.SSSZ` UTC everywhere. Dates (`due_on`, `target_date`) are local `YYYY-MM-DD`.

## 7. Known limits (v1)

- Tombstones are never pruned (small rows). A future version may prune lines older than the oldest device's last sync.
- An item edited on the phone whose parent was deleted on the desktop is skipped and reported, not resurrected.
- Moving an item between plans isn't supported by the app, so it isn't handled by sync.
