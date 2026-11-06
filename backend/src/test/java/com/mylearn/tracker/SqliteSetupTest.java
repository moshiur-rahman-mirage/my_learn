package com.mylearn.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies the SQLite connection is configured the way the rest of the app relies on. */
@SpringBootTest
class SqliteSetupTest {

    // Not @TempDir: on Windows the cached Spring context keeps the SQLite file open,
    // so JUnit cannot delete it. target/ is cleaned by `mvn clean` instead.
    static final Path tempDir = Path.of("target", "test-data", UUID.randomUUID().toString());

    @DynamicPropertySource
    static void dataDir(DynamicPropertyRegistry registry) {
        // A nested, not-yet-existing folder also checks that the data directory is created.
        registry.add("app.data-dir", () -> tempDir.resolve("nested/data").toString());
    }

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void databaseFileIsCreatedInDataDir() {
        assertThat(Files.exists(tempDir.resolve("nested/data/learning-tracker.db"))).isTrue();
    }

    @Test
    void foreignKeysAreEnforced() {
        assertThat(jdbc.queryForObject("PRAGMA foreign_keys", Integer.class)).isEqualTo(1);
    }

    @Test
    void walJournalModeIsActive() {
        assertThat(jdbc.queryForObject("PRAGMA journal_mode", String.class)).isEqualToIgnoringCase("wal");
    }

    @Test
    void recursiveCtesAreSupported() {
        Integer sum = jdbc.queryForObject("""
                WITH RECURSIVE n(x) AS (SELECT 1 UNION ALL SELECT x + 1 FROM n WHERE x < 4)
                SELECT SUM(x) FROM n
                """, Integer.class);
        assertThat(sum).isEqualTo(10);
    }
}
