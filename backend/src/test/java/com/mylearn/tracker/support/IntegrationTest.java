package com.mylearn.tracker.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Base for integration tests: a real Spring context on a real SQLite file (Flyway-migrated).
 * All subclasses share one context and one database file; every test runs in a transaction
 * that is rolled back, so tests don't see each other's data.
 */
@SpringBootTest
@Transactional
public abstract class IntegrationTest {

    // Under target/ (not @TempDir): on Windows the open SQLite file can't be deleted mid-run.
    private static final Path DATA_DIR = Path.of("target", "test-data", "it-" + UUID.randomUUID());

    @DynamicPropertySource
    static void sqliteDataDir(DynamicPropertyRegistry registry) {
        registry.add("app.data-dir", DATA_DIR::toString);
        registry.add("app.backup.automatic", () -> "false");
    }
}
