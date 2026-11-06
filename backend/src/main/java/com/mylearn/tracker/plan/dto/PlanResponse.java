package com.mylearn.tracker.plan.dto;

import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.PlanStatus;
import com.mylearn.tracker.progress.Progress;

import java.time.Instant;

/**
 * Plan as returned by the API, including derived leaf progress.
 * {@code updatedAt} is also bumped whenever one of the plan's items changes ("last activity").
 */
public record PlanResponse(
        Long id,
        String name,
        String description,
        PlanStatus status,
        long version,
        Instant createdAt,
        Instant updatedAt,
        Progress progress) {

    public static PlanResponse from(LearningPlan plan, Progress progress) {
        return new PlanResponse(plan.getId(), plan.getName(), plan.getDescription(), plan.getStatus(),
                plan.getVersion(), plan.getCreatedAt(), plan.getUpdatedAt(), progress == null ? Progress.NONE : progress);
    }
}
