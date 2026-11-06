package com.mylearn.tracker.importer;

import java.util.List;

/**
 * User overrides for parsing; any {@code null} means "detect automatically".
 *
 * @param sheet       sheet name (XLSX); defaults to the first sheet with data
 * @param headerRow   1-based header row number
 * @param columnRoles one role per column, left to right
 */
public record ImportOptions(String sheet, Integer headerRow, List<ColumnRole> columnRoles) {

    public static final ImportOptions AUTO = new ImportOptions(null, null, null);

    public ImportOptions forSheet(String sheetName) {
        return new ImportOptions(sheetName, null, null);
    }
}
