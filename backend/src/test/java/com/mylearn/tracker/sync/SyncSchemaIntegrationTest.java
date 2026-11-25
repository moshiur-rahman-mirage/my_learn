package com.mylearn.tracker.sync;

import com.mylearn.tracker.confidence.ConfidenceService;
import com.mylearn.tracker.flashcard.FlashcardService;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.link.LinkService;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteService;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import com.mylearn.tracker.tag.TagService;
import com.mylearn.tracker.timelog.TimeLogService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** V7: every write path fills external ids and records one sync_change line per row. */
class SyncSchemaIntegrationTest extends IntegrationTest {

    @Autowired PlanService plans;
    @Autowired ItemService items;
    @Autowired NoteService notes;
    @Autowired LinkService links;
    @Autowired TimeLogService timeLog;
    @Autowired TagService tags;
    @Autowired ConfidenceService confidence;
    @Autowired FlashcardService flashcards;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager em;

    Long planId;
    Long moduleId;
    Long topicId;
    String planKey;
    String topicKey;

    @BeforeEach
    void setUp() {
        planId = plans.create(new CreatePlanRequest("Sync plan", null)).id();
        moduleId = items.create(planId, new CreateItemRequest("Module", ItemType.MODULE, null, null, null, null)).id();
        topicId = items.create(planId, new CreateItemRequest("Topic", ItemType.TOPIC, moduleId, null, null, null)).id();
        em.flush();
        planKey = jdbc.queryForObject("SELECT external_id FROM learning_plan WHERE id = ?", String.class, planId);
        topicKey = jdbc.queryForObject("SELECT external_id FROM learning_item WHERE id = ?", String.class, topicId);
    }

    @Test
    void newPlanGetsAUuidAndAChangeLine() {
        assertThat(planKey).matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
        assertThat(line("plan", planKey)).containsEntry("deleted", 0);
        assertThat(line("item", topicKey)).isNotNull();
    }

    @Test
    void changeLinesUseMillisecondUtcTimestamps() {
        assertThat((String) line("plan", planKey).get("changed_at")).matches("\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d{3}Z");
    }

    @Test
    void oneLinePerRowWithTheNewestSeq() {
        long first = seq("item", topicKey);
        items.setCompletion(topicId, true);
        em.flush();

        assertThat(seq("item", topicKey)).isGreaterThan(first);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sync_change WHERE entity = 'item' AND key = ?",
                Integer.class, topicKey)).isEqualTo(1);
        // completing a topic schedules its review
        assertThat(line("review", topicKey)).containsEntry("deleted", 0);
    }

    @Test
    void notesLinksTimeTagsAndConfidenceAreLogged() {
        notes.save(topicId, "Q: What is a heap?\nA: A tree.", NoteFormat.MARKDOWN);
        links.add(topicId, "example.com/docs", null);
        timeLog.log(topicId, 25);
        tags.setTags(topicId, List.of("Interview"));
        confidence.rate(topicId, 4);
        em.flush();

        String linkKey = jdbc.queryForObject("SELECT external_id FROM item_link WHERE item_id = ?", String.class, topicId);
        String logKey = jdbc.queryForObject("SELECT external_id FROM time_log WHERE item_id = ?", String.class, topicId);
        assertThat(linkKey).isNotNull();
        assertThat(logKey).isNotNull();
        assertThat(line("note", topicKey)).isNotNull();
        assertThat(line("link", linkKey)).isNotNull();
        assertThat(line("timeLog", logKey)).isNotNull();
        assertThat(line("itemTag", topicKey + "|interview")).isNotNull();
        assertThat(line("confidence", topicKey)).isNotNull();
    }

    @Test
    void flashcardsAreLoggedOnlyWhenTheirScheduleChanges() {
        notes.save(topicId, "Q: What is a heap?\nA: A tree.", NoteFormat.MARKDOWN);
        em.flush();
        Map<String, Object> card = jdbc.queryForMap("SELECT id, card_key FROM flashcard WHERE item_id = ?", topicId);
        String key = topicKey + "|" + card.get("card_key");
        assertThat(lines("flashcard", key)).isEmpty();

        flashcards.review(((Number) card.get("id")).longValue(), true);

        assertThat(line("flashcard", key)).containsEntry("deleted", 0);
    }

    @Test
    void deletingANoteLeavesATombstone() {
        notes.save(topicId, "Some text", NoteFormat.MARKDOWN);
        notes.save(topicId, "", NoteFormat.MARKDOWN);
        em.flush();

        assertThat(line("note", topicKey)).containsEntry("deleted", 1);
    }

    @Test
    void deletingAnItemLeavesTombstonesForItAndItsSubtree() {
        String moduleKey = jdbc.queryForObject("SELECT external_id FROM learning_item WHERE id = ?", String.class, moduleId);
        items.delete(moduleId);
        em.flush();

        assertThat(line("item", moduleKey)).containsEntry("deleted", 1);
        assertThat(line("item", topicKey)).containsEntry("deleted", 1);
    }

    @Test
    void deletingAPlanLeavesATombstone() {
        plans.delete(planId);
        em.flush();

        assertThat(line("plan", planKey)).containsEntry("deleted", 1);
        assertThat(line("item", topicKey)).containsEntry("deleted", 1);
    }

    @Test
    void theDatabaseHasASyncEpoch() {
        assertThat(jdbc.queryForObject("SELECT value FROM app_meta WHERE key = 'sync_epoch'", String.class)).hasSize(32);
    }

    private Map<String, Object> line(String entity, String key) {
        List<Map<String, Object>> found = lines(entity, key);
        assertThat(found).as("sync_change line for %s %s", entity, key).hasSize(1);
        return found.getFirst();
    }

    private List<Map<String, Object>> lines(String entity, String key) {
        em.flush();
        return jdbc.queryForList("SELECT seq, changed_at, deleted, origin FROM sync_change WHERE entity = ? AND key = ?",
                entity, key);
    }

    private long seq(String entity, String key) {
        return ((Number) line(entity, key).get("seq")).longValue();
    }
}
