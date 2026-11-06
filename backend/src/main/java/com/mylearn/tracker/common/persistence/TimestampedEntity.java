package com.mylearn.tracker.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.hibernate.annotations.OptimisticLock;

import java.time.Instant;

/** Maintains {@code created_at} / {@code updated_at} for every entity. */
@MappedSuperclass
public abstract class TimestampedEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Bookkeeping only: changing it must never count as a "content change" for optimistic locking.
    @OptimisticLock(excluded = true)
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Timestamps.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Timestamps.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Marks the entity as changed "now" without touching content. Used e.g. to bump a plan's
     * last-activity time when one of its items changes. It doesn't increment the optimistic-lock
     * version, because {@code updatedAt} is excluded from locking.
     */
    public void touch() {
        updatedAt = Timestamps.now();
    }
}
