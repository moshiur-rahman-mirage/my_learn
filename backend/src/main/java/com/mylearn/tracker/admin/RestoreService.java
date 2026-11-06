package com.mylearn.tracker.admin;

import com.mylearn.tracker.admin.BackupService.BackupFile;
import com.mylearn.tracker.common.exception.BusinessRuleException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteConfig;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Replaces the live database with a backup <b>while the app keeps running</b>:
 * <ol>
 *   <li>check the file: a readable SQLite database, passes {@code quick_check}, has our tables,
 *       and was not made by a newer app version</li>
 *   <li>take a safety backup of the current data</li>
 *   <li>copy the backup into the live database with SQLite's online backup API
 *       ({@code restore from}); the single pooled connection is held meanwhile, so no request
 *       sees a half-restored database</li>
 *   <li>run Flyway, so a backup from an older version gets the newer schema</li>
 * </ol>
 */
@Service
public class RestoreService {

    private static final Logger log = LoggerFactory.getLogger(RestoreService.class);
    private static final Set<String> REQUIRED_TABLES = Set.of("learning_plan", "learning_item", "flyway_schema_history");

    private final DataSource dataSource;
    private final BackupService backups;
    private final Flyway flyway;
    private final JdbcTemplate jdbc;
    private final Path dataDir;

    public RestoreService(DataSource dataSource, BackupService backups, Flyway flyway, JdbcTemplate jdbc,
                          @Value("${app.data-dir}") String dataDir) {
        this.dataSource = dataSource;
        this.backups = backups;
        this.flyway = flyway;
        this.jdbc = jdbc;
        this.dataDir = Path.of(dataDir).toAbsolutePath().normalize();
    }

    public RestoreResult restoreBackup(String fileName) {
        Path file = backups.resolveBackup(fileName);
        // work on a copy: the source may be pruned by the safety backup we take first
        return withTempCopy(in -> Files.copy(file, in, StandardCopyOption.REPLACE_EXISTING), fileName);
    }

    public RestoreResult restoreUpload(String originalName, InputStream content) {
        return withTempCopy(in -> Files.copy(content, in, StandardCopyOption.REPLACE_EXISTING),
                originalName == null ? "uploaded file" : originalName);
    }

    private RestoreResult withTempCopy(Copier copier, String label) {
        Path temp = dataDir.resolve("restore-" + UUID.randomUUID() + ".db");
        try {
            Files.createDirectories(dataDir);
            copier.copy(temp);
            return restore(temp, label);
        } catch (IOException e) {
            throw new UncheckedIOException("Can't read the backup file", e);
        } finally {
            for (String suffix : new String[]{"", "-wal", "-shm", "-journal"}) {
                Path f = temp.resolveSibling(temp.getFileName() + suffix);
                try {
                    Files.deleteIfExists(f);
                } catch (IOException e) {
                    log.warn("Couldn't delete temporary file {}", f);
                }
            }
        }
    }

    private synchronized RestoreResult restore(Path file, String label) {
        String path = file.toAbsolutePath().toString();
        if (path.contains("'") || path.contains("\"")) {
            throw new BusinessRuleException("The data folder path contains a quote character, which restore can't handle: " + dataDir);
        }
        validate(file);

        BackupFile safety = backups.backupNow();

        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("restore from '" + path + "'");
        } catch (SQLException e) {
            throw new IllegalStateException("Restore failed; your data is unchanged. Safety backup: " + safety.fileName(), e);
        }
        flyway.migrate();

        Integer plans = jdbc.queryForObject("SELECT COUNT(*) FROM learning_plan", Integer.class);
        Integer items = jdbc.queryForObject("SELECT COUNT(*) FROM learning_item", Integer.class);
        log.info("Restored database from {} ({} plans, {} items); safety backup {}", label, plans, items, safety.fileName());
        return new RestoreResult(label, safety.fileName(), plans == null ? 0 : plans, items == null ? 0 : items);
    }

    /** Opens the file read-only, separately from the live database. */
    void validate(Path file) {
        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);
        try (Connection c = config.createConnection("jdbc:sqlite:" + file.toAbsolutePath());
             Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery("PRAGMA quick_check")) {
                if (!rs.next() || !"ok".equalsIgnoreCase(rs.getString(1))) {
                    throw new BusinessRuleException("This backup file is damaged (integrity check failed).");
                }
            }
            Set<String> tables = new HashSet<>();
            try (ResultSet rs = s.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'")) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }
            if (!tables.containsAll(REQUIRED_TABLES)) {
                throw new BusinessRuleException("This file is an SQLite database, but not a Learning Tracker backup.");
            }
            MigrationVersion newestInFile = MigrationVersion.EMPTY;
            try (ResultSet rs = s.executeQuery(
                    "SELECT version FROM flyway_schema_history WHERE success = 1 AND version IS NOT NULL")) {
                while (rs.next()) {
                    MigrationVersion v = MigrationVersion.fromVersion(rs.getString(1));
                    if (v.isNewerThan(newestInFile.getVersion())) {
                        newestInFile = v;
                    }
                }
            }
            MigrationVersion supported = newestKnownMigration();
            if (newestInFile.isNewerThan(supported.getVersion())) {
                throw new BusinessRuleException("This backup was made by a newer version of Learning Tracker (schema "
                        + newestInFile + "). Update the app first.");
            }
        } catch (SQLException e) {
            throw new BusinessRuleException("This file isn't a Learning Tracker backup (not a readable SQLite database).");
        }
    }

    private MigrationVersion newestKnownMigration() {
        return Arrays.stream(flyway.info().all())
                .map(MigrationInfo::getVersion)
                .filter(v -> v != null)
                .max(Comparator.naturalOrder())
                .orElse(MigrationVersion.EMPTY);
    }

    @FunctionalInterface
    private interface Copier {
        void copy(Path target) throws IOException;
    }

    /**
     * @param restoredFrom file the data now comes from
     * @param safetyBackup backup of the data as it was just before the restore (to undo it)
     */
    public record RestoreResult(String restoredFrom, String safetyBackup, int plans, int items) {
    }
}
