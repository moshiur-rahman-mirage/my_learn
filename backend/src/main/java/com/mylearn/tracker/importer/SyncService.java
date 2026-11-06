package com.mylearn.tracker.importer;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.importer.ParsedSheet.Node;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.item.TreeBuilder;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.review.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.mylearn.tracker.common.Text.blankToNull;

/**
 * CSV sync (docs/ARCHITECTURE.md §6.4): edit an export of a plan in Excel or Google Sheets, upload
 * it, see what changed, and apply the changes you choose.
 *
 * <p>The file is matched to the plan by the {@code id} column. Rows without a known id are
 * <b>additions</b>; known ids with different fields are <b>updates</b>; a different parent is a
 * <b>move</b>; items of the plan missing from the file are <b>deletions</b>, which are never
 * selected by default. If the {@code version} column shows an item was edited in the app after
 * the export, its update is a <b>conflict</b> and is also not selected by default.
 *
 * <p>Like import, preview and apply are stateless: apply re-reads the file and re-compares it with
 * the current data, then applies the selected changes in one transaction.
 */
@Service
@Transactional(readOnly = true)
public class SyncService {

    static final String ORDER_KEY = "order";

    private final PlanService planService;
    private final LearningItemRepository items;
    private final ReviewService reviews;

    public SyncService(PlanService planService, LearningItemRepository items, ReviewService reviews) {
        this.planService = planService;
        this.items = items;
        this.reviews = reviews;
    }

    public SyncPreview preview(Long planId, String fileName, byte[] content) {
        planService.findPlan(planId);
        Diff diff = diff(planId, fileName, content);
        return new SyncPreview(fileName, diff.errors, diff.warnings, diff.changes, diff.versionColumn,
                diff.errors.isEmpty() && !diff.changes.isEmpty());
    }

    // --- comparing ----------------------------------------------------------------------------

    /** One row of the file, linked to its parent row and (if it has a known id) the existing item. */
    private static final class Entry {
        final Node node;
        final Entry parent;
        final int index; // position among its siblings in the file
        final LearningItem existing;
        final Long fileVersion;

        Entry(Node node, Entry parent, int index, LearningItem existing, Long fileVersion) {
            this.node = node;
            this.parent = parent;
            this.index = index;
            this.existing = existing;
            this.fileVersion = fileVersion;
        }

        String key() {
            return existing == null ? "add:" + node.row : "update:" + existing.getExternalId();
        }
    }

    private static final class Diff {
        final List<String> errors = new ArrayList<>();
        final List<String> warnings = new ArrayList<>();
        final List<SyncChange> changes = new ArrayList<>();
        final List<Entry> entries = new ArrayList<>(); // file rows in tree (pre-)order
        final List<LearningItem> current = new ArrayList<>();
        boolean versionColumn;

        Set<String> keys() {
            Set<String> keys = new HashSet<>();
            changes.forEach(c -> keys.add(c.key()));
            return keys;
        }
    }

    private Diff diff(Long planId, String fileName, byte[] content) {
        Diff diff = new Diff();
        List<SheetData> sheets = SpreadsheetReader.read(fileName, content);
        SheetData sheet = sheets.stream()
                .filter(s -> s.rows().stream().anyMatch(r -> r.stream().anyMatch(c -> !c.isBlank())))
                .findFirst().orElse(sheets.getFirst());
        ParsedSheet parsed = ImportParser.parse(sheet, ImportOptions.AUTO);
        parsed.errors().forEach(i -> diff.errors.add(issue(i)));
        parsed.warnings().forEach(i -> diff.warnings.add(issue(i)));
        if (diff.errors.isEmpty() && parsed.columns().stream().noneMatch(c -> c.role() == ColumnRole.ID)) {
            diff.errors.add("This file has no id column. Sync needs a file exported from this plan "
                    + "(plan menu → Export CSV), edited and saved as CSV or Excel.");
        }
        if (!diff.errors.isEmpty()) {
            return diff;
        }
        Integer versionCol = parsed.columns().stream()
                .filter(c -> "version".equals(ImportParser.normalize(c.header())))
                .map(ParsedSheet.Column::index).findFirst().orElse(null);
        diff.versionColumn = versionCol != null;

        diff.current.addAll(items.findAllByPlan(planId));
        Map<String, LearningItem> byExternalId = new HashMap<>();
        diff.current.forEach(i -> byExternalId.put(i.getExternalId(), i));
        Map<Long, TreeNode> nodes = new HashMap<>();
        index(TreeBuilder.build(diff.current), nodes);

        // flatten the file, matching ids
        Set<String> seen = new HashSet<>();
        flatten(parsed.roots(), null, sheet, versionCol, byExternalId, seen, diff);
        if (!diff.errors.isEmpty()) {
            return diff;
        }

        Set<Long> inFile = new HashSet<>();
        for (Entry e : diff.entries) {
            if (e.existing != null) {
                inFile.add(e.existing.getId());
            }
        }

        for (Entry e : diff.entries) {
            if (e.existing == null) {
                diff.changes.add(addition(e));
                continue;
            }
            SyncChange update = update(e);
            if (update != null) {
                diff.changes.add(update);
            }
            SyncChange move = move(e, byIdOf(diff.current));
            if (move != null) {
                diff.changes.add(move);
            }
        }

        for (LearningItem item : diff.current) {
            if (!inFile.contains(item.getId())) {
                TreeNode node = nodes.get(item.getId());
                int inside = node == null ? 0 : node.descendantCount();
                diff.changes.add(new SyncChange("delete:" + item.getExternalId(), ChangeKind.DELETE, item.getName(),
                        pathOf(item), List.of(), false, false, null, null,
                        inside == 0 ? "Not in the file" : "Not in the file; also deletes %d item(s) inside it, unless they are moved out".formatted(inside)));
            }
        }

        int reordered = reorderedGroups(diff.entries);
        if (reordered > 0) {
            diff.changes.add(new SyncChange(ORDER_KEY, ChangeKind.ORDER, null, null, List.of(), false, true, null, null,
                    "%d list(s) of items are in a different order in the file".formatted(reordered)));
        }
        return diff;
    }

    private void flatten(List<Node> nodes, Entry parent, SheetData sheet, Integer versionCol,
                         Map<String, LearningItem> byExternalId, Set<String> seen, Diff diff) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            LearningItem existing = null;
            if (node.externalId != null) {
                if (!seen.add(node.externalId)) {
                    diff.errors.add("Row %d: the id of '%s' appears more than once in the file".formatted(node.row, node.name));
                }
                existing = byExternalId.get(node.externalId);
                if (existing == null) {
                    diff.warnings.add("Row %d: '%s' has an id that isn't in this plan; it will be added as a new item"
                            .formatted(node.row, node.name));
                }
            }
            Long version = null;
            if (versionCol != null) {
                String v = sheet.cell(node.row - 1, versionCol).strip();
                try {
                    version = v.isEmpty() ? null : Long.parseLong(v.replaceAll("\\.0+$", ""));
                } catch (NumberFormatException ignored) {
                    // a mangled version only disables the conflict check for this row
                }
            }
            Entry entry = new Entry(node, parent, i, existing, version);
            diff.entries.add(entry);
            flatten(node.children, entry, sheet, versionCol, byExternalId, seen, diff);
        }
    }

    private static SyncChange addition(Entry e) {
        List<FieldChange> fields = new ArrayList<>();
        fields.add(new FieldChange("type", null, label(e.node.type)));
        if (e.node.priority != null) {
            fields.add(new FieldChange("priority", null, e.node.priority.name()));
        }
        if (e.node.completed) {
            fields.add(new FieldChange("completed", null, "yes"));
        }
        String dependsOn = e.parent != null && e.parent.existing == null ? e.parent.key() : null;
        return new SyncChange(e.key(), ChangeKind.ADD, e.node.name, filePath(e), fields, false, true, e.node.row,
                dependsOn, null);
    }

    /** Field-by-field comparison of a row with its item; null when nothing changed. */
    private static SyncChange update(Entry e) {
        LearningItem item = e.existing;
        Node n = e.node;
        List<FieldChange> fields = new ArrayList<>();
        boolean content = false;
        if (!Objects.equals(item.getName(), n.name.strip())) {
            fields.add(new FieldChange("name", item.getName(), n.name.strip()));
            content = true;
        }
        if (n.type != null && item.getType() != n.type) {
            fields.add(new FieldChange("type", label(item.getType()), label(n.type)));
            content = true;
        }
        if (!Objects.equals(blankToNull(item.getDescription()), blankToNull(n.description))) {
            fields.add(new FieldChange("description", item.getDescription(), blankToNull(n.description)));
            content = true;
        }
        if (item.getPriority() != n.priority) {
            fields.add(new FieldChange("priority", name(item.getPriority()), name(n.priority)));
            content = true;
        }
        if (item.isCompleted() != n.completed) {
            fields.add(new FieldChange("completed", item.isCompleted() ? "yes" : "no", n.completed ? "yes" : "no"));
        }
        if (fields.isEmpty()) {
            return null;
        }
        boolean conflict = content && e.fileVersion != null && e.fileVersion != item.getVersion();
        return new SyncChange(e.key(), ChangeKind.UPDATE, item.getName(), pathOf(item), fields, conflict, !conflict,
                n.row, null, conflict ? "Edited in the app after this file was exported. Applying replaces the app's version." : null);
    }

    private static SyncChange move(Entry e, Map<Long, LearningItem> byId) {
        LearningItem item = e.existing;
        Long currentParent = item.getParentId();
        String to;
        String dependsOn = null;
        if (e.parent == null) {
            if (currentParent == null) {
                return null;
            }
            to = "Top level";
        } else if (e.parent.existing != null) {
            if (Objects.equals(currentParent, e.parent.existing.getId())) {
                return null;
            }
            to = e.parent.existing.getName();
        } else {
            to = e.parent.node.name + " (new)";
            dependsOn = e.parent.key();
        }
        LearningItem from = currentParent == null ? null : byId.get(currentParent);
        return new SyncChange("move:" + item.getExternalId(), ChangeKind.MOVE, item.getName(), pathOf(item),
                List.of(new FieldChange("parent", from == null ? "Top level" : from.getName(), to)),
                false, true, e.node.row, dependsOn, null);
    }

    /** Number of sibling lists whose existing items (staying under the same parent) are in another order. */
    private static int reorderedGroups(List<Entry> entries) {
        Map<Entry, List<Entry>> groups = new IdentityHashMap<>();
        List<Entry> roots = new ArrayList<>();
        for (Entry e : entries) {
            if (e.parent == null) {
                roots.add(e);
            } else {
                groups.computeIfAbsent(e.parent, k -> new ArrayList<>()).add(e);
            }
        }
        int count = isReordered(roots, null) ? 1 : 0;
        for (Map.Entry<Entry, List<Entry>> g : groups.entrySet()) {
            if (g.getKey().existing != null && isReordered(g.getValue(), g.getKey().existing.getId())) {
                count++;
            }
        }
        return count;
    }

    private static boolean isReordered(List<Entry> siblings, Long parentId) {
        List<LearningItem> fileOrder = siblings.stream()
                .filter(e -> e.existing != null && Objects.equals(e.existing.getParentId(), parentId))
                .map(e -> e.existing).toList();
        List<LearningItem> currentOrder = fileOrder.stream()
                .sorted(Comparator.comparingInt(LearningItem::getDisplayOrder).thenComparing(LearningItem::getId)).toList();
        return !fileOrder.equals(currentOrder);
    }

    // --- applying -----------------------------------------------------------------------------

    /**
     * Applies the selected changes in one transaction, in this order: content updates, additions
     * (parents before children), moves, completion, ordering, and deletions last.
     *
     * @param selected keys of the changes to apply, from the preview
     */
    @Transactional
    public SyncResult apply(Long planId, String fileName, byte[] content, Collection<String> selected) {
        LearningPlan plan = planService.findPlan(planId);
        Diff diff = diff(planId, fileName, content);
        if (!diff.errors.isEmpty()) {
            throw new BusinessRuleException("The file has errors: " + diff.errors.getFirst());
        }
        Set<String> keys = diff.keys();
        Set<String> chosen = new LinkedHashSet<>();
        List<String> skipped = new ArrayList<>();
        for (String key : selected) {
            if (keys.contains(key)) {
                chosen.add(key);
            } else {
                skipped.add("A selected change no longer applies (the plan changed since the preview): " + key);
            }
        }
        Instant now = Timestamps.now();
        int added = 0;
        int updated = 0;
        int moved = 0;
        int deleted = 0;

        Map<Entry, LearningItem> resolved = new IdentityHashMap<>();
        List<LearningItem> all = new ArrayList<>(diff.current);
        for (Entry e : diff.entries) {
            if (e.existing != null) {
                resolved.put(e, e.existing);
            }
        }
        Set<LearningItem> fileOrdered = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        Set<Long> touchedGroups = new HashSet<>();
        int appendAt = 1_000_000; // placed after existing siblings until the final renumbering

        // 1. content
        for (Entry e : diff.entries) {
            if (e.existing == null || !chosen.contains(e.key())) {
                continue;
            }
            LearningItem item = e.existing;
            Node n = e.node;
            item.setName(n.name.strip());
            if (n.type != null) {
                if (n.type == ItemType.MODULE && item.isCompleted()) {
                    item.clearCompletion();
                    reviews.cancel(item.getId());
                }
                item.setType(n.type);
            }
            item.setDescription(blankToNull(n.description));
            item.setPriority(n.priority);
            updated++;
        }

        // 2. additions, parents first (entries are in tree order)
        for (Entry e : diff.entries) {
            if (e.existing != null || !chosen.contains(e.key())) {
                continue;
            }
            LearningItem parent = null;
            if (e.parent != null) {
                parent = resolved.get(e.parent);
                if (parent == null) {
                    skipped.add("Row %d '%s' not added: its parent '%s' wasn't added".formatted(e.node.row, e.node.name, e.parent.node.name));
                    continue;
                }
            }
            LearningItem created = new LearningItem(plan, parent, e.node.type == null ? ItemType.TOPIC : e.node.type,
                    e.node.name.strip(), blankToNull(e.node.description), e.node.priority);
            created.setDisplayOrder(appendAt++);
            items.save(created);
            loseCompletion(parent);
            resolved.put(e, created);
            all.add(created);
            fileOrdered.add(created);
            touchedGroups.add(parent == null ? null : parent.getId());
            added++;
        }

        // 3. moves
        for (Entry e : diff.entries) {
            if (e.existing == null || !chosen.contains("move:" + e.existing.getExternalId())) {
                continue;
            }
            LearningItem item = e.existing;
            LearningItem target = null;
            if (e.parent != null) {
                target = resolved.get(e.parent);
                if (target == null) {
                    skipped.add("'%s' not moved: its new parent '%s' wasn't added".formatted(item.getName(), e.parent.node.name));
                    continue;
                }
                if (isSelfOrAncestor(item, target)) {
                    skipped.add("'%s' not moved: '%s' is inside it (apply the other moves too)".formatted(item.getName(), target.getName()));
                    continue;
                }
            }
            touchedGroups.add(item.getParentId());
            item.setParent(target);
            item.setDisplayOrder(appendAt++);
            loseCompletion(target);
            fileOrdered.add(item);
            touchedGroups.add(target == null ? null : target.getId());
            moved++;
        }

        // items about to be deleted, with everything still inside them
        Set<Long> deleteIds = new HashSet<>();
        for (LearningItem item : diff.current) {
            if (chosen.contains("delete:" + item.getExternalId())) {
                deleteIds.add(item.getId());
            }
        }
        Set<LearningItem> doomed = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        for (LearningItem item : all) {
            for (LearningItem p = item; p != null; p = p.getParent()) {
                if (deleteIds.contains(p.getId())) {
                    doomed.add(item);
                    break;
                }
            }
        }
        Map<Long, Integer> childCount = new HashMap<>();
        for (LearningItem item : all) {
            if (!doomed.contains(item) && item.getParentId() != null) {
                childCount.merge(item.getParentId(), 1, Integer::sum);
            }
        }

        // 4. completion (needs the final structure: only topics without sub-items can be ticked)
        for (Entry e : diff.entries) {
            LearningItem item = resolved.get(e);
            if (item == null || doomed.contains(item) || item.isCompleted() == e.node.completed) {
                continue;
            }
            boolean isAddition = e.existing == null;
            if (!isAddition && !chosen.contains(e.key())) {
                continue;
            }
            boolean checkable = item.getType() == ItemType.TOPIC && !childCount.containsKey(item.getId());
            if (e.node.completed) {
                if (!checkable) {
                    skipped.add("'%s' not ticked: only topics without sub-items can be completed".formatted(item.getName()));
                    continue;
                }
                item.markCompleted(now);
                if (!isAddition) {
                    reviews.schedule(item.getId());
                }
            } else {
                item.clearCompletion();
                reviews.cancel(item.getId());
            }
        }

        // 5. order
        boolean orderAll = chosen.contains(ORDER_KEY);
        Map<LearningItem, Integer> fileIndex = new IdentityHashMap<>();
        Map<LearningItem, Long> fileParent = new IdentityHashMap<>();
        for (Entry e : diff.entries) {
            LearningItem item = resolved.get(e);
            if (item != null) {
                fileIndex.put(item, e.index);
                LearningItem p = e.parent == null ? null : resolved.get(e.parent);
                fileParent.put(item, p == null ? null : p.getId());
            }
        }
        Map<Long, List<LearningItem>> groups = new HashMap<>();
        for (LearningItem item : all) {
            if (!doomed.contains(item)) {
                groups.computeIfAbsent(item.getParentId(), k -> new ArrayList<>()).add(item);
            }
        }
        for (Map.Entry<Long, List<LearningItem>> g : groups.entrySet()) {
            Long parentId = g.getKey();
            boolean useFile = orderAll || touchedGroups.contains(parentId);
            Comparator<LearningItem> byCurrent = Comparator.comparingInt(LearningItem::getDisplayOrder).thenComparing(LearningItem::getId);
            Comparator<LearningItem> order = byCurrent;
            if (useFile) {
                // items the file places here, in file order; then the rest as they were
                order = Comparator.<LearningItem>comparingInt(i -> placedHere(i, parentId, fileParent, orderAll, fileOrdered)
                                ? fileIndex.get(i) : Integer.MAX_VALUE)
                        .thenComparing(byCurrent);
            }
            List<LearningItem> siblings = g.getValue();
            siblings.sort(order);
            for (int i = 0; i < siblings.size(); i++) {
                if (siblings.get(i).getDisplayOrder() != i) {
                    siblings.get(i).setDisplayOrder(i);
                }
            }
        }

        // 6. deletions last: they clear the persistence context
        plan.touch();
        items.flush();
        for (Long id : deleteIds) {
            if (items.deleteWithSubtree(id) > 0) {
                deleted++;
            }
        }
        return new SyncResult(added, updated, moved, deleted, skipped);
    }

    /** With "order" chosen every item follows the file; otherwise only the ones added or moved here do. */
    private static boolean placedHere(LearningItem item, Long parentId, Map<LearningItem, Long> fileParent,
                                      boolean orderAll, Set<LearningItem> fileOrdered) {
        return fileParent.containsKey(item) && Objects.equals(fileParent.get(item), parentId)
                && (orderAll || fileOrdered.contains(item));
    }

    private void loseCompletion(LearningItem parent) {
        if (parent != null && parent.isCompleted()) {
            parent.clearCompletion(); // a topic that gains a sub-item is no longer checkable
            reviews.cancel(parent.getId());
        }
    }

    private static boolean isSelfOrAncestor(LearningItem item, LearningItem target) {
        for (LearningItem p = target; p != null; p = p.getParent()) {
            if (Objects.equals(p.getId(), item.getId())) {
                return true;
            }
        }
        return false;
    }

    // --- helpers ------------------------------------------------------------------------------

    private static Map<Long, LearningItem> byIdOf(List<LearningItem> list) {
        Map<Long, LearningItem> map = new HashMap<>();
        list.forEach(i -> map.put(i.getId(), i));
        return map;
    }

    private static void index(List<TreeNode> nodes, Map<Long, TreeNode> out) {
        for (TreeNode n : nodes) {
            out.put(n.id(), n);
            index(n.children(), out);
        }
    }

    private static String pathOf(LearningItem item) {
        List<String> names = new ArrayList<>();
        for (LearningItem p = item.getParent(); p != null; p = p.getParent()) {
            names.addFirst(p.getName());
        }
        return names.isEmpty() ? null : String.join(" › ", names);
    }

    private static String filePath(Entry e) {
        List<String> names = new ArrayList<>();
        for (Entry p = e.parent; p != null; p = p.parent) {
            names.addFirst(p.node.name);
        }
        return names.isEmpty() ? null : String.join(" › ", names);
    }

    private static String issue(ParsedSheet.Issue i) {
        return i.row() > 0 ? "Row %d: %s".formatted(i.row(), i.message()) : i.message();
    }

    private static String label(ItemType type) {
        return type == null ? null : type == ItemType.MODULE ? "Module" : "Topic";
    }

    private static String name(Priority p) {
        return p == null ? null : p.name();
    }

    public enum ChangeKind { ADD, UPDATE, MOVE, DELETE, ORDER }

    public record FieldChange(String field, String from, String to) {
    }

    /**
     * @param key             send back in {@code selected} to apply this change
     * @param path            where the item sits (ancestor names)
     * @param conflict        edited in the app after the export
     * @param defaultSelected pre-ticked in the preview (deletions and conflicts are not)
     * @param row             spreadsheet row, when the change comes from a row
     * @param dependsOn       key of an addition that must also be applied (the new parent)
     */
    public record SyncChange(String key, ChangeKind kind, String name, String path, List<FieldChange> fields,
                             boolean conflict, boolean defaultSelected, Integer row, String dependsOn, String detail) {
    }

    /**
     * @param versionColumn the file has the {@code version} column, so conflicts can be detected
     * @param canApply      no errors and at least one change
     */
    public record SyncPreview(String fileName, List<String> errors, List<String> warnings, List<SyncChange> changes,
                              boolean versionColumn, boolean canApply) {
    }

    /** @param skipped selected changes that couldn't be applied, and why */
    public record SyncResult(int added, int updated, int moved, int deleted, List<String> skipped) {
    }
}
