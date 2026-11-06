package com.mylearn.tracker.item.dto;

import com.mylearn.tracker.item.HierarchyQueries.PathEntry;
import com.mylearn.tracker.progress.Progress;

import java.util.List;

/**
 * A single item with derived state.
 *
 * @param path ancestors from the root down to (excluding) the item, for breadcrumbs
 */
public record ItemDetailResponse(
        ItemResponse item,
        boolean leaf,
        boolean checkable,
        int childCount,
        Progress progress,
        List<PathEntry> path) {
}
