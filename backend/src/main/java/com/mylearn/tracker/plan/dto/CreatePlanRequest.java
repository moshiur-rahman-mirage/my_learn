package com.mylearn.tracker.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlanRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 4000) String description) {
}
