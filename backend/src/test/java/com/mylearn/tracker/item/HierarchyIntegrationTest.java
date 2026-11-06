package com.mylearn.tracker.item;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.dashboard.DashboardService;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemDetailResponse;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.MoveItemRequest;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.item.dto.UpdateItemRequest;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.PlanStatus;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.PlanResponse;
import com.mylearn.tracker.plan.dto.UpdatePlanRequest;
import com.mylearn.tracker.progress.Progress;
import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.mylearn.tracker.item.ItemType.MODULE;
import static com.mylearn.tracker.item.ItemType.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Completion rules, move/reorder, cycle prevention and progress at every level (real DB). */
class HierarchyIntegrationTest extends IntegrationTest {

    @Autowired
    ItemService items;
    @Autowired
    PlanService plans;
    @Autowired
    DashboardService dashboard;

    Long planId;

    @BeforeEach
    void plan() {
        planId = plans.create(new CreatePlanRequest("Odoo", null)).id();
    }

    // --- completion -------------------------------------------------------------------------

    @Test
    void leafTopicCanBeCompletedAndUncompleted() {
        ItemResponse topic = create("Editions", TOPIC, null);

        ItemResponse done = items.setCompletion(topic.id(), true);
        assertThat(done.completed()).isTrue();
        assertThat(done.completedAt()).isNotNull();

        ItemResponse undone = items.setCompletion(topic.id(), false);
        assertThat(undone.completed()).isFalse();
        assertThat(undone.completedAt()).isNull();
    }

    @Test
    void completingTwiceKeepsTheOriginalCompletionTime() {
        ItemResponse topic = create("Editions", TOPIC, null);
        var first = items.setCompletion(topic.id(), true).completedAt();

        assertThat(items.setCompletion(topic.id(), true).completedAt()).isEqualTo(first);
    }

    @Test
    void modulesAndParentTopicsCannotBeCompleted() {
        ItemResponse module = create("Module", MODULE, null);
        ItemResponse parentTopic = create("Parent topic", TOPIC, null);
        create("Child", TOPIC, parentTopic.id());

        assertThatThrownBy(() -> items.setCompletion(module.id(), true)).isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> items.setCompletion(parentTopic.id(), true)).isInstanceOf(ConflictException.class);
    }

    @Test
    void topicThatLosesItsLastChildBecomesCheckableAgain() {
        ItemResponse parent = create("Relational fields", TOPIC, null);
        ItemResponse child = create("Many2one", TOPIC, parent.id());
        assertThat(node(parent.id()).checkable()).isFalse();

        items.delete(child.id());

        assertThat(node(parent.id()).checkable()).isTrue();
        assertThat(items.setCompletion(parent.id(), true).completed()).isTrue();
    }

    @Test
    void moduleThatLosesItsLastChildShowsNotApplicable() {
        ItemResponse module = create("Module", MODULE, null);
        ItemResponse child = create("Topic", TOPIC, module.id());
        items.delete(child.id());

        TreeNode node = node(module.id());
        assertThat(node.checkable()).isFalse();
        assertThat(node.progress().percent()).isNull();
    }

    // --- move -------------------------------------------------------------------------------

    @Test
    void movesAnItemWithItsSubtreeToAnotherParent() {
        ItemResponse phase1 = create("Phase 1", MODULE, null);
        ItemResponse phase2 = create("Phase 2", MODULE, null);
        ItemResponse fields = create("Fields", MODULE, phase1.id());
        create("Computed", TOPIC, fields.id());
        create("Stays", TOPIC, phase1.id());

        items.move(fields.id(), new MoveItemRequest(phase2.id(), null));

        assertThat(node(phase2.id()).children()).extracting(TreeNode::name).containsExactly("Fields");
        assertThat(node(fields.id()).children()).extracting(TreeNode::name).containsExactly("Computed");
        TreeNode p1 = node(phase1.id());
        assertThat(p1.children()).extracting(TreeNode::name).containsExactly("Stays");
        assertThat(p1.children().getFirst().displayOrder()).isZero(); // gap closed
    }

    @Test
    void movesToRootAndToASpecificPosition() {
        ItemResponse a = create("A", TOPIC, null);
        create("B", TOPIC, null);
        ItemResponse module = create("M", MODULE, null);
        ItemResponse nested = create("Nested", TOPIC, module.id());

        items.move(nested.id(), new MoveItemRequest(null, 1));

        assertThat(roots()).extracting(TreeNode::name).containsExactly("A", "Nested", "B", "M");
        assertThat(roots()).extracting(TreeNode::displayOrder).containsExactly(0, 1, 2, 3);
        assertThat(a.id()).isNotNull();
    }

    @Test
    void moveWithinTheSameParentActsAsReorder() {
        ItemResponse a = create("A", TOPIC, null);
        create("B", TOPIC, null);
        create("C", TOPIC, null);

        items.move(a.id(), new MoveItemRequest(null, 2));

        assertThat(roots()).extracting(TreeNode::name).containsExactly("B", "C", "A");
    }

    @Test
    void cannotMoveIntoItselfOrItsDescendants() {
        ItemResponse phase = create("Phase", MODULE, null);
        ItemResponse section = create("Section", MODULE, phase.id());
        ItemResponse deep = create("Deep", MODULE, section.id());

        assertThatThrownBy(() -> items.move(phase.id(), new MoveItemRequest(phase.id(), null)))
                .isInstanceOf(ConflictException.class).hasMessageContaining("own parent");
        assertThatThrownBy(() -> items.move(phase.id(), new MoveItemRequest(section.id(), null)))
                .isInstanceOf(ConflictException.class).hasMessageContaining("own sub-item");
        assertThatThrownBy(() -> items.move(phase.id(), new MoveItemRequest(deep.id(), null)))
                .isInstanceOf(ConflictException.class);
        // the tree is unchanged
        assertThat(roots()).extracting(TreeNode::name).containsExactly("Phase");
    }

    @Test
    void cannotMoveToAnotherPlan() {
        ItemResponse item = create("Item", TOPIC, null);
        Long other = plans.create(new CreatePlanRequest("Other", null)).id();
        ItemResponse foreign = items.create(other, new CreateItemRequest("Foreign", MODULE, null, null, null, null));

        assertThatThrownBy(() -> items.move(item.id(), new MoveItemRequest(foreign.id(), null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void movingIntoACompletedLeafClearsItsCompletion() {
        ItemResponse target = create("Target", TOPIC, null);
        items.setCompletion(target.id(), true);
        ItemResponse mover = create("Mover", TOPIC, null);

        items.move(mover.id(), new MoveItemRequest(target.id(), null));

        TreeNode t = node(target.id());
        assertThat(t.completed()).isFalse();
        assertThat(t.checkable()).isFalse();
    }

    // --- reorder ----------------------------------------------------------------------------

    @Test
    void reorderMovesUpAndDown() {
        create("A", TOPIC, null);
        ItemResponse b = create("B", TOPIC, null);
        create("C", TOPIC, null);

        items.reorder(b.id(), 0);
        assertThat(roots()).extracting(TreeNode::name).containsExactly("B", "A", "C");
        items.reorder(b.id(), 2);
        assertThat(roots()).extracting(TreeNode::name).containsExactly("A", "C", "B");
        assertThatThrownBy(() -> items.reorder(b.id(), 3)).isInstanceOf(BusinessRuleException.class);
    }

    // --- progress ---------------------------------------------------------------------------

    @Test
    void progressIsRecalculatedAfterEveryStructuralAndCompletionChange() {
        ItemResponse phase = create("Phase", MODULE, null);
        ItemResponse t1 = create("T1", TOPIC, phase.id());
        ItemResponse t2 = create("T2", TOPIC, phase.id());
        assertThat(plans.progressOf(planId)).isEqualTo(Progress.of(0, 2));

        items.setCompletion(t1.id(), true);
        assertThat(plans.progressOf(planId)).isEqualTo(Progress.of(1, 2));
        assertThat(items.progressOf(phase.id())).isEqualTo(Progress.of(1, 2));

        create("T1a", TOPIC, t1.id());                   // completed leaf gains a child
        assertThat(plans.progressOf(planId)).isEqualTo(Progress.of(0, 2));

        items.update(t2.id(), new UpdateItemRequest("T2", MODULE, null, null, t2.version())); // now an empty module
        assertThat(plans.progressOf(planId)).isEqualTo(Progress.of(0, 1));

        items.delete(phase.id());
        assertThat(plans.progressOf(planId)).isEqualTo(Progress.NONE);
    }

    @Test
    void itemDetailHasPathProgressAndFlags() {
        ItemResponse phase = create("Phase 3", MODULE, null);
        ItemResponse fields = create("Fields", MODULE, phase.id());
        ItemResponse leaf = create("Computed", TOPIC, fields.id());
        items.setCompletion(leaf.id(), true);
        create("Related", TOPIC, fields.id());

        ItemDetailResponse detail = items.detail(fields.id());
        assertThat(detail.path()).extracting(HierarchyQueries.PathEntry::name).containsExactly("Phase 3");
        assertThat(detail.childCount()).isEqualTo(2);
        assertThat(detail.leaf()).isFalse();
        assertThat(detail.progress()).isEqualTo(Progress.of(1, 2));

        ItemDetailResponse leafDetail = items.detail(leaf.id());
        assertThat(leafDetail.checkable()).isTrue();
        assertThat(leafDetail.path()).extracting(HierarchyQueries.PathEntry::name).containsExactly("Phase 3", "Fields");
    }

    @Test
    void dashboardOverallIsWeightedByLeavesNotAveragedAcrossPlans() {
        // Plan A: 1 of 1 done (100%). Plan B: 1 of 3 done (33.3%).
        ItemResponse a1 = create("A1", TOPIC, null);
        items.setCompletion(a1.id(), true);
        Long planB = plans.create(new CreatePlanRequest("Plan B", null)).id();
        ItemResponse b1 = items.create(planB, new CreateItemRequest("B1", TOPIC, null, null, null, null));
        items.create(planB, new CreateItemRequest("B2", TOPIC, null, null, null, null));
        items.create(planB, new CreateItemRequest("B3", TOPIC, null, null, null, null));
        items.setCompletion(b1.id(), true);

        var summary = dashboard.summary();

        // Weighted: 2 of 4 = 50%. An average of percentages would be 66.7%.
        assertThat(summary.overall()).isEqualTo(Progress.of(2, 4));
        assertThat(summary.overall().percent()).isEqualTo(50.0);
        assertThat(summary.pendingLeaves()).isEqualTo(2);
        assertThat(summary.recentlyCompleted()).extracting(r -> r.itemName()).contains("A1", "B1");
    }

    @Test
    void archivedPlansAreExcludedFromDashboardTotals() {
        create("Leaf", TOPIC, null);
        PlanResponse plan = plans.get(planId);
        plans.update(planId, new UpdatePlanRequest(plan.name(), null, PlanStatus.ARCHIVED, plan.version()));

        var summary = dashboard.summary();
        assertThat(summary.activePlans()).isZero();
        assertThat(summary.totalLeaves()).isZero();
        assertThat(summary.overall().percent()).isNull();
    }

    @Test
    void itemChangesBumpThePlansLastActivityButNotItsVersion() throws InterruptedException {
        PlanResponse before = plans.get(planId);
        Thread.sleep(5);
        create("New", TOPIC, null);

        PlanResponse after = plans.get(planId);
        assertThat(after.updatedAt()).isAfter(before.updatedAt());
        assertThat(after.version()).isEqualTo(before.version());
    }

    // --- helpers ----------------------------------------------------------------------------

    private ItemResponse create(String name, ItemType type, Long parentId) {
        return items.create(planId, new CreateItemRequest(name, type, parentId, null, null, null));
    }

    private List<TreeNode> roots() {
        return items.tree(planId);
    }

    private TreeNode node(Long id) {
        return find(items.tree(planId), id);
    }

    private static TreeNode find(List<TreeNode> nodes, Long id) {
        for (TreeNode n : nodes) {
            if (n.id().equals(id)) {
                return n;
            }
            TreeNode inChild = find(n.children(), id);
            if (inChild != null) {
                return inChild;
            }
        }
        return null;
    }
}
