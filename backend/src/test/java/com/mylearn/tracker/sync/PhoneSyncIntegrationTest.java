package com.mylearn.tracker.sync;

import com.mylearn.tracker.admin.AppSettings;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteService;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import com.mylearn.tracker.sync.SyncDtos.Change;
import com.mylearn.tracker.sync.SyncDtos.SyncRequest;
import com.mylearn.tracker.sync.SyncDtos.SyncResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Two-way phone sync (docs/SYNC_PROTOCOL.md), with this test playing the phone. */
@AutoConfigureMockMvc
class PhoneSyncIntegrationTest extends IntegrationTest {

    static final String DEVICE = "phone-1";

    @Autowired PhoneSyncService sync;
    @Autowired PlanService plans;
    @Autowired ItemService items;
    @Autowired NoteService notes;
    @Autowired AppSettings settings;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager em;
    @Autowired MockMvc mvc;

    Long planId;
    Long topicId;
    String planKey;
    String topicKey;

    @BeforeEach
    void setUp() {
        planId = plans.create(new CreatePlanRequest("Desk plan", null)).id();
        Long moduleId = items.create(planId, new CreateItemRequest("Module", ItemType.MODULE, null, null, null, null)).id();
        topicId = items.create(planId, new CreateItemRequest("Heap", ItemType.TOPIC, moduleId, null, null, null)).id();
        notes.save(topicId, "Q: What is a heap?\nA: A tree.", NoteFormat.MARKDOWN);
        em.flush();
        planKey = key("learning_plan", planId);
        topicKey = key("learning_item", topicId);
    }

    // --- pull ----------------------------------------------------------------------------------

    @Test
    void firstSyncPullsEverythingWithExternalKeys() {
        SyncResponse r = sync(0, List.of());

        assertThat(r.reset()).isFalse();
        assertThat(r.epoch()).hasSize(32);
        Change plan = find(r, "plan", planKey);
        assertThat(plan.data()).containsEntry("name", "Desk plan").containsEntry("status", "ACTIVE");
        Change topic = find(r, "item", topicKey);
        assertThat(topic.data()).containsEntry("planKey", planKey).containsEntry("type", "TOPIC")
                .containsEntry("completed", false).containsKey("parentKey");
        assertThat(topic.data().get("parentKey")).isNotNull();
        assertThat(find(r, "note", topicKey).data()).containsEntry("format", "MARKDOWN").containsEntry("itemKey", topicKey);
    }

    @Test
    void secondSyncWithTheCursorIsEmpty() {
        SyncResponse first = sync(0, List.of());
        SyncResponse second = sync(first.cursor(), List.of());

        assertThat(second.changes()).isEmpty();
        assertThat(second.cursor()).isEqualTo(first.cursor());
    }

    @Test
    void laterDesktopChangesArePulled() {
        long cursor = sync(0, List.of()).cursor();
        items.setCompletion(topicId, true);
        em.flush();

        SyncResponse r = sync(cursor, List.of());

        assertThat(find(r, "item", topicKey).data()).containsEntry("completed", true);
        assertThat(find(r, "review", topicKey).data()).containsEntry("stage", 0);
    }

    // --- push ----------------------------------------------------------------------------------

    @Test
    void phoneCreatesAPlanWithANestedTreeInAnyOrder() {
        String p = UUID.randomUUID().toString();
        String module = UUID.randomUUID().toString();
        String leaf = UUID.randomUUID().toString();
        long cursor = sync(0, List.of()).cursor();

        // child listed before its parent: applied parent-first anyway
        SyncResponse r = sync(cursor, List.of(
                item(leaf, p, module, "TOPIC", "Leaf", 0),
                item(module, p, null, "MODULE", "Phone module", 0),
                plan(p, "Phone plan")));

        assertThat(r.skipped()).isEmpty();
        assertThat(r.applied()).isEqualTo(3);
        assertThat(jdbc.queryForObject("""
                SELECT pa.name FROM learning_item i JOIN learning_item pa ON pa.id = i.parent_id WHERE i.external_id = ?""",
                String.class, leaf)).isEqualTo("Phone module");
        // not echoed back
        assertThat(r.changes()).noneMatch(c -> c.key().equals(leaf) || c.key().equals(p));
    }

    @Test
    void anOlderPhoneEditLosesToANewerDesktopEdit() {
        long cursor = sync(0, List.of()).cursor();
        Instant phoneTime = Instant.now().minusSeconds(60);
        items.update(topicId, new com.mylearn.tracker.item.dto.UpdateItemRequest("Desk name", ItemType.TOPIC, null, null,
                versionOf(topicId)));
        em.flush();

        SyncResponse r = sync(cursor, List.of(itemAt(topicKey, "Phone name", phoneTime)));

        assertThat(nameOf(topicId)).isEqualTo("Desk name");
        assertThat(r.applied()).isZero();
        assertThat(find(r, "item", topicKey).data()).containsEntry("name", "Desk name"); // the phone gets the winner
    }

    @Test
    void aNewerPhoneEditWins() {
        long cursor = sync(0, List.of()).cursor();

        SyncResponse r = sync(cursor, List.of(itemAt(topicKey, "Phone name", Instant.now().plusSeconds(5))));

        assertThat(r.applied()).isEqualTo(1);
        assertThat(nameOf(topicId)).isEqualTo("Phone name");
    }

    @Test
    void noteEditedOnBothSidesKeepsBothTexts() {
        long cursor = sync(0, List.of()).cursor();
        notes.save(topicId, "Desk text", NoteFormat.MARKDOWN);
        em.flush();

        SyncResponse r = sync(cursor, List.of(new Change("note", topicKey, InstantText.format(Instant.now().plusSeconds(5)), false,
                Map.of("itemKey", topicKey, "content", "Phone text", "format", "MARKDOWN"))));

        String merged = jdbc.queryForObject("SELECT content FROM item_note WHERE item_id = ?", String.class, topicId);
        assertThat(merged).startsWith("Phone text").contains("Conflicting copy from this computer").contains("Desk text");
        assertThat(r.conflicts()).singleElement().extracting(SyncDtos.Conflict::resolution).isEqualTo("MERGED");
        assertThat(find(r, "note", topicKey).data().get("content")).isEqualTo(merged); // pulled back to the phone
    }

    @Test
    void phoneDeletesASubtree() {
        long cursor = sync(0, List.of()).cursor();
        String moduleKey = jdbc.queryForObject("SELECT pa.external_id FROM learning_item i JOIN learning_item pa ON pa.id = i.parent_id"
                + " WHERE i.id = ?", String.class, topicId);

        sync(cursor, List.of(new Change("item", moduleKey, now(), true, null)));

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM learning_item WHERE plan_id = ?", Integer.class, planId)).isZero();
    }

    @Test
    void flashcardScheduleFollowsItsNote() {
        long cursor = sync(0, List.of()).cursor();
        String cardKey = jdbc.queryForObject("SELECT card_key FROM flashcard WHERE item_id = ?", String.class, topicId);

        SyncResponse r = sync(cursor, List.of(new Change("flashcard", topicKey + "|" + cardKey, now(), false,
                Map.of("itemKey", topicKey, "cardKey", cardKey, "stage", 2, "dueOn", "2030-01-02",
                        "lastReviewedAt", "2026-11-25T10:00:00.000Z"))));

        assertThat(r.skipped()).isEmpty();
        assertThat(jdbc.queryForMap("SELECT stage, due_on FROM flashcard WHERE item_id = ?", topicId))
                .containsEntry("stage", 2).containsEntry("due_on", "2030-01-02");
    }

    @Test
    void newChildOfACompletedTopicClearsItsCompletion() {
        items.setCompletion(topicId, true);
        em.flush();
        long cursor = sync(0, List.of()).cursor();

        SyncResponse r = sync(cursor, List.of(item(UUID.randomUUID().toString(), planKey, topicKey, "TOPIC", "Sub", 0)));

        assertThat(jdbc.queryForObject("SELECT is_completed FROM learning_item WHERE id = ?", Integer.class, topicId)).isZero();
        assertThat(find(r, "item", topicKey).data()).containsEntry("completed", false); // the repair goes back to the phone
    }

    @Test
    void movingAnItemUnderItsOwnChildIsSkipped() {
        long cursor = sync(0, List.of()).cursor();
        String child = UUID.randomUUID().toString();
        sync(cursor, List.of(item(child, planKey, topicKey, "TOPIC", "Child", 0)));
        String moduleKey = jdbc.queryForObject("SELECT pa.external_id FROM learning_item i JOIN learning_item pa ON pa.id = i.parent_id"
                + " WHERE i.id = ?", String.class, topicId);

        SyncResponse r = sync(cursor, List.of(item(topicKey, planKey, child, "TOPIC", "Heap", 0)));

        assertThat(r.skipped()).singleElement().extracting(SyncDtos.Skipped::reason).isEqualTo("CYCLE");
        assertThat(moduleKey).isNotNull();
    }

    @Test
    void invalidRowsAreSkippedWithoutFailingTheBatch() {
        long cursor = sync(0, List.of()).cursor();
        String ok = UUID.randomUUID().toString();

        SyncResponse r = sync(cursor, List.of(
                item(UUID.randomUUID().toString(), planKey, null, "BOGUS", "Bad type", 0),
                item(UUID.randomUUID().toString(), UUID.randomUUID().toString(), null, "TOPIC", "No plan", 0),
                item(ok, planKey, null, "TOPIC", "Fine", 0)));

        assertThat(r.skipped()).extracting(SyncDtos.Skipped::reason).containsExactlyInAnyOrder("INVALID", "PLAN_DELETED");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM learning_item WHERE external_id = ?", Integer.class, ok)).isEqualTo(1);
    }

    @Test
    void planNameClashGetsASuffix() {
        long cursor = sync(0, List.of()).cursor();
        String p = UUID.randomUUID().toString();

        sync(cursor, List.of(plan(p, "desk PLAN")));

        assertThat(jdbc.queryForObject("SELECT name FROM learning_plan WHERE external_id = ?", String.class, p)).isEqualTo("desk PLAN (2)");
    }

    @Test
    void resetSendsEvenWhatCameFromThisPhone() {
        long cursor = sync(0, List.of()).cursor();
        String p = UUID.randomUUID().toString();
        sync(cursor, List.of(plan(p, "From the phone")));

        // the phone wipes its data on a reset, so it needs its own rows back too
        SyncResponse r = sync.sync(new SyncRequest(1, DEVICE, "Test phone", "old-epoch", 999_999L, now(), List.of()));

        assertThat(r.reset()).isTrue();
        assertThat(r.changes()).anyMatch(c -> c.key().equals(p));
    }

    @Test
    void wrongEpochResetsThePhone() {
        SyncResponse r = sync.sync(new SyncRequest(1, DEVICE, "Test phone", "old-epoch", 999_999L, now(), List.of()));

        assertThat(r.reset()).isTrue();
        assertThat(r.changes()).anyMatch(c -> c.key().equals(planKey));
    }

    // --- HTTP: token and remote access --------------------------------------------------------

    @Test
    void syncNeedsTheToken() throws Exception {
        String body = "{\"protocol\":1,\"deviceId\":\"x\",\"since\":0,\"changes\":[]}";
        mvc.perform(post("/api/sync").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/sync").header("Authorization", "Bearer wrong").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/sync").header("Authorization", "Bearer " + settings.syncToken())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.protocol").value(1));
    }

    @Test
    void otherDevicesReachOnlyTheSyncEndpoints() throws Exception {
        mvc.perform(get("/api/plans").with(r -> { r.setRemoteAddr("192.168.1.50"); return r; }))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/sync/pairing").with(r -> { r.setRemoteAddr("192.168.1.50"); return r; }))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/sync/ping").header("Authorization", "Bearer " + settings.syncToken())
                        .with(r -> { r.setRemoteAddr("192.168.1.50"); return r; }))
                .andExpect(status().isOk()).andExpect(jsonPath("$.app").value("learning-tracker"));
        mvc.perform(get("/api/plans")).andExpect(status().isOk()); // this computer: everything
    }

    @Test
    void pairingShowsTheTokenAndCanReplaceIt() throws Exception {
        String before = settings.syncToken();
        mvc.perform(get("/api/sync/pairing")).andExpect(status().isOk()).andExpect(jsonPath("$.token").value(before));
        mvc.perform(post("/api/sync/pairing/token")).andExpect(status().isOk());
        assertThat(settings.syncToken()).isNotEqualTo(before).hasSize(43);
    }

    @Test
    void wrongProtocolIsAConflict() throws Exception {
        mvc.perform(post("/api/sync").header("Authorization", "Bearer " + settings.syncToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"protocol\":99,\"deviceId\":\"x\"}"))
                .andExpect(status().isConflict());
    }

    // --- helpers -------------------------------------------------------------------------------

    private SyncResponse sync(long since, List<Change> changes) {
        em.flush();
        SyncResponse r = sync.sync(new SyncRequest(1, DEVICE, "Test phone", sync.epoch(), since, now(), changes));
        em.clear();
        return r;
    }

    private static Change plan(String key, String name) {
        return new Change("plan", key, now(), false, Map.of("name", name, "status", "ACTIVE"));
    }

    private static Change item(String key, String plan, String parent, String type, String name, int order) {
        Map<String, Object> d = new HashMap<>();
        d.put("planKey", plan);
        d.put("parentKey", parent);
        d.put("type", type);
        d.put("name", name);
        d.put("completed", false);
        d.put("displayOrder", order);
        return new Change("item", key, now(), false, d);
    }

    private Change itemAt(String key, String name, Instant at) {
        Map<String, Object> d = new HashMap<>(sync.sync(new SyncRequest(1, "peek", null, null, 0L, now(), List.of()))
                .changes().stream().filter(c -> c.key().equals(key) && c.entity().equals("item")).findFirst().orElseThrow().data());
        d.put("name", name);
        return new Change("item", key, InstantText.format(at), false, d);
    }

    private static Change find(SyncResponse r, String entity, String key) {
        return r.changes().stream().filter(c -> c.entity().equals(entity) && c.key().equals(key)).findFirst()
                .orElseThrow(() -> new AssertionError("no " + entity + " " + key + " in " + r.changes()));
    }

    private String key(String table, Long id) {
        return jdbc.queryForObject("SELECT external_id FROM " + table + " WHERE id = ?", String.class, id);
    }

    private String nameOf(Long id) {
        return jdbc.queryForObject("SELECT name FROM learning_item WHERE id = ?", String.class, id);
    }

    private long versionOf(Long id) {
        return jdbc.queryForObject("SELECT version FROM learning_item WHERE id = ?", Long.class, id);
    }

    private static String now() {
        return InstantText.format(Instant.now());
    }
}
