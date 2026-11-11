package com.mylearn.tracker.item;

import com.mylearn.tracker.item.dto.LinkResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Per-item data kept outside {@code learning_item} that the tree shows, loaded per plan.
 *
 * @param withNotes ids of items that have a note
 * @param links     links per item, in display order
 * @param tags      tag names per item, alphabetical
 * @param minutes   time logged on the item itself
 * @param reviewDue next review day of completed topics still being reviewed
 * @param confidence rating 1–5 per rated item
 * @param flashcards number of flashcards per item with any
 */
public record TreeExtras(Set<Long> withNotes, Map<Long, List<LinkResponse>> links, Map<Long, List<String>> tags,
                         Map<Long, Integer> minutes, Map<Long, LocalDate> reviewDue, Map<Long, Integer> confidence,
                         Map<Long, Integer> flashcards) {

    public static final TreeExtras NONE = new TreeExtras(Set.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());

    public static TreeExtras notesOnly(Set<Long> withNotes) {
        return new TreeExtras(withNotes, Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
    }
}
