package com.mylearn.tracker.item.dto;

import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.Priority;

import java.time.Instant;

/** A single item, flat. The tree node DTO with progress and children comes in Phase 3. */
public record ItemResponse(
        Long id,
        String externalId,
        Long planId,
        Long parentId,
        ItemType type,
        String name,
        String description,
        Priority priority,
        boolean completed,
        Instant completedAt,
        int displayOrder,
        long version,
        Instant createdAt,
        Instant updatedAt) {

    public static ItemResponse from(LearningItem item) {
        return new ItemResponse(item.getId(), item.getExternalId(), item.getPlanId(), item.getParentId(),
                item.getType(), item.getName(), item.getDescription(), item.getPriority(),
                item.isCompleted(), item.getCompletedAt(), item.getDisplayOrder(), item.getVersion(),
                item.getCreatedAt(), item.getUpdatedAt());
    }
}
