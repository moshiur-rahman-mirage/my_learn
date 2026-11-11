package com.mylearn.tracker.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ItemNoteRepository extends JpaRepository<ItemNote, Long> {

    /** Ids of the items of a plan that have a note (for the tree's note markers). */
    @Query("SELECT n.itemId FROM ItemNote n WHERE n.itemId IN "
            + "(SELECT i.id FROM LearningItem i WHERE i.plan.id = :planId)")
    Set<Long> findItemIdsWithNotes(@Param("planId") Long planId);

    /** All notes of a plan's items (for export and sync). */
    @Query("SELECT n FROM ItemNote n WHERE n.itemId IN "
            + "(SELECT i.id FROM LearningItem i WHERE i.plan.id = :planId)")
    List<ItemNote> findAllByPlan(@Param("planId") Long planId);
}
