package com.mylearn.tracker.plan;

import com.mylearn.tracker.common.persistence.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;

/**
 * A learning roadmap. Its items reference it via {@code learning_item.plan_id}. There is
 * deliberately no {@code items} collection here: the database cascades deletes, and hierarchy
 * operations use targeted repository queries instead of loading object graphs.
 */
@Entity
@Table(name = "learning_plan")
public class LearningPlan extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status = PlanStatus.ACTIVE;

    /** Optional "finish by" day. */
    @Column(name = "target_date")
    private LocalDate targetDate;

    @Version
    private long version;

    protected LearningPlan() {
        // for JPA
    }

    public LearningPlan(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public long getVersion() {
        return version;
    }
}
