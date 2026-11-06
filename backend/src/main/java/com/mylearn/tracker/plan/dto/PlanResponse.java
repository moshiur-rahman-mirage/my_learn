package com.mylearn.tracker.plan.dto;

import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.Pace;
import com.mylearn.tracker.plan.PlanStatus;
import com.mylearn.tracker.progress.Progress;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Plan as returned by the API, including derived leaf progress.
 * {@code updatedAt} is also bumped whenever one of the plan's items changes ("last activity").
 *
 * @param pace null when the plan has no target date
 */
public record PlanResponse(
        Long id,
        String name,
        String description,
        PlanStatus status,
        long version,
        Instant createdAt,
        Instant updatedAt,
        Progress progress,
        LocalDate targetDate,
        Pace pace) {

    public static PlanResponse from(LearningPlan plan, Progress progress) {
        return from(plan, progress, null);
    }

    public static PlanResponse from(LearningPlan plan, Progress progress, Pace pace) {
        return new PlanResponse(plan.getId(), plan.getName(), plan.getDescription(), plan.getStatus(),
                plan.getVersion(), plan.getCreatedAt(), plan.getUpdatedAt(), progress == null ? Progress.NONE : progress,
                plan.getTargetDate(), pace);
    }
}
