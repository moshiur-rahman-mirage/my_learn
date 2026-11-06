package com.mylearn.tracker.item;

import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.progress.Progress;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static com.mylearn.tracker.item.ItemType.MODULE;
import static com.mylearn.tracker.item.ItemType.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

/** Pure unit tests of tree assembly and progress roll-up (no Spring, no database). */
class TreeBuilderTest {

    private final LearningPlan plan = new LearningPlan("Plan", null);
    private final List<LearningItem> items = new ArrayList<>();
    private long nextId = 1;

    @Test
    void rollsUpLeafProgressThroughEveryLevel() {
        LearningItem phase = item(null, MODULE, "Phase 3", 0);
        LearningItem fields = item(phase, MODULE, "Fields", 0);
        LearningItem relational = item(fields, TOPIC, "Relational", 0);   // topic with sub-topics
        done(item(relational, TOPIC, "Many2one", 0));
        item(relational, TOPIC, "One2many", 1);
        done(item(fields, TOPIC, "Computed", 1));
        item(phase, TOPIC, "CRUD", 1);

        TreeNode root = TreeBuilder.build(items).getFirst();

        assertThat(root.progress()).isEqualTo(Progress.of(2, 4));
        assertThat(root.progress().percent()).isEqualTo(50.0);
        assertThat(root.descendantCount()).isEqualTo(6);
        TreeNode fieldsNode = root.children().getFirst();
        assertThat(fieldsNode.progress()).isEqualTo(Progress.of(2, 3));
        assertThat(fieldsNode.progress().percent()).isEqualTo(66.7);
        TreeNode relationalNode = fieldsNode.children().getFirst();
        assertThat(relationalNode.checkable()).isFalse();
        assertThat(relationalNode.leaf()).isFalse();
        assertThat(relationalNode.progress()).isEqualTo(Progress.of(1, 2));
    }

    @Test
    void checkableOnlyForLeafTopics_emptyModuleIsNotApplicable() {
        item(null, MODULE, "Empty module", 0);
        done(item(null, TOPIC, "Leaf topic", 1));

        List<TreeNode> roots = TreeBuilder.build(items);

        TreeNode emptyModule = roots.get(0);
        assertThat(emptyModule.leaf()).isTrue();
        assertThat(emptyModule.checkable()).isFalse();
        assertThat(emptyModule.progress()).isEqualTo(Progress.NONE);
        assertThat(emptyModule.progress().percent()).isNull();     // UI shows N/A
        TreeNode leaf = roots.get(1);
        assertThat(leaf.checkable()).isTrue();
        assertThat(leaf.completed()).isTrue();
        assertThat(leaf.progress()).isEqualTo(Progress.of(1, 1));
    }

    @Test
    void emptyModuleDoesNotDragDownItsParent() {
        LearningItem phase = item(null, MODULE, "Phase", 0);
        done(item(phase, TOPIC, "Done", 0));
        item(phase, MODULE, "Not started module", 1);

        assertThat(TreeBuilder.build(items).getFirst().progress().percent()).isEqualTo(100.0);
    }

    @Test
    void staleCompletionOnAParentIsIgnored() {
        LearningItem parent = item(null, TOPIC, "Parent with old tick", 0);
        done(parent);
        item(parent, TOPIC, "Child", 0);

        TreeNode node = TreeBuilder.build(items).getFirst();
        assertThat(node.completed()).isFalse();
        assertThat(node.progress()).isEqualTo(Progress.of(0, 1));
    }

    @Test
    void ordersSiblingsByDisplayOrder() {
        item(null, TOPIC, "C", 2);
        item(null, TOPIC, "A", 0);
        item(null, TOPIC, "B", 1);

        assertThat(TreeBuilder.build(items)).extracting(TreeNode::name).containsExactly("A", "B", "C");
    }

    @Test
    void emptyPlanGivesEmptyTree() {
        assertThat(TreeBuilder.build(List.of())).isEmpty();
    }

    private LearningItem item(LearningItem parent, ItemType type, String name, int order) {
        LearningItem item = new LearningItem(plan, parent, type, name, null, null);
        ReflectionTestUtils.setField(item, "id", nextId++);
        item.setDisplayOrder(order);
        items.add(item);
        return item;
    }

    private static void done(LearningItem item) {
        item.markCompleted(Timestamps.now());
    }
}
