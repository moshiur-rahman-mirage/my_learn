package com.mylearn.tracker.item;

import com.mylearn.tracker.item.dto.LinkResponse;
import com.mylearn.tracker.note.ItemNoteRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Loads {@link TreeExtras} for one plan: one small query per kind of data. */
@Repository
public class ItemExtrasQueries {

    private final JdbcTemplate jdbc;
    private final EntityManager em;
    private final ItemNoteRepository notes;

    public ItemExtrasQueries(JdbcTemplate jdbc, EntityManager em, ItemNoteRepository notes) {
        this.jdbc = jdbc;
        this.em = em;
        this.notes = notes;
    }

    public TreeExtras forPlan(Long planId) {
        em.flush();
        Map<Long, List<LinkResponse>> links = new HashMap<>();
        jdbc.query("""
                        SELECT l.id, l.item_id, l.url, l.title FROM item_link l
                        JOIN learning_item i ON i.id = l.item_id
                        WHERE i.plan_id = ? ORDER BY l.item_id, l.display_order, l.id""",
                rs -> {
                    links.computeIfAbsent(rs.getLong("item_id"), k -> new ArrayList<>())
                            .add(new LinkResponse(rs.getLong("id"), rs.getString("url"), rs.getString("title")));
                }, planId);

        Map<Long, List<String>> tags = new HashMap<>();
        jdbc.query("""
                        SELECT it.item_id, t.name FROM item_tag it
                        JOIN tag t ON t.id = it.tag_id
                        JOIN learning_item i ON i.id = it.item_id
                        WHERE i.plan_id = ? ORDER BY lower(t.name)""",
                rs -> {
                    tags.computeIfAbsent(rs.getLong("item_id"), k -> new ArrayList<>()).add(rs.getString("name"));
                }, planId);

        Map<Long, Integer> minutes = new HashMap<>();
        jdbc.query("""
                        SELECT t.item_id, SUM(t.minutes) AS total FROM time_log t
                        JOIN learning_item i ON i.id = t.item_id
                        WHERE i.plan_id = ? GROUP BY t.item_id""",
                rs -> {
                    minutes.put(rs.getLong("item_id"), rs.getInt("total"));
                }, planId);

        Map<Long, LocalDate> reviews = new HashMap<>();
        jdbc.query("""
                        SELECT r.item_id, r.due_on FROM item_review r
                        JOIN learning_item i ON i.id = r.item_id
                        WHERE i.plan_id = ?""",
                rs -> {
                    reviews.put(rs.getLong("item_id"), LocalDate.parse(rs.getString("due_on")));
                }, planId);

        Map<Long, Integer> confidence = new HashMap<>();
        jdbc.query("""
                        SELECT c.item_id, c.level FROM item_confidence c
                        JOIN learning_item i ON i.id = c.item_id
                        WHERE i.plan_id = ?""",
                rs -> {
                    confidence.put(rs.getLong("item_id"), rs.getInt("level"));
                }, planId);

        Map<Long, Integer> flashcards = new HashMap<>();
        jdbc.query("""
                        SELECT f.item_id, COUNT(*) AS n FROM flashcard f
                        JOIN learning_item i ON i.id = f.item_id
                        WHERE i.plan_id = ? GROUP BY f.item_id""",
                rs -> {
                    flashcards.put(rs.getLong("item_id"), rs.getInt("n"));
                }, planId);

        return new TreeExtras(notes.findItemIdsWithNotes(planId), links, tags, minutes, reviews, confidence, flashcards);
    }
}
