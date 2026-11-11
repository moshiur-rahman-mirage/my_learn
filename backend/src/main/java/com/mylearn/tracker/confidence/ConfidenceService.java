package com.mylearn.tracker.confidence;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.InstantText;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.review.ReviewService;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * How well you know an item, from 1 (shaky) to 5 (solid). Rating a completed topic 1 or 2 brings
 * its spaced-repetition review forward to tomorrow (starting the 1/7/30-day cycle again), so weak
 * topics come back sooner. Ratings don't change the item's version.
 */
@Service
@Transactional(readOnly = true)
public class ConfidenceService {

    /** Ratings at or below this count as "shaky". */
    public static final int LOW = 2;

    private final JdbcTemplate jdbc;
    private final EntityManager em;
    private final LearningItemRepository items;
    private final ReviewService reviews;

    public ConfidenceService(JdbcTemplate jdbc, EntityManager em, LearningItemRepository items, ReviewService reviews) {
        this.jdbc = jdbc;
        this.em = em;
        this.items = items;
        this.reviews = reviews;
    }

    /**
     * @param level 1–5, or null to clear the rating
     * @return the review day when the rating brought the review forward, else null
     */
    @Transactional
    public Rated rate(Long itemId, Integer level) {
        LearningItem item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Item", itemId));
        em.flush();
        if (level == null) {
            jdbc.update("DELETE FROM item_confidence WHERE item_id = ?", itemId);
            return new Rated(itemId, null, null);
        }
        if (level < 1 || level > 5) {
            throw new BusinessRuleException("Confidence must be between 1 and 5");
        }
        jdbc.update("INSERT OR REPLACE INTO item_confidence (item_id, level, rated_at) VALUES (?, ?, ?)",
                itemId, level, InstantText.format(Timestamps.now()));
        LocalDate reviewOn = null;
        if (level <= LOW && item.isCompleted() && item.getType() == ItemType.TOPIC && isLeaf(itemId)) {
            List<String> due = jdbc.queryForList("SELECT due_on FROM item_review WHERE item_id = ?", String.class, itemId);
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            if (due.isEmpty() || LocalDate.parse(due.getFirst()).isAfter(tomorrow)) {
                reviews.schedule(itemId);
                reviewOn = tomorrow;
            }
        }
        return new Rated(itemId, level, reviewOn);
    }

    /** Items rated shaky (1–2) in active plans, weakest and oldest rating first. */
    public List<Shaky> shaky() {
        em.flush();
        return jdbc.query("""
                        SELECT c.item_id, c.level, c.rated_at, i.name, i.plan_id, p.name AS plan_name
                        FROM item_confidence c
                        JOIN learning_item i ON i.id = c.item_id
                        JOIN learning_plan p ON p.id = i.plan_id
                        WHERE p.status = 'ACTIVE' AND c.level <= ?
                        ORDER BY c.level, c.rated_at
                        LIMIT 50""",
                (rs, n) -> new Shaky(rs.getLong("item_id"), rs.getString("name"), rs.getLong("plan_id"),
                        rs.getString("plan_name"), rs.getInt("level"), Instant.parse(rs.getString("rated_at"))),
                LOW);
    }

    private boolean isLeaf(Long itemId) {
        Integer children = jdbc.queryForObject("SELECT COUNT(*) FROM learning_item WHERE parent_id = ?", Integer.class, itemId);
        return children == null || children == 0;
    }

    /** @param reviewOn set when the rating moved the topic's review to that day */
    public record Rated(Long itemId, Integer level, LocalDate reviewOn) {
    }

    public record Shaky(Long itemId, String name, Long planId, String planName, int level, Instant ratedAt) {
    }
}
