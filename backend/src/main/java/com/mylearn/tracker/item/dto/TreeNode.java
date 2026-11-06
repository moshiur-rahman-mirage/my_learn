package com.mylearn.tracker.item.dto;

import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.progress.Progress;

import java.time.Instant;
import java.util.List;

/**
 * One node of a plan's roadmap tree, with derived state.
 *
 * @param leaf            has no children
 * @param checkable       a leaf TOPIC, the only kind of item that can be completed
 * @param progress        leaf progress of this subtree (a checkable leaf is 0/1 or 1/1; an empty module is N/A)
 * @param descendantCount number of items below this one (shown when confirming a delete)
 * @param hasNotes        the item has a note (the note itself is loaded separately)
 */
public record TreeNode(
        Long id,
        String externalId,
        Long parentId,
        ItemType type,
        String name,
        String description,
        Priority priority,
        boolean completed,
        Instant completedAt,
        int displayOrder,
        long version,
        boolean leaf,
        boolean checkable,
        Progress progress,
        int descendantCount,
        boolean hasNotes,
        List<TreeNode> children) {
}
