package com.mylearn.tracker.item;

import com.mylearn.tracker.common.persistence.TimestampedEntity;
import com.mylearn.tracker.plan.LearningPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.OptimisticLock;

import java.time.Instant;
import java.util.UUID;

/**
 * A node in a plan's hierarchy (self-referencing via {@code parent_id}).
 *
 * <p><b>Optimistic locking covers content only</b> (name, type, description, priority). Structural
 * and progress fields (parent, display order, completion) are excluded, so that renumbering
 * siblings or ticking a checkbox doesn't make an open edit form "stale".
 *
 * <p>Only the parent reference is mapped, with no {@code children} collection. Children are read
 * through repository queries, and subtree deletes are done by the database's {@code ON DELETE CASCADE}.
 */
@Entity
@Table(name = "learning_item")
public class LearningItem extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, updatable = false, unique = true)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false, updatable = false)
    private LearningPlan plan;

    @OptimisticLock(excluded = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private LearningItem parent;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType type;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @OptimisticLock(excluded = true)
    @Column(name = "is_completed", nullable = false)
    private boolean completed;

    @OptimisticLock(excluded = true)
    @Column(name = "completed_at")
    private Instant completedAt;

    @OptimisticLock(excluded = true)
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Version
    private long version;

    protected LearningItem() {
        // for JPA
    }

    public LearningItem(LearningPlan plan, LearningItem parent, ItemType type, String name,
                        String description, Priority priority) {
        this.externalId = UUID.randomUUID().toString();
        this.plan = plan;
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    // --- completion -------------------------------------------------------------------------
    // The "who may be completed" rules live in the service; the entity just keeps both fields in sync.

    public void markCompleted(Instant at) {
        this.completed = true;
        this.completedAt = at;
    }

    public void clearCompletion() {
        this.completed = false;
        this.completedAt = null;
    }

    // --- accessors --------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public LearningPlan getPlan() {
        return plan;
    }

    /** Plan id without initializing the lazy plan proxy. */
    public Long getPlanId() {
        return plan.getId();
    }

    public LearningItem getParent() {
        return parent;
    }

    /** Parent id (or {@code null} for a root item) without initializing the lazy parent proxy. */
    public Long getParentId() {
        return parent == null ? null : parent.getId();
    }

    public void setParent(LearningItem parent) {
        this.parent = parent;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public long getVersion() {
        return version;
    }
}
