package com.mylearn.tracker.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Settings changed from the UI, kept in {@code <data-dir>/settings.properties}.
 * A file rather than a table, so that restoring an old database doesn't also restore old settings
 * (for example, the backup folder you chose afterwards).
 */
@Component
public class AppSettings {

    private static final String BACKUP_DIR = "backup.dir";
    private static final String MIRROR_DIR = "backup.mirror.dir";

    private final Path file;
    private final Path defaultBackupDir;
    private final Properties props = new Properties();

    public AppSettings(@Value("${app.data-dir}") String dataDir) {
        Path data = Path.of(dataDir).toAbsolutePath().normalize();
        this.file = data.resolve("settings.properties");
        this.defaultBackupDir = data.resolve("backups");
        if (Files.isRegularFile(file)) {
            try (Reader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                props.load(in);
            } catch (IOException e) {
                throw new UncheckedIOException("Can't read " + file, e);
            }
        }
    }

    public Path defaultBackupDir() {
        return defaultBackupDir;
    }

    public synchronized Path backupDir() {
        String custom = props.getProperty(BACKUP_DIR);
        return custom == null || custom.isBlank() ? defaultBackupDir : Path.of(custom);
    }

    public synchronized boolean isCustomBackupDir() {
        String custom = props.getProperty(BACKUP_DIR);
        return custom != null && !custom.isBlank();
    }

    /** @param dir null resets to the default folder */
    public synchronized void setBackupDir(Path dir) {
        if (dir == null || dir.equals(defaultBackupDir)) {
            props.remove(BACKUP_DIR);
        } else {
            props.setProperty(BACKUP_DIR, dir.toString());
        }
        save();
    }

    /** Second folder every backup is also copied to (e.g. OneDrive or a USB drive); null = none. */
    public synchronized Path mirrorDir() {
        String dir = props.getProperty(MIRROR_DIR);
        return dir == null || dir.isBlank() ? null : Path.of(dir);
    }

    /** @param dir null turns copying off */
    public synchronized void setMirrorDir(Path dir) {
        if (dir == null) {
            props.remove(MIRROR_DIR);
        } else {
            props.setProperty(MIRROR_DIR, dir.toString());
        }
        save();
    }

    // --- backup retention -----------------------------------------------------------------

    /**
     * How many backups to keep: the newest {@code recent}, plus the newest of each of the last
     * {@code daily} days, {@code weekly} weeks and {@code monthly} months that have a backup.
     */
    public record Retention(int recent, int daily, int weekly, int monthly) {
    }

    public synchronized Retention retention(int defaultRecent) {
        return new Retention(intProp("backup.keep.recent", defaultRecent), intProp("backup.keep.daily", 14),
                intProp("backup.keep.weekly", 8), intProp("backup.keep.monthly", 12));
    }

    public synchronized void setRetention(Retention r) {
        props.setProperty("backup.keep.recent", String.valueOf(r.recent()));
        props.setProperty("backup.keep.daily", String.valueOf(r.daily()));
        props.setProperty("backup.keep.weekly", String.valueOf(r.weekly()));
        props.setProperty("backup.keep.monthly", String.valueOf(r.monthly()));
        save();
    }

    private int intProp(String key, int fallback) {
        try {
            String v = props.getProperty(key);
            return v == null ? fallback : Math.max(0, Integer.parseInt(v.strip()));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (Writer out = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                props.store(out, "Learning Tracker settings (edited from the Settings page)");
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Can't write " + file, e);
        }
    }
}
