package com.mylearn.tracker.importer;

/** What a spreadsheet column means to the importer. */
public enum ColumnRole {
    /**
     * Name of an item at one hierarchy level. Several LEVEL columns mean "outline by column":
     * the leftmost (e.g. Phase) is the top level and each further column is one level deeper.
     */
    LEVEL,
    /** Numeric depth (1 = top), used with a single LEVEL/name column (this app's export format). */
    DEPTH,
    TYPE,
    ID,
    DESCRIPTION,
    PRIORITY,
    COMPLETED,
    IGNORE
}
