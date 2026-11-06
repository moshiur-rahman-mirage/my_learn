package com.mylearn.tracker.item.dto;

import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * @param parentId {@code null} creates a root item of the plan
 * @param position 0-based slot among the new siblings; {@code null} appends at the end
 */
public record CreateItemRequest(
        @NotBlank @Size(max = 300) String name,
        @NotNull ItemType type,
        Long parentId,
        @Size(max = 4000) String description,
        Priority priority,
        @PositiveOrZero Integer position) {
}
