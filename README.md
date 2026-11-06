# Personal Learning Tracker

A small, locally hosted web app that replaces spreadsheet learning roadmaps.

- Multiple **learning plans**, each a tree of **modules** and **topics** (unlimited depth)
- **Leaf-level completion**: tick topics that have no sub-items. Progress for every module, plan and
  the dashboard is *calculated* (completed leaves ÷ all leaves), never typed in or stored
- Priorities (Must / Should / Nice), search and filters, drag-and-drop reordering, light/dark theme
- **Notes** on any module or topic: a formatted summary of what you learned, saved automatically
- **Charts** on the dashboard: overall % over time, topics completed per day, streaks
- **Import** your existing CSV / Excel roadmaps (several sheets at once, or pasted text), **export** to CSV
- Automatic daily backups to a folder you choose, and **restore** from the Settings page

Stack: Java 21 · Spring Boot 3.5 · JPA/Hibernate · SQLite · Flyway · Vue 3 · TypeScript · Vite · Tailwind CSS.
Design notes: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## 1. Run it (choose one)

| | Option | Port | Data lives in |
|---|---|---|---|
| **W** | **Windows installer (.exe)**: a normal desktop app with a tray icon | `38427` | `%LOCALAPPDATA%\LearningTracker\data\` |
| **B** | **Native JAR + systemd** (Linux) | `8080` (`SERVER_PORT`) | `/var/lib/learning-tracker/` |
| **B** | Native JAR + Task Scheduler (Windows) | `8080` (`-Port`) | `%LOCALAPPDATA%\LearningTracker\data\` |
| **A** | Docker Compose | `8088` (`LT_PORT`) | `./data/` next to `docker-compose.yml` |

In the native options **one process serves both the UI and the API on one port**: open `http://127.0.0.1:8080/`.

### Option W: Windows installer

The installer includes its own Java, so the PC needs nothing else installed.

**Build it** (needs JDK 21+, Maven, Node):

```powershell
# once, in an *administrator* PowerShell (the installer step of jpackage needs WiX 3):
winget install WiXToolset.WiXToolset
# then, in a normal PowerShell:
.\scripts\build-installer.ps1        # -> dist\Learning Tracker-<version>.exe
```

| Flag | Effect |
|---|---|
| `-SkipBuild` | Reuse `backend\target\learning-tracker.jar` |
| `-AppImageOnly` | Only `dist\Learning Tracker\` (portable folder, runs without installing, no WiX needed) |
| `-Port 40000` | Use another port (default `38427`) |

**If Windows Smart App Control blocks the installer.** The installer isn't code-signed, so Smart App Control may refuse it.
The app itself still runs: its Java runtime is signed. Install it with the script instead, which gives the same result
(Start-menu and desktop shortcuts, an entry in *Installed apps*, same folders):

```powershell
.\scripts\build-installer.ps1 -AppImageOnly                                   # or -SkipBuild if already built
powershell -ExecutionPolicy Bypass -File deploy\windows\install-app.ps1       # install or update (stops the running app first)
powershell -ExecutionPolicy Bypass -File deploy\windows\install-app.ps1 -Uninstall
```

To make the real installer pass Smart App Control, sign it with a code-signing certificate that Windows trusts,
e.g. Microsoft's Azure Trusted Signing. A self-signed certificate is not enough.

**Using the installed app:**
- **Installing:** for your user only, no admin rights. You get a Start-menu entry and, optionally, a desktop shortcut.
- **Starting:** starts the server in the background with no window, adds a tray icon by the clock, and opens `http://127.0.0.1:38427/`. Starting it again while it runs just opens the browser.
- **Tray menu** (right-click): *Open*, *Open data folder* (database, backups, logs), *Start with Windows*, *Quit*.
- **Updating:** quit the app from the tray, then run the newer installer. Your data in `%LOCALAPPDATA%\LearningTracker\data` is kept, and uninstalling doesn't delete it either.
- **Changing the port after installing:** edit `-Dserver.port=` in `<install folder>\app\Learning Tracker.cfg`.
- **Problems:** see `%LOCALAPPDATA%\LearningTracker\data\logs\learning-tracker.log`.

### Option B: native, Linux with systemd

Requirements: Java 21+ to run. To build, also Maven 3.9+ and Node 20+.

```bash
./scripts/build.sh                    # -> backend/target/learning-tracker.jar (UI bundled)
sudo ./deploy/install-linux.sh        # installs to /opt, creates the service, starts it
```

| Task | Command |
|---|---|
| Status / logs | `systemctl status learning-tracker` · `journalctl -u learning-tracker -f` |
| Stop / start / restart | `sudo systemctl stop\|start\|restart learning-tracker` |
| Disable autostart | `sudo systemctl disable learning-tracker` |
| Change port / data dir | create `/etc/learning-tracker.env` with e.g. `SERVER_PORT=9000`, then restart |

The service starts at boot (`WantedBy=multi-user.target`), restarts on failure, listens on
`127.0.0.1` only, and runs as an unprivileged `learntracker` user. Unit file:
[deploy/systemd/learning-tracker.service](deploy/systemd/learning-tracker.service).

### Option B: native, Windows (start at log-on)

```powershell
.\scripts\build.ps1
powershell -ExecutionPolicy Bypass -File deploy\windows\install-autostart.ps1   # optional: -Port 9000
```

| Task | Command |
|---|---|
| Stop / start | `Stop-ScheduledTask LearningTracker` · `Start-ScheduledTask LearningTracker` |
| Remove autostart | `Unregister-ScheduledTask -TaskName LearningTracker -Confirm:$false` |

### Option A: Docker Compose

Requirements: Docker Desktop (enable *Start Docker Desktop when you sign in* so the app comes back after a reboot).

```bash
docker compose up -d --build          # http://127.0.0.1:8088
docker compose ps                     # both services should become "healthy"
docker compose logs -f backend
docker compose down                   # stop; data stays in ./data
```

Both containers use `restart: unless-stopped` and have health checks. The database is **bind-mounted** from
`./data`, so it is never stored only inside a container.

### Run without installing

```bash
java -jar backend/target/learning-tracker.jar --server.port=8080 --app.data-dir=/path/to/data
```

---

## 2. Configuration

Each value can be set as an environment variable or a `--flag`:

| Env var | Flag | Default | Meaning |
|---|---|---|---|
| `SERVER_PORT` | `--server.port` | `8080` | HTTP port (UI + API) |
| `SERVER_ADDRESS` | `--server.address` | all interfaces | Use `127.0.0.1` to allow only this machine |
| `APP_DATA_DIR` | `--app.data-dir` | `./data` | Folder for `learning-tracker.db` and `backups/` (created if missing) |
| `APP_BACKUP_AUTOMATIC` | `--app.backup.automatic` | `true` | Daily automatic backup |
| `APP_BACKUP_KEEP` | `--app.backup.keep` | `14` | Number of backup files kept |

---

## 3. Update to a new version

1. `git pull` (or copy in the new source)
2. Rebuild and reinstall:
   - systemd: `./scripts/build.sh && sudo ./deploy/install-linux.sh`
   - Windows: `.\scripts\build.ps1; deploy\windows\install-autostart.ps1`
   - Docker: `docker compose up -d --build`

The installers copy the database to `backups/pre-update-<timestamp>.db` before starting the new version.
**Database migrations run automatically** on start-up (see §5).

---

## 4. Backup and restore

**Automatic:** if the newest backup is older than 20 hours, a new one is written to the backup folder.
This is checked a minute after start-up and every 6 hours after that, so it still works on a laptop that isn't always on.
The 14 newest backups are kept.

**Backup folder:** *Settings → Backups → Browse…* (or type a full path), for example a OneDrive or Google Drive
folder or another disk. The default is `<data-dir>/backups/`. The choice is stored in `<data-dir>/settings.properties`,
so restoring an old database doesn't change it. Backups already in the old folder stay there.

**Manual:** *Settings → Back up now* in the UI, or `curl -X POST http://127.0.0.1:8080/api/admin/backup`.

Backups use SQLite's `VACUUM INTO`. Each one is a complete, consistent `.db` file, taken while the app runs.
Don't copy the live `learning-tracker.db` by hand while the app is running: in WAL mode, recent changes
are still in the `-wal` file.

**Restore from the UI** (no need to stop the app): *Settings → Restore*, then either:
- *Restore* next to one of the listed backups, or
- *Restore from a file…* for any backup `.db`, e.g. one kept on a USB drive.

The file is checked first: it must be a readable SQLite database that passes `quick_check`, contains the app's tables,
and wasn't made by a newer app version. Then a **safety backup of your current data** is written, so you can undo a
restore by restoring that file. Backups from older app versions are migrated to the current schema automatically.

**Restore by hand** (if the app won't start):
```bash
sudo systemctl stop learning-tracker             # or: docker compose down / Stop-ScheduledTask
cd /var/lib/learning-tracker                     # your data dir
mv learning-tracker.db learning-tracker.db.old
rm -f learning-tracker.db-wal learning-tracker.db-shm
cp backups/learning-tracker-20261106-031500.db learning-tracker.db
sudo chown learntracker:learntracker learning-tracker.db   # systemd install only
sudo systemctl start learning-tracker
```

For an extra copy elsewhere, sync the `backups/` folder (not the live database) to cloud storage or a USB drive.
You can also export a plan to CSV from its menu.

---

## 5. Database migrations

The schema is versioned with **Flyway** (`backend/src/main/resources/db/migration/V<n>__description.sql`).
On start-up, the app applies any new migrations in order and records them in `flyway_schema_history`.
Nothing needs to be run by hand. To change the schema during development:

1. Add a new file such as `V2__add_notes_column.sql`. Never edit a migration that has already been applied.
2. Update the matching JPA entity.
3. Run `mvn test`. Every integration test starts from an empty database migrated through all versions.

Hibernate never alters the schema (`ddl-auto: none`).

---

## 6. Importing your spreadsheets

*Import* in the sidebar → choose a target → pick a `.csv` or `.xlsx` **or switch to *Paste text* and paste CSV** → check the preview → *Import*.

- **Pasting:** pasted text goes through the same preview, validation and duplicate check as an uploaded file. It can be CSV with a header row, or cells copied straight from Excel or Google Sheets (tab-separated). The preview updates as you type. A single top-level module's name is suggested as the plan name.

- **Layouts understood:**
  - Outline by column (`Phase | Topic | Subtopic | What to Learn | Priority | ✓`)
  - Flat lists (`code | name | ✓`)
  - This app's own export (`id | level | type | name | …`)
- **Headers:** the header row is found automatically, even below a title or progress row. Columns are mapped by name. An unlabeled checkbox column is detected from its TRUE/FALSE values.
- **Overrides:** you can change the header row and any column's role in the preview.
- **What's kept:** descriptions, priorities, ticks and row order. Leading bullets such as `■` and `•` are stripped.
- **Structure:** rows that end up with children become modules; the rest become topics. Ticks on parent rows are ignored, with a warning, because their progress is calculated.
- **Safety:**
  - Errors are shown before anything is saved.
  - Re-importing the same content needs an explicit "import anyway".
  - Import only **adds** items. It never changes or deletes existing ones.
- **Templates:** at the top of the Import page:
  - **Blank CSV (headers only):** `level,type,name,description,priority,completed`. Fill it in and import it.
  - **Example CSV:** the same columns plus sample rows that show the nesting.
  - Both are also at `GET /api/import/template` (blank) and `GET /api/import/template?examples=true`.

---

## 7. Development

```bash
cd backend && mvn spring-boot:run    # API on :8080, dev DB in backend/data/
cd frontend && npm install && npm run dev   # UI on :5173, proxies /api to :8080
cd backend && mvn test               # 100 tests (JUnit 5, Mockito, Spring integration on real SQLite)
```

### REST API (summary)

| | |
|---|---|
| Plans | `GET/POST /api/plans` · `GET/PUT/DELETE /api/plans/{id}` · `GET /api/plans/{id}/progress` |
| Items | `GET/POST /api/plans/{planId}/items` (GET = tree) · `GET/PUT/DELETE /api/items/{id}` |
| Structure | `PATCH /api/items/{id}/completion` · `/move` · `/reorder` · `GET /api/items/{id}/progress` |
| Notes | `GET/PUT /api/items/{id}/notes` (HTML; empty content deletes the note) |
| Dashboard | `GET /api/dashboard/summary` · `GET /api/dashboard/activity?days=90&tz=Asia/Dhaka[&planId=]` (`days=0` = all) |
| Import | `POST /api/plans/{id}/import/preview\|confirm` · `POST /api/import/preview\|confirm` · `GET /api/import/template` |
| Export | `GET /api/plans/{id}/export` · `GET /api/items/{id}/export` |
| Admin | `POST /api/admin/backup` · `GET /api/admin/backups` · `GET/PUT /api/admin/backup-settings` · `GET /api/admin/folders?path=` · `POST /api/admin/backups/{file}/restore` · `POST /api/admin/restore` (upload) |

Errors always have the shape `{status, error, message, fieldErrors[], path, timestamp}`.
"# my_learn" 
