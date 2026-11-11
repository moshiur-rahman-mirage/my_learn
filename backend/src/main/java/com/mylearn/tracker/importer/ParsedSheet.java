package com.mylearn.tracker.importer;

import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.note.NoteFormat;

import java.util.ArrayList;
import java.util.List;

/** Result of parsing one sheet: the hierarchy to create plus everything found wrong with it. */
public record ParsedSheet(
        String sheetName,
        int headerRow,
        List<Column> columns,
        List<Node> roots,
        List<Issue> errors,
        List<Issue> warnings,
        Stats stats,
        String contentHash) {

    public record Column(int index, String header, ColumnRole role) {
    }

    /** {@code row} is the 1-based spreadsheet row number (0 = whole sheet). */
    public record Issue(int row, String message) {
    }

    public record Stats(int modules, int topics, int leafTopics, int completed, int notes) {

        public Stats(int modules, int topics, int leafTopics, int completed) {
            this(modules, topics, leafTopics, completed, 0);
        }
    }

    /** Mutable while parsing; read-only afterwards. */
    public static final class Node {
        public final int row;
        public final String name;
        public ItemType type;
        public String description;
        public Priority priority;
        public boolean completed;
        public String externalId;
        /** Note content, or null when the row has none. */
        public String note;
        public NoteFormat noteFormat;
        public final List<Node> children = new ArrayList<>();

        Node(int row, String name) {
            this.row = row;
            this.name = name;
        }
    }
}
