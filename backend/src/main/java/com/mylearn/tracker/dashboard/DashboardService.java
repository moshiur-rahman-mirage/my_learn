package com.mylearn.tracker.dashboard;

import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.dashboard.ActivityCalculator.Activity;
import com.mylearn.tracker.plan.LearningPlanRepository;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.PlanStatus;
import com.mylearn.tracker.plan.dto.PlanResponse;
import com.mylearn.tracker.progress.Progress;
import com.mylearn.tracker.progress.ProgressQueries;
import com.mylearn.tracker.progress.ProgressQueries.RecentCompletion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final int RECENT_PLANS = 5;
    private static final int RECENT_COMPLETIONS = 10;

    private final LearningPlanRepository plans;
    private final ProgressQueries progress;
    private final PlanService planService;

    public DashboardService(LearningPlanRepository plans, ProgressQueries progress, PlanService planService) {
        this.plans = plans;
        this.progress = progress;
        this.planService = planService;
    }

    /**
     * Totals cover <b>active</b> plans: archived plans are out of focus. The overall percentage is
     * weighted by leaf count (Σ completed / Σ leaves), never an average of plan percentages.
     */
    public DashboardSummary summary() {
        List<PlanResponse> all = planService.withProgress(plans.findAll());

        List<PlanResponse> active = all.stream()
                .filter(p -> p.status() == PlanStatus.ACTIVE)
                .sorted(Comparator.comparing(p -> p.name().toLowerCase()))
                .toList();
        Progress overall = active.stream().map(PlanResponse::progress).reduce(Progress.NONE, Progress::plus);

        List<PlanResponse> recent = all.stream()
                .sorted(Comparator.comparing(PlanResponse::updatedAt).reversed())
                .limit(RECENT_PLANS)
                .toList();

        return new DashboardSummary(all.size(), active.size(), overall.total(), overall.completed(),
                overall.remaining(), overall, active, recent, progress.recentCompletions(RECENT_COMPLETIONS));
    }

    /**
     * Day-by-day progress for the charts.
     *
     * @param days   days ending today; 0 = all history
     * @param zone   the viewer's time zone, so "a day" matches their calendar
     * @param planId one plan, or null for all active plans
     */
    public Activity activity(int days, ZoneId zone, Long planId) {
        if (planId != null) {
            plans.findById(planId).orElseThrow(() -> new NotFoundException("Plan", planId));
        }
        return ActivityCalculator.compute(progress.leafTimeline(planId), LocalDate.now(zone), zone, days);
    }

    public record DashboardSummary(
            int totalPlans,
            int activePlans,
            int totalLeaves,
            int completedLeaves,
            int pendingLeaves,
            Progress overall,
            List<PlanResponse> plans,
            List<PlanResponse> recentlyUpdated,
            List<RecentCompletion> recentlyCompleted) {
    }
}
