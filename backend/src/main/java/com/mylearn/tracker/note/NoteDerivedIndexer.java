package com.mylearn.tracker.note;

import com.mylearn.tracker.flashcard.FlashcardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Re-derives flashcards and [[link]] references from every note: at start-up (so notes written
 * before these features, or restored from an old backup, are covered) and after a restore.
 * Card schedules are kept, because cards are matched by their question.
 */
@Component
public class NoteDerivedIndexer {

    private static final Logger log = LoggerFactory.getLogger(NoteDerivedIndexer.class);

    private final JdbcTemplate jdbc;
    private final FlashcardService flashcards;
    private final NoteLinks links;

    public NoteDerivedIndexer(JdbcTemplate jdbc, FlashcardService flashcards, NoteLinks links) {
        this.jdbc = jdbc;
        this.flashcards = flashcards;
        this.links = links;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(10)
    @Transactional
    public void rebuild() {
        jdbc.update("DELETE FROM flashcard WHERE item_id NOT IN (SELECT item_id FROM item_note)");
        jdbc.update("DELETE FROM note_ref WHERE source_item_id NOT IN (SELECT item_id FROM item_note)");
        var notes = jdbc.queryForList("SELECT item_id, content, format FROM item_note");
        int cards = 0;
        for (var note : notes) {
            Long itemId = ((Number) note.get("item_id")).longValue();
            String content = (String) note.get("content");
            NoteFormat format = NoteFormat.valueOf((String) note.get("format"));
            cards += flashcards.syncFromNote(itemId, content, format);
            links.update(itemId, content, format);
        }
        log.info("Indexed {} note(s): {} flashcard(s)", notes.size(), cards);
    }
}
