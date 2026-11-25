package com.mylearn.tracker.sync;

import com.mylearn.tracker.common.HtmlText;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.flashcard.FlashcardService;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteLinks;
import com.mylearn.tracker.search.SearchIndexer;
import com.mylearn.tracker.sync.SyncDtos.Change;
import com.mylearn.tracker.sync.SyncDtos.Conflict;
import com.mylearn.tracker.sync.SyncDtos.Skipped;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Applies a phone's changes to this database, last-writer-wins per row
 * (docs/SYNC_PROTOCOL.md §3, steps 3–5). Runs inside the caller's transaction and writes with
 * plain JDBC so that the exact values from the phone are stored.
 */
@Component
class SyncApplier {

    static final Set<String> PRIORITIES = Set.of("MUST", "SHOULD", "NICE");

    private final JdbcTemplate jdbc;
    private final SearchIndexer searchIndex;
    private final FlashcardService flashcards;
    private final NoteLinks noteLinks;

    SyncApplier(JdbcTemplate jdbc, SearchIndexer searchIndex, FlashcardService flashcards, NoteLinks noteLinks) {
        this.jdbc = jdbc;
        this.searchIndex = searchIndex;
        this.flashcards = flashcards;
        this.noteLinks = noteLinks;
    }

    /** Context and outcome of one apply run. */
    static final class Run {
        final long since;
        final long skewMillis;
        final String deviceId;
        final String deviceName;
        int applied;
        final List<Skipped> skipped = new ArrayList<>();
        final List<Conflict> conflicts = new ArrayList<>();
        /** Lines whose incoming change won: their changed_at becomes the (shifted) phone time. */
        final List<Winner> winners = new ArrayList<>();
        /** Notes merged here: the phone must pull them back. */
        final List<String> mergedNotes = new ArrayList<>();
        /** (planId, parentId) sibling groups whose order must be renumbered. */
        final Set<List<Long>> touchedParents = new LinkedHashSet<>();

        Run(long since, long skewMillis, String deviceId, String deviceName) {
            this.since = since;
            this.skewMillis = skewMillis;
            this.deviceId = deviceId;
            this.deviceName = deviceName;
        }
    }

    record Winner(String entity, String key, String changedAt) {
    }

    /** A rejected change; becomes an entry of {@code skipped}. */
    private static final class Skip extends RuntimeException {
        final String reason;

        Skip(String reason) {
            super(reason, null, false, false);
            this.reason = reason;
        }
    }

    void apply(List<Change> changes, Run run) {
        // upserts in parent-first order, then deletes in reverse order (a moved item escapes its old
        // parent before that parent is deleted)
        for (String entity : SyncKeys.ENTITY_ORDER) {
            List<Change> upserts = changes.stream().filter(c -> entity.equals(c.entity()) && !c.deleted()).toList();
            if (entity.equals("item")) {
                applyItemsParentFirst(upserts, run);
            } else {
                upserts.forEach(c -> applyOne(c, run));
            }
        }
        for (String entity : SyncKeys.ENTITY_ORDER.reversed()) {
            changes.stream().filter(c -> entity.equals(c.entity()) && c.deleted()).forEach(c -> applyOne(c, run));
        }
        changes.stream().filter(c -> !SyncKeys.ENTITY_ORDER.contains(c.entity()))
                .forEach(c -> run.skipped.add(new Skipped(c.entity(), c.key(), "INVALID")));
    }

    /** Items whose parent arrives later in the same batch are retried until no more progress is made. */
    private void applyItemsParentFirst(List<Change> items, Run run) {
        List<Change> pending = new ArrayList<>(items);
        boolean progress = true;
        while (!pending.isEmpty() && progress) {
            progress = false;
            List<Change> retry = new ArrayList<>();
            for (Change c : pending) {
                String parentKey = str(c.data(), "parentKey");
                boolean parentWaiting = parentKey != null && itemId(parentKey) == null
                        && pending.stream().anyMatch(p -> p != c && parentKey.equals(p.key()));
                if (parentWaiting) {
                    retry.add(c);
                } else {
                    applyOne(c, run);
                    progress = true;
                }
            }
            pending = retry;
        }
        pending.forEach(c -> run.skipped.add(new Skipped(c.entity(), c.key(), "PARENT_DELETED"))); // a cycle within the batch
    }

    private void applyOne(Change c, Run run) {
        try {
            if (c.key() == null || c.key().isBlank() || (!c.deleted() && c.data() == null)) {
                throw new Skip("INVALID");
            }
            Instant shifted = shiftedTime(c, run);
            Line line = line(c.entity(), c.key());
            boolean incomingWins = line == null || !Instant.parse(line.changedAt).isAfter(shifted);
            if (c.entity().equals("note")) {
                applyNote(c, line, incomingWins, shifted, run);
                return;
            }
            if (!incomingWins) {
                return; // this device has a newer change; the phone pulls it
            }
            switch (c.entity()) {
                case "plan" -> plan(c);
                case "item" -> item(c, run);
                case "link" -> link(c);
                case "timeLog" -> timeLog(c);
                case "itemTag" -> itemTag(c);
                case "review" -> review(c);
                case "confidence" -> confidence(c);
                case "flashcard" -> flashcard(c);
                default -> throw new Skip("INVALID");
            }
            win(c, shifted, run);
        } catch (Skip s) {
            run.skipped.add(new Skipped(c.entity(), c.key(), s.reason));
        } catch (IllegalArgumentException | ClassCastException | DateTimeParseException | NullPointerException e) {
            run.skipped.add(new Skipped(c.entity(), c.key(), "INVALID"));
        }
    }

    private void win(Change c, Instant shifted, Run run) {
        run.applied++;
        run.winners.add(new Winner(c.entity(), c.key(), InstantText.format(shifted)));
    }

    private static Instant shiftedTime(Change c, Run run) {
        Instant at = c.changedAt() == null ? Timestamps.now() : Instant.parse(c.changedAt());
        return at.plusMillis(run.skewMillis);
    }

    // --- entities -----------------------------------------------------------------------------

    private void plan(Change c) {
        Long id = planId(c.key());
        if (c.deleted()) {
            if (id != null) {
                jdbc.update("DELETE FROM learning_plan WHERE id = ?", id);
            }
            return;
        }
        Map<String, Object> d = c.data();
        String name = requireText(d, "name", 200);
        String status = str(d, "status") == null ? "ACTIVE" : str(d, "status");
        if (!status.equals("ACTIVE") && !status.equals("ARCHIVED")) {
            throw new Skip("INVALID");
        }
        String targetDate = date(d, "targetDate");
        name = freePlanName(name, c.key());
        String now = InstantText.format(Timestamps.now());
        if (id == null) {
            jdbc.update("""
                            INSERT INTO learning_plan (external_id, name, description, status, target_date, version, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, 0, ?, ?)""",
                    c.key(), name, str(d, "description"), status, targetDate, orDefault(str(d, "createdAt"), now),
                    orDefault(str(d, "updatedAt"), now));
        } else {
            jdbc.update("""
                            UPDATE learning_plan SET name = ?, description = ?, status = ?, target_date = ?, updated_at = ?,
                                   version = version + 1
                            WHERE id = ?""",
                    name, str(d, "description"), status, targetDate, orDefault(str(d, "updatedAt"), now), id);
        }
    }

    /** Plan names are unique regardless of case: a clash with another plan gets " (2)", " (3)", … */
    private String freePlanName(String name, String key) {
        String candidate = name;
        for (int n = 2; taken(candidate, key); n++) {
            candidate = name + " (" + n + ")";
        }
        return candidate;
    }

    private boolean taken(String name, String key) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM learning_plan WHERE name = ? COLLATE NOCASE AND external_id <> ?",
                Integer.class, name, key);
        return n != null && n > 0;
    }

    private void item(Change c, Run run) {
        Map<String, Object> existing = jdbc.queryForList("SELECT id, plan_id, parent_id FROM learning_item WHERE external_id = ?",
                c.key()).stream().findFirst().orElse(null);
        if (c.deleted()) {
            if (existing != null) {
                run.touchedParents.add(parentGroup(existing.get("plan_id"), existing.get("parent_id")));
                jdbc.update("DELETE FROM learning_item WHERE id = ?", existing.get("id"));
            }
            return;
        }
        Map<String, Object> d = c.data();
        Long planId = planId(str(d, "planKey"));
        if (planId == null) {
            throw new Skip("PLAN_DELETED");
        }
        Long parentId = null;
        String parentKey = str(d, "parentKey");
        if (parentKey != null) {
            Map<String, Object> parent = jdbc.queryForList("SELECT id, plan_id FROM learning_item WHERE external_id = ?", parentKey)
                    .stream().findFirst().orElseThrow(() -> new Skip("PARENT_DELETED"));
            parentId = ((Number) parent.get("id")).longValue();
            if (((Number) parent.get("plan_id")).longValue() != planId) {
                throw new Skip("INVALID");
            }
        }
        String type = str(d, "type");
        if (!"MODULE".equals(type) && !"TOPIC".equals(type)) {
            throw new Skip("INVALID");
        }
        String priority = str(d, "priority");
        if (priority != null && !PRIORITIES.contains(priority)) {
            throw new Skip("INVALID");
        }
        String name = requireText(d, "name", 300);
        boolean completed = Boolean.TRUE.equals(d.get("completed"));
        int order = Math.max(0, intVal(d, "displayOrder", 0));
        String now = InstantText.format(Timestamps.now());

        if (existing == null) {
            jdbc.update("""
                            INSERT INTO learning_item (external_id, plan_id, parent_id, item_type, name, description, priority,
                                                       is_completed, completed_at, display_order, version, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?)""",
                    c.key(), planId, parentId, type, name, str(d, "description"), priority, completed ? 1 : 0,
                    completed ? str(d, "completedAt") : null, order, orDefault(str(d, "createdAt"), now),
                    orDefault(str(d, "updatedAt"), now));
        } else {
            long id = ((Number) existing.get("id")).longValue();
            if (((Number) existing.get("plan_id")).longValue() != planId) {
                throw new Skip("INVALID"); // moving between plans isn't supported
            }
            if (parentId != null && (parentId == id || ancestors(parentId).contains(id))) {
                throw new Skip("CYCLE"); // both sides moved items into each other
            }
            run.touchedParents.add(parentGroup(existing.get("plan_id"), existing.get("parent_id")));
            jdbc.update("""
                            UPDATE learning_item SET parent_id = ?, item_type = ?, name = ?, description = ?, priority = ?,
                                   is_completed = ?, completed_at = ?, display_order = ?, updated_at = ?, version = version + 1
                            WHERE id = ?""",
                    parentId, type, name, str(d, "description"), priority, completed ? 1 : 0,
                    completed ? str(d, "completedAt") : null, order, orDefault(str(d, "updatedAt"), now), id);
        }
        run.touchedParents.add(parentGroup(planId, parentId));
    }

    private Set<Long> ancestors(long itemId) {
        return new HashSet<>(jdbc.queryForList("""
                WITH RECURSIVE up(id) AS (
                    SELECT parent_id FROM learning_item WHERE id = ?
                    UNION SELECT i.parent_id FROM learning_item i JOIN up ON i.id = up.id WHERE i.parent_id IS NOT NULL)
                SELECT id FROM up WHERE id IS NOT NULL""", Long.class, itemId));
    }

    private static List<Long> parentGroup(Object planId, Object parentId) {
        return java.util.Arrays.asList(((Number) planId).longValue(), parentId == null ? null : ((Number) parentId).longValue());
    }

    private void applyNote(Change c, Line line, boolean incomingWins, Instant shifted, Run run) {
        Long itemId = itemId(c.key());
        if (itemId == null) {
            if (!c.deleted()) {
                throw new Skip("ITEM_DELETED");
            }
            return;
        }
        Map<String, Object> current = jdbc.queryForList("SELECT content, format FROM item_note WHERE item_id = ?", itemId)
                .stream().findFirst().orElse(null);
        String incomingContent = c.deleted() ? null : str(c.data(), "content");
        NoteFormat incomingFormat = c.deleted() ? NoteFormat.MARKDOWN : NoteFormat.valueOf(orDefault(str(c.data(), "format"), "HTML"));
        if (!c.deleted() && (incomingContent == null || incomingContent.isBlank())) {
            throw new Skip("INVALID"); // an empty note travels as a delete
        }
        boolean changedHereSinceLastSync = line != null && line.deleted == 0 && line.seq > run.since
                && !run.deviceId.equals(line.origin);
        boolean differ = current != null && !Objects.equals(current.get("content"), incomingContent);

        if (changedHereSinceLastSync && differ) {
            if (c.deleted()) {
                return; // edited here, deleted on the phone: the edit is kept and pulled back
            }
            String localContent = (String) current.get("content");
            NoteFormat localFormat = NoteFormat.valueOf((String) current.get("format"));
            boolean phoneWins = incomingWins;
            String winner = phoneWins ? incomingContent : localContent;
            NoteFormat winnerFormat = phoneWins ? incomingFormat : localFormat;
            String loser = phoneWins ? localContent : incomingContent;
            NoteFormat loserFormat = phoneWins ? localFormat : incomingFormat;
            String loserSource = phoneWins ? "this computer" : (run.deviceName == null ? "the phone" : run.deviceName);
            String merged = appendConflict(winner, winnerFormat, loser, loserFormat, loserSource);
            writeNote(itemId, merged, winnerFormat, InstantText.format(Timestamps.now()));
            run.conflicts.add(new Conflict("note", c.key(), "MERGED"));
            run.mergedNotes.add(c.key());
            run.applied++;
            return;
        }
        if (!incomingWins) {
            return;
        }
        if (c.deleted()) {
            jdbc.update("DELETE FROM item_note WHERE item_id = ?", itemId);
            deriveFromNote(itemId, null, NoteFormat.MARKDOWN);
        } else {
            writeNote(itemId, incomingContent, incomingFormat, orDefault(str(c.data(), "updatedAt"), InstantText.format(shifted)));
        }
        win(c, shifted, run);
    }

    private void writeNote(long itemId, String content, NoteFormat format, String updatedAt) {
        int n = jdbc.update("UPDATE item_note SET content = ?, format = ?, updated_at = ? WHERE item_id = ?",
                content, format.name(), updatedAt, itemId);
        if (n == 0) {
            jdbc.update("INSERT INTO item_note (item_id, content, format, updated_at) VALUES (?, ?, ?, ?)",
                    itemId, content, format.name(), updatedAt);
        }
        deriveFromNote(itemId, content, format);
    }

    private void deriveFromNote(long itemId, String content, NoteFormat format) {
        searchIndex.indexNote(itemId, content, format);
        flashcards.syncFromNote(itemId, content, format);
        noteLinks.update(itemId, content, format);
    }

    /** Keeps both versions of a note edited on both sides: the loser's text goes below a marker. */
    static String appendConflict(String winner, NoteFormat winnerFormat, String loser, NoteFormat loserFormat, String from) {
        String label = "Conflicting copy from " + from + " (" + InstantText.format(Timestamps.now()) + ")";
        if (winnerFormat == NoteFormat.HTML) {
            String body = loserFormat == NoteFormat.HTML ? loser : "<pre>" + escapeHtml(loser) + "</pre>";
            return winner + "<hr><p><strong>" + escapeHtml(label) + "</strong></p>" + body;
        }
        String body = loserFormat == NoteFormat.HTML ? HtmlText.toLines(loser) : loser;
        return winner + "\n\n---\n\n**" + label + "**\n\n" + body;
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void link(Change c) {
        if (c.deleted()) {
            jdbc.update("DELETE FROM item_link WHERE external_id = ?", c.key());
            return;
        }
        Map<String, Object> d = c.data();
        long itemId = requireItem(str(d, "itemKey"));
        String url = requireText(d, "url", 2000);
        int order = Math.max(0, intVal(d, "displayOrder", 0));
        if (jdbc.update("UPDATE item_link SET item_id = ?, url = ?, title = ?, display_order = ? WHERE external_id = ?",
                itemId, url, str(d, "title"), order, c.key()) == 0) {
            jdbc.update("INSERT INTO item_link (external_id, item_id, url, title, display_order, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                    c.key(), itemId, url, str(d, "title"), order, orDefault(str(d, "createdAt"), InstantText.format(Timestamps.now())));
        }
    }

    private void timeLog(Change c) {
        if (c.deleted()) {
            jdbc.update("DELETE FROM time_log WHERE external_id = ?", c.key());
            return;
        }
        Map<String, Object> d = c.data();
        long itemId = requireItem(str(d, "itemKey"));
        int minutes = intVal(d, "minutes", 0);
        if (minutes < 1 || minutes > 1440) {
            throw new Skip("INVALID");
        }
        String loggedAt = str(d, "loggedAt");
        Instant.parse(loggedAt); // must be a timestamp; a bad one is skipped as INVALID
        if (jdbc.update("UPDATE time_log SET item_id = ?, minutes = ?, logged_at = ? WHERE external_id = ?",
                itemId, minutes, loggedAt, c.key()) == 0) {
            jdbc.update("INSERT INTO time_log (external_id, item_id, minutes, logged_at) VALUES (?, ?, ?, ?)",
                    c.key(), itemId, minutes, loggedAt);
        }
    }

    private void itemTag(Change c) {
        String[] parts = SyncKeys.split(c.key());
        if (c.deleted()) {
            Long itemId = itemId(parts[0]);
            if (itemId != null) {
                jdbc.update("DELETE FROM item_tag WHERE item_id = ? AND tag_id IN (SELECT id FROM tag WHERE lower(name) = ?)",
                        itemId, parts[1]);
                jdbc.update("DELETE FROM tag WHERE id NOT IN (SELECT tag_id FROM item_tag)");
            }
            return;
        }
        long itemId = requireItem(str(c.data(), "itemKey"));
        String name = requireText(c.data(), "tagName", 40).strip().replaceAll("\\s+", " ");
        List<Long> found = jdbc.queryForList("SELECT id FROM tag WHERE name = ? COLLATE NOCASE", Long.class, name);
        long tagId;
        if (found.isEmpty()) {
            jdbc.update("INSERT INTO tag (name) VALUES (?)", name);
            tagId = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        } else {
            tagId = found.getFirst();
        }
        jdbc.update("INSERT OR IGNORE INTO item_tag (item_id, tag_id) VALUES (?, ?)", itemId, tagId);
    }

    private void review(Change c) {
        if (c.deleted()) {
            Long itemId = itemId(c.key());
            if (itemId != null) {
                jdbc.update("DELETE FROM item_review WHERE item_id = ?", itemId);
            }
            return;
        }
        Map<String, Object> d = c.data();
        long itemId = requireItem(c.key());
        int stage = intVal(d, "stage", -1);
        if (stage < 0) {
            throw new Skip("INVALID");
        }
        jdbc.update("INSERT OR REPLACE INTO item_review (item_id, stage, due_on, last_reviewed_at) VALUES (?, ?, ?, ?)",
                itemId, stage, requireDate(d, "dueOn"), str(d, "lastReviewedAt"));
    }

    private void confidence(Change c) {
        if (c.deleted()) {
            Long itemId = itemId(c.key());
            if (itemId != null) {
                jdbc.update("DELETE FROM item_confidence WHERE item_id = ?", itemId);
            }
            return;
        }
        Map<String, Object> d = c.data();
        long itemId = requireItem(c.key());
        int level = intVal(d, "level", 0);
        if (level < 1 || level > 5) {
            throw new Skip("INVALID");
        }
        jdbc.update("INSERT OR REPLACE INTO item_confidence (item_id, level, rated_at) VALUES (?, ?, ?)",
                itemId, level, orDefault(str(d, "ratedAt"), InstantText.format(Timestamps.now())));
    }

    private void flashcard(Change c) {
        if (c.deleted()) {
            return; // cards come and go with their notes on each device
        }
        String[] parts = SyncKeys.split(c.key());
        Map<String, Object> d = c.data();
        long itemId = requireItem(parts[0]);
        int stage = intVal(d, "stage", -1);
        if (stage < 0) {
            throw new Skip("INVALID");
        }
        if (jdbc.update("UPDATE flashcard SET stage = ?, due_on = ?, last_reviewed_at = ? WHERE item_id = ? AND card_key = ?",
                stage, requireDate(d, "dueOn"), str(d, "lastReviewedAt"), itemId, parts[1]) == 0) {
            throw new Skip("CARD_NOT_FOUND");
        }
    }

    // --- integrity after applying (§3 step 5) ------------------------------------------------

    /**
     * Restores the tree rules for every sibling group touched: gapless order 0..n-1, and a topic
     * with children has no completion and no review.
     */
    void repair(Run run) {
        for (List<Long> group : run.touchedParents) {
            Long planId = group.get(0);
            Long parentId = group.get(1);
            List<Long> ids = jdbc.queryForList("""
                            SELECT id FROM learning_item WHERE plan_id = ? AND parent_id IS ?
                            ORDER BY display_order, updated_at, id""", Long.class, planId, parentId);
            for (int i = 0; i < ids.size(); i++) {
                jdbc.update("UPDATE learning_item SET display_order = ? WHERE id = ? AND display_order <> ?", i, ids.get(i), i);
            }
            if (parentId != null) {
                Integer completed = jdbc.queryForObject("SELECT COUNT(*) FROM learning_item WHERE id = ? AND is_completed = 1"
                        + " AND EXISTS (SELECT 1 FROM learning_item c WHERE c.parent_id = ?)", Integer.class, parentId, parentId);
                if (completed != null && completed > 0) {
                    jdbc.update("UPDATE learning_item SET is_completed = 0, completed_at = NULL, version = version + 1 WHERE id = ?",
                            parentId);
                    jdbc.update("DELETE FROM item_review WHERE item_id = ?", parentId);
                }
            }
        }
    }

    // --- helpers -----------------------------------------------------------------------------

    record Line(long seq, String changedAt, int deleted, String origin) {
    }

    Line line(String entity, String key) {
        return jdbc.query("SELECT seq, changed_at, deleted, origin FROM sync_change WHERE entity = ? AND key = ?",
                (rs, n) -> new Line(rs.getLong("seq"), rs.getString("changed_at"), rs.getInt("deleted"), rs.getString("origin")),
                entity, key).stream().findFirst().orElse(null);
    }

    private Long planId(String key) {
        return key == null ? null
                : jdbc.queryForList("SELECT id FROM learning_plan WHERE external_id = ?", Long.class, key).stream().findFirst().orElse(null);
    }

    private Long itemId(String key) {
        return key == null ? null
                : jdbc.queryForList("SELECT id FROM learning_item WHERE external_id = ?", Long.class, key).stream().findFirst().orElse(null);
    }

    private long requireItem(String key) {
        Long id = itemId(key);
        if (id == null) {
            throw new Skip("ITEM_DELETED");
        }
        return id;
    }

    private static String str(Map<String, Object> d, String field) {
        Object v = d == null ? null : d.get(field);
        return v == null ? null : v.toString();
    }

    private static String orDefault(String v, String fallback) {
        return v == null || v.isBlank() ? fallback : v;
    }

    private static int intVal(Map<String, Object> d, String field, int fallback) {
        Object v = d.get(field);
        return v instanceof Number n ? n.intValue() : fallback;
    }

    private static String requireText(Map<String, Object> d, String field, int max) {
        String v = str(d, field);
        if (v == null || v.isBlank() || v.length() > max) {
            throw new Skip("INVALID");
        }
        return v;
    }

    private static String date(Map<String, Object> d, String field) {
        String v = str(d, field);
        return v == null || v.isBlank() ? null : LocalDate.parse(v).toString();
    }

    private static String requireDate(Map<String, Object> d, String field) {
        String v = date(d, field);
        if (v == null) {
            throw new Skip("INVALID");
        }
        return v;
    }
}
