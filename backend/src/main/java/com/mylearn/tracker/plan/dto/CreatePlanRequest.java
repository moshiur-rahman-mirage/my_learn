package com.mylearn.tracker.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** @param targetDate optional "finish by" day */
public record CreatePlanRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 4000) String description,
        LocalDate targetDate) {

    public CreatePlanRequest(String name, String description) {
        this(name, description, null);
    }
}
