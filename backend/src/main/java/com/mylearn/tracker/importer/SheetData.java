package com.mylearn.tracker.importer;

import java.util.List;

/** One sheet as plain text cells (empty string for blank cells). A CSV file is a single sheet. */
public record SheetData(String name, List<List<String>> rows) {

    public String cell(int row, int col) {
        List<String> r = rows.get(row);
        return col < r.size() && r.get(col) != null ? r.get(col) : "";
    }

    public int columnCount() {
        return rows.stream().mapToInt(List::size).max().orElse(0);
    }
}
