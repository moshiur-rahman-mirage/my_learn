package com.mylearn.tracker.item.dto;

import jakarta.validation.constraints.PositiveOrZero;

/**
 * @param parentId new parent in the same plan, or {@code null} to make the item a root item
 * @param position 0-based slot among the new siblings; {@code null} appends at the end
 */
public record MoveItemRequest(Long parentId, @PositiveOrZero Integer position) {
}
