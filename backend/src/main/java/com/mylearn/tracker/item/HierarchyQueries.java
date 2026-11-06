package com.mylearn.tracker.item;

import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Recursive hierarchy lookups in plain SQL (recursive CTEs). */
@Repository
public class HierarchyQueries {

    private final JdbcTemplate jdbc;
    private final EntityManager em;

    public HierarchyQueries(JdbcTemplate jdbc, EntityManager em) {
        this.jdbc = jdbc;
        this.em = em;
    }

    /** The item and all its ancestors, ordered root first and the item itself last. */
    public List<PathEntry> pathTo(Long itemId) {
        em.flush(); // JdbcTemplate doesn't trigger Hibernate's auto-flush
        return jdbc.query("""
                        WITH RECURSIVE anc(id, name, parent_id, depth) AS (
                            SELECT id, name, parent_id, 0 FROM learning_item WHERE id = ?
                            UNION ALL
                            SELECT i.id, i.name, i.parent_id, a.depth + 1
                            FROM learning_item i JOIN anc a ON i.id = a.parent_id
                        )
                        SELECT id, name FROM anc ORDER BY depth DESC""",
                (rs, n) -> new PathEntry(rs.getLong("id"), rs.getString("name")), itemId);
    }

    /** True if {@code candidateAncestorId} is {@code itemId} itself or one of its ancestors. */
    public boolean isSelfOrAncestor(Long candidateAncestorId, Long itemId) {
        return pathTo(itemId).stream().anyMatch(p -> p.id().equals(candidateAncestorId));
    }

    public record PathEntry(Long id, String name) {
    }
}
