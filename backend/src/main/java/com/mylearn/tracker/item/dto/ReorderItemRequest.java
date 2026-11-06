package com.mylearn.tracker.item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** New 0-based position among the current siblings ("move up" = current - 1). */
public record ReorderItemRequest(@NotNull @PositiveOrZero Integer position) {
}
