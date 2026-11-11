package com.mylearn.tracker.flashcard;

import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.flashcard.FlashcardParser.Card;
import com.mylearn.tracker.note.NoteFormat;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Flashcards derived from notes, reviewed with growing intervals (1, 3, 7, 14, 30, 60, 120 days).
 * "Got it" moves a card to the next interval; "again" makes it due today, so it comes back later
 * in the same session. New cards are due right away.
 */
@Service
@Transactional(readOnly = true)
public class FlashcardService {

    static final int[] INTERVALS = {1, 3, 7, 14, 30, 60, 120};

    private final JdbcTemplate jdbc;
    private final EntityManager em;

    public FlashcardService(JdbcTemplate jdbc, EntityManager em) {
        this.jdbc = jdbc;
        this.em = em;
    }

    /**
     * Brings the item's cards in line with its note: new questions become new cards, edited
     * answers are updated in place (schedule kept), removed questions are deleted.
     *
     * @return number of cards the note now has
     */
    @Transactional
    public int syncFromNote(Long itemId, String content, NoteFormat format) {
        em.flush();
        List<Card> cards = content == null ? List.of() : FlashcardParser.parse(content, format);
        Map<String, Long> existing = new HashMap<>();
        jdbc.query("SELECT id, card_key FROM flashcard WHERE item_id = ?",
                rs -> {
                    existing.put(rs.getString("card_key"), rs.getLong("id"));
                }, itemId);
        Set<String> keep = new HashSet<>();
        String now = InstantText.format(Timestamps.now());
        String today = today().toString();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            keep.add(card.key());
            Long id = existing.get(card.key());
            if (id == null) {
                jdbc.update("""
                                INSERT INTO flashcard (item_id, card_key, question, answer, position, stage, due_on, created_at)
                                VALUES (?, ?, ?, ?, ?, 0, ?, ?)""",
                        itemId, card.key(), card.question(), card.answer(), i, today, now);
            } else {
                jdbc.update("UPDATE flashcard SET question = ?, answer = ?, position = ? WHERE id = ?",
                        card.question(), card.answer(), i, id);
            }
        }
        for (Map.Entry<String, Long> e : existing.entrySet()) {
            if (!keep.contains(e.getKey())) {
                jdbc.update("DELETE FROM flashcard WHERE id = ?", e.getValue());
            }
        }
        return cards.size();
    }

    /** Cards due today or earlier in active plans (optionally one plan), most overdue first. */
    public List<DueCard> due(Long planId) {
        em.flush();
        return jdbc.query("""
                        SELECT f.id, f.item_id, f.question, f.answer, f.stage, f.due_on,
                               i.name AS item_name, i.plan_id, p.name AS plan_name, COALESCE(n.format, 'MARKDOWN') AS format
                        FROM flashcard f
                        JOIN learning_item i ON i.id = f.item_id
                        JOIN learning_plan p ON p.id = i.plan_id
                        LEFT JOIN item_note n ON n.item_id = f.item_id
                        WHERE p.status = 'ACTIVE' AND f.due_on <= ? AND (? IS NULL OR i.plan_id = ?)
                        ORDER BY f.due_on, i.plan_id, i.display_order, f.item_id, f.position""",
                (rs, n) -> new DueCard(rs.getLong("id"), rs.getLong("item_id"), rs.getString("item_name"),
                        rs.getLong("plan_id"), rs.getString("plan_name"), rs.getString("question"), rs.getString("answer"),
                        NoteFormat.valueOf(rs.getString("format")), rs.getInt("stage"), LocalDate.parse(rs.getString("due_on"))),
                today().toString(), planId, planId);
    }

    /** The cards of one item's note, in note order, with their schedule. */
    public List<ItemCard> forItem(Long itemId) {
        em.flush();
        return jdbc.query("SELECT id, question, stage, due_on FROM flashcard WHERE item_id = ? ORDER BY position",
                (rs, n) -> new ItemCard(rs.getLong("id"), rs.getString("question"), rs.getInt("stage"),
                        LocalDate.parse(rs.getString("due_on"))),
                itemId);
    }

    /** Number of cards and due cards per plan (for the plan page). */
    public CardCounts counts(Long planId) {
        em.flush();
        return jdbc.queryForObject("""
                        SELECT COUNT(*) AS total, COALESCE(SUM(CASE WHEN f.due_on <= ? THEN 1 ELSE 0 END), 0) AS due
                        FROM flashcard f JOIN learning_item i ON i.id = f.item_id WHERE i.plan_id = ?""",
                (rs, n) -> new CardCounts(rs.getInt("total"), rs.getInt("due")), today().toString(), planId);
    }

    /** @param remembered true: next interval; false: start over, due again today */
    @Transactional
    public ReviewOutcome review(Long cardId, boolean remembered) {
        List<Integer> stages = jdbc.queryForList("SELECT stage FROM flashcard WHERE id = ?", Integer.class, cardId);
        if (stages.isEmpty()) {
            throw new NotFoundException("Flashcard", cardId);
        }
        int stage = stages.getFirst();
        int next = remembered ? stage + 1 : 0;
        LocalDate due = remembered ? today().plusDays(INTERVALS[Math.min(stage, INTERVALS.length - 1)]) : today();
        jdbc.update("UPDATE flashcard SET stage = ?, due_on = ?, last_reviewed_at = ? WHERE id = ?",
                next, due.toString(), InstantText.format(Timestamps.now()), cardId);
        return new ReviewOutcome(cardId, next, due);
    }

    /** Restores a card's schedule from a plan backup (matched by question). */
    @Transactional
    public void restoreSchedule(Long itemId, String question, int stage, LocalDate dueOn, String lastReviewedAt) {
        jdbc.update("UPDATE flashcard SET stage = ?, due_on = ?, last_reviewed_at = ? WHERE item_id = ? AND card_key = ?",
                Math.max(0, stage), dueOn.toString(), lastReviewedAt, itemId, FlashcardParser.key(question));
    }

    LocalDate today() {
        return LocalDate.now();
    }

    public record DueCard(Long id, Long itemId, String itemName, Long planId, String planName, String question,
                          String answer, NoteFormat format, int stage, LocalDate dueOn) {
    }

    public record ItemCard(Long id, String question, int stage, LocalDate dueOn) {
    }

    public record CardCounts(int total, int due) {
    }

    public record ReviewOutcome(Long id, int stage, LocalDate nextDue) {
    }
}
