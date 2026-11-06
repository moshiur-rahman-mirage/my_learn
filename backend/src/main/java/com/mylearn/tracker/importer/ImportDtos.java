package com.mylearn.tracker.importer;

import com.mylearn.tracker.importer.ParsedSheet.Column;
import com.mylearn.tracker.importer.ParsedSheet.Issue;
import com.mylearn.tracker.importer.ParsedSheet.Node;
import com.mylearn.tracker.importer.ParsedSheet.Stats;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.Priority;

import java.time.Instant;
import java.util.List;

/** Request/response shapes of the import endpoints. */
public final class ImportDtos {

    private ImportDtos() {
    }

    /**
     * What the user sees before confirming.
     *
     * @param canImport  no errors (duplicates still need explicit confirmation)
     * @param duplicateOf set when identical content was imported before
     */
    public record ImportPreview(
            String fileName,
            List<String> sheets,
            String sheet,
            int headerRow,
            List<Column> columns,
            List<PreviewNode> tree,
            Stats stats,
            List<Issue> errors,
            List<Issue> warnings,
            DuplicateInfo duplicateOf,
            boolean canImport) {
    }

    public record PreviewNode(int row, String name, ItemType type, String description, Priority priority,
                              boolean completed, List<PreviewNode> children) {

        static PreviewNode of(Node n) {
            return new PreviewNode(n.row, n.name, n.type, n.description, n.priority, n.completed,
                    n.children.stream().map(PreviewNode::of).toList());
        }
    }

    public record DuplicateInfo(Long planId, String planName, Instant importedAt) {
    }

    public record ImportResult(List<ImportedPlan> plans) {
    }

    public record ImportedPlan(Long planId, String planName, String sheet, int itemsCreated) {
    }
}
