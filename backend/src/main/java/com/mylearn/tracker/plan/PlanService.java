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
        return PlanResponse.from(plan, Progress.NONE);
    }

    public List<PlanResponse> search(String search, PlanStatus status) {
        String term = search == null || search.isBlank() ? null : escapeLike(search.strip());
        Map<Long, Progress> byPlan = progress.progressByPlan();
        return plans.search(term, status).stream()
                .map(p -> PlanResponse.from(p, byPlan.get(p.getId())))
                .toList();
    }

    public PlanResponse get(Long id) {
        return PlanResponse.from(findPlan(id), progress.progressOfPlan(id));
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
        // Flush now so the response carries the incremented version.
        plans.saveAndFlush(plan);
        return PlanResponse.from(plan, progress.progressOfPlan(id));
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
