package com.mylearn.tracker.note;

import com.mylearn.tracker.search.SearchIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Owns the full rebuild of everything derived from notes: search text ({@link SearchIndexer})
 * plus flashcards and [[link]] references ({@link NoteDerivedIndexer}).
 *
 * <p>Saving a note (every write path goes through {@link NoteService#save}) already keeps its own
 * derived rows up to date, so a full rebuild is only needed when that bookkeeping may be stale:
 * <ul>
 *   <li>after a restore (the caller runs {@link #rebuildAll()});</li>
 *   <li>after a schema migration;</li>
 *   <li>after the parsing rules change (bump {@link #INDEX_VERSION}).</li>
 * </ul>
 * Start-up compares a fingerprint of the last two with the one stored in {@code app_meta} and
 * skips the rebuild when they match. With ~1,500 notes the rebuild took about 6 s, and it grows
 * with every note.
 */
@Component
public class NoteIndexes {

    /**
     * Bump this whenever the output of {@code FlashcardParser}, {@link NoteLinks} or
     * {@code HtmlText.toPlainText} changes for the same note, so the next start re-derives all
     * notes with the new rules.
     */
    static final int INDEX_VERSION = 1;

    static final String FINGERPRINT_KEY = "note_index_fingerprint";

    private static final Logger log = LoggerFactory.getLogger(NoteIndexes.class);

    private final JdbcTemplate jdbc;
    private final SearchIndexer searchIndex;
    private final NoteDerivedIndexer derivedIndex;

    public NoteIndexes(JdbcTemplate jdbc, SearchIndexer searchIndex, NoteDerivedIndexer derivedIndex) {
        this.jdbc = jdbc;
        this.searchIndex = searchIndex;
        this.derivedIndex = derivedIndex;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void rebuildIfStale() {
        String expected = fingerprint();
        String stored = storedFingerprint();
        if (expected.equals(stored)) {
            log.info("Note indexes are up to date ({}); skipping the rebuild", expected);
            return;
        }
        log.info("Rebuilding note indexes ({} -> {})", stored == null ? "none" : stored, expected);
        rebuildAll();
    }

    /** Re-derives search text, flashcards and links from every note and records the fingerprint. */
    @Transactional
    public void rebuildAll() {
        long start = System.nanoTime();
        searchIndex.reindexNotes();
        derivedIndex.rebuild();
        jdbc.update("INSERT INTO app_meta (key, value) VALUES (?, ?) "
                + "ON CONFLICT (key) DO UPDATE SET value = excluded.value", FINGERPRINT_KEY, fingerprint());
        log.info("Note indexes rebuilt in {} ms", (System.nanoTime() - start) / 1_000_000);
    }

    /**
     * Parser version + current schema version, e.g. {@code "v1/schema 6"}. Read with SQL, not
     * {@code Flyway.info()}: Flyway would ask the pool for a second connection, and the pool has
     * only one (held by the surrounding transaction).
     */
    String fingerprint() {
        List<String> versions = jdbc.queryForList("SELECT version FROM flyway_schema_history "
                + "WHERE success = 1 AND version IS NOT NULL ORDER BY installed_rank DESC LIMIT 1", String.class);
        return "v" + INDEX_VERSION + "/schema " + (versions.isEmpty() ? "?" : versions.get(0));
    }

    private String storedFingerprint() {
        List<String> rows = jdbc.queryForList("SELECT value FROM app_meta WHERE key = ?", String.class, FINGERPRINT_KEY);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
