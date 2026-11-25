package com.mylearn.tracker.note;

import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Start-up skips the full note re-index unless the parser or schema version changed. */
class NoteIndexesIntegrationTest extends IntegrationTest {

    @Autowired
    NoteIndexes noteIndexes;
    @Autowired
    PlanService plans;
    @Autowired
    ItemService items;
    @Autowired
    JdbcTemplate jdbc;

    Long topicId;

    @BeforeEach
    void setUp() {
        Long planId = plans.create(new CreatePlanRequest("Index plan", null)).id();
        topicId = items.create(planId, new CreateItemRequest("Topic", ItemType.TOPIC, null, null, null, null)).id();
    }

    @Test
    void startupRecordsTheCurrentFingerprint() {
        assertThat(storedFingerprint()).containsExactly(noteIndexes.fingerprint());
        assertThat(noteIndexes.fingerprint()).startsWith("v" + NoteIndexes.INDEX_VERSION + "/schema ");
    }

    @Test
    void upToDateFingerprintSkipsTheRebuild() {
        insertNoteBypassingService();

        noteIndexes.rebuildIfStale();

        assertThat(flashcardCount()).isZero();
        assertThat(searchNoteText()).isEmpty();
    }

    @Test
    void staleFingerprintRebuildsEverythingDerivedFromNotes() {
        insertNoteBypassingService();
        jdbc.update("UPDATE app_meta SET value = 'v0/schema 5' WHERE key = ?", NoteIndexes.FINGERPRINT_KEY);

        noteIndexes.rebuildIfStale();

        assertThat(flashcardCount()).isEqualTo(1);
        assertThat(searchNoteText()).contains("What is a heap");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM note_ref WHERE source_item_id = ?", Integer.class, topicId))
                .isEqualTo(1);
        assertThat(storedFingerprint()).containsExactly(noteIndexes.fingerprint());
    }

    @Test
    void missingFingerprintRebuilds() {
        insertNoteBypassingService();
        jdbc.update("DELETE FROM app_meta WHERE key = ?", NoteIndexes.FINGERPRINT_KEY);

        noteIndexes.rebuildIfStale();

        assertThat(flashcardCount()).isEqualTo(1);
        assertThat(storedFingerprint()).containsExactly(noteIndexes.fingerprint());
    }

    /** A note written straight to the table, as an old backup would contain it: nothing derived yet. */
    private void insertNoteBypassingService() {
        jdbc.update("INSERT INTO item_note (item_id, content, updated_at, format) VALUES (?, ?, ?, 'MARKDOWN')",
                topicId, "See [[Other topic]].\n\nQ: What is a heap?\nA: A tree-shaped priority queue.",
                "2026-11-23T00:00:00.000Z");
    }

    private int flashcardCount() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM flashcard WHERE item_id = ?", Integer.class, topicId);
    }

    private String searchNoteText() {
        return jdbc.queryForObject("SELECT note FROM search_index WHERE rowid = ?", String.class, topicId);
    }

    private List<String> storedFingerprint() {
        return jdbc.queryForList("SELECT value FROM app_meta WHERE key = ?", String.class, NoteIndexes.FINGERPRINT_KEY);
    }
}
