package com.mylearn.tracker.review;

import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Spaced-repetition reviews: a topic you complete comes back for review after 1, 7 and 30 days.
 * "Remembered" moves it to the next interval (after the last one it is done); "forgot" starts over.
 *
 * <p>Only topics you tick yourself are scheduled; topics imported as already completed are not.
 * Un-ticking a topic (or it gaining sub-items) cancels its reviews. Days follow the server's
 * calendar, which is the viewer's for the desktop app.
 */
@Service
@Transactional(readOnly = true)
public class ReviewService {

    /** Days until the next review, by the number of reviews done so far. */
    static final int[] INTERVALS = {1, 7, 30};

    private final JdbcTemplate jdbc;
    private final EntityManager em;

    public ReviewService(JdbcTemplate jdbc, EntityManager em) {
        this.jdbc = jdbc;
        this.em = em;
    }

    /** First review tomorrow (replaces any earlier schedule of the topic). */
    @Transactional
    public void schedule(Long itemId) {
        em.flush(); // the item row must be written before the review row refers to it
        jdbc.update("INSERT OR REPLACE INTO item_review (item_id, stage, due_on, last_reviewed_at) VALUES (?, 0, ?, NULL)",
                itemId, today().plusDays(INTERVALS[0]).toString());
    }

    @Transactional
    public void cancel(Long itemId) {
        jdbc.update("DELETE FROM item_review WHERE item_id = ?", itemId);
    }

    /** Reviews due today or earlier, in active plans; most overdue first. */
    public List<DueReview> due() {
        em.flush();
        return jdbc.query("""
                        SELECT r.item_id, r.stage, r.due_on, i.name, i.plan_id, p.name AS plan_name,
                               (SELECT pi.name FROM learning_item pi WHERE pi.id = i.parent_id) AS parent_name
                        FROM item_review r
                        JOIN learning_item i ON i.id = r.item_id
                        JOIN learning_plan p ON p.id = i.plan_id
                        WHERE p.status = 'ACTIVE' AND r.due_on <= ?
                        ORDER BY r.due_on, lower(p.name), i.id""",
                (rs, n) -> new DueReview(rs.getLong("item_id"), rs.getString("name"), rs.getLong("plan_id"),
                        rs.getString("plan_name"), rs.getString("parent_name"), rs.getInt("stage") + 1, INTERVALS.length,
                        LocalDate.parse(rs.getString("due_on"))),
                today().toString());
    }

    /**
     * Records a review.
     *
     * @param remembered true: move on to the next interval; false: start over (review again tomorrow)
     */
    @Transactional
    public ReviewResult review(Long itemId, boolean remembered) {
        List<Integer> stages = jdbc.queryForList("SELECT stage FROM item_review WHERE item_id = ?", Integer.class, itemId);
        if (stages.isEmpty()) {
            throw new NotFoundException("Review of item", itemId);
        }
        int next = remembered ? stages.getFirst() + 1 : 0;
        String now = InstantText.format(Timestamps.now());
        if (next >= INTERVALS.length) {
            jdbc.update("DELETE FROM item_review WHERE item_id = ?", itemId);
            return new ReviewResult(itemId, true, null);
        }
        LocalDate due = today().plusDays(INTERVALS[next]);
        jdbc.update("UPDATE item_review SET stage = ?, due_on = ?, last_reviewed_at = ? WHERE item_id = ?",
                next, due.toString(), now, itemId);
        return new ReviewResult(itemId, false, due);
    }

    LocalDate today() {
        return LocalDate.now();
    }

    /**
     * @param review     which review this is (1 = first)
     * @param of         total number of reviews
     * @param parentName the module (or topic) the item sits in, for context; null at the top level
     */
    public record DueReview(Long itemId, String name, Long planId, String planName, String parentName,
                            int review, int of, LocalDate dueOn) {
    }

    /** @param finished all reviews done; {@code nextDue} is then null */
    public record ReviewResult(Long itemId, boolean finished, LocalDate nextDue) {
    }
}
