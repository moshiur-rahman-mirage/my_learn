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
                .map(n -> new NoteResponse(itemId, n.getContent(), n.getUpdatedAt()))
                .orElse(new NoteResponse(itemId, null, null));
    }

    /** Saves the note; an empty editor (e.g. {@code <p></p>}) deletes it. */
    @Transactional
    public NoteResponse save(Long itemId, String content) {
        requireItem(itemId);
        if (isEmpty(content)) {
            notes.deleteById(itemId);
            searchIndex.indexNote(itemId, null);
            return new NoteResponse(itemId, null, null);
        }
        Instant now = Timestamps.now();
        ItemNote note = notes.findById(itemId).orElse(null);
        if (note == null) {
            note = notes.save(new ItemNote(itemId, content, now));
        } else {
            note.update(content, now);
        }
        searchIndex.indexNote(itemId, content);
        return new NoteResponse(itemId, note.getContent(), note.getUpdatedAt());
    }

    static boolean isEmpty(String html) {
        if (html == null || html.isBlank()) {
            return true;
        }
        if (NON_TEXT_CONTENT.matcher(html).find()) {
            return false;
        }
        return TAG.matcher(html).replaceAll("").replace("&nbsp;", " ").isBlank();
    }

    private void requireItem(Long itemId) {
        if (!items.existsById(itemId)) {
            throw new NotFoundException("Item", itemId);
        }
    }

    /** {@code content} and {@code updatedAt} are null when the item has no note. */
    public record NoteResponse(Long itemId, String content, Instant updatedAt) {
    }
}
