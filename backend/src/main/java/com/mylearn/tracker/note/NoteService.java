package com.mylearn.tracker.note;

import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.search.SearchIndexer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Notes are saved separately from the item's content fields: they don't bump the item's version,
 * so an open "edit item" dialog never becomes stale because a note was auto-saved.
 */
@Service
@Transactional(readOnly = true)
public class NoteService {

    private static final Pattern TAG = Pattern.compile("<[^>]*>");
    /** Elements that are content on their own, even without text. */
    private static final Pattern NON_TEXT_CONTENT = Pattern.compile("<(hr|img)\\b", Pattern.CASE_INSENSITIVE);

    private final ItemNoteRepository notes;
    private final LearningItemRepository items;
    private final SearchIndexer searchIndex;

    public NoteService(ItemNoteRepository notes, LearningItemRepository items, SearchIndexer searchIndex) {
        this.notes = notes;
        this.items = items;
        this.searchIndex = searchIndex;
    }

    public NoteResponse get(Long itemId) {
        requireItem(itemId);
        return notes.findById(itemId)
                .map(n -> new NoteResponse(itemId, n.getContent(), n.getFormat(), n.getUpdatedAt()))
                .orElse(new NoteResponse(itemId, null, null, null));
    }

    /** Saves rich-text (HTML) content; kept for callers that predate Markdown notes. */
    @Transactional
    public NoteResponse save(Long itemId, String content) {
        return save(itemId, content, NoteFormat.HTML);
    }

    /** Saves the note; an empty editor (e.g. {@code <p></p>} or blank Markdown) deletes it. */
    @Transactional
    public NoteResponse save(Long itemId, String content, NoteFormat format) {
        requireItem(itemId);
        NoteFormat fmt = format == null ? NoteFormat.HTML : format;
        if (isEmpty(content, fmt)) {
            notes.deleteById(itemId);
            searchIndex.indexNote(itemId, null, fmt);
            return new NoteResponse(itemId, null, null, null);
        }
        Instant now = Timestamps.now();
        ItemNote note = notes.findById(itemId).orElse(null);
        if (note == null) {
            note = notes.save(new ItemNote(itemId, content, fmt, now));
        } else {
            note.update(content, fmt, now);
        }
        searchIndex.indexNote(itemId, content, fmt);
        return new NoteResponse(itemId, note.getContent(), note.getFormat(), note.getUpdatedAt());
    }

    static boolean isEmpty(String html) {
        return isEmpty(html, NoteFormat.HTML);
    }

    static boolean isEmpty(String content, NoteFormat format) {
        if (content == null || content.isBlank()) {
            return true;
        }
        if (format == NoteFormat.MARKDOWN) {
            return false;
        }
        if (NON_TEXT_CONTENT.matcher(content).find()) {
            return false;
        }
        return TAG.matcher(content).replaceAll("").replace("&nbsp;", " ").isBlank();
    }

    private void requireItem(Long itemId) {
        if (!items.existsById(itemId)) {
            throw new NotFoundException("Item", itemId);
        }
    }

    /** {@code content}, {@code format} and {@code updatedAt} are null when the item has no note. */
    public record NoteResponse(Long itemId, String content, NoteFormat format, Instant updatedAt) {
    }
}
