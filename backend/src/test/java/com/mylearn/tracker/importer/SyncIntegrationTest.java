package com.mylearn.tracker.importer;

import com.mylearn.tracker.importer.SyncService.ChangeKind;
import com.mylearn.tracker.importer.SyncService.SyncChange;
import com.mylearn.tracker.importer.SyncService.SyncPreview;
import com.mylearn.tracker.importer.SyncService.SyncResult;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.item.dto.UpdateItemRequest;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.mylearn.tracker.item.ItemType.MODULE;
import static com.mylearn.tracker.item.ItemType.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

class SyncIntegrationTest extends IntegrationTest {

    @Autowired SyncService sync;
    @Autowired ExportService export;
    @Autowired ItemService items;
    @Autowired PlanService plans;

    Long planId;
    ItemResponse basics;
    ItemResponse intro;
    ItemResponse setup;
    ItemResponse advanced;
    ItemResponse old;

    @BeforeEach
    void plan() {
        planId = plans.create(new CreatePlanRequest("Synced", null)).id();
        basics = create("Basics", MODULE, null);
        intro = create("Intro", TOPIC, basics.id());
        setup = create("Setup", TOPIC, basics.id());
        advanced = create("Advanced", MODULE, null);
        old = create("Old topic", TOPIC, advanced.id());
    }

    private ItemResponse create(String name, ItemType type, Long parentId) {
        return items.create(planId, new CreateItemRequest(name, type, parentId, null, null, null));
    }

    private String exported() {
        return new String(export.exportPlan(planId).content(), StandardCharsets.UTF_8);
    }

    private static byte[] csv(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static SyncChange change(SyncPreview p, ChangeKind kind, String name) {
        return p.changes().stream().filter(c -> c.kind() == kind && name.equals(c.name())).findFirst().orElseThrow();
    }

    @Test
    void unchangedExportHasNothingToApply() {
        SyncPreview preview = sync.preview(planId, "synced.csv", csv(exported()));
        assertThat(preview.errors()).isEmpty();
        assertThat(preview.changes()).isEmpty();
        assertThat(preview.versionColumn()).isTrue();
        assertThat(preview.canApply()).isFalse();
    }

    @Test
    void editedExport_previewsAndAppliesAdditionsUpdatesMovesAndDeletions() {
        String file = exported()
                .replace(",Intro,", ",Introduction,")                       // rename
                .replace(",TOPIC,Setup,,,FALSE,", ",TOPIC,Setup,,MUST,TRUE,") // priority + tick
                .replaceAll("(?m)^.*Old topic.*\\R", "")                       // deleted from the file
                + ",2,TOPIC,Brand new,Added in Excel,,FALSE,\r\n";            // new row under Advanced
        // move Setup from Basics to Advanced: cut its row and paste it after the Advanced row
        List<String> lines = new ArrayList<>(List.of(file.split("\r\n")));
        String setupRow = lines.stream().filter(l -> l.contains(",Setup,")).findFirst().orElseThrow();
        lines.remove(setupRow);
        int advancedRow = lines.indexOf(lines.stream().filter(l -> l.contains(",Advanced,")).findFirst().orElseThrow());
        lines.add(advancedRow + 1, setupRow);
        byte[] edited = csv(String.join("\r\n", lines) + "\r\n");

        SyncPreview preview = sync.preview(planId, "synced.csv", edited);
        assertThat(preview.errors()).isEmpty();
        assertThat(change(preview, ChangeKind.UPDATE, "Intro").fields()).extracting(f -> f.field()).containsExactly("name");
        assertThat(change(preview, ChangeKind.UPDATE, "Setup").fields()).extracting(f -> f.field())
                .containsExactly("priority", "completed");
        assertThat(change(preview, ChangeKind.MOVE, "Setup").fields().getFirst().to()).isEqualTo("Advanced");
        assertThat(change(preview, ChangeKind.ADD, "Brand new").defaultSelected()).isTrue();
        SyncChange delete = change(preview, ChangeKind.DELETE, "Old topic");
        assertThat(delete.defaultSelected()).isFalse(); // deletions are never pre-selected

        List<String> all = preview.changes().stream().map(SyncChange::key).toList();
        SyncResult result = sync.apply(planId, "synced.csv", edited, all);

        assertThat(result.skipped()).isEmpty();
        assertThat(result.added()).isEqualTo(1);
        assertThat(result.deleted()).isEqualTo(1);
        List<TreeNode> tree = items.tree(planId);
        assertThat(tree).extracting(TreeNode::name).containsExactly("Basics", "Advanced");
        assertThat(tree.get(0).children()).extracting(TreeNode::name).containsExactly("Introduction");
        assertThat(tree.get(1).children()).extracting(TreeNode::name).containsExactly("Setup", "Brand new");
        TreeNode setupNode = tree.get(1).children().get(0);
        assertThat(setupNode.completed()).isTrue();
        assertThat(setupNode.priority()).isEqualTo(com.mylearn.tracker.item.Priority.MUST);
        assertThat(tree.get(1).children().get(1).description()).isEqualTo("Added in Excel");
        assertThat(tree.get(1).children()).extracting(TreeNode::displayOrder).containsExactly(0, 1);
    }

    @Test
    void onlySelectedChangesAreApplied() {
        String file = exported().replace(",Intro,", ",Introduction,").replace(",Setup,", ",Setup v2,");
        SyncPreview preview = sync.preview(planId, "synced.csv", csv(file));

        sync.apply(planId, "synced.csv", csv(file), List.of(change(preview, ChangeKind.UPDATE, "Setup").key()));

        assertThat(items.tree(planId).getFirst().children()).extracting(TreeNode::name).containsExactly("Intro", "Setup v2");
    }

    @Test
    void itemEditedInTheAppAfterExportIsAConflict_notPreselected() {
        String file = exported().replace(",Intro,", ",Intro (from Excel),");
        TreeNode current = items.tree(planId).getFirst().children().getFirst();
        items.update(intro.id(), new UpdateItemRequest("Intro (from app)", TOPIC, null, null, current.version()));

        SyncChange update = change(sync.preview(planId, "synced.csv", csv(file)), ChangeKind.UPDATE, "Intro (from app)");
        assertThat(update.conflict()).isTrue();
        assertThat(update.defaultSelected()).isFalse();
    }

    @Test
    void addingUnderANewParentDependsOnIt_andIsSkippedWithoutIt() {
        String file = exported() + ",1,MODULE,New phase,,,,\r\n,2,TOPIC,Inside new phase,,,FALSE,\r\n";
        SyncPreview preview = sync.preview(planId, "synced.csv", csv(file));
        SyncChange child = change(preview, ChangeKind.ADD, "Inside new phase");
        assertThat(child.dependsOn()).isEqualTo(change(preview, ChangeKind.ADD, "New phase").key());

        SyncResult result = sync.apply(planId, "synced.csv", csv(file), List.of(child.key()));
        assertThat(result.added()).isZero();
        assertThat(result.skipped()).singleElement().asString().contains("parent");
    }

    @Test
    void fileWithoutIdColumnIsRejected() {
        SyncPreview preview = sync.preview(planId, "plain.csv", csv("name,completed\r\nSomething,FALSE\r\n"));
        assertThat(preview.errors()).singleElement().asString().contains("no id column");
        assertThat(preview.canApply()).isFalse();
    }

    @Test
    void reorderingInTheFileIsOffered() {
        List<String> lines = new ArrayList<>(List.of(exported().split("\r\n")));
        int i = lines.indexOf(lines.stream().filter(l -> l.contains(",Intro,")).findFirst().orElseThrow());
        String introRow = lines.remove(i);
        lines.add(i + 1, introRow); // Setup, then Intro
        byte[] file = csv(String.join("\r\n", lines) + "\r\n");

        SyncPreview preview = sync.preview(planId, "synced.csv", file);
        assertThat(preview.changes()).extracting(SyncChange::kind).containsExactly(ChangeKind.ORDER);
        sync.apply(planId, "synced.csv", file, List.of(SyncService.ORDER_KEY));
        assertThat(items.tree(planId).getFirst().children()).extracting(TreeNode::name).containsExactly("Setup", "Intro");
        assertThat(setup.id()).isNotNull();
        assertThat(old.id()).isNotNull();
    }
}
