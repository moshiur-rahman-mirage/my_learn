package com.mylearn.tracker.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface LearningItemRepository extends JpaRepository<LearningItem, Long> {

    /** All items of a plan, flat. Tree assembly happens in Java (one query per plan). */
    @Query("select i from LearningItem i where i.plan.id = :planId order by i.displayOrder, i.id")
    List<LearningItem> findAllByPlan(@Param("planId") Long planId);

    @Query("select i from LearningItem i where i.plan.id = :planId and i.parent is null order by i.displayOrder")
    List<LearningItem> findRoots(@Param("planId") Long planId);

    @Query("select i from LearningItem i where i.parent.id = :parentId order by i.displayOrder")
    List<LearningItem> findChildren(@Param("parentId") Long parentId);

    boolean existsByParent_Id(Long parentId);

    @Query("select i.externalId from LearningItem i where i.externalId in :ids")
    List<String> findExistingExternalIds(@Param("ids") Collection<String> ids);

    long countByPlan_Id(Long planId);

    /**
     * Deletes one item. The database's {@code ON DELETE CASCADE} removes the whole subtree in the
     * same statement, so a subtree can never be half-deleted. The persistence context is
     * flushed before and cleared after, so no stale descendant entities survive in memory.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from LearningItem i where i.id = :id")
    int deleteWithSubtree(@Param("id") Long id);
}
