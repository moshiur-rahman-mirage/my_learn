package com.mylearn.tracker.search;

import com.mylearn.tracker.common.HtmlText;
import com.mylearn.tracker.note.NoteFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writes the plain text of notes into the search index (see V3__learning_features.sql for why
 * this isn't a trigger). Saving a note updates its row; {@link #reindexNotes()} rebuilds all of
 * them at start-up and after a restore.
 */
@Component
public class SearchIndexer {

    private static final Logger log = LoggerFactory.getLogger(SearchIndexer.class);

    private final JdbcTemplate jdbc;

    public SearchIndexer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** @param content null or empty when the note was deleted */
    @Transactional
    public void indexNote(Long itemId, String content, NoteFormat format) {
        String text = format == NoteFormat.MARKDOWN
                ? (content == null ? "" : content.replaceAll("\\s+", " ").strip())
                : HtmlText.toPlainText(content);
        jdbc.update("UPDATE search_index SET note = ? WHERE rowid = ?", text, itemId);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void reindexNotes() {
        jdbc.update("UPDATE search_index SET note = '' WHERE note <> ''");
        var notes = jdbc.queryForList("SELECT item_id, content, format FROM item_note");
        for (var note : notes) {
            indexNote(((Number) note.get("item_id")).longValue(), (String) note.get("content"),
                    NoteFormat.valueOf((String) note.get("format")));
        }
        log.debug("Search index: {} note(s) indexed", notes.size());
    }
}
