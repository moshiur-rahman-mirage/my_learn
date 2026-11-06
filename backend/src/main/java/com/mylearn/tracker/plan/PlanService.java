package com.mylearn.tracker.plan;

import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.exception.StaleUpdateException;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.PlanResponse;
import com.mylearn.tracker.plan.dto.UpdatePlanRequest;
import com.mylearn.tracker.progress.Progress;
import com.mylearn.tracker.progress.ProgressQueries;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.mylearn.tracker.common.Text.blankToNull;

@Service
@Transactional(readOnly = true)
public class PlanService {

    private final LearningPlanRepository plans;
    private final ProgressQueries progress;

    public PlanService(LearningPlanRepository plans, ProgressQueries progress) {
        this.plans = plans;
        this.progress = progress;
    }

    @Transactional
    public PlanResponse create(CreatePlanRequest request) {
        LearningPlan plan = createEntity(request.name(), request.description());
        plan.setTargetDate(request.targetDate());
        return PlanResponse.from(plan, Progress.NONE, pace(plan, Progress.NONE, Map.of()));
    }

    public List<PlanResponse> search(String search, PlanStatus status) {
        String term = search == null || search.isBlank() ? null : escapeLike(search.strip());
        return withProgress(plans.search(term, status));
    }

    /** Plans as API responses with progress and pace (two queries in total). */
    public List<PlanResponse> withProgress(List<LearningPlan> list) {
        Map<Long, Progress> byPlan = progress.progressByPlan();
        Map<Long, Integer> recent = recentCompletions();
        return list.stream().map(p -> {
            Progress pr = byPlan.getOrDefault(p.getId(), Progress.NONE);
            return PlanResponse.from(p, pr, pace(p, pr, recent));
        }).toList();
    }

    public PlanResponse get(Long id) {
        return response(findPlan(id));
    }

    private PlanResponse response(LearningPlan plan) {
        Progress pr = progress.progressOfPlan(plan.getId());
        return PlanResponse.from(plan, pr, pace(plan, pr, plan.getTargetDate() == null ? Map.of() : recentCompletions()));
    }

    private Map<Long, Integer> recentCompletions() {
        return progress.completedSinceByPlan(Instant.now().minus(Duration.ofDays(Pace.RECENT_DAYS)));
    }

    private static Pace pace(LearningPlan plan, Progress pr, Map<Long, Integer> recent) {
        return Pace.of(plan.getTargetDate(), pr, recent.getOrDefault(plan.getId(), 0), LocalDate.now());
    }

    public Progress progressOf(Long id) {
        findPlan(id);
        return progress.progressOfPlan(id);
    }

    @Transactional
    public PlanResponse update(Long id, UpdatePlanRequest request) {
        LearningPlan plan = findPlan(id);
        if (plan.getVersion() != request.version()) {
            throw new StaleUpdateException("Plan", id, request.version(), plan.getVersion());
        }
        String name = request.name().strip();
        ensureNameFree(name, id);
        plan.setName(name);
        plan.setDescription(blankToNull(request.description()));
        plan.setStatus(request.status());
        plan.setTargetDate(request.targetDate());
        // Flush now so the response carries the incremented version.
        plans.saveAndFlush(plan);
        return response(plan);
    }

    /** Deletes the plan; the database cascades the delete to all of its items. */
    @Transactional
    public void delete(Long id) {
        if (plans.deleteWithContents(id) == 0) {
            throw new NotFoundException("Plan", id);
        }
    }

    /** Lookup used by other services (items, import) that need the managed entity. */
    public LearningPlan findPlan(Long id) {
        return plans.findById(id).orElseThrow(() -> new NotFoundException("Plan", id));
    }

    /** Creates and saves a plan (also used by import into a new plan). */
    @Transactional
    public LearningPlan createEntity(String name, String description) {
        String clean = name.strip();
        ensureNameFree(clean, null);
        return plans.save(new LearningPlan(clean, blankToNull(description)));
    }

    public void ensureNameFree(String name, Long exceptId) {
        boolean taken = exceptId == null
                ? plans.existsByNameIgnoreCase(name)
                : plans.existsByNameIgnoreCaseAndIdNot(name, exceptId);
        if (taken) {
            throw new ConflictException("A plan named '%s' already exists".formatted(name));
        }
    }

    /** Records activity on a plan (called when its items change). */
    @Transactional
    public void touch(Long planId) {
        findPlan(planId).touch();
    }

    /** Makes user input safe inside a LIKE pattern (see the {@code escape '\'} in the query). */
    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
