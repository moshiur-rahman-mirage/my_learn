package com.mylearn.tracker.importer;

import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.item.TreeBuilder;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.PlanService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * CSV export in the app's own format: one row per item in tree order.
 * {@code id} (stable external id) + {@code level} + row order fully describe the hierarchy,
 * and the file opens and edits cleanly in Excel / Google Sheets.
 */
@Service
@Transactional(readOnly = true)
public class ExportService {

    static final String[] HEADER = {"id", "level", "type", "name", "description", "priority", "completed"};

    private final PlanService planService;
    private final LearningItemRepository items;

    public ExportService(PlanService planService, LearningItemRepository items) {
        this.planService = planService;
        this.items = items;
    }

    public Export exportPlan(Long planId) {
        LearningPlan plan = planService.findPlan(planId);
        return new Export(slug(plan.getName()) + ".csv", csv(TreeBuilder.build(items.findAllByPlan(planId))));
    }

    public Export exportSubtree(Long itemId) {
        var item = items.findById(itemId).orElseThrow(() -> new NotFoundException("Item", itemId));
        TreeNode node = find(TreeBuilder.build(items.findAllByPlan(item.getPlanId())), itemId).orElseThrow();
        return new Export(slug(item.getPlan().getName() + "-" + item.getName()) + ".csv", csv(List.of(node)));
    }

    /**
     * Columns of the import templates: the export format without {@code id}. Ids only matter
     * in exports of existing items; a hand-written file describes new content.
     */
    static final String[] TEMPLATE_HEADER = {"level", "type", "name", "description", "priority", "completed"};

    /**
     * A file to fill in by hand and import.
     *
     * @param withExamples {@code false}: header row only; {@code true}: plus example rows showing nesting
     */
    public Export template(boolean withExamples) {
        StringWriter out = new StringWriter();
        try (CSVPrinter csv = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(TEMPLATE_HEADER).get())) {
            if (withExamples) {
                csv.printRecord(1, "MODULE", "Phase 1 — Foundations", "Start here", "", "");
                csv.printRecord(2, "MODULE", "Core concepts", "", "MUST", "");
                csv.printRecord(3, "TOPIC", "First topic", "What to learn about it", "MUST", "TRUE");
                csv.printRecord(3, "TOPIC", "Second topic", "", "SHOULD", "FALSE");
                csv.printRecord(2, "TOPIC", "A topic directly under the phase", "", "NICE", "FALSE");
                csv.printRecord(1, "MODULE", "Phase 2 — Practice", "", "", "");
                csv.printRecord(2, "TOPIC", "Build a small project", "", "MUST", "FALSE");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        String name = withExamples ? "learning-plan-example.csv" : "learning-plan-template.csv";
        return new Export(name, withBom(out.toString()));
    }

    private static byte[] csv(List<TreeNode> roots) {
        StringWriter out = new StringWriter();
        try (CSVPrinter csv = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(HEADER).get())) {
            for (TreeNode root : roots) {
                write(csv, root, 1);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return withBom(out.toString());
    }

    private static void write(CSVPrinter csv, TreeNode node, int level) throws IOException {
        csv.printRecord(node.externalId(), level, node.type(), node.name(),
                node.description() == null ? "" : node.description(),
                node.priority() == null ? "" : node.priority(),
                node.checkable() ? (node.completed() ? "TRUE" : "FALSE") : "");
        for (TreeNode child : node.children()) {
            write(csv, child, level + 1);
        }
    }

    /** A UTF-8 BOM makes Excel detect UTF-8, so non-Latin text (e.g. Bangla) displays correctly. */
    private static byte[] withBom(String text) {
        return ("﻿" + text).getBytes(StandardCharsets.UTF_8);
    }

    private static Optional<TreeNode> find(List<TreeNode> nodes, Long id) {
        for (TreeNode n : nodes) {
            if (n.id().equals(id)) {
                return Optional.of(n);
            }
            Optional<TreeNode> found = find(n.children(), id);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    private static String slug(String name) {
        String s = name.toLowerCase().replaceAll("[^\\p{L}\\p{N}]+", "-").replaceAll("(^-|-$)", "");
        return s.isEmpty() ? "export" : s;
    }

    public record Export(String fileName, byte[] content) {
    }
}
