package com.mylearn.tracker.item;

import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.progress.Progress;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a plan's tree from its flat item list and rolls progress up from the leaves, in O(n).
 * A pure function with no database access, so it is unit-testable on its own.
 */
public final class TreeBuilder {

    private TreeBuilder() {
    }

    public static List<TreeNode> build(List<LearningItem> items) {
        return build(items, Set.of());
    }

    /** @param withNotes ids of items that have a note */
    public static List<TreeNode> build(List<LearningItem> items, Set<Long> withNotes) {
        Map<Long, List<LearningItem>> childrenByParent = new HashMap<>();
        for (LearningItem item : items) {
            childrenByParent.computeIfAbsent(item.getParentId(), k -> new ArrayList<>()).add(item);
        }
        Comparator<LearningItem> order = Comparator.comparingInt(LearningItem::getDisplayOrder)
                .thenComparing(LearningItem::getId);
        childrenByParent.values().forEach(list -> list.sort(order));

        return toNodes(childrenByParent.getOrDefault(null, List.of()), childrenByParent, withNotes);
    }

    private static List<TreeNode> toNodes(List<LearningItem> items, Map<Long, List<LearningItem>> childrenByParent,
                                          Set<Long> withNotes) {
        List<TreeNode> nodes = new ArrayList<>(items.size());
        for (LearningItem item : items) {
            List<TreeNode> children = toNodes(childrenByParent.getOrDefault(item.getId(), List.of()), childrenByParent, withNotes);
            boolean leaf = children.isEmpty();
            boolean checkable = leaf && item.getType() == ItemType.TOPIC;

            Progress progress;
            int descendants = 0;
            if (leaf) {
                progress = checkable ? Progress.of(item.isCompleted() ? 1 : 0, 1) : Progress.NONE;
            } else {
                progress = Progress.NONE;
                for (TreeNode child : children) {
                    progress = progress.plus(child.progress());
                    descendants += child.descendantCount() + 1;
                }
            }

            nodes.add(new TreeNode(item.getId(), item.getExternalId(), item.getParentId(), item.getType(),
                    item.getName(), item.getDescription(), item.getPriority(),
                    checkable && item.isCompleted(), checkable ? item.getCompletedAt() : null,
                    item.getDisplayOrder(), item.getVersion(), leaf, checkable, progress, descendants,
                    withNotes.contains(item.getId()), children));
        }
        return nodes;
    }
}
