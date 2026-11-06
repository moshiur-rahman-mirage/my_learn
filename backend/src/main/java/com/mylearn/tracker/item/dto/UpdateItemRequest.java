package com.mylearn.tracker.item.dto;

import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Content-only update (no structure: moving and reordering have their own operations).
 * {@code priority = null} removes the priority. {@code version} must be the version last read.
 */
public record UpdateItemRequest(
        @NotBlank @Size(max = 300) String name,
        @NotNull ItemType type,
        @Size(max = 4000) String description,
        Priority priority,
        @NotNull Long version) {
}
