package com.mylearn.tracker.importer;

import com.mylearn.tracker.importer.ParsedSheet.Node;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Parser rules on in-memory sheets shaped like the real roadmap spreadsheets. */
class ImportParserTest {

    /** Same structure as the user's "Odoo" sheet: title row first, header on row 2, unlabeled checkbox column. */
    private static final SheetData ODOO_LIKE = sheet("Odoo",
            row("", "", "", "", "", "13.09"),
            row("Phase", "Topic", "Subtopic", "What to Learn", "Priority", "FALSE"),
            row("PHASE 0 — FOUNDATIONS", "", "", "What to Learn", "", "TRUE"),
            row("", "■ What is Odoo", "", "", "", "FALSE"),
            row("", "", "• Editions", "Community vs Enterprise", "Must", "TRUE"),
            row("", "", "• Versioning", "Release cycle", "Should", "FALSE"),
            row("", "■ Odoo as a User", "", "", "Must", "FALSE"),
            row("", "", "• Studio", "No-code", "Nice", "TRUE"),
            row("", "", "", "", "", ""),
            row("PHASE 1 — SETUP", "", "", "", "", "FALSE"),
            row("", "■ Installation", "", "", "Must", "FALSE"));

    @Test
    void parsesOutlineByColumnLikeTheOdooSheet() {
        ParsedSheet parsed = ImportParser.parse(ODOO_LIKE, ImportOptions.AUTO);

        assertThat(parsed.errors()).isEmpty();
        assertThat(parsed.headerRow()).isEqualTo(2);
        assertThat(parsed.columns()).extracting(ParsedSheet.Column::role).containsExactly(
                ColumnRole.LEVEL, ColumnRole.LEVEL, ColumnRole.LEVEL, ColumnRole.DESCRIPTION, ColumnRole.PRIORITY,
                ColumnRole.COMPLETED);

        assertThat(parsed.roots()).extracting(n -> n.name).containsExactly("PHASE 0 — FOUNDATIONS", "PHASE 1 — SETUP");
        Node phase0 = parsed.roots().get(0);
        assertThat(phase0.type).isEqualTo(ItemType.MODULE);
        assertThat(phase0.completed).isFalse();                        // stale parent tick dropped
        assertThat(phase0.description).isNull();                       // "What to Learn" header text dropped
        Node whatIs = phase0.children.get(0);
        assertThat(whatIs.name).isEqualTo("What is Odoo");             // bullet stripped
        assertThat(whatIs.children).extracting(n -> n.name).containsExactly("Editions", "Versioning");
        Node editions = whatIs.children.get(0);
        assertThat(editions.type).isEqualTo(ItemType.TOPIC);
        assertThat(editions.priority).isEqualTo(Priority.MUST);
        assertThat(editions.description).isEqualTo("Community vs Enterprise");
        assertThat(editions.completed).isTrue();
        assertThat(phase0.children.get(1).priority).isEqualTo(Priority.MUST); // priority on a parent is kept

        // "Installation" has no children -> a checkable leaf topic
        assertThat(parsed.roots().get(1).children.get(0).type).isEqualTo(ItemType.TOPIC);
        assertThat(parsed.stats()).isEqualTo(new ParsedSheet.Stats(4, 4, 4, 2));
        assertThat(parsed.warnings()).extracting(ParsedSheet.Issue::message)
                .anyMatch(m -> m.contains("looks like a column header"))
                .anyMatch(m -> m.contains("tick is ignored"));
    }

    @Test
    void flatListWithCodeColumnBecomesRootLevelTopics() {
        SheetData accounting = sheet("Accounting",
                row("", "", "32.7"),
                row("code", "name", "TRUE"),
                row("FA1", "Accounting Basics", "TRUE"),
                row("FA2", "Accounting Equation", "FALSE"),
                row("", "Bank Reconciliation", "FALSE"));

        ParsedSheet parsed = ImportParser.parse(accounting, ImportOptions.AUTO);

        assertThat(parsed.errors()).isEmpty();
        assertThat(parsed.columns().get(0).role()).isEqualTo(ColumnRole.IGNORE);   // codes dropped
        assertThat(parsed.roots()).extracting(n -> n.name)
                .containsExactly("Accounting Basics", "Accounting Equation", "Bank Reconciliation");
        assertThat(parsed.roots()).allMatch(n -> n.type == ItemType.TOPIC);
        assertThat(parsed.stats().completed()).isEqualTo(1);
    }

    @Test
    void parsesTheAppsOwnExportFormatWithNumericLevels() {
        SheetData export = sheet("export",
                row("id", "level", "type", "name", "description", "priority", "completed"),
                row("", "1", "MODULE", "Phase", "", "", ""),
                row("", "2", "TOPIC", "Relational", "", "MUST", ""),
                row("", "3", "TOPIC", "Many2one", "", "", "TRUE"),
                row("", "2", "MODULE", "Empty module", "", "", ""),
                row("", "1", "TOPIC", "Root topic", "", "NICE", "FALSE"));

        ParsedSheet parsed = ImportParser.parse(export, ImportOptions.AUTO);

        assertThat(parsed.errors()).isEmpty();
        Node phase = parsed.roots().get(0);
        assertThat(phase.children).extracting(n -> n.name).containsExactly("Relational", "Empty module");
        assertThat(phase.children.get(0).type).isEqualTo(ItemType.TOPIC);     // topic with a sub-topic
        assertThat(phase.children.get(1).type).isEqualTo(ItemType.MODULE);    // explicit type kept on a leaf
        assertThat(phase.children.get(0).children.get(0).completed).isTrue();
        assertThat(parsed.roots().get(1).name).isEqualTo("Root topic");
    }

    @Test
    void repeatedParentNamesAreMergedNotDuplicated() {
        SheetData denormalised = sheet("s",
                row("Phase", "Topic"),
                row("Basics", "Variables"),
                row("Basics", "Loops"),
                row("Advanced", "Streams"));

        ParsedSheet parsed = ImportParser.parse(denormalised, ImportOptions.AUTO);

        assertThat(parsed.roots()).extracting(n -> n.name).containsExactly("Basics", "Advanced");
        assertThat(parsed.roots().get(0).children).extracting(n -> n.name).containsExactly("Variables", "Loops");
    }

    @Test
    void reportsErrorsAndWarnings() {
        SheetData bad = sheet("s",
                row("Phase", "Topic", "Description", "Priority", "Done"),
                row("", "Orphan topic", "", "", ""),
                row("", "", "Description without a name", "", ""),
                row("P", "", "", "", ""),
                row("", "T", "", "URGENT", "maybe"));

        ParsedSheet parsed = ImportParser.parse(bad, ImportOptions.AUTO);

        assertThat(parsed.errors()).extracting(ParsedSheet.Issue::row).containsExactly(3);
        assertThat(parsed.warnings()).extracting(ParsedSheet.Issue::message)
                .anyMatch(m -> m.contains("no parent row"))
                .anyMatch(m -> m.contains("Unknown priority 'URGENT'"))
                .anyMatch(m -> m.contains("'maybe' not understood"));
    }

    @Test
    void noRecognisableHeaderIsAnError_andOverridesFixIt() {
        SheetData unknown = sheet("s",
                row("Kapitel", "Thema"),
                row("Grundlagen", "Variablen"));

        assertThat(ImportParser.parse(unknown, ImportOptions.AUTO).errors()).isNotEmpty();

        ParsedSheet fixed = ImportParser.parse(unknown,
                new ImportOptions(null, 1, List.of(ColumnRole.LEVEL, ColumnRole.LEVEL)));
        assertThat(fixed.errors()).isEmpty();
        assertThat(fixed.roots().get(0).children.get(0).name).isEqualTo("Variablen");
    }

    @Test
    void sameContentGivesSameHash() {
        String a = ImportParser.parse(ODOO_LIKE, ImportOptions.AUTO).contentHash();
        String b = ImportParser.parse(ODOO_LIKE, ImportOptions.AUTO).contentHash();
        assertThat(a).hasSize(64).isEqualTo(b);
    }

    @Test
    void valueParsers() {
        assertThat(ImportParser.cleanName("■ What is Odoo")).isEqualTo("What is Odoo");
        assertThat(ImportParser.cleanName("- item")).isEqualTo("item");
        assertThat(ImportParser.cleanName("-i, -u flags")).isEqualTo("-i, -u flags"); // a dash without a space is kept
        assertThat(ImportParser.parseBoolean("✓")).isTrue();
        assertThat(ImportParser.parseBoolean("No")).isFalse();
        assertThat(ImportParser.parsePriority("Nice to have")).isEqualTo(Priority.NICE);
        assertThat(ImportParser.parseType("Phase")).isEqualTo(ItemType.MODULE);
    }

    private static SheetData sheet(String name, List<String>... rows) {
        return new SheetData(name, Arrays.asList(rows));
    }

    private static List<String> row(String... cells) {
        return Arrays.asList(cells);
    }
}
