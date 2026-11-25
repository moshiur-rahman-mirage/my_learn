package com.mylearn.tracker.sync;

import java.util.List;

/** Entity names and composite keys of the sync protocol. */
final class SyncKeys {

    /** Order in which incoming upserts are applied (parents before children); deletes run in reverse. */
    static final List<String> ENTITY_ORDER =
            List.of("plan", "item", "note", "link", "timeLog", "itemTag", "review", "confidence", "flashcard");

    private SyncKeys() {
    }

    /**
     * Splits {@code <itemKey>|<rest>} at the FIRST bar: item keys are UUIDs (no bar), while the
     * rest may be a tag name that contains one.
     */
    static String[] split(String key) {
        int bar = key.indexOf('|');
        if (bar <= 0 || bar == key.length() - 1) {
            throw new IllegalArgumentException("Not a composite key: " + key);
        }
        return new String[]{key.substring(0, bar), key.substring(bar + 1)};
    }
}
