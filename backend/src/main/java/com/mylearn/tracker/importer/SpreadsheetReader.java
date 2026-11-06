package com.mylearn.tracker.importer;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Turns an uploaded CSV or XLSX file into plain-text {@link SheetData}. */
public final class SpreadsheetReader {

    private SpreadsheetReader() {
    }

    public static List<SheetData> read(String fileName, byte[] content) {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        try {
            if (lower.endsWith(".xlsx")) {
                return readXlsx(content);
            }
            if (lower.endsWith(".csv") || lower.endsWith(".txt")) {
                return List.of(readCsv(baseName(fileName), content));
            }
        } catch (IOException | RuntimeException e) {
            throw new BusinessRuleException("Could not read '%s': %s".formatted(fileName, e.getMessage()));
        }
        throw new BusinessRuleException("Unsupported file type. Upload a .csv or .xlsx file.");
    }

    // --- XLSX ---------------------------------------------------------------------------------

    private static List<SheetData> readXlsx(byte[] content) throws IOException {
        List<SheetData> sheets = new ArrayList<>();
        DataFormatter dates = new DataFormatter(Locale.ROOT);
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(content))) {
            for (Sheet sheet : wb) {
                List<List<String>> rows = new ArrayList<>();
                for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    List<String> cells = new ArrayList<>();
                    if (row != null) {
                        for (int c = 0; c < Math.max(row.getLastCellNum(), 0); c++) {
                            cells.add(cellText(row.getCell(c), dates));
                        }
                    }
                    rows.add(cells);
                }
                sheets.add(new SheetData(sheet.getSheetName(), rows));
            }
        }
        return sheets;
    }

    /** Text of a cell; formula cells use their cached result (we never evaluate formulas). */
    private static String cellText(Cell cell, DataFormatter dates) {
        if (cell == null) {
            return "";
        }
        CellType type = cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
        return switch (type) {
            case STRING -> cell.getStringCellValue().strip();
            case BOOLEAN -> cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? dates.formatCellValue(cell) : number(cell.getNumericCellValue());
            default -> "";
        };
    }

    private static String number(double d) {
        if (d == Math.rint(d) && Math.abs(d) < 1e15) {
            return Long.toString((long) d);
        }
        return BigDecimal.valueOf(d).stripTrailingZeros().toPlainString();
    }

    // --- CSV ----------------------------------------------------------------------------------

    private static SheetData readCsv(String name, byte[] content) throws IOException {
        String text = decode(content);
        if (text.startsWith("﻿")) {
            text = text.substring(1); // UTF-8 BOM written by Excel
        }
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(guessDelimiter(text))
                .setIgnoreEmptyLines(false)
                .setTrim(true)
                .get();
        List<List<String>> rows = new ArrayList<>();
        try (CSVParser parser = CSVParser.parse(new StringReader(text), format)) {
            for (CSVRecord record : parser) {
                rows.add(record.toList());
            }
        }
        return new SheetData(name, rows);
    }

    /** UTF-8 if valid, otherwise Windows-1252 (what older Excel versions write). */
    private static String decode(byte[] content) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(content)).toString();
        } catch (CharacterCodingException e) {
            return new String(content, java.nio.charset.Charset.forName("windows-1252"));
        }
    }

    /** Comma, semicolon (European Excel) or tab, whichever is most frequent in the first line. */
    private static char guessDelimiter(String text) {
        String firstLine = text.lines().filter(l -> !l.isBlank()).findFirst().orElse("");
        char best = ',';
        long bestCount = firstLine.chars().filter(ch -> ch == ',').count();
        for (char candidate : new char[]{';', '\t'}) {
            long count = firstLine.chars().filter(ch -> ch == candidate).count();
            if (count > bestCount) {
                best = candidate;
                bestCount = count;
            }
        }
        return best;
    }

    private static String baseName(String fileName) {
        String name = fileName.replaceAll(".*[/\\\\]", "");
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
