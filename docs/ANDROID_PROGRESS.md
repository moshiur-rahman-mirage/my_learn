# Android app: plan and progress

> **Resuming after a break?** Read this whole file first, then continue with the first unchecked
> `[ ]` task under "Tasks". Tick tasks (`[x]`) and add a line to the "Log" as soon as each one is done.
> Keep this file current; it is the only hand-off between sessions.

## Goal

An Android app for the Learning Tracker so plans and notes can be read and studied offline on the go.
It has the same data model as the desktop app and keeps its data in step with it through two-way sync over Wi-Fi.

## Decisions (made with the user on 2026-11-25)

| Topic | Decision |
|---|---|
| Stack | React Native · Expo · TypeScript · Expo Router · Expo SQLite · Expo FileSystem · Zustand (only where needed) · Markdown rendering |
| Architecture | **Offline-first.** The phone has its own full SQLite copy, and every screen reads and writes it locally. The network is only used for sync. |
| Data compatibility | The phone uses the **same schema as the desktop, migrations V1–V6**, plus the sync additions from V7 (below). The business rules are ported from the Java services to TypeScript in `mobile/src/domain/`. |
| Sync | **Two-way sync over Wi-Fi** with the desktop server. A "Sync" button on the phone works when both are on the same network. Each row merges last-writer-wins by `updated_at`, deletes travel as tombstones, and no data is lost when both sides were edited. |
| Delivery | **Phased, reading first** (see phases). Every phase ends with an installable APK. |
| Location | New folder `mobile/` in this repo. |

### Sync design (draft, refine in task P0)

Backend migration **V7__sync.sql** (the phone gets the same migration):
- Add `external_id TEXT UNIQUE` (UUID) to `learning_plan`, `item_link` and `time_log`, filled for existing rows. Items already have `external_id`.
- Add a `sync_change(entity, key, updated_at)` table filled by **triggers** on every synced table. Triggers cover every write path (UI, import, sync, restore), just as the FTS triggers do.
- Add a `sync_tombstone(entity, key, deleted_at)` table filled by AFTER DELETE triggers.
- Keys used to match rows across devices (database ids differ per device):
  - plan: `external_id`
  - item: `external_id`
  - note, review, confidence: the item's `external_id`
  - link, time_log: their new `external_id`
  - tag: name (case-insensitive)
  - item_tag: item ext id + tag name
  - flashcard: item ext id + `card_key`
- Derived tables are **not** synced; each side rebuilds them locally: `search_index`, `note_ref`, flashcard Q/A text (re-derived from notes; only the schedule columns sync).

Endpoint `POST /api/sync`:
- The request carries `{deviceId, since: <server cursor>, changes: [...]}`.
- The server applies the changes with last-writer-wins, then returns `{cursor, changes: [... since 'since']}`.
- The phone applies those changes in one transaction and stores the new cursor.

Safety:
- The desktop must listen on the LAN for sync. Check `server.address`, the installer cfg and the firewall.
- Every sync request needs a **pairing token**. The desktop Settings page shows it as text or a QR code, and the phone stores it once.
- Clock skew: the server records its own receive time alongside the client's `updated_at`. Ties go to the server.

### Business rules to port (source → mobile)

- Progress: only leaf TOPICs count; an empty MODULE shows N/A; percentages are weighted. Source: `item/TreeBuilder`, `progress/*`
- Completion: a leaf that gains a child loses its completion; siblings are renumbered gaplessly 0..n-1. Source: `item/ItemService`
- Reviews are due after 1, 7 and 30 days. Source: `review/ReviewService`
- Flashcards: `Q:`/`A:` in notes; intervals of 1, 3, 7, 14, 30, 60 and 120 days; `card_key` is a hash of the question. Source: `flashcard/FlashcardParser`, `FlashcardService`
- Notes are stored as HTML or MARKDOWN; `[[links]]` use `wikiKey`. Source: `note/*`, `frontend/src/lib/markdown.ts`
- Search uses FTS5 prefix matching. Source: `search/*`
- Today view, streaks and the activity grid. Source: `dashboard/TodayService`, `ActivityCalculator`

### Note rendering

Notes use marked, KaTeX, Mermaid and highlight.js on the desktop. For identical output offline, the phone renders notes in a **WebView with those libraries bundled locally** (no CDN). Native Markdown components can't render KaTeX or Mermaid faithfully.

## Phases

1. **P1 Reading and studying (MVP):** plans and trees with progress, note viewing and editing, ticking topics, Today, reviews, flashcards, search, Wi-Fi sync, first sync from the desktop.
2. **P2 Structure editing:** add, edit, delete, move and reorder items; plan create, edit and archive; target dates and pace.
3. **P3 Extras:** tags, links, time log and Pomodoro, confidence, dashboard charts, activity heatmap, completed history.
4. **P4 Import/export:** CSV/XLSX import, CSV export, per-plan JSON backup and restore, local .db backups and share.

## Tasks

### P0 Foundations
- [x] P0.1 Finalize the sync design: read the backend services and write the exact JSON contract in `docs/SYNC_PROTOCOL.md` (**the protocol file replaces the "Sync design (draft)" above**)
- [x] P0.2 Backend: `V7__sync.sql` (external ids, `sync_change`, tombstones, triggers) plus an integration test (`sync/SyncSchemaIntegrationTest`). The restore now renews `sync_epoch`.
- [x] P0.3 Backend: pairing token (settings), LAN binding check, `POST /api/sync`, LWW merge service, tests. Classes in `backend/.../sync/`:
  - `PhoneSyncService` (named so it doesn't clash with the CSV `importer.SyncService`)
  - `SyncApplier`, `SyncSnapshots`, `SyncKeys`, `SyncDtos`
  - `SyncController`: `/api/sync`, `/ping`, `/pairing`, `/pairing/token`
  - `RemoteAccessFilter`
- [x] P0.4 Desktop UI: Settings → "Phone sync" section (address, token, QR): `frontend/src/components/PhoneSyncSettings.vue` (uses the `qrcode` npm package). QR payload: `{"url":"http://<ip>:<port>","token":"…"}`
- [x] P0.5 Scaffold `mobile/` (Expo, TS, Router, SQLite, FileSystem, Zustand), lint and tsc passing
- [x] P0.6 Mobile DB layer: run V1–V7 migrations, open with FKs on, plus an `app_meta` table on the phone

### P1 Reading and studying
- [x] P1.1 Domain port: tree builder and progress (TS) with unit tests
- [x] P1.2 Domain port: completion rules, reviews, flashcard parser and scheduler, wikiKey, search queries
- [x] P1.3 Sync client: pairing screen, push and pull, apply in one transaction, rebuild derived tables (logic done; the pairing **screen** is built in P1.4+)
- [x] P1.4 Screens: Plans list, Plan tree (expand and collapse, tick leaves, progress bars), plus the Sync tab and pairing screen
- [x] P1.5 Item screen: details, note viewer (WebView renderer with bundled marked, KaTeX, Mermaid and hljs), note editor (Markdown)
- [x] P1.6 Today screen: reviews due, next topics, streak
- [x] P1.7 Flashcards screen (due cards, Remembered/Forgot) and Reviews
- [x] P1.8 Search screen
- [x] P1.9 Theme (light/dark), app icon, build an APK (EAS local build or `expo prebuild` + Gradle), test on a device (the first APK was tested on the phone with a real sync; the final polished APK is built but **not yet installed**, because the phone dropped off wireless adb)

### P2 Structure editing
- [x] P2.1 Port ItemService/PlanService structure rules to `src/data/structure.ts`: create, update (version check), move (cycle check, parent loses its tick), reorder, delete subtree; plan create, update and delete (case-insensitive unique name, version check); renumber siblings with a version bump like JPA. Tests.
- [x] P2.2 Port `Pace` (target date: days left, needed and recent per week, projected finish, status) plus recent completions per plan. Tests.
- [x] P2.3 Item screens: add child or sibling, edit (name, type, priority, description), move (parent picker), move up/down, delete with a descendant-count confirm
- [x] P2.4 Plan screens: new plan, edit (name, description, target date), archive/activate, delete (type the name to confirm); pace badge on the plan list and plan screen
- [x] P2.5 E2E: structure edits on the phone sync to the desktop (extend the e2e test); rebuild the APK

### Stages requested 2026-11-25 (work through without waiting for "go next"; build and try to install after each)
Design references: `docs/Screenshot 2026-11-25 222136.png` (reading app: dark "Continue reading" hero, library with filter chips and covers with progress, overview stats and bar chart) and `docs/Screenshot 2026-11-25 222158.png` (lavender learning app: "Well done!" banner, week day row, big daily-goal ring, three stat tiles, "Start studying", summary statistics). The theme was already switched to a lavender palette with `radius` tokens (`src/ui/theme.ts`, edited outside these sessions at 22:25).
- [x] S1 Repeated or switched syncs never duplicate plans (rebuild on a new computer; "Download everything again"; unsent-changes guard)
- [x] S2 Settings screen: dark mode (system/light/dark), reading font (family, size, line spacing), stored locally; a ThemeProvider replaces `useColorScheme`
- [x] S3 Reading mode: full-page note reader (`read/[id]`), reading font and theme, progress bar, previous/next topic, mark done
- [x] S4 Gamification: XP and levels, daily goal ring, streak and week row, achievements (derived from synced data, so the same on every device)
- [x] S5 Clean dashboard (Home): greeting banner, goal ring, stat tiles, continue-reading hero, due tiles, weekly chart
- [x] S6 Modern UI pass: library with covers and filter chips, item and cards screens, tab bar; Profile tab (level, badges, settings, sync)
- [x] S7 (P3) Topic extras on the phone: tags (edit), links (add/remove), time log (+15/+25/+50, delete) and a Pomodoro timer, confidence rating 1–5 (low rating brings a review forward, like the desktop)
- [x] S8 (P4) Data: share/export a plan as CSV, full local backup file (.db) via the share sheet

## Log

- 2026-11-25: Surveyed the codebase (8.4k lines of Java and 8.2k lines of Vue/TS; schema V1–V6). Decided on two-way Wi-Fi sync and phased, reading-first delivery. Created this file. Next: P0.1.
- 2026-11-25: P0.1 done. `docs/SYNC_PROTOCOL.md` written. Main choices:
  - a trigger-fed `sync_change` log (one line per row, its seq used as the cursor, `deleted` lines as tombstones);
  - last-writer-wins with client clock-skew correction;
  - a note conflict appends the losing text instead of dropping it;
  - an epoch that changes on desktop restore forces the phone to rebuild;
  - a pairing token in `settings.properties`, plus a remote-access filter.
  
  Found: the Windows installer binds to all interfaces, so the full API is reachable from the LAN today. The new filter fixes this. Next: P0.2 (V7 migration).
- 2026-11-25: P0.2 done.
  - `V7__sync.sql`: new `external_id` on plan, link and time_log, filled by an AFTER INSERT trigger, so no Java write path changed. Also adds `sync_change` with I/U/D triggers on 9 entities, `sync_device`, a `sync_epoch` in app_meta, and change lines seeded for existing rows.
  - Verified that a trigger-issued UPDATE fires the other (sync) trigger on the same table even with `recursive_triggers` off.
  - `RestoreService` renews the epoch after a restore.
  - `BackupRestoreIntegrationTest`'s fake V1 backup now also undoes V7.
  - The full backend suite passes: **151 tests, 0 failures**.
  
  Next: P0.3 (`POST /api/sync`, token, remote-access filter).
- 2026-11-25: P0.3 done.
  - The sync endpoint is implemented to `docs/SYNC_PROTOCOL.md`. It also skips moves that would create a cycle (reason `CYCLE`, not in the original list).
  - The token lives in `settings.properties` (`sync.token`, 43-char base64url).
  - `RemoteAccessFilter`: non-loopback clients reach only `/api/sync` and `/api/sync/ping`. Docker sets `APP_REMOTE_ACCESS=all`.
  - `install-autostart.ps1` gained `-AllowPhoneSync`.
  - **Behaviour change:** opening the desktop app from another PC's browser over the LAN now gets 403.
  - Tests: `sync/PhoneSyncIntegrationTest` (18 tests). The full backend suite passes: **169 tests, 0 failures**.
  
  Next: P0.4 (desktop Settings → Phone sync UI).
- 2026-11-25: P0.4 done.
  - Settings shows the address picker, the token (hidden, copyable), a QR code, an "Unpair all phones" button (new token) and the list of synced devices.
  - `npm run build` (vue-tsc + vite) passes. The page is **not yet checked visually in a browser**.
  
  Next: P0.5 (Expo scaffold).
- 2026-11-25: P0.5 and P0.6 done. Mobile app in `mobile/`:
  - **Versions:** Expo SDK 58 (RN 0.88, React 19.3). Routes live in `src/app/` (Expo Router; `main: expo-router/entry`). Alias `@/*` → `src/*`. Android package `com.mylearn.tracker`.
  - **Config:** `expo-build-properties` sets `usesCleartextTraffic`, needed for `http://<pc-ip>` sync. The `expo-sqlite` plugin has `enableFTS`. `react-dom` is pinned to 19.3.0 to satisfy npm peer resolution.
  - **Database layer** (`src/db/`):
    - The `Db` interface plus a `TxQueue` serialize transactions on ONE connection. Don't use `withExclusiveTransactionAsync`: it opens a new connection, so `foreign_keys` would be off.
    - `expoDb.ts` is the app adapter. `migrate.ts` runs the desktop migrations and tracks the version in **`PRAGMA user_version`**, not `flyway_schema_history`; P4's .db export must add Flyway history.
  - **Migrations:** `scripts/gen-migrations.mjs` copies `backend/.../db/migration/*.sql` into `src/db/migrations.generated.ts`. Run `npm run migrations` after any new backend migration.
  - **Tests:** vitest on Node's built-in `node:sqlite` (`test/nodeDb.ts`, which has FTS5), so domain and sync code is tested in Node. `test/migrate.test.ts`: 6 passing.
  - **App shell:** `src/db/DbProvider.tsx` and a placeholder `src/app/index.tsx`.
  - **Checks:** `npx tsc --noEmit` ✔ · `npx expo lint` ✔ · `npx expo-doctor` 20/20 ✔. Not yet run on a device or emulator.
  - **Phone app_meta keys** (V7 creates the phone's OWN `sync_epoch`, so don't reuse that key): `device_id`, `sync_server_url`, `sync_token`, `sync_server_epoch`, `sync_since`, `sync_pushed_seq`.
  
  Next: P1.1 (domain port: tree builder + progress).
- 2026-11-25: P1.1 done.
  - **Pure domain** (`src/domain/`): `types.ts` mirrors the DTOs; `progress.ts` (`progressOf`, `plus`, `percentLabel`, same 1-decimal rounding as Java); `tree.ts` (`buildTree` ports TreeBuilder, plus `walk` and `findWithPath`).
  - **Data layer** (`src/data/`):
    - `plans.ts`: `listPlans` (by `lower(name)`, status and search filters), `getPlan`, `progressByPlan`, `overallProgress`, and the `IS_LEAF_TOPIC` fragment.
    - `tree.ts`: `planItems`, `getItem`, `planExtras` (same queries as ItemExtrasQueries) and `loadTree`.
  - **Tests:** `test/tree.test.ts` mirrors every case in the Java `TreeBuilderTest` plus the query tests. `vitest.config.ts` adds the `@` alias. Mobile total: **18 passing**, with tsc and lint clean.
  - **Deferred:** `Pace` (target-date pacing) moves to P2 with target dates.
  
  Next: P1.2 (completion rules, reviews, flashcard parser and scheduler, wikiKey, search).
- 2026-11-25: P1.2 done.
  - **Domain** (`src/domain/`):
    - `text.ts`: Java-exact `\s`, `.`, `strip` and `isBlank`; `htmlToLines`/`htmlToPlainText`, the HtmlText port; `wikiKey`. The Java whitespace set is built from numeric code points: **don't put a literal U+2028/U+2029 in a regex literal**, it breaks esbuild.
    - `flashcards.ts`: `parseCards`, `cardKey`, `cardsOf`.
    - `hash.ts`: injectable SHA-256. The app wires expo-crypto in `src/platform/crypto.ts`, imported first by `_layout`; tests use Node crypto in `test/setup.ts`.
    - `dates.ts` (`nowIso`, local `today`, `addDays`) and `search.ts` (`toMatchQuery`, `segments`).
  - **Data** (`src/data/`):
    - `notes.ts`: save, then derive search text, cards and `note_ref`; `backlinks`, `resolveLink`.
    - `flashcards.ts`: `syncCardsFromNote`, `dueCards`, `cardCounts`, `reviewCard`.
    - `reviews.ts`: schedule, cancel, due, review.
    - `items.ts`: `setCompletion` with `RuleError`, and `touchPlan`.
    - `search.ts`: FTS5 with bm25, tag filter and ancestor path.
  - **Parity fixture:** `backend/src/test/resources/parity/flashcard-fixtures.json` has 17 tricky cases (NEL, NBSP, em space, CRLF, fences, entities, İ lower-casing…). Java generates and checks it (`FlashcardParityFixtureTest`; regenerate with `-Dparity.regenerate=true`). Mobile `test/parity.test.ts` checks the same file, and **all 17 match**.
  - **Totals:** mobile **54 tests passing**; tsc and lint clean.
  
  Next: P1.3 (sync client).
- 2026-11-25: P1.3 done (logic; screens come later).
  - **Sync code** (`mobile/src/sync/`):
    - `protocol.ts`: wire types.
    - `snapshots.ts`: port of SyncSnapshots.
    - `apply.ts`: applies server changes; skips rows edited locally during the request; parent-first items; `repair`.
    - `client.ts`: `readConfig`, `pair` (ping, then store; a new computer resets `since`), `unpair`, `syncNow` (pages; wipes on `reset`), `pendingCount`, `parsePairingCode`, `normalizeUrl`, `fetchTransport` (readable `SyncError` kinds: not-paired, unreachable, server, protocol).
  - **Server bug found by e2e and fixed:** after a reset the pull still excluded `origin = deviceId` lines, so a wiped phone never got back rows it had pushed earlier (the plan went missing, so every item was skipped). `PhoneSyncService` now sends everything on a reset. Regression test: `resetSendsEvenWhatCameFromThisPhone`.
  - **E2E test** `mobile/test/e2e/sync.e2e.test.ts`: spawns the real backend jar on a temp DB and a free port, drives the desktop over REST and the phone through its own code, and after every step asserts **both databases dump identically**. That covers every synced entity, the derived flashcards (keys) and `note_ref`. Scenarios: first pull, no-op, phone pushes, desktop changes, note conflict merge, phone delete, desktop restore and reset. **7/7 pass.** Run with `cd backend && mvn -q package -DskipTests`, then `cd mobile && npm run test:e2e`. The normal `npm test` skips it.
  - **Unit tests** `test/syncClient.test.ts`: pairing input, push-once, the edited-during-request rule, paging, failed page, reset.
  - **Totals:** mobile **63 unit + 7 e2e**; backend **171**; tsc and lint clean.
  
  Next: P1.4 (screens: Plans list and Plan tree), plus the Sync and pairing screen.
- 2026-11-25: P1.4 done.
  - **SDK 58 notes:** React Navigation is forked inside expo-router (no `@react-navigation/*` packages). Import tabs from **`expo-router/js-tabs`**; `Tabs` from `expo-router` is deprecated. Added `@expo/vector-icons` (Ionicons) and `expo-camera` (`CameraView`, `barcodeScannerSettings`, `onBarcodeScanned`, `useCameraPermissions`; plugin in app.json with the camera permission text and no microphone).
  - **UI kit:** `src/ui/theme.ts` (the desktop's slate and emerald palette, light and dark via `useColorScheme`); `src/ui/components.tsx` (`ProgressBar`, `PriorityBadge`, `Button`, `Chip`, `Card`, `EmptyState`, `Loading`, `ErrorText`).
  - **State:**
    - `src/state/stores.ts` (Zustand): `useDataVersion`, bumped after writes so screens reload; `useExpanded`, per-plan expanded rows.
    - `src/state/useQuery.ts`: `useQuery(load, key)` reloads on key change, focus and data version. **It takes a string key, not a deps array** (react-hooks lint rules).
    - `src/sync/useSync.ts`: shared sync status, `run(db)`, `deviceName()`.
  - **Routes:**
    - `src/app/_layout.tsx`: Stack with `(tabs)`, `plan/[id]`, `pair` (modal).
    - `src/app/(tabs)/_layout.tsx`: Plans and Sync tabs; the Sync tab shows a "!" badge after an error.
    - `src/app/(tabs)/index.tsx`: Plans, with overall progress, Active/Archived/All, pull-to-refresh (syncs when paired), and a "Pair" call to action when empty.
    - `src/app/plan/[id].tsx`: virtualized tree (`domain/visibleRows.ts`), expand/collapse, Expand all, Collapse all, Hide done; tick leaves (`RuleError` shows an alert); priority, notes and card badges.
    - `src/app/(tabs)/sync.tsx`: status, last sync, pending count, Sync now, last report (reset, conflicts, skipped), unpair.
    - `src/app/pair.tsx`: QR scan, or typed address and token; pairs, then starts the first sync.
  - **Checks:** tsc ✔, lint ✔, mobile tests **67 passing** (+ `test/visibleRows.test.ts`); `npx expo export --platform android` bundles ✔ (2.8 MB Hermes).
  - **Not yet run on a device or emulator:** no USB device attached; the Android SDK has no emulator or system image. A tree row tap on a leaf does nothing yet; it opens the item screen in P1.5.
  
  Next: P1.5 (item screen: details, note viewer in a WebView, Markdown editor).
- 2026-11-25: P1.5 done.
  - **Note viewer:**
    - `mobile/renderer/main.ts` imports the **desktop's own** `frontend/src/lib/markdown.ts` and `diagrams.ts`; `note.css` mirrors the desktop's `.note-content`/prose look and hljs palette.
    - `scripts/build-renderer.mjs` (`npm run renderer`) uses esbuild, resolving packages from `frontend/node_modules`, and writes ONE offline file, `assets/renderer/note-viewer.html` (~6 MB; KaTeX woff2 fonts inlined, woff/ttf dropped; Mermaid inlined).
    - **Rebuild it after changing `renderer/` or `frontend/src/lib/markdown.ts`.**
    - Checked in headless Chrome screenshots: Markdown, KaTeX (`\sum`, `\log`), "$5 and $10" stays text, Mermaid, tables, task lists, wikilinks (missing ones look different), dark mode, HTML notes, an `onerror` image sanitized.
    - (Git Bash mangles backslashes in `node -e` arguments; put test inputs in a file.)
  - **App side:**
    - `metro.config.js` adds `html` to assetExts. `expo-asset` loads the file.
    - `src/note/NoteView.tsx`: WebView from a local `file://` URI; renders via `injectJavaScript(window.renderNote)`; auto height; `[[link]]` → `resolveLink`; http links → `Linking`; other navigation blocked; `toMarkdown()` uses the desktop's Turndown.
    - **WebView typing:** react-native-webview 14 types its props as iOS & Android & Windows, which TS 6 reduces to `never`, so it's cast to `AndroidWebViewProps`. That also caught `scrollEnabled` (iOS-only).
    - `tsconfig` excludes `renderer/`.
  - **Screens:**
    - `src/data/itemDetail.ts` gathers everything the item screen shows.
    - `src/app/item/[id].tsx`: breadcrumbs, tick, description, tags, links, time/review/cards meta, rendered note, Edit, Linked from.
    - `src/app/note/[id].tsx`: Markdown editor with autosave (1.2 s, and on leave or blur), Write/Preview, snippet toolbar (Q/A, `[[ ]]`, bold, code, checklist…). Rich-text notes are converted to Markdown with the desktop converter after confirmation.
    - Tree rows open the item; the chevron expands.
  - **Checks:** mobile **68 tests passing**; tsc and lint clean; `expo export` bundles the viewer asset.
  - **Phone:** wireless adb works with a Motorola Edge 60 Fusion (Android 17 / API 37, arm64-v8a). The PC has JDK 17, 21 and 25 in `C:\Program Files\Java` (Gradle needs 17 or 21, **not 25**). The SDK has platform 36 and build-tools 36; no NDK or CMake yet (Gradle downloads them).
  
  Next: first device build (`expo prebuild` + Gradle release APK, install over adb), then P1.6 (Today).
- 2026-11-25: P1.6, P1.7 and P1.8 done (screens and logic; not yet seen on the device).
  - **Today:** `src/domain/today.ts` (ports of `nextTopics` — MUST, SHOULD, none, NICE with a stable sort, 3 per plan — and the streak and completion counts, where imports within 5 s are excluded); `src/data/today.ts` `loadToday`; `test/today.test.ts`.
  - **Tabs** are now Today (`index`), Plans (`plans.tsx`, moved from index), Search, Cards and Sync.
  - **Today screen:** stats (streak, done today, last 7 days, minutes); "Study N flashcards"; due reviews with Forgot/Remembered; Next up per plan with tick and open.
  - **Cards:** one card at a time; the answer is rendered by `NoteView` (Markdown); Forgot/Remembered.
  - **Search:** debounced FTS prefix search with highlighted snippets and plan paths.
  - **Android build:**
    - Prebuild generates `mobile/android/` (git-ignored; never edit by hand; regenerate with `npx expo prebuild --platform android`). Write `android/local.properties` with `sdk.dir` after prebuild.
    - Added `expo-system-ui`; without it, `userInterfaceStyle: automatic` (dark mode) does nothing on Android.
    - Build with `JAVA_HOME=C:\Program Files\Java\jdk-21.0.11`, then `android\gradlew.bat assembleRelease -PreactNativeArchitectures=arm64-v8a`.
  - **Checks:** mobile **72 tests passing**; tsc and lint clean.
  - **Windows build gotcha:** the first `assembleRelease` failed after 16 min in `buildCMakeRelWithDebInfo`: *"Filename longer than 260 characters"* (react-native-gesture-handler, pulled in by expo-router). Windows LongPathsEnabled is already 1; the culprit is **ninja 1.10.2** inside SDK CMake 3.22.1. **Fix:** install `cmake;3.31.6` (ninja 1.12.1, long-path aware) with sdkmanager and write `cmake.dir=<sdk>/cmake/3.31.6` into `android/local.properties`, next to `sdk.dir`. Redo both after every prebuild. `subst` to a short drive only saves about 15 characters; not enough.
- 2026-11-25: **First APK runs on the phone.**
  - The rebuild with CMake 3.31.6 succeeded (6 min 40 s); `app-release.apk` is 41 MB (arm64), installed with `adb install -r` and launched. Today screen, dark mode and the five tabs work; no JS errors in logcat.
  - **Test server on a COPY of the user's real data:**
    - The copy was taken with `VACUUM INTO` through a read-only node:sqlite connection on `%LOCALAPPDATA%\LearningTracker\data\learning-tracker.db` (20 plans, 3191 items, 1879 notes, 5043 cards), into `<scratchpad>/lt-test`.
    - Server: `java -jar backend/target/learning-tracker.jar --server.port=38500 --app.data-dir=<scratchpad>/lt-test --app.backup.automatic=false`. V7 migrated the real-data copy in 35 ms.
    - **The user's installed desktop app (port 38427, old version) and live DB were NOT touched.**
  - **Network checks:** phone → PC `172.16.0.119:38500` is reachable (no firewall block). LAN `/actuator/health` returns 403, so `RemoteAccessFilter` works on a real network.
  - **Fixed:** the pairing address list included Hyper-V/WSL adapters (`172.23.x`, `172.24.x`). `SyncController.isVirtualAdapter` filters by name (vethernet, hyper-v, wsl, docker, virtualbox, vmware, virtual).
  - **Build note:** the jar only bundles the Vue UI with **`mvn -Pui package`** (plain `package` keeps the old UI). Settings → Phone sync was checked in a headless Chrome screenshot: it renders with the QR code.
  - **adb tips:** in Git Bash, `export MSYS_NO_PATHCONV=1` before `adb shell … /sdcard/…`. **Don't drive the phone UI with `adb input` while the user is using the phone** (a uiautomator dump showed Khan Academy in the foreground, so I stopped).
  - **Next:** the user pairs (scans the QR at http://127.0.0.1:38500/settings), then the first real sync; check on the device; then P1.9 (icon, polish, final APK).
- 2026-11-25: **First real sync to the phone works.**
  - The user asked to "send plan to phone". The app was in the foreground, so I paired via adb UI (typed the address and token into the manual form; the QR wasn't used), then the first sync ran.
  - **Real data:** 5,282 change lines arrived in 3 pages (2000/2000/1282) in about 25 s. Phone and desktop plan percentages match (NodeJs 8.3%, odoo 39%, PKSF 64.5%, MBA 0/414). Desktop active total: 224/2417. The device shows on the desktop as "motorola motorola edge 60 fusion".
  - **UX issues to fix in P1.9:**
    - (a) During the first sync the Plans tab says "No plans here, pull down to sync" while plans are downloading. Show "Downloading your plans…" with page progress instead.
    - (b) Pages appear one by one (each page commits), so totals grow mid-sync (2099 → 2417). That's fine, but a progress line would explain it.
    - (c) The device name has a duplicated brand ("motorola motorola edge…"): Brand + Model where the Model already contains the brand.
    - (d) Tab-bar accessibility labels read ", Today" (leading comma).
- 2026-11-25: The test server (port 38500) was **stopped by Claude Code under low system memory**. It was not restarted; restart it only when the user asks. The phone keeps its data offline.
- 2026-11-25: **P1.9 polish:**
  - **Icon:** the app icon is the desktop's `backend/src/main/resources/desktop/tray-icon.png`. Generated with Pillow (a throwaway venv in the scratchpad): white corners flood-filled to transparent, the tile at **0.64** scale on a blue diagonal gradient (`#28BAFD` → `#1470FA`). That scale fits the artwork inside the visible circle, checked with circle and squircle mask previews; 0.80 clipped the lock badge. Outputs `assets/android-icon-foreground.png` and `assets/icon.png` (0.92). `adaptiveIcon.backgroundColor` is `#1C8EFC`.
  - **Assets:** the template's monochrome, background and favicon images were removed, so themed icons don't show the Expo logo; the `web` config was dropped (Android-only).
  - **Splash:** `expo-splash-screen` plugin, using the icon, on `#f8fafc` or `#020617` in dark mode.
  - **expo-doctor:** found missing `expo-font` (a peer dependency of @expo/vector-icons). Installed; 20/20 pass.
  - **UX fixes (a)–(d):**
    - `syncNow(..., onProgress)` reports `{pages, received, more}` after each page.
    - `useSync.progress`; `src/ui/SyncBanner.tsx` on Today and Plans; the Plans empty state says "Downloading your plans…" while syncing.
    - `src/sync/phoneName.ts` drops the doubled brand.
    - `tabBarAccessibilityLabel` on every tab.
  - **Checks:** mobile **73 tests passing**; tsc and lint clean.
  - **Build:** prebuild was re-run, which **re-applies `cmake.dir` in local.properties**.
  - **Final Phase 1 APK:** `BUILD SUCCESSFUL in 12m 16s`, 42.5 MB, copied to **`dist/Learning Tracker-android.apk`** (dist/ is git-ignored). Not installed: the phone left wireless adb (no mDNS service). Install with `adb install -r`, or copy the APK to the phone. It keeps app data: same debug signing key.
  - **Phase 1 is complete.**
  - **Open decisions for the user:**
    - (1) Install the new desktop version on the REAL data (V7 migration, one-way, covered by backups) and pair the phone with it; until then the phone is paired with the stopped test server.
    - (2) Commit the work (nothing is committed yet).
    - (3) Start P2 (structure editing).
- 2026-11-25: **P2.1 and P2.2 done.**
  - `src/data/structure.ts`: `createItem` (inserted at its final position, so version 0 like JPA), `updateItem` (version check, `StaleError`; TOPIC→MODULE drops the tick), `moveItem` (self/subtree check via a recursive CTE; another plan's parent refused), `reorderItem`, `deleteItem` (cascade, then renumber), `createPlan`/`updatePlan`/`deletePlan` (case-insensitive unique name, version check, yyyy-MM-dd target date). Renumbering bumps version and `updated_at` only on moved rows, like JPA dirty checking. A parent that gains a child loses its tick and review. 13 tests in `test/structure.test.ts`.
  - `src/domain/pace.ts`: a `paceOf` port (daysLeft includes the target day; projected = today + ceil(remaining/(recent/7)) - 1). `data/plans.ts` adds `recentCompletionsByPlan` (same SQL as Java: 28 days, imports > 5 s excluded); `Plan.pace` is filled by `listPlans`/`getPlan`. 7 tests in `test/pace.test.ts`.
  - Mobile total: **93 passing**; tsc clean.
- 2026-11-25: **Final Phase 1 APK installed on the phone** (`adb install -r`; data kept, same key). The Today screen matches the desktop: 177 reviews, 4781 flashcards, streak 10.
  - The phone and PC moved to a new network: phone **192.168.0.100**, PC **192.168.0.102**. mDNS only found the phone after `adb kill-server` + `start-server`.
  - **The phone's saved sync address is the old `http://172.16.0.119:38500`** (and the test server is stopped), so the phone must re-pair before the next sync.
- 2026-11-25: **Phase 2 done (P2.3–P2.5).**
  - **Data helpers** (`structure.ts`): `descendantCount`, `siblingPosition`, `moveTargets` (tree order; excludes the item's subtree and current parent; offers "Top level" when not already there). Tested in `test/structureHelpers.test.ts`.
  - **UI pieces:**
    - `src/ui/ActionSheet.tsx`: a bottom-sheet Modal (Android `Alert` allows only 3 buttons).
    - `src/ui/PaceBadge.tsx`: a port of the desktop PaceBadge (same wording and colours).
    - `src/ui/itemActions.ts`: shared item actions — Edit, Add a sub-item, Add an item after this, Move up/down, Move to…, Delete (the confirm shows the descendant count).
  - **Routes** (registered as modals in `_layout`):
    - `item-form` (create with parentId/position, or edit with a version check; `StaleError` → alert);
    - `move/[id]` (parent picker);
    - `plan-form` (new or edit; target date typed as YYYY-MM-DD with +1/+3/+6 months chips — no native date picker dependency);
    - `plan-delete/[id]` (type the plan name to confirm).
  - **Plan screen:** header "+" (add a top-level item) and "⋯" (add, edit, archive/activate, delete); long-press a row for item actions (with an accessibility action as well); `PaceBadge detailed` under the progress bar.
  - **Item screen:** a header "⋯" with item actions; after a delete it goes back.
  - **Plans tab:** a "+" header button for a new plan; `PaceBadge` on each plan card.
  - **Tooling note:** a long Python edit in a bash heredoc broke bash parsing. Put such scripts in a scratchpad `.py` file and run that.
  - **E2E:** a new step, "pushes structure edits made on the phone" (new plan with target date, items at positions, move to top level, reorder, delete, archive; a cross-plan move is refused). **8/8 pass** with DB equality.
  - **Checks:** mobile **94 unit tests passing**; tsc and lint clean.
  - **APK:** rebuilt with no prebuild needed (JS only), **1 min**. Copied to `dist/Learning Tracker-android.apk` and installed on the phone (YouTube was in the foreground, so no interruption).
  - **Still pending user decisions:** (1) switch the desktop to the real data + re-pair the phone; (2) commit. Next build phase: P3 (tags, links, time log and Pomodoro, confidence, dashboard charts, heatmap).
- 2026-11-25: **Why pairing with the real desktop failed, and the fix.**
  - The user's installed desktop app had updated itself from source (tray "Update from source code…" → `install-app.ps1`), so the REAL DB is now at **V7** (log 21:05:57) and `/api/sync/ping` answers 401 (exists).
  - **But** the app listened on `127.0.0.1:38427` only (netstat), so the phone's connection timed out.
  - **Cause:** `backend/src/main/resources/application-desktop.yml` sets `server.address: 127.0.0.1` (the desktop profile). I had missed this file. **Correction to earlier notes:** the installer never exposed the API on the LAN; that claim in the P0.1/P0.3 log entries was wrong, and SYNC_PROTOCOL §5 is fixed.
  - **Fix:** `server.address: 0.0.0.0` in the desktop profile. `RemoteAccessFilter` keeps everything except `/api/sync` and `/api/sync/ping` this-PC-only. `PhoneSyncIntegrationTest` (19) and `DesktopLauncherTest` (2) pass.
  - **To take effect:** the user runs tray → *Update from source code…* (rebuild, all tests, reinstall, restart), allows the Windows Firewall prompt for **private networks**, then re-pairs the phone: Sync tab → *Pair with a different computer* → scan the QR in desktop Settings → Phone sync (`http://192.168.0.102:38427`).
- 2026-11-25: **S1 done: no duplicate plans.**
  - **Cause:** V7 gives each database copy its OWN random plan/link/time-log keys; item keys are shared. The phone held the test copy's plans; the real desktop's same plans had different keys → `freePlanName` would create "Java (2)" and items would be skipped as cross-plan moves. The desktop was unaffected (the phone pushed nothing).
  - **Fix** (`client.ts`):
    - New app_meta keys `sync_history_url` (the computer whose data the phone holds, set after a completed sync; kept on unpair) and `sync_rebuild`.
    - `pair()` to a different computer than the history sets rebuild; with unsent changes it throws `SyncError('unsent-changes')` unless `discardUnsent`. A never-synced phone keeps and uploads its data.
    - `syncNow` with rebuild sends since 0, epoch null and no changes, wipes in the first page's transaction, and clears the flag after the last page. `report.rebuilt`. `requestRebuild()`.
    - Pairing a new computer clears `lastSyncAt`.
  - **UI:** `pair.tsx` asks "Discard and pair"; the Sync tab has a "Download everything again" button and a rebuilt message.
  - **Tests:** 6 new in `syncClient.test.ts` (switch computers, 5 repeated syncs, re-pair same computer, first pairing uploads, unsent guard, download again). Mobile **101 passing**.
  - **Lesson:** a test file with a parse error silently drops out of the count; check `Test Files … failed`, not only `Tests … passed`.
  - **`mobile/scripts/ship-android.sh`:** builds the APK (JDK 21, Windows paths because of `MSYS_NO_PATHCONV`), copies it to dist/, finds the phone (adb restart + mDNS) and installs (`cygpath -w` for the APK path). The S1 install was slow over wireless.
  - **The user's phone needs to update the APK, then use Sync → "Download everything again"** (or re-pair) to clear any "(2)" plans from the earlier merge.
- 2026-11-26: **S2–S5 done.**
  - **S2 Settings:**
    - `src/state/prefs.ts`: Zustand store plus app_meta `pref_*` keys (local, never synced): theme system/light/dark, reading font sans/serif/mono, textScale, lineHeight, readingTheme auto/paper/sepia/night, dailyGoal.
    - `src/state/PrefsLoader.tsx` loads prefs and calls **`Appearance.setColorScheme('light'|'dark'|'auto')`** (RN 0.88), so every `useColorScheme()` follows, and sets `SystemUI.setBackgroundColorAsync`.
    - `src/app/settings.tsx`: segmented controls, background swatches, a live preview through the real `NoteView` in page mode, daily goal.
  - **S3 Reading mode:**
    - `src/app/read/[id].tsx`: full-screen page; the controls show and hide on tap. The renderer posts `{type:'tap'}` for taps outside links, because a WebView swallows touches so a `Pressable` around it never fires.
    - Reading progress bar (renderer posts `progress` in page mode), A−/A+, background cycle, previous/next note in roadmap order (`src/data/reading.ts` `readingNav`), Mark as done.
    - Remembers `last_read_item` (app_meta).
    - Renderer payload adds `font`, `lineHeight`, `readingTheme`, `mode`; CSS has sepia/paper/night page themes and serif/mono fonts.
    - Entry points: the item screen's "Read" button and tapping the note.
  - **S4 Gamification:**
    - `src/domain/gamification.ts`: XP = 20/topic you completed (not imports) + 15/note + 10/review stage + 5/flashcard stage; level n starts at 50·n·(n−1) XP; titles; 13 badges; `encouragement()`.
    - `src/data/stats.ts` `loadStats`: actions per day from completions plus `last_reviewed_at` of cards and reviews (only the latest is stored, so past days are approximate and today is exact), streaks over action days, finished plans.
    - `src/app/(tabs)/profile.tsx`: level card, lifetime numbers, badge grid, links to Settings and Sync.
  - **S5 Home** (`src/app/(tabs)/index.tsx`, own header): date and greeting, level circle (→ Profile), settings gear, encouragement banner, card with `WeekRow`, `GoalRing` (react-native-svg), 3 `StatTile`s and `LevelBar`, "Start studying", a dark "Continue reading" hero, due tiles, an inline review list (3, expandable), "Topics this week" `BarChart`, Up next. Components in `src/ui/dashboard.tsx`.
  - **Tabs:** Home, Plans, Search, Cards, Profile, with filled icons when active. **Sync is no longer a tab**: it's `src/app/sync.tsx` (stack), opened from Profile and Settings.
  - **Dependencies:** `react-native-svg` added (no config plugin → no prebuild needed).
  - **Route types:** `.expo/types/router.d.ts` is only regenerated by the dev server: `CI=1 timeout 50 npx expo start --offline --port 8099`. `expo export` does not regenerate it.
  - **Checks:** mobile **112 tests passing**; tsc and lint clean.
  - **Installs:** S2–S3 installed 00:01. The S4–S5 install failed (the phone went offline mid-install, then wasn't discoverable); the APK is in dist/.
- 2026-11-26: **S6 done: modern UI pass.**
  - `src/ui/PlanCover.tsx`: generated book covers (SVG gradient from a hash of the plan name, so each plan has a stable colour; an icon matched from words in the name — Java, AWS, maths, DSA, Node, Python, design, finance, data…; initials).
  - **Plans tab** is now a library: a dark "all active plans" hero, a search field (`listPlans` search), filter chips, cover cards with serif titles, progress, "% · N topics left" and the pace badge.
  - **Plan screen** header: large cover, serif title and description, percentage.
  - **Item screen:** a title card with a mini cover, breadcrumbs, a serif title, meta chips and a primary **"Read the note"** button; "Mark as done" becomes secondary when there is a note.
  - **Search** hits have a mini plan cover.
  - **Checks:** 112 tests passing; tsc and lint clean.
  - **Install:** skipped (phone not reachable); the APK in dist/ contains S4–S6.
- 2026-11-26: **S7 done: topic study tools.**
  - `src/data/extras.ts` ports TagService (normalize, max 20 tags of 40 characters, existing spelling kept, orphan tags removed), LinkService (`https://` added, http(s) only, max 50), TimeLogService (1–1440 minutes) and ConfidenceService (≤2 on a completed leaf topic sets the review to tomorrow unless one is due by then). 7 tests.
  - `src/state/pomodoro.ts`: one app-wide timer. Only `endsAt` is stored (app_meta 'pomodoro'), so it survives backgrounding or a restart; `finishIfDue` logs the minutes. 2 tests.
  - `src/ui/PomodoroBar.tsx`: a floating countdown above the tab bar (mounted in the root layout); it checks every second and on resume, and alerts when done.
  - `src/ui/itemTools.tsx`: `ConfidenceCard` (1–5, "Shaky"…"Solid"; tap again to clear), `TimeCard` (+15/+25/+50, start 25-min focus), `TagsCard` (removable chips, add field, suggestions from `allTags`), `LinksCard` (open, add with optional title, long-press to delete). All are wired into the item screen below the notes.
  - **Checks:** mobile **121 tests passing**; tsc and lint clean.
  - **Install:** skipped (phone not reachable).
  - Note: one vitest run stalled for 5 minutes (system short of memory); the rerun took 0.7 s. Use `timeout` on test commands.
- 2026-11-26: **S8 done: data export.**
  - `src/data/exportCsv.ts`: plan CSV in the **desktop's ExportService format** (id, level, type, name, description, priority, completed, version, note_format, note; tree order; RFC quoting; UTF-8 BOM; `
`), so the desktop can import or sync it. 3 tests, including a strict RFC parser round-trip.
  - **Slug fix on BOTH sides:** `[^\p{L}\p{M}\p{N}]+`. Without `\p{M}`, Bangla vowel signs were stripped ("বাংলা" became "ব-ল"); the desktop `ExportService` had the same bug. Desktop `ImportExportIntegrationTest` passes (9).
  - `src/platform/share.ts`: `shareTextFile` (expo-file-system `File` in `Paths.cache` + expo-sharing) and `sharePhoneBackup` (`VACUUM INTO` a cache file, then share as application/vnd.sqlite3).
  - **UI:** the plan ⋯ menu has "Export as CSV"; Settings has a "Data" section with "Back up this phone". The phone .db isn't restorable by the desktop's Restore (no flyway_schema_history); it's a phone-side spare.
  - **expo-sharing adds a config plugin → prebuild was re-run and local.properties restored (sdk.dir + cmake.dir).**
  - **Checks:** mobile **124 tests passing**; tsc and lint clean.
- 2026-11-26: **The S8 build was stopped by Claude Code under low system memory** (not a build failure). It was not restarted: restart only when the user asks (`bash mobile/scripts/ship-android.sh`; prebuild and local.properties are already done). `dist/Learning Tracker-android.apk` is still the S7 build (S1–S7). The phone holds the S3 build (installed 00:01).
- 2026-11-26: **Device check of S1–S8 (all installed 06:10; fixes installed afterwards).**
  - Checked on the phone with screenshots: Home (greeting, streak banner, week row, goal ring, stat tiles, level, Continue reading), the Plans library (covers and icons matched; 224/2417 = 9.3% like the desktop), Profile badges, and Settings (sepia + serif live preview). No JS errors.
  - **Fixed after the check:**
    1. **XP was inflated (31,860, level 25).** 1,879 notes × 15 came from imported plans. Measured on the real DB: only **25** notes were saved >5 s / 60 s / 5 min after their topic was created (robust). Notes now count only when written >60 s after the topic's creation; finished-review credit excludes imports. Expected XP ≈ 4,050, level 9.
    2. The goal ring drew a dot at 0% (round line cap on a zero-length arc); it's now not drawn at 0%.
    3. "a 11-day" → `article()` ("an" for 8…, 11, 18, and 11/18 thousands).
    4. Scrolled content ran under the status bar on Home, Profile and Cards → `StatusBarShade` in `src/ui/dashboard.tsx`.
  - Mobile **126 tests passing**; tsc and lint clean.
  - **INCIDENT / RULE:** while navigating with `adb input`, a Back key left the app, and the next tap and swipe landed in the user's **Phone app (call log)**. Verified that no call was made (`dumpsys telecom`: no calls; `mCallState=0`). **Rule from now on: no `adb input` on the user's phone at all, unless the user explicitly asks for it in that turn. If asked, check `topResumedActivity` is `com.mylearn.tracker` before EVERY tap, and never send Back (keyevent 4) at the app's main screen.** Install with `adb install -r` and take read-only screenshots only.
- 2026-11-26: **New study-friendly theme (user: the lavender palette "not suitable for a study app, mainly on dark mode").**
  - `src/ui/palette.ts` (pure, tested) holds the neutral bases: light = warm off-white `#f6f7f5`; dark = colourless charcoal `#111315` with softened text `#e6e9eb`. Plus "hero" tokens for the dark feature cards and ONE accent from `ACCENTS` (teal default, blue, green, amber, violet), each with a deep light-mode shade and a soft dark-mode shade.
  - `theme.ts` `useColors()` = `colorsFor(scheme, prefs.accent)`. New pref `accent` (`prefs.ts`); Settings → Appearance has an "Accent colour" picker.
  - All hard-coded purples were replaced with tokens: Home and Library heroes, PomodoroBar, the card shadow (now black), the reader controls (the progress bar follows the accent; night page `#15181b`), the settings night swatch, and the renderer's night page. Plan covers use muted natural gradients (teal, slate, olive, terracotta, plum, ocean, ochre, graphite, forest, rosewood).
  - `test/palette.test.ts`: WCAG checks for every accent in both modes — text ≥7:1 (AAA) on bg and card, muted ≥4.5, button text ≥4.5, accent on card ≥3, hero text ≥7, hero accent ≥4.5 — and dark text below 17:1 (softer than white on black). **11 pass.**
  - Mobile **137 tests passing**; tsc and lint clean. Built and installed on the phone (install only, no screen input).
  - **Not changed:** the splash screen colours in app.json (`#f8fafc` / dark `#020617`). Changing them needs a prebuild and a full native rebuild; do it with the next native change: light `#f6f7f5`, dark `#111315`.
