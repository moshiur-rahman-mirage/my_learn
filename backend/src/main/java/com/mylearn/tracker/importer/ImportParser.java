package com.mylearn.tracker.importer;

import com.mylearn.tracker.importer.ParsedSheet.Column;
import com.mylearn.tracker.importer.ParsedSheet.Issue;
import com.mylearn.tracker.importer.ParsedSheet.Node;
import com.mylearn.tracker.importer.ParsedSheet.Stats;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HexFormat;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Converts spreadsheet rows into an item hierarchy. Pure logic, no database.
 *
 * <p>Two layouts are understood:
 * <ol>
 *   <li><b>Outline by column</b> (e.g. Phase | Topic | Subtopic). A row's level is its leftmost
 *       filled level column; its parent is the nearest earlier row at a shallower level. If a row
 *       repeats its parents' names (Phase and Topic both filled), existing parents are reused.</li>
 *   <li><b>Numeric level</b> (this app's export: id | level | type | name | ...).</li>
 * </ol>
 * Items with children become MODULEs and items without become TOPICs, unless a type column says otherwise.
 * Ticks on anything but a leaf topic are dropped, with a warning.
 */
public final class ImportParser {

    private static final int HEADER_SCAN_ROWS = 10;

    /** Leading bullets such as "■ What is Odoo" or "• Editions". '-' and '*' only count when followed by a space. */
    private static final Pattern BULLETS = Pattern.compile("^(?:[■□▪▫●○•◦‣►▶·]\\s*|[-–—*]\\s+)+");
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final Map<String, ColumnRole> ALIASES = Map.ofEntries(
            Map.entry("phase", ColumnRole.LEVEL), Map.entry("module", ColumnRole.LEVEL),
            Map.entry("section", ColumnRole.LEVEL), Map.entry("chapter", ColumnRole.LEVEL),
            Map.entry("part", ColumnRole.LEVEL), Map.entry("unit", ColumnRole.LEVEL),
            Map.entry("topic", ColumnRole.LEVEL), Map.entry("subtopic", ColumnRole.LEVEL),
            Map.entry("sub-topic", ColumnRole.LEVEL), Map.entry("sub topic", ColumnRole.LEVEL),
            Map.entry("name", ColumnRole.LEVEL), Map.entry("title", ColumnRole.LEVEL),
            Map.entry("item", ColumnRole.LEVEL), Map.entry("lesson", ColumnRole.LEVEL),
            Map.entry("video", ColumnRole.LEVEL),
            Map.entry("level", ColumnRole.DEPTH), Map.entry("depth", ColumnRole.DEPTH),
            Map.entry("type", ColumnRole.TYPE), Map.entry("item type", ColumnRole.TYPE),
            Map.entry("id", ColumnRole.ID), Map.entry("external id", ColumnRole.ID),
            Map.entry("what to learn", ColumnRole.DESCRIPTION), Map.entry("description", ColumnRole.DESCRIPTION),
            Map.entry("details", ColumnRole.DESCRIPTION), Map.entry("notes", ColumnRole.DESCRIPTION),
            Map.entry("note", ColumnRole.DESCRIPTION), Map.entry("summary", ColumnRole.DESCRIPTION),
            Map.entry("priority", ColumnRole.PRIORITY), Map.entry("importance", ColumnRole.PRIORITY),
            Map.entry("completed", ColumnRole.COMPLETED), Map.entry("complete", ColumnRole.COMPLETED),
            Map.entry("done", ColumnRole.COMPLETED), Map.entry("status", ColumnRole.COMPLETED),
            Map.entry("finished", ColumnRole.COMPLETED), Map.entry("checked", ColumnRole.COMPLETED),
            Map.entry("✓", ColumnRole.COMPLETED), Map.entry("✔", ColumnRole.COMPLETED),
            Map.entry("code", ColumnRole.IGNORE), Map.entry("serial", ColumnRole.IGNORE),
            Map.entry("#", ColumnRole.IGNORE), Map.entry("no", ColumnRole.IGNORE),
            Map.entry("no.", ColumnRole.IGNORE), Map.entry("sl", ColumnRole.IGNORE),
            Map.entry("s/n", ColumnRole.IGNORE), Map.entry("sn", ColumnRole.IGNORE));

    /** Roles of which only the first matching column is used. */
    private static final Set<ColumnRole> SINGLE = EnumSet.of(ColumnRole.DEPTH, ColumnRole.TYPE, ColumnRole.ID,
            ColumnRole.DESCRIPTION, ColumnRole.PRIORITY, ColumnRole.COMPLETED);

    private ImportParser() {
    }

    public static ParsedSheet parse(SheetData sheet, ImportOptions options) {
        return new Run(sheet, options == null ? ImportOptions.AUTO : options).parse();
    }

    // --- parsing state for one sheet -----------------------------------------------------------

    private static final class Run {
        private final SheetData sheet;
        private final ImportOptions options;
        private final List<Issue> errors = new ArrayList<>();
        private final List<Issue> warnings = new ArrayList<>();
        private final List<Node> roots = new ArrayList<>();
        private final Map<Node, Node> parentOf = new IdentityHashMap<>();
        private final Map<Node, Integer> levelOf = new IdentityHashMap<>();
        private List<Column> columns = List.of();
        private int header = -1;

        Run(SheetData sheet, ImportOptions options) {
            this.sheet = sheet;
            this.options = options;
        }

        ParsedSheet parse() {
            header = options.headerRow() != null ? options.headerRow() - 1 : detectHeader();
            if (header < 0 || header >= sheet.rows().size()) {
                errors.add(new Issue(0, options.headerRow() != null
                        ? "Header row %d does not exist".formatted(options.headerRow())
                        : "No header row found in the first %d rows. Expected column names such as Name, Phase, Topic, Subtopic, Description, Priority, Completed.".formatted(HEADER_SCAN_ROWS)));
                return result(new Stats(0, 0, 0, 0), "");
            }
            columns = options.columnRoles() != null ? overriddenColumns() : detectColumns();
            List<Integer> levelCols = indexesOf(ColumnRole.LEVEL);
            if (levelCols.isEmpty()) {
                errors.add(new Issue(header + 1, "No name column. Mark at least one column as LEVEL (e.g. Name, Phase or Topic)."));
                return result(new Stats(0, 0, 0, 0), "");
            }

            Integer depthCol = first(ColumnRole.DEPTH);
            List<Node> stack = new ArrayList<>();
            for (int r = header + 1; r < sheet.rows().size(); r++) {
                if (isBlankRow(r)) {
                    continue;
                }
                if (depthCol != null) {
                    parseDepthRow(r, depthCol, levelCols.getFirst(), stack);
                } else {
                    parseOutlineRow(r, levelCols, stack);
                }
            }
            if (roots.isEmpty() && errors.isEmpty()) {
                errors.add(new Issue(0, "No items found below the header row."));
            }

            int[] counts = new int[4];
            StringBuilder hashInput = new StringBuilder();
            for (Node root : roots) {
                finish(root, 0, counts, hashInput);
            }
            return result(new Stats(counts[0], counts[1], counts[2], counts[3]), sha256(hashInput.toString()));
        }

        // --- header & columns ------------------------------------------------------------------

        private int detectHeader() {
            int limit = Math.min(HEADER_SCAN_ROWS, sheet.rows().size());
            for (int minMatches = 2; minMatches >= 1; minMatches--) {
                for (int r = 0; r < limit; r++) {
                    int matches = 0;
                    for (String cell : sheet.rows().get(r)) {
                        if (ALIASES.containsKey(normalize(cell))) {
                            matches++;
                        }
                    }
                    if (matches >= minMatches) {
                        return r;
                    }
                }
            }
            return -1;
        }

        private List<Column> detectColumns() {
            List<Column> result = new ArrayList<>();
            Set<ColumnRole> used = EnumSet.noneOf(ColumnRole.class);
            for (int c = 0; c < sheet.columnCount(); c++) {
                String headerText = sheet.cell(header, c);
                ColumnRole role = ALIASES.get(normalize(headerText));
                if (role == null) {
                    // e.g. a checkbox column whose header cell is itself a checkbox (no text)
                    role = !used.contains(ColumnRole.COMPLETED) && isBooleanColumn(c) ? ColumnRole.COMPLETED : ColumnRole.IGNORE;
                }
                if (SINGLE.contains(role) && !used.add(role)) {
                    role = ColumnRole.IGNORE;
                }
                result.add(new Column(c, headerText, role));
            }
            return result;
        }

        private List<Column> overriddenColumns() {
            List<Column> result = new ArrayList<>();
            Set<ColumnRole> used = EnumSet.noneOf(ColumnRole.class);
            List<ColumnRole> roles = options.columnRoles();
            for (int c = 0; c < sheet.columnCount(); c++) {
                ColumnRole role = c < roles.size() && roles.get(c) != null ? roles.get(c) : ColumnRole.IGNORE;
                if (SINGLE.contains(role) && !used.add(role)) {
                    warnings.add(new Issue(header + 1, "Only one %s column is used; column %d ignored".formatted(role, c + 1)));
                    role = ColumnRole.IGNORE;
                }
                result.add(new Column(c, sheet.cell(header, c), role));
            }
            return result;
        }

        private boolean isBooleanColumn(int c) {
            int values = 0;
            int booleans = 0;
            for (int r = header + 1; r < sheet.rows().size(); r++) {
                String v = sheet.cell(r, c);
                if (!v.isBlank()) {
                    values++;
                    if (parseBoolean(v) != null) {
                        booleans++;
                    }
                }
            }
            return values > 0 && booleans >= values * 0.8;
        }

        // --- rows ------------------------------------------------------------------------------

        private void parseOutlineRow(int r, List<Integer> levelCols, List<Node> stack) {
            List<Integer> filled = new ArrayList<>();
            String[] names = new String[levelCols.size()];
            for (int l = 0; l < levelCols.size(); l++) {
                names[l] = cleanName(sheet.cell(r, levelCols.get(l)));
                if (!names[l].isEmpty()) {
                    filled.add(l);
                }
            }
            if (filled.isEmpty()) {
                reportNameless(r);
                return;
            }
            Node deepest = null;
            for (int k = 0; k < filled.size(); k++) {
                int level = filled.get(k);
                boolean last = k == filled.size() - 1;
                Node parent = nearestAbove(stack, level);
                Node existing = level < stack.size() ? stack.get(level) : null;
                if (!last && existing != null && existing.name.equals(names[level]) && parentOf.get(existing) == parent) {
                    deepest = existing; // repeated parent name in a denormalised sheet
                    continue;
                }
                if (k == 0) {
                    warnAboutGaps(r, level, parent, names[level]);
                }
                deepest = add(r, names[level], parent, level, stack);
            }
            applyDetails(r, deepest);
        }

        private void parseDepthRow(int r, int depthCol, int nameCol, List<Node> stack) {
            String name = cleanName(sheet.cell(r, nameCol));
            if (name.isEmpty()) {
                reportNameless(r);
                return;
            }
            String rawDepth = sheet.cell(r, depthCol).strip();
            int level;
            try {
                level = Integer.parseInt(rawDepth.replaceAll("\\.0+$", "")) - 1;
            } catch (NumberFormatException e) {
                level = -1;
            }
            if (level < 0) {
                errors.add(new Issue(r + 1, "Level '%s' is not a whole number of 1 or more".formatted(rawDepth)));
                return;
            }
            Node parent = nearestAbove(stack, level);
            warnAboutGaps(r, level, parent, name);
            applyDetails(r, add(r, name, parent, level, stack));
        }

        private Node add(int r, String name, Node parent, int level, List<Node> stack) {
            Node node = new Node(r + 1, name);
            (parent == null ? roots : parent.children).add(node);
            parentOf.put(node, parent);
            levelOf.put(node, level);
            while (stack.size() <= level) {
                stack.add(null);
            }
            stack.set(level, node);
            for (int i = level + 1; i < stack.size(); i++) {
                stack.set(i, null);
            }
            return node;
        }

        private void warnAboutGaps(int r, int level, Node parent, String name) {
            int parentLevel = parent == null ? -1 : levelOf.get(parent);
            if (level > parentLevel + 1) {
                warnings.add(new Issue(r + 1, parent == null
                        ? "'%s' has no parent row above it; added at the top level".formatted(name)
                        : "'%s' skips a level; added under '%s'".formatted(name, parent.name)));
            }
        }

        private void reportNameless(int r) {
            boolean hasDetails = columns.stream()
                    .filter(c -> c.role() == ColumnRole.DESCRIPTION || c.role() == ColumnRole.PRIORITY)
                    .anyMatch(c -> !sheet.cell(r, c.index()).isBlank());
            if (hasDetails) {
                errors.add(new Issue(r + 1, "Row has a description or priority but no name"));
            }
        }

        private void applyDetails(int r, Node node) {
            String description = text(r, ColumnRole.DESCRIPTION);
            if (description != null && isHeaderLabel(description)) {
                warnings.add(new Issue(r + 1, "Description '%s' looks like a column header; ignored".formatted(description)));
                description = null;
            }
            node.description = description;

            String priority = text(r, ColumnRole.PRIORITY);
            if (priority != null) {
                node.priority = parsePriority(priority);
                if (node.priority == null) {
                    warnings.add(new Issue(r + 1, "Unknown priority '%s' ignored (use Must, Should or Nice)".formatted(priority)));
                }
            }

            String completed = text(r, ColumnRole.COMPLETED);
            if (completed != null) {
                Boolean value = parseBoolean(completed);
                if (value == null) {
                    warnings.add(new Issue(r + 1, "Completion value '%s' not understood; treated as not completed".formatted(completed)));
                } else {
                    node.completed = value;
                }
            }

            String type = text(r, ColumnRole.TYPE);
            if (type != null) {
                node.type = parseType(type);
                if (node.type == null) {
                    warnings.add(new Issue(r + 1, "Unknown type '%s'; detected from structure instead".formatted(type)));
                }
            }

            String id = text(r, ColumnRole.ID);
            if (id != null) {
                if (UUID_PATTERN.matcher(id).matches()) {
                    node.externalId = id.toLowerCase(Locale.ROOT);
                } else {
                    warnings.add(new Issue(r + 1, "Id '%s' is not a valid id; ignored".formatted(id)));
                }
            }
        }

        // --- post-processing -------------------------------------------------------------------

        /** Decides types, drops ticks that can't apply, counts and hashes, depth-first in sheet order. */
        private void finish(Node node, int depth, int[] counts, StringBuilder hash) {
            boolean leaf = node.children.isEmpty();
            if (node.type == null) {
                node.type = leaf ? ItemType.TOPIC : ItemType.MODULE;
            }
            boolean checkable = leaf && node.type == ItemType.TOPIC;
            if (node.completed && !checkable) {
                warnings.add(new Issue(node.row, "'%s' %s, so its tick is ignored; its progress is calculated from its contents"
                        .formatted(node.name, leaf ? "is a module" : "has sub-items")));
                node.completed = false;
            }
            counts[node.type == ItemType.MODULE ? 0 : 1]++;
            if (checkable) {
                counts[2]++;
                if (node.completed) {
                    counts[3]++;
                }
            }
            hash.append(depth).append('|').append(node.type).append('|').append(node.name).append('|')
                    .append(node.description).append('|').append(node.priority).append('|').append(node.completed).append('\n');
            for (Node child : node.children) {
                finish(child, depth + 1, counts, hash);
            }
        }

        // --- helpers ---------------------------------------------------------------------------

        private ParsedSheet result(Stats stats, String hash) {
            return new ParsedSheet(sheet.name(), header + 1, columns, roots, errors, warnings, stats, hash);
        }

        private Node nearestAbove(List<Node> stack, int level) {
            for (int l = Math.min(level, stack.size()) - 1; l >= 0; l--) {
                if (stack.get(l) != null) {
                    return stack.get(l);
                }
            }
            return null;
        }

        private boolean isBlankRow(int r) {
            return sheet.rows().get(r).stream().allMatch(c -> c == null || c.isBlank());
        }

        private String text(int r, ColumnRole role) {
            Integer col = first(role);
            if (col == null) {
                return null;
            }
            String v = sheet.cell(r, col).strip();
            return v.isEmpty() ? null : v;
        }

        private Integer first(ColumnRole role) {
            return columns.stream().filter(c -> c.role() == role).map(Column::index).findFirst().orElse(null);
        }

        private List<Integer> indexesOf(ColumnRole role) {
            return columns.stream().filter(c -> c.role() == role).map(Column::index).toList();
        }

        private boolean isHeaderLabel(String value) {
            String n = normalize(value);
            return ALIASES.get(n) == ColumnRole.DESCRIPTION
                    || columns.stream().anyMatch(c -> normalize(c.header()).equals(n));
        }
    }

    // --- value parsing (package-private for tests) ----------------------------------------------

    static String normalize(String s) {
        return s == null ? "" : s.strip().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").replaceAll(":$", "");
    }

    static String cleanName(String s) {
        return s == null ? "" : BULLETS.matcher(s.strip()).replaceFirst("").strip();
    }

    static Boolean parseBoolean(String s) {
        return switch (normalize(s)) {
            case "true", "yes", "y", "1", "x", "✓", "✔", "✅", "done", "completed", "complete" -> Boolean.TRUE;
            case "false", "no", "n", "0", "", "pending", "todo", "to do", "not done" -> Boolean.FALSE;
            default -> null;
        };
    }

    static Priority parsePriority(String s) {
        return switch (normalize(s)) {
            case "must", "must have", "must-have", "high" -> Priority.MUST;
            case "should", "should have", "should-have", "medium" -> Priority.SHOULD;
            case "nice", "nice to have", "nice-to-have", "could", "low" -> Priority.NICE;
            default -> null;
        };
    }

    static ItemType parseType(String s) {
        return switch (normalize(s)) {
            case "module", "phase", "section", "chapter" -> ItemType.MODULE;
            case "topic", "subtopic", "sub-topic", "lesson" -> ItemType.TOPIC;
            default -> null;
        };
    }

    private static String sha256(String s) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
