package com.mylearn.tracker.plan;

import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.exception.StaleUpdateException;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.UpdatePlanRequest;
import com.mylearn.tracker.progress.ProgressQueries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pure unit tests of PlanService's decision logic, with the repository mocked. */
@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    LearningPlanRepository plans;
    @Mock
    ProgressQueries progress;

    @InjectMocks
    PlanService service;

    @Test
    void createRejectsDuplicateNameAndSavesNothing() {
        when(plans.existsByNameIgnoreCase("Core Java")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreatePlanRequest("  Core Java ", null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Core Java");
        verify(plans, never()).save(any());
    }

    @Test
    void getUnknownPlanThrowsNotFound() {
        when(plans.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(42L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Plan 42 not found");
    }

    @Test
    void updateWithOutdatedVersionIsRejectedBeforeAnyChange() {
        LearningPlan stored = new LearningPlan("Core Java", null); // version 0
        when(plans.findById(1L)).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> service.update(1L, new UpdatePlanRequest("Renamed", null, PlanStatus.ACTIVE, 5L)))
                .isInstanceOf(StaleUpdateException.class);
        verify(plans, never()).saveAndFlush(any());
    }
}
