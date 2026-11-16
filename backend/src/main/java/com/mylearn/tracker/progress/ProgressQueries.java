package com.mylearn.tracker.progress;

import com.mylearn.tracker.common.persistence.InstantText;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Progress aggregates in plain SQL.
 *
 * <p>A <b>countable leaf</b> is a TOPIC with no children. Every leaf carries {@code plan_id}, so
 * plan totals need no recursion. Only a single item's subtree needs a recursive CTE.
 *
 * <p>Each method flushes pending JPA changes first: JdbcTemplate queries share the transaction's
 * connection, but Hibernate does not auto-flush for them.
 */
@Repository
public class ProgressQueries {

    // A plain string with surrounding spaces: text blocks strip trailing whitespace, which would
    // glue "WHERE" to this fragment when concatenated.
    private static final String IS_LEAF_TOPIC =
            " i.item_type = 'TOPIC' AND NOT EXISTS (SELECT 1 FROM learning_item c WHERE c.parent_id = i.id) ";

    private final JdbcTemplate jdbc;
    private final EntityManager em;

    public ProgressQueries(JdbcTemplate jdbc, EntityManager em) {
        this.jdbc = jdbc;
        this.em = em;
    }

    /** Progress of every plan that has at least one leaf (plans without leaves are absent: N/A). */
    public Map<Long, Progress> progressByPlan() {
        em.flush();
        Map<Long, Progress> result = new HashMap<>();
        jdbc.query("SELECT i.plan_id, COUNT(*) AS total, COALESCE(SUM(i.is_completed), 0) AS done "
                        + "FROM learning_item i WHERE " + IS_LEAF_TOPIC + " GROUP BY i.plan_id",
                rs -> {
                    result.put(rs.getLong("plan_id"), Progress.of(rs.getInt("done"), rs.getInt("total")));
                });
        return result;
    }

    public Progress progressOfPlan(Long planId) {
        em.flush();
        return jdbc.queryForObject("SELECT COUNT(*) AS total, COALESCE(SUM(i.is_completed), 0) AS done "
                        + "FROM learning_item i WHERE i.plan_id = ? AND " + IS_LEAF_TOPIC,
                (rs, n) -> Progress.of(rs.getInt("done"), rs.getInt("total")), planId);
    }

    /**
     * Creation and completion time of every current leaf topic in active plans (or one plan),
     * the raw material for the progress-over-time charts.
     */
    public List<LeafTimes> leafTimeline(Long planId) {
        em.flush();
        String sql = "SELECT i.created_at, i.completed_at FROM learning_item i "
                + "JOIN learning_plan p ON p.id = i.plan_id "
                + "WHERE p.status = 'ACTIVE' AND " + IS_LEAF_TOPIC
                + (planId == null ? "" : " AND i.plan_id = ?");
        Object[] args = planId == null ? new Object[0] : new Object[]{planId};
        return jdbc.query(sql, (rs, n) -> {
            String done = rs.getString("completed_at");
            return new LeafTimes(Instant.parse(rs.getString("created_at")), done == null ? null : Instant.parse(done));
        }, args);
    }

    /**
     * Topics completed by you since {@code since}, per plan. Topics that arrived already ticked
     * (completed within 5 seconds of being created, i.e. by an import) don't count.
     */
    public Map<Long, Integer> completedSinceByPlan(Instant since) {
        em.flush();
        Map<Long, Integer> result = new HashMap<>();
        jdbc.query("SELECT i.plan_id, COUNT(*) AS n FROM learning_item i "
                        + "WHERE i.is_completed = 1 AND i.completed_at >= ? "
                        + "AND abs(julianday(i.completed_at) - julianday(i.created_at)) * 86400 > 5 AND " + IS_LEAF_TOPIC
                        + "GROUP BY i.plan_id",
                rs -> {
                    result.put(rs.getLong("plan_id"), rs.getInt("n"));
                }, InstantText.format(since));
        return result;
    }

    /** @param completedAt null when not completed */
    public record LeafTimes(Instant createdAt, Instant completedAt) {
    }

    /** Progress of an item's subtree (the item itself counts if it is a leaf topic). */
    public Progress progressOfItem(Long itemId) {
        em.flush();
        return jdbc.queryForObject("""
                        WITH RECURSIVE sub(id) AS (
                            SELECT ?
                            UNION ALL
                            SELECT i.id FROM learning_item i JOIN sub s ON i.parent_id = s.id
                        )
                        SELECT COUNT(*) AS total, COALESCE(SUM(i.is_completed), 0) AS done
                        FROM learning_item i JOIN sub s ON s.id = i.id
                        WHERE """ + IS_LEAF_TOPIC,
                (rs, n) -> Progress.of(rs.getInt("done"), rs.getInt("total")), itemId);
    }

    public List<RecentCompletion> recentCompletions(int limit) {
        em.flush();
        return jdbc.query("""
                        SELECT i.id, i.name, i.completed_at, p.id AS plan_id, p.name AS plan_name
                        FROM learning_item i JOIN learning_plan p ON p.id = i.plan_id
                        WHERE i.is_completed = 1 AND i.completed_at IS NOT NULL AND """ + IS_LEAF_TOPIC + """

                        ORDER BY i.completed_at DESC
                        LIMIT ?""",
                (rs, n) -> new RecentCompletion(rs.getLong("id"), rs.getString("name"),
                        Instant.parse(rs.getString("completed_at")), rs.getLong("plan_id"), rs.getString("plan_name")),
                limit);
    }

    /**
     * Leaf topics completed by you in {@code [from, to)} (any plan status), oldest first. Topics
     * that arrived already ticked (import) are left out, like in the activity history.
     */
    public List<CompletedTopic> completedBetween(Instant from, Instant to, Long planId) {
        em.flush();
        String sql = "SELECT i.id, i.parent_id, i.name, i.priority, i.completed_at, p.id AS plan_id, p.name AS plan_name "
                + "FROM learning_item i JOIN learning_plan p ON p.id = i.plan_id "
                + "WHERE i.is_completed = 1 AND i.completed_at >= ? AND i.completed_at < ? "
                + "AND abs(julianday(i.completed_at) - julianday(i.created_at)) * 86400 > 5 AND " + IS_LEAF_TOPIC
                + (planId == null ? "" : " AND i.plan_id = ? ")
                + "ORDER BY i.completed_at";
        Object[] args = planId == null
                ? new Object[]{InstantText.format(from), InstantText.format(to)}
                : new Object[]{InstantText.format(from), InstantText.format(to), planId};
        return jdbc.query(sql, (rs, n) -> {
            long parent = rs.getLong("parent_id");
            return new CompletedTopic(rs.getLong("id"), rs.wasNull() ? null : parent, rs.getString("name"),
                    rs.getString("priority"), Instant.parse(rs.getString("completed_at")),
                    rs.getLong("plan_id"), rs.getString("plan_name"));
        }, args);
    }

    /** id → (parent id, name) of every item in the given plans, for building ancestor paths. */
    public Map<Long, NameAndParent> namesOfPlans(java.util.Collection<Long> planIds) {
        Map<Long, NameAndParent> result = new HashMap<>();
        if (planIds.isEmpty()) {
            return result;
        }
        String in = String.join(",", java.util.Collections.nCopies(planIds.size(), "?"));
        jdbc.query("SELECT id, parent_id, name FROM learning_item WHERE plan_id IN (" + in + ")", rs -> {
            long parent = rs.getLong("parent_id");
            result.put(rs.getLong("id"), new NameAndParent(rs.wasNull() ? null : parent, rs.getString("name")));
        }, planIds.toArray());
        return result;
    }

    public record CompletedTopic(Long itemId, Long parentId, String name, String priority, Instant completedAt,
                                 Long planId, String planName) {
    }

    public record NameAndParent(Long parentId, String name) {
    }

    public record RecentCompletion(Long itemId, String itemName, Instant completedAt, Long planId, String planName) {
    }
}
