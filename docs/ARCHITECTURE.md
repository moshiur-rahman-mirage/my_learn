# Personal Learning Tracker — Architecture

> Status: phases 1–9 implemented, including CSV sync/merge (§6.4, `importer/SyncService`) and the learning features of migration V3 (target dates, reviews, links, time log, tags, FTS5 search). Run/deploy instructions: [README](../README.md).

## 1. Overview

```
 Browser (Vue 3 SPA)
      │  same origin: /            → static UI
      │               /api/**      → REST
      ▼
 ┌──────────────── Docker (Option A) ────────────────┐     Native (Option B)
 │ nginx:alpine  ── /api ──►  Spring Boot (Java 21)  │     one Spring Boot JAR serves
 │ (static UI)               │                       │     both the UI (classpath:/static)
 └───────────────────────────┼───────────────────────┘     and /api
                             ▼
                 SQLite file on the HOST: <data-dir>/learning-tracker.db
```

* One backend process, one SQLite file, one SPA. No auth, no cache, no broker.
* The UI only ever calls **relative** `/api/...` URLs. Dev: Vite proxy. Docker: Nginx proxy.
  Native: same JAR. So there is **no CORS config** anywhere.

## 2. Project structure

```
mylearn/
├── backend/                         Spring Boot (Maven)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/mylearn/tracker/
│       │   ├── LearningTrackerApplication.java
│       │   ├── DataDirectoryInitializer.java   creates app.data-dir before the DataSource starts
│       │   ├── common/        error model, GlobalExceptionHandler, base exceptions      (Phase 2/4)
│       │   ├── plan/          LearningPlan entity, repo, service, controller, dto/      (Phase 2/4)
│       │   ├── item/          LearningItem entity, repo, service, controller, dto/      (Phase 2-4)
│       │   ├── progress/      ProgressCalculator, progress queries, dashboard           (Phase 3/4)
│       │   └── importer/      CSV/XLSX readers, RowHierarchyBuilder, preview/confirm    (Phase 6)
│       ├── main/resources/
│       │   ├── application.yml
│       │   ├── META-INF/spring.factories
│       │   └── db/migration/  V1__init.sql, ...                                         (Phase 2)
│       └── test/java/...      unit tests (Mockito) + @SpringBootTest on a throwaway SQLite file
├── frontend/                        Vue 3 + TS + Vite + Tailwind v4
│   └── src/
│       ├── api/               typed fetch client per resource
│       ├── types/             DTO interfaces that mirror backend DTOs
│       ├── router/
│       ├── views/             Dashboard, Plans, PlanDetail (tree), Import
│       ├── components/        TreeNode, ProgressBar, PriorityBadge, ConfirmDialog, ...
│       └── composables/       useTheme, useExpandedState (per-plan, localStorage)
├── deploy/                    Dockerfiles, compose, nginx.conf, native scripts          (Phase 7)
├── data/                      local dev database (git-ignored)
└── docs/
```

**Package-by-feature** (`plan/`, `item/`, ...) rather than package-by-layer: each feature's
controller, service, repository and DTOs sit together, which is easier to navigate in a small app.
Only three layers: Controller (HTTP + DTO mapping) → Service (rules, transactions) → Repository.
No ports/adapters, no mapper framework (DTOs are Java `record`s with static `from(...)` methods).

## 3. Dependencies

| Area | Now (Phase 1) | Added later |
|---|---|---|
| Backend | spring-boot-starter-web, -validation, -data-jpa, -actuator (health only), `org.xerial:sqlite-jdbc`, `hibernate-community-dialects` (SQLiteDialect, version pinned to `${hibernate.version}`), flyway-core, spring-boot-starter-test (JUnit 5, Mockito, AssertJ) | Phase 6: Apache POI `poi-ooxml` (XLSX), Apache Commons CSV |
| Frontend | vue, vue-router, lucide-vue-next (icons), tailwindcss + @tailwindcss/vite | Phase 5: vue-draggable-plus (SortableJS) for sibling reordering, if it proves useful |

Spring Boot **3.5.x** (latest 3.x line), compiled with `--release 21`.
Deliberately not used: Lombok, MapStruct, Pinia, a UI component kit, any state server.

## 4. Database strategy

### Connection settings (`application.yml`)
| Setting | Why |
|---|---|
| `foreign_keys=on` (JDBC URL) | SQLite ignores FKs unless this is set **on every connection**. |
| `journal_mode=WAL` | Crash-safe writes; readers don't block the writer. |
| `busy_timeout=5000` | Wait instead of failing if the file is briefly locked (e.g. a backup). |
| Hikari `maximum-pool-size: 1` | SQLite has one writer. A single connection serializes access, so we never hit `SQLITE_BUSY` lock-upgrade failures. More than enough for one user. |
| `ddl-auto: none` + **Flyway** | Schema is versioned SQL owned by Flyway; Hibernate never alters it. |
| `open-in-view: false` | Transactions end in the service layer; no lazy loading in controllers. |

Verified in `SqliteSetupTest` and a manual run: the DB file is created, FKs are on, WAL is active,
recursive CTEs work, and Flyway creates its history table through the single pooled connection.

### Schema draft (implemented in Phase 2 as `V1__init.sql`)

```sql
CREATE TABLE learning_plan (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  name        TEXT NOT NULL CHECK (length(trim(name)) > 0),
  description TEXT,
  status      TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','ARCHIVED')),
  version     INTEGER NOT NULL DEFAULT 0,  -- optimistic locking (@Version)
  created_at  TEXT NOT NULL,            -- ISO-8601 UTC
  updated_at  TEXT NOT NULL
);
CREATE UNIQUE INDEX ux_plan_name ON learning_plan (name COLLATE NOCASE);

CREATE TABLE learning_item (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  external_id   TEXT NOT NULL UNIQUE,      -- UUID, never changes; the key for CSV export/sync
  plan_id       INTEGER NOT NULL REFERENCES learning_plan(id) ON DELETE CASCADE,
  parent_id     INTEGER,
  item_type     TEXT NOT NULL CHECK (item_type IN ('MODULE','TOPIC')),
  name          TEXT NOT NULL CHECK (length(trim(name)) > 0),
  description   TEXT,
  priority      TEXT CHECK (priority IN ('MUST','SHOULD','NICE')),   -- nullable = no priority
  is_completed  INTEGER NOT NULL DEFAULT 0 CHECK (is_completed IN (0,1)),
  completed_at  TEXT,                    -- for "recently completed" on the dashboard
  display_order INTEGER NOT NULL,
  version       INTEGER NOT NULL DEFAULT 0, -- optimistic locking (@Version)
  created_at    TEXT NOT NULL,
  updated_at    TEXT NOT NULL,
  UNIQUE (id, plan_id),
  -- a parent must belong to the SAME plan: enforced by the database, not only the service
  FOREIGN KEY (parent_id, plan_id) REFERENCES learning_item(id, plan_id) ON DELETE CASCADE
);
CREATE INDEX ix_item_plan_parent_order ON learning_item (plan_id, parent_id, display_order);
CREATE INDEX ix_item_parent            ON learning_item (parent_id);

CREATE TABLE import_batch (               -- small audit table for duplicate-import detection
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  plan_id     INTEGER NOT NULL REFERENCES learning_plan(id) ON DELETE CASCADE,
  file_name   TEXT NOT NULL,
  sheet_name  TEXT,
  content_sha256 TEXT NOT NULL,           -- hash of the parsed rows, not the raw bytes
  item_count  INTEGER NOT NULL,
  imported_at TEXT NOT NULL
);
```

Additions to the spec, and why:
* `completed_at`: the dashboard needs "recently completed topics", and `updated_at` also changes on renames.
* Composite FK `(parent_id, plan_id)`: enforces "no cross-plan parents" in the database too. The service still checks first so the user gets a readable error.
* `import_batch`: needed to detect re-imports of the same sheet.
* Unique plan name, case-insensitive: avoids two "Odoo Developer Roadmap" plans.
* `external_id` (UUID): a stable identifier that survives moves, renames and re-exports. CSV sync needs it to
  match rows back to items. The database `id` stays internal.
* `version`: optimistic locking. An edit sent from a stale screen (e.g. a second tab) gets `409 Conflict` instead of
  silently overwriting a newer change.

### Item model: MODULE and TOPIC (service layer, Phase 3)
There are two types and unlimited depth:
* **MODULE**: a container. It can hold modules and topics at any depth. It never has a checkbox.
* **TOPIC**: a unit of learning. A topic **with no children** is a **checkable leaf**.

Which items count toward progress:

| Item | Checkbox | Counts toward progress | Shown as |
|---|---|---|---|
| TOPIC with no children | yes | yes | ☐ / ☑ |
| TOPIC with children (sub-topics) | disabled | through its descendant leaves | % bar |
| MODULE with children | no | through its descendant leaves | % bar |
| MODULE with no children | no | no (0 leaves) | **N/A** |

This keeps every spec rule consistent:
* Only leaf topics can be completed. Completing anything else returns `409`.
* An empty module shows N/A instead of counting as a pending item. A new empty module therefore never drags progress down.
* **A leaf topic that gains a child** loses its completion (`is_completed`/`completed_at` cleared **in the same
  transaction**), and its checkbox becomes disabled.
* **A topic that loses its last child** becomes a checkable leaf again (unchecked). A module that loses its last child becomes N/A.
* Changing an item's type is allowed. Changing a completed leaf topic to MODULE clears its completion.

Structural validation (the service checks with readable errors, and the schema backs it up):
* The new parent must be in the same plan (also enforced by the composite FK).
* An item can't be its own parent, and can't be moved into its own descendant. A recursive CTE walks the
  target parent's ancestors, and the move is rejected if the item is among them.
* Siblings are renumbered `0..n-1` in the same transaction on every create, move, reorder and delete.
  `display_order` is therefore always gapless, and "up/down" is just ±1.
* Deleting an item deletes its whole subtree in one statement (`ON DELETE CASCADE`) inside one transaction, so a
  subtree is never half-deleted. The UI first shows how many descendants will go (`descendantCount` is in every tree node).
* Progress is never stored. It is recomputed on every read, so every structural or completion change is reflected
  immediately. Parent rows' `is_completed` is ignored by every progress query.

### Progress queries
| Need | Approach |
|---|---|
| Whole plan tree with per-node progress (roadmap screen) | One `SELECT` of all items in the plan, then build the tree and roll leaf counts up in Java, O(n). A few hundred rows is trivial. |
| Plan totals and dashboard | No recursion needed, because every leaf carries `plan_id`: `SELECT plan_id, COUNT(*), SUM(is_completed) FROM learning_item i WHERE i.item_type = 'TOPIC' AND NOT EXISTS (SELECT 1 FROM learning_item c WHERE c.parent_id = i.id) GROUP BY plan_id`. The overall percentage is Σcompleted / Σleaves across plans (weighted, not an average of percentages). |
| Single item `/api/items/{id}/progress` | Recursive CTE over descendants, counting leaves only. |
| Empty parent / plan | `total = 0` → API returns `percent: null` → UI shows **N/A**. |

## 5. What your spreadsheet taught the import design

The workbook `Accounting Learning (1).xlsx` has **three sheets with two different shapes**:

| Sheet | Shape | Rows |
|---|---|---|
| Odoo | Hierarchical: `Phase` / `Topic` / `Subtopic` / `What to Learn` / `Priority` / ✓ | 17 phases, 58 topics, 177 subtopic leaves |
| Accounting | Flat: `code` (FA1…FA54) / `name` / ✓ | 54 items |
| Node JS | Flat: `Serial` / `Name` / ✓ | 36 items |

Findings and the resulting rules:

1. **One workbook holds several roadmaps (one per sheet).** The preview lists the sheets. You import one sheet into a plan, or each sheet as its own new plan.
2. **The hierarchy comes from which column is filled, not from a "type" column.** A row's level is its
   *leftmost non-empty level column* (Phase=A, Topic=B, Subtopic=C). Its parent is the nearest earlier
   row at a shallower level. **Type comes from the structure:** a row that ends up with children becomes a
   `MODULE`, and a row without children becomes a `TOPIC`. So in Odoo, phases and topics become modules, and subtopics become topics.
3. **The header is not on row 1.** Row 1 holds the progress sparkline/percentage formula. The importer
   scans the first ~10 rows for the header. The preview shows the detected mapping, and you can override it.
4. **The checkbox column has no header text.** Its header cell is itself a checkbox. A column whose values are all booleans is auto-detected as "completed".
5. **Names carry bullet prefixes** (`■ What is Odoo`, `• Editions`). Leading `■ • ▪ ► - *` are stripped.
6. **Parent rows have stale checkboxes.** 4 phase/topic rows in Odoo are ticked (e.g. `PHASE 1`). They are ignored and shown as a warning.
7. **Your sheet's progress is inconsistent.** The `%` cell for Odoo shows **13.1%** because it counts parent
   rows (33 ticked of 252 rows). Counting leaves only gives **16.4%** (29 of 177). The app uses the leaf rule.
8. **Priorities sit on topic rows too** (`■ Fields` = Must). Priority is optional on every item, so it is kept.
9. **Junk cells exist** (the stray `=COUNT(F3:F253)` in G11; "What to Learn" repeated as a description on the
   `PHASE 0` row). Unmapped columns are ignored. A description equal to a header name is dropped with a warning.
10. **Flat sheets have no phases.** Every row becomes a root-level leaf `TOPIC`. `display_order` follows the row order, so `FA1…FA54` stay in sequence.
11. Formula cells: the cached value is read (Apache POI). Booleans accepted: `TRUE/FALSE`, `1/0`, `yes/no`, `x`, `✓`.

**Flow.** `preview` is stateless. It parses the upload and returns the sheets, the detected mapping,
the resulting tree, row-level **errors** (which block import, e.g. a description with no name) and **warnings**
(which don't, e.g. an unknown priority dropped). `confirm` re-sends the same file plus the chosen options. Parsing
is deterministic, so the result matches the preview. It all runs in one transaction.

**Duplicate protection.** A SHA-256 hash of the normalized rows is stored in `import_batch`. A match warns
"already imported into *X* on *date*", and confirming then requires an explicit `allowDuplicate=true`.
Import is **append-only**. It adds new items after the existing ones, gives them new `external_id`s, and never
modifies or deletes existing items or their progress. If an uploaded row carries an `id` that already exists (i.e. an
edited export), append mode rejects it and points you to the sync workflow (§6.4). Nothing is overwritten silently.

**Endpoints.** The spec's `/api/plans/{id}/import/preview|confirm` handle existing plans. They are joined by
`/api/import/preview|confirm` for "new plan(s) from file", so an aborted import never leaves an empty plan behind.

## 6. Content management (full CRUD)

### 6.1 REST API
| Method | Path | Purpose / body | Success | Typical errors |
|---|---|---|---|---|
| POST | `/api/plans` | `{name, description}` | 201 + `Location` | 400 validation, 409 duplicate name |
| GET | `/api/plans?search=&status=` | list with progress + counts | 200 | |
| GET | `/api/plans/{id}` | plan details + progress | 200 | 404 |
| PUT | `/api/plans/{id}` | `{name, description, status, version}`. Archive/activate is a status change. | 200 | 400, 404, 409 stale/duplicate |
| DELETE | `/api/plans/{id}` | deletes plan + all items | 204 | 404 |
| POST | `/api/plans/{planId}/items` | `{name, type, parentId?, description?, priority?, position?}`. Position omitted = append at end. | 201 | 400, 404 parent, 422 cross-plan parent |
| GET | `/api/plans/{planId}/items` | **whole tree**; each node: id, externalId, type, name, description, priority, completed, leaf, checkable, progress `{completed, total, percent or null}`, descendantCount, version, children[] | 200 | 404 |
| GET | `/api/items/{id}` | one item + progress + breadcrumb path | 200 | 404 |
| PUT | `/api/items/{id}` | `{name, type, description, priority, version}` (content only, no structure) | 200 | 400, 404, 409 stale |
| DELETE | `/api/items/{id}` | deletes the item and its subtree | 204 | 404 |
| PATCH | `/api/items/{id}/completion` | `{completed}` | 200 + updated ancestor progress | 409 not a leaf topic |
| PATCH | `/api/items/{id}/move` | `{parentId or null, position?}` to another parent in the same plan | 200 | 409 cycle/self, 422 cross-plan |
| PATCH | `/api/items/{id}/reorder` | `{position}` among current siblings (up/down = position ±1) | 200 | 400 out of range |
| GET | `/api/items/{id}/progress`, `/api/plans/{id}/progress`, `/api/dashboard/summary` | progress | 200 | 404 |
| GET | `/api/plans/{id}/export`, `/api/items/{id}/export` | CSV export of a plan or subtree (Phase 6) | 200 | 404 |

Structure (`move`/`reorder`) and content (`PUT`) are separate endpoints. A rename can never accidentally move an
item, and each operation has one clear validation path. Errors always use one JSON shape:
`{status, error, message, fieldErrors[], path, timestamp}`, produced by a single `@RestControllerAdvice`.
Controllers only map HTTP↔DTO. All rules and `@Transactional` boundaries live in the services.

**Stale updates.** `PUT` requests carry `version`. A mismatch returns `409` with the current state, and the UI offers
"reload". Completion toggles and moves don't need the version: they are idempotent or re-validated against the current tree.

### 6.2 UI behaviour
* **Tree:** a recursive `TreeNode` with indentation and expand/collapse. Expanded state is kept per plan in localStorage.
  It has a search box (name/description) and filter chips: priority, completed/pending, module/topic. Matching
  nodes keep their ancestors visible.
* **Per-node ⋮ menu**, depending on the node:
  * Module: *Add module · Add topic · Edit · Move… · Move up/down · Export subtree · Delete*
  * Topic: *Add sub-topic · Edit · Move… · Move up/down · Delete*
* **Create/Edit dialog:** name (required), type, parent (picker), description, priority. Position defaults to the end.
* **Drag-and-drop:** reorders within a parent and drops onto another module to move there (SortableJS via
  `vue-draggable-plus`, nested lists). *Move…* (parent picker) and up/down stay as keyboard-friendly alternatives.
  Invalid drops (into own descendant) are blocked in the UI and rejected by the API.
* **Deleting** a node with descendants shows "Delete *Fields* and its **N** items?" A leaf gets a simple confirm. Deleting a plan requires typing its name.
* **Optimistic checkbox:** the progress of the node and all its ancestors updates instantly. On API failure it rolls
  back and shows an error toast. Every create/edit/move/delete shows a success or error toast.

### 6.3 CSV export format (Phase 6)
The format is one row per item, in tree order, and is readable and editable in Excel:

| id | level | type | name | description | priority | completed |
|---|---|---|---|---|---|---|
| 3f2a… | 1 | MODULE | Phase 3 — Models and ORM | | | |
| 9c1b… | 2 | MODULE | Fields | | MUST | |
| 77de… | 3 | TOPIC | Computed fields | @api.depends, store… | MUST | TRUE |

* `level` (1..n) plus row order define the hierarchy, the same idea as your spreadsheet's columns but with unlimited depth.
* `id` = `external_id`. It is blank for new rows you add by hand.
* The download template is this format with sample rows. The importer accepts both this format and your spreadsheet's Phase/Topic/Subtopic column layout.

### 6.4 CSV sync / merge (later phase, separate workflow)
Edit an export externally → upload to **Sync** (not Import) → the server diffs by `id` and shows a preview:
**additions** (no id), **updates** (field-by-field old → new), **moves**, and **deletions** (ids missing from the file;
unticked by default). You apply the changes you choose in one transaction. If an item changed in the app after the
export, it is flagged as a conflict and is never overwritten automatically. Plain import never does any of this.

## 7. Deployment strategy (built in Phase 7)

**Option A: Docker Compose (preferred)**
* `backend`: multi-stage build (Maven + JDK 21 → `eclipse-temurin:21-jre`), non-root user,
  `APP_DATA_DIR=/data`, healthcheck on `/actuator/health`.
* `frontend`: multi-stage build (Node → `nginx:alpine`). It serves `dist/`, falls back to `index.html` for
  SPA routes, and proxies `/api` to `backend:8080`. It waits for `backend` to be healthy before starting.
* **Bind mount** `./data:/data`. The DB lives on your disk, never only in a container.
* `restart: unless-stopped` + Docker Desktop "Start when you sign in" ⇒ the app is back after a reboot.
* Published on `127.0.0.1` only (not reachable from your network).

**Option B: Native (Windows)**
* One executable JAR. The Maven profile `ui` copies `frontend/dist` into `classpath:/static`, and `SpaWebConfig`
  serves it, falling back to `index.html` for client routes. Built by `scripts/build.(sh|ps1)`.
* Linux: systemd unit + `deploy/install-linux.sh`. Windows: Task Scheduler "At log on" (`deploy/windows/install-autostart.ps1`).

**Backups.** `BackupService` runs SQLite `VACUUM INTO '<data>/backups/…db'`, which takes a consistent snapshot while the app
runs. It is called on demand (`POST /api/admin/backup`, Settings page) and automatically when the newest backup is older than 20h
(checked 1 min after start, then every 6h). The 14 newest are kept. Restore = stop, replace the file, start.
**Migrations** run automatically at startup (Flyway). Upgrades are: back up → pull/build → restart.

## 8. Revised phase plan

| Phase | Scope |
|---|---|
| 2 | Entities, Flyway V1 (schema above), repositories, DTOs, services for plan/item CRUD |
| 3 | Hierarchy engine: leaf/checkable rules, completion, move/reorder/delete, cycle checks, progress rollup, optimistic locking. Unit tests. |
| 4 | REST API (§6.1), validation, error model, API tests incl. stale-update 409 |
| 5 | Frontend: dashboard, plans, tree with ⋮ menus, dialogs, drag-and-drop, search/filters, toasts, themes |
| 6 | Import (CSV/XLSX, preview, append-only, duplicate detection), CSV export (plan/subtree), template |
| 7 | Docker Compose, native install, backup/restore |
| 8 | End-to-end checks, performance, docs |
| 9 | CSV sync/merge with diff preview (§6.4): `importer/SyncService`. Exports carry a `version` column for conflict detection. |

## 9. Decisions recap
* SQLite + Flyway + single connection: simple and correct transactional behavior.
* Progress is always derived: one flat query plus an in-memory rollup for trees, and SQL aggregates for totals.
* Integrity in two places: readable checks in the service, hard guarantees in the schema (CHECKs, composite FK, cascades).
* Two item types (MODULE/TOPIC), unlimited depth, leaves derived from the structure; empty modules are N/A.
* Stable `external_id` and optimistic `version` from day one; import is append-only; sync is a separate, previewed workflow.
* Same-origin `/api` in every environment, so there is no CORS and no per-environment frontend config.
