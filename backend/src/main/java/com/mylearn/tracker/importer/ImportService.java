package com.mylearn.tracker.importer;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.persistence.Timestamps;
import com.mylearn.tracker.importer.ImportDtos.DuplicateInfo;
import com.mylearn.tracker.importer.ImportDtos.ImportPreview;
import com.mylearn.tracker.importer.ImportDtos.ImportResult;
import com.mylearn.tracker.importer.ImportDtos.ImportedPlan;
import com.mylearn.tracker.importer.ImportDtos.PreviewNode;
import com.mylearn.tracker.importer.ParsedSheet.Issue;
import com.mylearn.tracker.importer.ParsedSheet.Node;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItem;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.PlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Preview and confirm for spreadsheet imports. Import is <b>append-only</b>: it never modifies or
 * deletes existing items. Preview is stateless; confirm re-parses the same file with the same
 * options, so it creates exactly what was previewed.
 */
@Service
@Transactional(readOnly = true)
public class ImportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final PlanService planService;
    private final LearningItemRepository items;
    private final ImportBatchRepository batches;

    public ImportService(PlanService planService, LearningItemRepository items, ImportBatchRepository batches) {
        this.planService = planService;
        this.items = items;
        this.batches = batches;
    }

    // --- preview ----------------------------------------------------------------------------

    /** @param planId target plan, or {@code null} when importing into a new plan */
    public ImportPreview preview(Long planId, String fileName, byte[] content, ImportOptions options) {
        if (planId != null) {
            planService.findPlan(planId);
        }
        List<SheetData> sheets = SpreadsheetReader.read(fileName, content);
        SheetData sheet = pickSheet(sheets, options.sheet());
        ParsedSheet parsed = check(ImportParser.parse(sheet, options));
        return toPreview(fileName, sheets, parsed);
    }

    // --- confirm ----------------------------------------------------------------------------

    /** Appends the sheet's items to an existing plan. */
    @Transactional
    public ImportResult confirmIntoPlan(Long planId, String fileName, byte[] content, ImportOptions options,
                                        boolean allowDuplicate) {
        LearningPlan plan = planService.findPlan(planId);
        SheetData sheet = pickSheet(SpreadsheetReader.read(fileName, content), options.sheet());
        ParsedSheet parsed = validated(check(ImportParser.parse(sheet, options)), allowDuplicate);
        return new ImportResult(List.of(persist(plan, parsed, fileName)));
    }

    /**
     * Creates new plan(s). With {@code allSheets}, every sheet that contains data becomes its own
     * plan named after the sheet (auto-detected layout). Otherwise the chosen sheet becomes
     * {@code newPlanName} (default: the sheet name). All-or-nothing: one transaction.
     */
    @Transactional
    public ImportResult confirmAsNewPlans(String fileName, byte[] content, ImportOptions options, String newPlanName,
                                          boolean allSheets, boolean allowDuplicate) {
        List<SheetData> sheets = SpreadsheetReader.read(fileName, content);
        List<ImportedPlan> created = new ArrayList<>();
        if (allSheets) {
            List<String> problems = new ArrayList<>();
            List<ParsedSheet> parsedSheets = new ArrayList<>();
            for (SheetData sheet : sheets) {
                if (sheet.rows().stream().allMatch(r -> r.stream().allMatch(String::isBlank))) {
                    continue; // empty sheet
                }
                ParsedSheet parsed = check(ImportParser.parse(sheet, ImportOptions.AUTO));
                if (!parsed.errors().isEmpty()) {
                    problems.add("'%s': %s".formatted(sheet.name(), parsed.errors().getFirst().message()));
                } else {
                    parsedSheets.add(parsed);
                }
            }
            if (!problems.isEmpty()) {
                throw new BusinessRuleException("Some sheets can't be imported. " + String.join("; ", problems));
            }
            for (ParsedSheet parsed : parsedSheets) {
                validated(parsed, allowDuplicate);
                LearningPlan plan = planService.createEntity(parsed.sheetName(), null);
                created.add(persist(plan, parsed, fileName));
            }
        } else {
            SheetData sheet = pickSheet(sheets, options.sheet());
            ParsedSheet parsed = validated(check(ImportParser.parse(sheet, options)), allowDuplicate);
            String name = newPlanName == null || newPlanName.isBlank() ? sheet.name() : newPlanName;
            LearningPlan plan = planService.createEntity(name, null);
            created.add(persist(plan, parsed, fileName));
        }
        return new ImportResult(created);
    }

    // --- internals --------------------------------------------------------------------------

    private SheetData pickSheet(List<SheetData> sheets, String name) {
        if (sheets.isEmpty()) {
            throw new BusinessRuleException("The file contains no sheets");
        }
        if (name != null && !name.isBlank()) {
            return sheets.stream().filter(s -> s.name().equals(name)).findFirst()
                    .orElseThrow(() -> new BusinessRuleException("Sheet '%s' not found".formatted(name)));
        }
        return sheets.stream()
                .filter(s -> s.rows().stream().anyMatch(r -> r.stream().anyMatch(c -> !c.isBlank())))
                .findFirst().orElse(sheets.getFirst());
    }

    /** Database-dependent checks: ids from an earlier export that already exist mean "use sync instead". */
    private ParsedSheet check(ParsedSheet parsed) {
        Set<String> ids = new HashSet<>();
        collectIds(parsed.roots(), ids);
        if (!ids.isEmpty()) {
            List<String> existing = items.findExistingExternalIds(ids);
            if (!existing.isEmpty()) {
                List<Issue> errors = new ArrayList<>(parsed.errors());
                errors.add(new Issue(0, ("%d row(s) have ids of items that already exist (this looks like an edited export). "
                        + "Import only adds new content and never overwrites; clear the id column to import them as copies.")
                        .formatted(existing.size())));
                return new ParsedSheet(parsed.sheetName(), parsed.headerRow(), parsed.columns(), parsed.roots(), errors,
                        parsed.warnings(), parsed.stats(), parsed.contentHash());
            }
        }
        return parsed;
    }

    private ParsedSheet validated(ParsedSheet parsed, boolean allowDuplicate) {
        if (!parsed.errors().isEmpty()) {
            throw new BusinessRuleException("'%s' has %d error(s); fix them and preview again. First: %s"
                    .formatted(parsed.sheetName(), parsed.errors().size(), parsed.errors().getFirst().message()));
        }
        DuplicateInfo duplicate = duplicateOf(parsed);
        if (duplicate != null && !allowDuplicate) {
            throw new ConflictException("'%s' was already imported into '%s' on %s. Confirm again with 'import anyway' to add it a second time."
                    .formatted(parsed.sheetName(), duplicate.planName(), DATE.format(duplicate.importedAt())));
        }
        return parsed;
    }

    private DuplicateInfo duplicateOf(ParsedSheet parsed) {
        if (parsed.contentHash().isEmpty()) {
            return null;
        }
        return batches.findByHash(parsed.contentHash()).stream().findFirst()
                .map(b -> new DuplicateInfo(b.getPlan().getId(), b.getPlan().getName(), b.getImportedAt()))
                .orElse(null);
    }

    private ImportedPlan persist(LearningPlan plan, ParsedSheet parsed, String fileName) {
        Instant now = Timestamps.now();
        int offset = items.findRoots(plan.getId()).size(); // append after existing root items
        int[] count = {0};
        for (int i = 0; i < parsed.roots().size(); i++) {
            save(plan, null, parsed.roots().get(i), offset + i, now, count);
        }
        batches.save(new ImportBatch(plan, fileName, parsed.sheetName(), parsed.contentHash(), count[0], now));
        plan.touch();
        return new ImportedPlan(plan.getId(), plan.getName(), parsed.sheetName(), count[0]);
    }

    private void save(LearningPlan plan, LearningItem parent, Node node, int order, Instant now, int[] count) {
        LearningItem item = new LearningItem(plan, parent, node.type, node.name, node.description, node.priority);
        item.setDisplayOrder(order);
        if (node.completed && node.type == ItemType.TOPIC && node.children.isEmpty()) {
            item.markCompleted(now);
        }
        items.save(item);
        count[0]++;
        for (int i = 0; i < node.children.size(); i++) {
            save(plan, item, node.children.get(i), i, now, count);
        }
    }

    private ImportPreview toPreview(String fileName, List<SheetData> sheets, ParsedSheet parsed) {
        return new ImportPreview(fileName, sheets.stream().map(SheetData::name).toList(), parsed.sheetName(),
                parsed.headerRow(), parsed.columns(), parsed.roots().stream().map(PreviewNode::of).toList(),
                parsed.stats(), parsed.errors(), parsed.warnings(), duplicateOf(parsed), parsed.errors().isEmpty());
    }

    private static void collectIds(List<Node> nodes, Set<String> ids) {
        for (Node n : nodes) {
            if (n.externalId != null) {
                ids.add(n.externalId);
            }
            collectIds(n.children, ids);
        }
    }
}
