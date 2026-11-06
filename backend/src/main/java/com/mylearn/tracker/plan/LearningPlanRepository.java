package com.mylearn.tracker.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningPlanRepository extends JpaRepository<LearningPlan, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Optional filters: {@code search} matches name or description (case-insensitive, substring),
     * {@code status} restricts to one status. Pass {@code null} to skip a filter.
     */
    @Query("""
            select p from LearningPlan p
            where (:status is null or p.status = :status)
              and (:search is null
                   or lower(p.name) like lower(concat('%', :search, '%')) escape '\\'
                   or lower(p.description) like lower(concat('%', :search, '%')) escape '\\')
            order by lower(p.name)
            """)
    List<LearningPlan> search(@Param("search") String search, @Param("status") PlanStatus status);

    /**
     * Deletes a plan with one statement; {@code ON DELETE CASCADE} removes its items and import
     * history. Bulk delete (instead of {@code delete(entity)}) also works when items of the plan
     * are loaded in the current persistence context, which is flushed before and cleared after.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from LearningPlan p where p.id = :id")
    int deleteWithContents(@Param("id") Long id);
}
