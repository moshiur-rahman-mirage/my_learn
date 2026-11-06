package com.mylearn.tracker.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** The note of one item; the primary key is the item id (at most one note per item). */
@Entity
@Table(name = "item_note")
public class ItemNote {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(nullable = false)
    private String content;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ItemNote() {
        // for JPA
    }

    public ItemNote(Long itemId, String content, Instant updatedAt) {
        this.itemId = itemId;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    public void update(String content, Instant at) {
        this.content = content;
        this.updatedAt = at;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getContent() {
        return content;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
