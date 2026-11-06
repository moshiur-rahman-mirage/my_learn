package com.mylearn.tracker.item;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.exception.StaleUpdateException;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.UpdateItemRequest;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.mylearn.tracker.item.ItemType.MODULE;
import static com.mylearn.tracker.item.ItemType.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemServiceIntegrationTest extends IntegrationTest {

    @Autowired
    ItemService items;
    @Autowired
    PlanService plans;
    @Autowired
    LearningItemRepository repository;
    @Autowired
    EntityManager em;
    @Autowired
    JdbcTemplate jdbc;

    Long planId;

    @BeforeEach
    void plan() {
        planId = plans.create(new CreatePlanRequest("Odoo Developer Roadmap", null)).id();
    }

    // --- create -----------------------------------------------------------------------------

    @Test
    void createsNestedModulesAndTopicsAtAnyDepth() {
        ItemResponse phase = create("Phase 3 — Models and ORM", MODULE, null);
        ItemResponse fields = create("Fields", MODULE, phase.id());
        ItemResponse relational = create("Relational fields", TOPIC, fields.id());
        ItemResponse many2one = create("Many2one", TOPIC, relational.id()); // sub-topic under a topic

        assertThat(phase.parentId()).isNull();
        assertThat(fields.parentId()).isEqualTo(phase.id());
        assertThat(many2one.parentId()).isEqualTo(relational.id());
        assertThat(many2one.planId()).isEqualTo(planId);
        assertThat(many2one.externalId()).hasSize(36); // UUID
        assertThat(repository.countByPlan_Id(planId)).isEqualTo(4);
    }

    @Test
    void appendsByDefaultAndInsertsAtPositionShiftingSiblings() {
        ItemResponse a = create("A", TOPIC, null);
        ItemResponse b = create("B", TOPIC, null);
        ItemResponse first = items.create(planId, new CreateItemRequest("First", TOPIC, null, null, null, 0));
        ItemResponse middle = items.create(planId, new CreateItemRequest("Middle", TOPIC, null, null, null, 2));

        assertThat(rootNames()).containsExactly("First", "A", "Middle", "B");
        assertThat(rootOrders()).containsExactly(0, 1, 2, 3);
        assertThat(List.of(first.displayOrder(), middle.displayOrder())).containsExactly(0, 2);
        assertThat(a.displayOrder()).isZero(); // as returned at creation time
        assertThat(b.displayOrder()).isEqualTo(1);
    }

    @Test
    void positionBeyondTheEndIsRejected() {
        create("Only", TOPIC, null);

        assertThatThrownBy(() -> items.create(planId, new CreateItemRequest("X", TOPIC, null, null, null, 2)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void parentFromAnotherPlanIsRejected() {
        Long otherPlan = plans.create(new CreatePlanRequest("Core Java", null)).id();
        ItemResponse foreignParent = items.create(otherPlan, new CreateItemRequest("Foreign", MODULE, null, null, null, null));

        assertThatThrownBy(() -> create("Child", TOPIC, foreignParent.id()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("different plan");
    }

    @Test
    void unknownParentOrPlanIsNotFound() {
        assertThatThrownBy(() -> create("Child", TOPIC, 9999L))
                .isInstanceOf(NotFoundException.class).hasMessage("Parent item 9999 not found");
        assertThatThrownBy(() -> items.create(9999L, new CreateItemRequest("X", TOPIC, null, null, null, null)))
                .isInstanceOf(NotFoundException.class).hasMessage("Plan 9999 not found");
    }

    @Test
    void addingAChildToACompletedLeafClearsItsCompletion() {
        ItemResponse leaf = create("Computed fields", TOPIC, null);
        complete(leaf.id());

        create("@api.depends", TOPIC, leaf.id());

        LearningItem former = reload(leaf.id());
        assertThat(former.isCompleted()).isFalse();
        assertThat(former.getCompletedAt()).isNull();
    }

    // --- update -----------------------------------------------------------------------------

    @Test
    void updatesContentFieldsAndIncrementsVersion() {
        ItemResponse item = create("Feilds", TOPIC, null);

        ItemResponse updated = items.update(item.id(),
                new UpdateItemRequest(" Fields ", TOPIC, "Char, Text, Integer", Priority.MUST, item.version()));

        assertThat(updated.name()).isEqualTo("Fields");
        assertThat(updated.description()).isEqualTo("Char, Text, Integer");
        assertThat(updated.priority()).isEqualTo(Priority.MUST);
        assertThat(updated.version()).isEqualTo(item.version() + 1);

        ItemResponse cleared = items.update(item.id(), new UpdateItemRequest("Fields", TOPIC, "", null, updated.version()));
        assertThat(cleared.priority()).isNull();
        assertThat(cleared.description()).isNull();
    }

    @Test
    void staleUpdateIsRejected() {
        ItemResponse item = create("Views", TOPIC, null);
        items.update(item.id(), new UpdateItemRequest("Views and UI", TOPIC, null, null, item.version()));

        assertThatThrownBy(() -> items.update(item.id(), new UpdateItemRequest("Other", TOPIC, null, null, item.version())))
                .isInstanceOf(StaleUpdateException.class);
    }

    @Test
    void changingACompletedTopicIntoAModuleClearsCompletion() {
        ItemResponse item = create("Security", TOPIC, null);
        complete(item.id());

        ItemResponse updated = items.update(item.id(), new UpdateItemRequest("Security", MODULE, null, null, item.version()));

        assertThat(updated.type()).isEqualTo(MODULE);
        assertThat(updated.completed()).isFalse();
    }

    @Test
    void siblingRenumberingDoesNotMakeOpenEditsStale() {
        ItemResponse a = create("A", TOPIC, null);
        // Inserting at the front shifts A's display order...
        items.create(planId, new CreateItemRequest("Front", TOPIC, null, null, null, 0));
        em.flush();

        // ...but A's version is unchanged, so an edit form opened before still saves fine.
        assertThat(jdbc.queryForObject("SELECT version FROM learning_item WHERE id = ?", Long.class, a.id())).isZero();
        assertThat(items.update(a.id(), new UpdateItemRequest("A2", TOPIC, null, null, a.version())).name()).isEqualTo("A2");
    }

    // --- delete -----------------------------------------------------------------------------

    @Test
    void deletingAModuleDeletesItsSubtreeAndClosesTheGap() {
        ItemResponse first = create("Phase 0", MODULE, null);
        ItemResponse doomed = create("Phase 1", MODULE, null);
        ItemResponse last = create("Phase 2", MODULE, null);
        ItemResponse section = create("Installation", MODULE, doomed.id());
        create("Source install", TOPIC, section.id());
        create("odoo.conf", TOPIC, doomed.id());
        create("Editions", TOPIC, first.id());

        items.delete(doomed.id());

        assertThat(rootNames()).containsExactly("Phase 0", "Phase 2");
        assertThat(rootOrders()).containsExactly(0, 1);
        assertThat(repository.findAllByPlan(planId)).extracting(LearningItem::getName)
                .containsExactlyInAnyOrder("Phase 0", "Phase 2", "Editions");
        assertThat(repository.findById(last.id())).isPresent();
    }

    @Test
    void deletingTheLastChildLeavesTheParentInPlace() {
        ItemResponse parent = create("Relational fields", TOPIC, null);
        ItemResponse child = create("Many2one", TOPIC, parent.id());

        items.delete(child.id());

        assertThat(repository.existsByParent_Id(parent.id())).isFalse(); // parent is a leaf again
        assertThat(reload(parent.id()).isCompleted()).isFalse();
    }

    @Test
    void deletingUnknownItemIsNotFound() {
        assertThatThrownBy(() -> items.delete(12345L)).isInstanceOf(NotFoundException.class);
    }

    // --- persistence details ------------------------------------------------------------------

    @Test
    void timestampsAreStoredAsSortableIsoText() {
        ItemResponse item = create("Timestamps", TOPIC, null);
        em.flush();

        String stored = jdbc.queryForObject("SELECT created_at FROM learning_item WHERE id = ?", String.class, item.id());
        assertThat(stored).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z");
        assertThat(reload(item.id()).getCreatedAt()).isEqualTo(item.createdAt());
    }

    // --- helpers ----------------------------------------------------------------------------

    private ItemResponse create(String name, ItemType type, Long parentId) {
        return items.create(planId, new CreateItemRequest(name, type, parentId, null, null, null));
    }

    /** Phase 2 has no completion API yet; set the state directly on the entity. */
    private void complete(Long id) {
        repository.findById(id).orElseThrow().markCompleted(Timestamps.now());
        em.flush();
    }

    private LearningItem reload(Long id) {
        em.flush();
        em.clear();
        return repository.findById(id).orElseThrow();
    }

    private List<String> rootNames() {
        em.flush();
        return jdbc.queryForList("SELECT name FROM learning_item WHERE plan_id = ? AND parent_id IS NULL ORDER BY display_order",
                String.class, planId);
    }

    private List<Integer> rootOrders() {
        return jdbc.queryForList("SELECT display_order FROM learning_item WHERE plan_id = ? AND parent_id IS NULL ORDER BY display_order",
                Integer.class, planId);
    }
}
