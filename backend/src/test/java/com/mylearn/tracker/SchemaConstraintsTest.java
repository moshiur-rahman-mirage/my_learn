package com.mylearn.tracker;

import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The database's own guarantees, tested with plain SQL (bypassing the service layer).
 * These are the safety net in case a service-level check is ever missed.
 */
class SchemaConstraintsTest extends IntegrationTest {

    private static final String TS = "2026-01-01T00:00:00.000Z";

    @Autowired
    JdbcTemplate jdbc;

    long planA;
    long planB;

    @BeforeEach
    void plans() {
        planA = insertPlan("Plan A");
        planB = insertPlan("Plan B");
    }

    @Test
    void planNamesAreUniqueIgnoringCase() {
        assertThatThrownBy(() -> insertPlan("plan a"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void parentMustBelongToTheSamePlan() {
        long parentInA = insertItem(planA, null, "MODULE", "Module in A");

        assertThatThrownBy(() -> insertItem(planB, parentInA, "TOPIC", "Child in B"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("FOREIGN KEY");
    }

    @Test
    void parentMustExist() {
        assertThatThrownBy(() -> insertItem(planA, 9999L, "TOPIC", "Orphan"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itemCannotBeItsOwnParent() {
        long id = insertItem(planA, null, "TOPIC", "Self");

        assertThatThrownBy(() -> jdbc.update("UPDATE learning_item SET parent_id = id WHERE id = ?", id))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deletingAnItemDeletesItsWholeSubtree() {
        long phase = insertItem(planA, null, "MODULE", "Phase");
        long section = insertItem(planA, phase, "MODULE", "Section");
        insertItem(planA, section, "TOPIC", "Leaf 1");
        insertItem(planA, section, "TOPIC", "Leaf 2");
        long sibling = insertItem(planA, null, "TOPIC", "Untouched sibling");

        jdbc.update("DELETE FROM learning_item WHERE id = ?", phase);

        assertThat(jdbc.queryForList("SELECT id FROM learning_item WHERE plan_id = ?", Long.class, planA))
                .containsExactly(sibling);
    }

    @Test
    void deletingAPlanDeletesAllItsItemsAndImportBatches() {
        long phase = insertItem(planA, null, "MODULE", "Phase");
        insertItem(planA, phase, "TOPIC", "Leaf");
        jdbc.update("INSERT INTO import_batch (plan_id, file_name, content_sha256, item_count, imported_at) "
                + "VALUES (?, 'x.csv', 'abc', 2, ?)", planA, TS);
        insertItem(planB, null, "TOPIC", "Other plan");

        jdbc.update("DELETE FROM learning_plan WHERE id = ?", planA);

        assertThat(count("SELECT COUNT(*) FROM learning_item WHERE plan_id = " + planA)).isZero();
        assertThat(count("SELECT COUNT(*) FROM import_batch")).isZero();
        assertThat(count("SELECT COUNT(*) FROM learning_item WHERE plan_id = " + planB)).isEqualTo(1);
    }

    @Test
    void rejectsInvalidEnumValuesAndBlankNames() {
        assertThatThrownBy(() -> insertItem(planA, null, "PHASE", "Old type"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> insertItem(planA, null, "TOPIC", "   "))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO learning_item (external_id, plan_id, item_type, name, priority, display_order, created_at, updated_at) "
                        + "VALUES (?, ?, 'TOPIC', 'x', 'URGENT', 0, ?, ?)", UUID.randomUUID().toString(), planA, TS, TS))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE learning_plan SET status = 'DELETED' WHERE id = ?", planA))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // --- helpers ----------------------------------------------------------------------------

    private long insertPlan(String name) {
        jdbc.update("INSERT INTO learning_plan (name, created_at, updated_at) VALUES (?, ?, ?)", name, TS, TS);
        return lastId();
    }

    private long insertItem(long planId, Long parentId, String type, String name) {
        jdbc.update("""
                INSERT INTO learning_item (external_id, plan_id, parent_id, item_type, name, display_order, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, 0, ?, ?)""", UUID.randomUUID().toString(), planId, parentId, type, name, TS, TS);
        return lastId();
    }

    private long lastId() {
        return jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
    }

    private long count(String sql) {
        return jdbc.queryForObject(sql, Long.class);
    }
}
