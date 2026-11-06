package com.mylearn.tracker.plan.dto;

import com.mylearn.tracker.plan.PlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Full replacement of the editable plan fields. Archiving/activating is a {@code status} change.
 * {@code version} must be the version the client last read; otherwise the update is rejected as stale.
 */
public record UpdatePlanRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 4000) String description,
        @NotNull PlanStatus status,
        @NotNull Long version) {
}
