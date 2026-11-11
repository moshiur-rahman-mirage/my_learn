package com.mylearn.tracker.admin;

import com.mylearn.tracker.admin.AppSettings.Retention;
import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.IsoFields;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Consistent online backups using SQLite's {@code VACUUM INTO}. It produces a clean, standalone
 * copy even while the app is running, which is safer than copying the live WAL-mode files.
 *
 * <p>Backups go to the folder chosen on the Settings page ({@link AppSettings}), by default
 * {@code <data-dir>/backups}. An automatic backup is taken when the newest one is older than
 * 20 hours (checked shortly after start-up and then every 6 hours, which suits a laptop that is
 * not always on).
 *
 * <p><b>Checks.</b> Every new backup is opened read-only and integrity-checked; a backup that
 * fails is deleted and the backup reported as failed. The result is kept next to the file
 * ({@code <name>.check}), and any backup can be re-checked from the Settings page.
 *
 * <p><b>Clean-up.</b> Old backups are thinned out by {@link Retention}: the newest few, plus one
 * per day, week and month going back. The same rule applies to the second (mirror) folder.
 */
@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final String PREFIX = "learning-tracker-";
    private static final String CHECK_SUFFIX = ".check";
    /** The dashboard warns when the newest backup is older than this. */
    static final Duration STALE = Duration.ofDays(3);

    private final JdbcTemplate jdbc;
    private final AppSettings settings;
    private final int defaultRecent;
    private final boolean automatic;

    public BackupService(JdbcTemplate jdbc, AppSettings settings,
                         @Value("${app.backup.keep:7}") int defaultRecent,
                         @Value("${app.backup.automatic:true}") boolean automatic) {
        this.jdbc = jdbc;
        this.settings = settings;
        this.defaultRecent = defaultRecent;
        this.automatic = automatic;
    }

    private volatile String mirrorError;

    public BackupSettings settings() {
        Path mirror = settings.mirrorDir();
        Retention r = retention();
        return new BackupSettings(settings.backupDir().toString(), settings.defaultBackupDir().toString(),
                settings.isCustomBackupDir(), automatic, r.recent(), r, mirror == null ? null : mirror.toString(),
                mirror == null ? null : mirrorError);
    }

    public Retention retention() {
        Retention r = settings.retention(defaultRecent);
        return new Retention(Math.max(1, r.recent()), r.daily(), r.weekly(), r.monthly());
    }

    /** Saves the clean-up rule; it is applied with the next backup or "Clean up now". */
    public synchronized BackupSettings changeRetention(Retention r) {
        if (r.recent() < 1 || r.recent() > 500 || r.daily() < 0 || r.daily() > 3650 || r.weekly() < 0 || r.weekly() > 520
                || r.monthly() < 0 || r.monthly() > 240) {
            throw new BusinessRuleException("Keep at least 1 recent backup; days up to 3650, weeks up to 520, months up to 240.");
        }
        settings.setRetention(r);
        return settings();
    }

    /**
     * Changes the backup folder after checking that it can be created and written to.
     * Existing backups stay where they are.
     *
     * @param directory absolute path; blank = back to the default folder
     */
    public synchronized BackupSettings changeDirectory(String directory) {
        if (directory == null || directory.isBlank()) {
            settings.setBackupDir(null);
            return settings();
        }
        Path dir = writableFolder(directory);
        settings.setBackupDir(dir);
        log.info("Backup folder changed to {}", dir);
        return settings();
    }

    /**
     * Sets the second folder each new backup is also copied to, and copies the newest backup
     * there right away.
     *
     * @param directory absolute path; blank = stop copying
     */
    public synchronized BackupSettings changeMirror(String directory) {
        if (directory == null || directory.isBlank()) {
            settings.setMirrorDir(null);
            mirrorError = null;
            return settings();
        }
        Path dir = writableFolder(directory);
        if (dir.equals(settings.backupDir().toAbsolutePath().normalize())) {
            throw new BusinessRuleException("Choose a different folder than the backup folder itself.");
        }
        settings.setMirrorDir(dir);
        log.info("Backups will also be copied to {}", dir);
        List<BackupFile> existing = list();
        if (!existing.isEmpty()) {
            mirror(Path.of(existing.getFirst().path()));
        }
        return settings();
    }

    /** Checks that the folder can be created and written to. */
    private static Path writableFolder(String directory) {
        Path dir;
        try {
            dir = Path.of(directory.strip());
        } catch (InvalidPathException e) {
            throw new BusinessRuleException("Not a valid folder path: " + directory);
        }
        if (!dir.isAbsolute()) {
            throw new BusinessRuleException("Use a full path, e.g. D:\\Backups\\LearningTracker or /home/me/backups");
        }
        dir = dir.normalize();
        if (Files.exists(dir) && !Files.isDirectory(dir)) {
            throw new BusinessRuleException("That path is a file, not a folder: " + dir);
        }
        try {
            Files.createDirectories(dir);
            Path probe = dir.resolve(".write-test-" + UUID.randomUUID());
            Files.writeString(probe, "ok");
            Files.delete(probe);
        } catch (IOException | SecurityException e) {
            throw new BusinessRuleException("Can't write to " + dir + " (" + e.getClass().getSimpleName() + ")");
        }
        return dir;
    }

    /** Creates a backup now, checks it, then cleans up; must not run inside a transaction (VACUUM can't). */
    public synchronized BackupFile backupNow() {
        Path dir = settings.backupDir();
        Path target;
        try {
            Files.createDirectories(dir);
            target = dir.resolve(PREFIX + STAMP.format(LocalDateTime.now()) + ".db");
            while (Files.exists(target)) { // names sort by time, so never invent a non-time suffix
                Thread.onSpinWait();
                target = dir.resolve(PREFIX + STAMP.format(LocalDateTime.now()) + ".db");
            }
            jdbc.execute("VACUUM INTO '" + target.toAbsolutePath().toString().replace("'", "''") + "'");
        } catch (IOException e) {
            throw new UncheckedIOException("Backup failed: can't write to " + dir, e);
        }
        BackupCheck check = check(target);
        if (!check.ok()) {
            deleteWithCheck(target);
            log.error("Backup {} failed its check and was removed: {}", target, check.message());
            throw new BusinessRuleException("The backup failed its check and was removed: " + check.message());
        }
        log.info("Backup written to {} ({} plans, {} items, {} notes; checked)", target.toAbsolutePath(),
                check.plans(), check.items(), check.notes());
        try {
            prune(dir, retention());
        } catch (IOException e) {
            log.warn("Couldn't clean up old backups in {}: {}", dir, e.toString());
        }
        mirror(target);
        return describe(target);
    }

    // --- checking ---------------------------------------------------------------------------

    /** Re-checks one backup in the current folder and remembers the result. */
    public BackupCheck checkFile(String fileName) {
        return check(resolveBackup(fileName));
    }

    /**
     * Opens the file read-only, runs SQLite's integrity check and counts its contents. The
     * result is written next to the file so it survives restarts.
     */
    BackupCheck check(Path file) {
        BackupCheck result;
        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);
        try (Connection c = config.createConnection("jdbc:sqlite:" + file.toAbsolutePath());
             Statement s = c.createStatement()) {
            String integrity;
            try (ResultSet rs = s.executeQuery("PRAGMA integrity_check")) {
                integrity = rs.next() ? rs.getString(1) : "no result";
            }
            if (!"ok".equalsIgnoreCase(integrity)) {
                result = BackupCheck.failed("Integrity check failed: " + integrity);
            } else {
                int plans = count(s, "learning_plan");
                int items = count(s, "learning_item");
                int notes = count(s, "item_note");
                result = new BackupCheck(true, "OK", plans, items, notes, Instant.now());
            }
        } catch (SQLException e) {
            result = BackupCheck.failed("Not a readable Learning Tracker database (" + e.getMessage() + ")");
        }
        writeCheck(file, result);
        return result;
    }

    private static int count(Statement s, String table) throws SQLException {
        try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static void writeCheck(Path file, BackupCheck check) {
        Properties p = new Properties();
        p.setProperty("ok", String.valueOf(check.ok()));
        p.setProperty("message", check.message());
        p.setProperty("plans", String.valueOf(check.plans()));
        p.setProperty("items", String.valueOf(check.items()));
        p.setProperty("notes", String.valueOf(check.notes()));
        p.setProperty("checkedAt", check.checkedAt().toString());
        try (Writer out = Files.newBufferedWriter(checkFileOf(file), StandardCharsets.UTF_8)) {
            p.store(out, "Result of checking " + file.getFileName());
        } catch (IOException e) {
            log.warn("Couldn't save the check result of {}: {}", file, e.toString());
        }
    }

    private static BackupCheck readCheck(Path file) {
        Path checkFile = checkFileOf(file);
        if (!Files.isRegularFile(checkFile)) {
            return null;
        }
        Properties p = new Properties();
        try (Reader in = Files.newBufferedReader(checkFile, StandardCharsets.UTF_8)) {
            p.load(in);
            return new BackupCheck(Boolean.parseBoolean(p.getProperty("ok")), p.getProperty("message", ""),
                    Integer.parseInt(p.getProperty("plans", "0")), Integer.parseInt(p.getProperty("items", "0")),
                    Integer.parseInt(p.getProperty("notes", "0")), Instant.parse(p.getProperty("checkedAt")));
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private static Path checkFileOf(Path backup) {
        return backup.resolveSibling(backup.getFileName() + CHECK_SUFFIX);
    }

    private static void deleteWithCheck(Path backup) {
        try {
            Files.deleteIfExists(backup);
            Files.deleteIfExists(checkFileOf(backup));
        } catch (IOException e) {
            log.warn("Couldn't delete {}: {}", backup, e.toString());
        }
    }

    /** Whether backups are healthy, for the dashboard: recent enough, checked OK, copied OK. */
    public BackupStatus status() {
        List<BackupFile> all = list();
        if (all.isEmpty()) {
            return new BackupStatus(null, null, "No backups yet. Make one on the Settings page.");
        }
        BackupFile newest = all.getFirst();
        String warning = null;
        if (newest.check() != null && !newest.check().ok()) {
            warning = "The newest backup failed its check: " + newest.check().message();
        } else if (Duration.between(newest.createdAt(), Instant.now()).compareTo(STALE) > 0) {
            warning = "The newest backup is %d days old.".formatted(Duration.between(newest.createdAt(), Instant.now()).toDays());
        } else if (settings.mirrorDir() != null && mirrorError != null) {
            warning = mirrorError;
        }
        return new BackupStatus(newest.createdAt(), newest.check(), warning);
    }

    // --- listing & deleting -------------------------------------------------------------------

    public List<BackupFile> list() {
        return listIn(settings.backupDir()).stream().map(BackupService::describe).toList();
    }

    /** Backup files of a folder, newest first. */
    private static List<Path> listIn(Path dir) {
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(BackupService::isBackup)
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** A backup in the current folder, by file name only (no paths, so nothing outside the folder). */
    Path resolveBackup(String fileName) {
        Path dir = settings.backupDir();
        Path file = dir.resolve(fileName).normalize();
        if (!file.getParent().equals(dir.normalize()) || !isBackup(file) || !Files.isRegularFile(file)) {
            throw new NotFoundException("Backup", fileName);
        }
        return file;
    }

    /** Deletes one backup in the current folder, by file name only (same rules as restore). */
    public synchronized void delete(String fileName) {
        Path file = resolveBackup(fileName);
        try {
            Files.delete(file);
            Files.deleteIfExists(checkFileOf(file));
            log.info("Backup deleted: {}", file);
        } catch (IOException e) {
            throw new BusinessRuleException("Couldn't delete " + fileName + " (" + e.getClass().getSimpleName() + ")");
        }
    }

    // --- clean-up -----------------------------------------------------------------------------

    /** The backups the current rule would delete, without deleting anything. */
    public List<BackupFile> cleanupPreview() {
        List<Path> all = listIn(settings.backupDir());
        Set<Path> keep = toKeep(all, retention());
        return all.stream().filter(p -> !keep.contains(p)).map(BackupService::describe).toList();
    }

    /** Applies the clean-up rule now, to the backup folder and the second folder. */
    public synchronized List<BackupFile> cleanup() {
        List<BackupFile> doomed = cleanupPreview();
        for (BackupFile f : doomed) {
            deleteWithCheck(Path.of(f.path()));
        }
        Path mirror = settings.mirrorDir();
        if (mirror != null) {
            try {
                prune(mirror, retention());
            } catch (IOException | UncheckedIOException e) {
                log.warn("Couldn't clean up {}: {}", mirror, e.toString());
            }
        }
        log.info("Backup clean-up removed {} file(s)", doomed.size());
        return doomed;
    }

    private static void prune(Path dir, Retention r) throws IOException {
        List<Path> all = listIn(dir);
        Set<Path> keep = toKeep(all, r);
        for (Path p : all) {
            if (!keep.contains(p)) {
                Files.deleteIfExists(p);
                Files.deleteIfExists(checkFileOf(p));
            }
        }
    }

    /**
     * Newest {@code recent}, plus the newest backup of each of the last {@code daily} days,
     * {@code weekly} ISO weeks and {@code monthly} months that have backups.
     *
     * @param newestFirst backups sorted newest first
     */
    static Set<Path> toKeep(List<Path> newestFirst, Retention r) {
        Set<Path> keep = new HashSet<>(newestFirst.subList(0, Math.min(Math.max(1, r.recent()), newestFirst.size())));
        keepOnePer(newestFirst, r.daily(), d -> d.toString(), keep);
        keepOnePer(newestFirst, r.weekly(), d -> d.get(IsoFields.WEEK_BASED_YEAR) + "-W" + d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR), keep);
        keepOnePer(newestFirst, r.monthly(), d -> d.getYear() + "-" + d.getMonthValue(), keep);
        return keep;
    }

    private static void keepOnePer(List<Path> newestFirst, int periods, Function<LocalDate, String> period, Set<Path> keep) {
        Set<String> seen = new HashSet<>();
        for (Path p : newestFirst) {
            if (seen.size() >= periods) {
                return;
            }
            if (seen.add(period.apply(dayOf(p)))) {
                keep.add(p); // the first one met is the newest of its period
            }
        }
    }

    /** The day a backup was made, from its name (copies keep the name but not always the file time). */
    static LocalDate dayOf(Path p) {
        String name = p.getFileName().toString();
        try {
            return LocalDateTime.parse(name.substring(PREFIX.length(), name.length() - ".db".length()), STAMP).toLocalDate();
        } catch (DateTimeParseException | IndexOutOfBoundsException e) {
            try {
                return LocalDate.ofInstant(Files.getLastModifiedTime(p).toInstant(), ZoneId.systemDefault());
            } catch (IOException ex) {
                return LocalDate.now();
            }
        }
    }

    @Scheduled(initialDelayString = "PT1M", fixedDelayString = "PT6H")
    public void backupIfDue() {
        if (!automatic) {
            return;
        }
        try {
            List<BackupFile> existing = list();
            boolean due = existing.isEmpty()
                    || Duration.between(existing.getFirst().createdAt(), Instant.now()).toHours() >= 20;
            if (due) {
                backupNow();
            }
        } catch (RuntimeException e) {
            log.error("Automatic backup failed", e);
        }
    }

    /**
     * Copies a backup to the second folder, if one is set, and cleans up there with the same rule.
     * A failure (e.g. the USB drive is unplugged) never fails the backup itself; it is shown on
     * the Settings page and the dashboard until the next copy succeeds.
     */
    private void mirror(Path backup) {
        Path dir = settings.mirrorDir();
        if (dir == null) {
            return;
        }
        try {
            Files.createDirectories(dir);
            Path tmp = dir.resolve(backup.getFileName() + ".part");
            Files.copy(backup, tmp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tmp, dir.resolve(backup.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            prune(dir, retention());
            mirrorError = null;
        } catch (IOException | SecurityException | UncheckedIOException e) {
            mirrorError = "Last copy to " + dir + " failed: " + e.getClass().getSimpleName()
                    + (e.getMessage() == null ? "" : " (" + e.getMessage() + ")");
            log.warn(mirrorError);
        }
    }

    private static boolean isBackup(Path p) {
        String name = p.getFileName().toString();
        return name.startsWith(PREFIX) && name.endsWith(".db");
    }

    private static BackupFile describe(Path p) {
        try {
            return new BackupFile(p.getFileName().toString(), p.toAbsolutePath().toString(), Files.size(p),
                    Files.getLastModifiedTime(p).toInstant(), readCheck(p));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** @param check result of the last check of this file; null = never checked (e.g. made by an older version) */
    public record BackupFile(String fileName, String path, long sizeBytes, Instant createdAt, BackupCheck check) {
    }

    public record BackupCheck(boolean ok, String message, int plans, int items, int notes, Instant checkedAt) {
        static BackupCheck failed(String message) {
            return new BackupCheck(false, message, 0, 0, 0, Instant.now());
        }
    }

    /**
     * @param lastBackupAt time of the newest backup (null = none)
     * @param lastCheck    check of the newest backup (null = not checked)
     * @param warning      something needing attention, or null when all is well
     */
    public record BackupStatus(Instant lastBackupAt, BackupCheck lastCheck, String warning) {
    }

    /**
     * @param directory        where backups are written now
     * @param defaultDirectory {@code <data-dir>/backups}
     * @param custom           a folder was chosen on the Settings page
     * @param keep             newest backups always kept (same as {@code retention.recent})
     * @param mirrorDirectory  second folder every backup is copied to (null = none)
     * @param mirrorError      why the last copy failed (null = fine)
     */
    public record BackupSettings(String directory, String defaultDirectory, boolean custom,
                                 boolean automatic, int keep, Retention retention, String mirrorDirectory,
                                 String mirrorError) {
    }
}
