package com.mylearn.tracker.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoteFormat format;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ItemNote() {
        // for JPA
    }

    public ItemNote(Long itemId, String content, NoteFormat format, Instant updatedAt) {
        this.itemId = itemId;
        this.content = content;
        this.format = format;
        this.updatedAt = updatedAt;
    }

    public void update(String content, NoteFormat format, Instant at) {
        this.content = content;
        this.format = format;
        this.updatedAt = at;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getContent() {
        return content;
    }

    public NoteFormat getFormat() {
        return format;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
