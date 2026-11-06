package com.mylearn.tracker.admin;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Consistent online backups using SQLite's {@code VACUUM INTO}. It produces a clean, standalone
 * copy even while the app is running, which is safer than copying the live WAL-mode files.
 *
 * <p>Backups go to the folder chosen on the Settings page ({@link AppSettings}), by default
 * {@code <data-dir>/backups}. An automatic backup is taken when the newest one is older than
 * 20 hours (checked shortly after start-up and then every 6 hours, which suits a laptop that is
 * not always on). The newest {@code app.backup.keep} files are kept.
 */
@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final String PREFIX = "learning-tracker-";

    private final JdbcTemplate jdbc;
    private final AppSettings settings;
    private final int keep;
    private final boolean automatic;

    public BackupService(JdbcTemplate jdbc, AppSettings settings,
                         @Value("${app.backup.keep:14}") int keep,
                         @Value("${app.backup.automatic:true}") boolean automatic) {
        this.jdbc = jdbc;
        this.settings = settings;
        this.keep = keep;
        this.automatic = automatic;
    }

    private volatile String mirrorError;

    public BackupSettings settings() {
        Path mirror = settings.mirrorDir();
        return new BackupSettings(settings.backupDir().toString(), settings.defaultBackupDir().toString(),
                settings.isCustomBackupDir(), automatic, keep, mirror == null ? null : mirror.toString(),
                mirror == null ? null : mirrorError);
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

    /** Creates a backup now; must not run inside a transaction (VACUUM can't). */
    public synchronized BackupFile backupNow() {
        Path dir = settings.backupDir();
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(PREFIX + STAMP.format(LocalDateTime.now()) + ".db");
            while (Files.exists(target)) { // names sort by time, so never invent a non-time suffix
                Thread.onSpinWait();
                target = dir.resolve(PREFIX + STAMP.format(LocalDateTime.now()) + ".db");
            }
            jdbc.execute("VACUUM INTO '" + target.toAbsolutePath().toString().replace("'", "''") + "'");
            prune(dir);
            log.info("Backup written to {}", target.toAbsolutePath());
            mirror(target);
            return describe(target);
        } catch (IOException e) {
            throw new UncheckedIOException("Backup failed: can't write to " + dir, e);
        }
    }

    public List<BackupFile> list() {
        Path dir = settings.backupDir();
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(BackupService::isBackup)
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .map(BackupService::describe)
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
            log.info("Backup deleted: {}", file);
        } catch (IOException e) {
            throw new BusinessRuleException("Couldn't delete " + fileName + " (" + e.getClass().getSimpleName() + ")");
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
     * Copies a backup to the second folder, if one is set, and keeps the newest {@code keep}
     * there too. A failure (e.g. the USB drive is unplugged) never fails the backup itself;
     * it is shown on the Settings page until the next copy succeeds.
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
            try (Stream<Path> files = Files.list(dir)) {
                List<Path> backups = files.filter(BackupService::isBackup)
                        .sorted(Comparator.comparing(Path::getFileName).reversed()).toList();
                for (Path old : backups.subList(Math.min(keep, backups.size()), backups.size())) {
                    Files.deleteIfExists(old);
                }
            }
            mirrorError = null;
        } catch (IOException | SecurityException e) {
            mirrorError = "Last copy to " + dir + " failed: " + e.getClass().getSimpleName()
                    + (e.getMessage() == null ? "" : " (" + e.getMessage() + ")");
            log.warn(mirrorError);
        }
    }

    private void prune(Path dir) throws IOException {
        List<BackupFile> all = list();
        for (BackupFile old : all.subList(Math.min(keep, all.size()), all.size())) {
            Files.deleteIfExists(dir.resolve(old.fileName()));
        }
    }

    private static boolean isBackup(Path p) {
        String name = p.getFileName().toString();
        return name.startsWith(PREFIX) && name.endsWith(".db");
    }

    private static BackupFile describe(Path p) {
        try {
            return new BackupFile(p.getFileName().toString(), p.toAbsolutePath().toString(), Files.size(p),
                    Files.getLastModifiedTime(p).toInstant());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public record BackupFile(String fileName, String path, long sizeBytes, Instant createdAt) {
    }

    /**
     * @param directory        where backups are written now
     * @param defaultDirectory {@code <data-dir>/backups}
     * @param custom           a folder was chosen on the Settings page
     * @param mirrorDirectory  second folder every backup is copied to (null = none)
     * @param mirrorError      why the last copy failed (null = fine)
     */
    public record BackupSettings(String directory, String defaultDirectory, boolean custom,
                                 boolean automatic, int keep, String mirrorDirectory, String mirrorError) {
    }
}
