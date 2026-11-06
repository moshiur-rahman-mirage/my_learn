package com.mylearn.tracker.plan;

import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.PlanResponse;
import com.mylearn.tracker.plan.dto.UpdatePlanRequest;
import com.mylearn.tracker.progress.Progress;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService plans;

    public PlanController(PlanService plans) {
        this.plans = plans;
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody CreatePlanRequest request) {
        PlanResponse created = plans.create(request);
        return ResponseEntity.created(URI.create("/api/plans/" + created.id())).body(created);
    }

    @GetMapping
    public List<PlanResponse> list(@RequestParam(required = false) String search,
                                   @RequestParam(required = false) PlanStatus status) {
        return plans.search(search, status);
    }

    @GetMapping("/{id}")
    public PlanResponse get(@PathVariable Long id) {
        return plans.get(id);
    }

    @PutMapping("/{id}")
    public PlanResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePlanRequest request) {
        return plans.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plans.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    public Progress progress(@PathVariable Long id) {
        return plans.progressOf(id);
    }
}
