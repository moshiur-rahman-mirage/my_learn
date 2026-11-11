package com.mylearn.tracker.importer;

import com.mylearn.tracker.importer.ImportDtos.ImportPreview;
import com.mylearn.tracker.importer.SyncService.ChangeKind;
import com.mylearn.tracker.importer.SyncService.SyncChange;
import com.mylearn.tracker.importer.SyncService.SyncPreview;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteService;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Notes travel with a plan's CSV export: import restores them into a new plan, sync into the same plan. */
class NoteBackupIntegrationTest extends IntegrationTest {

    static final String MARKDOWN = "# Key ideas\n\n- one, with \"quotes\"\n- two\n\n```java\nint x = 1;\n```\n";
    static final String HTML = "<p>Rich <strong>text</strong> note</p>";

    @Autowired ImportService imports;
    @Autowired ExportService exports;
    @Autowired SyncService sync;
    @Autowired NoteService notes;
    @Autowired ItemService items;
    @Autowired PlanService plans;

    Long planId;
    ItemResponse module;
    ItemResponse topic;
    ItemResponse bare;

    @BeforeEach
    void plan() {
        planId = plans.create(new CreatePlanRequest("With notes", null)).id();
        module = items.create(planId, new CreateItemRequest("Module", ItemType.MODULE, null, "desc", null, null));
        topic = items.create(planId, new CreateItemRequest("Topic", ItemType.TOPIC, module.id(), null, null, null));
        bare = items.create(planId, new CreateItemRequest("No note", ItemType.TOPIC, module.id(), null, null, null));
        notes.save(module.id(), HTML, NoteFormat.HTML);
        notes.save(topic.id(), MARKDOWN, NoteFormat.MARKDOWN);
    }

    private byte[] exported() {
        return exports.exportPlan(planId).content();
    }

    /** The export without its id column, i.e. importable as fresh content. */
    private byte[] withoutIds() {
        String csv = new String(exported(), StandardCharsets.UTF_8);
        return csv.replaceAll("(?m)^(﻿?)(id|[0-9a-f-]{36}),", "$1,").getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void exportContainsNotesAndImportRestoresThemIntoANewPlan() {
        String csv = new String(exported(), StandardCharsets.UTF_8);
        assertThat(csv).contains("note_format,note").contains("MARKDOWN").contains("HTML");

        // the plan is gone; restore it from the backup file (ids unknown now, so import accepts them)
        byte[] backup = exported();
        plans.delete(planId);
        ImportPreview preview = imports.preview(null, "backup.csv", backup, ImportOptions.AUTO);
        assertThat(preview.canImport()).isTrue();
        assertThat(preview.stats().notes()).isEqualTo(2);
        assertThat(preview.columns()).extracting(ParsedSheet.Column::role)
                .contains(ColumnRole.NOTE, ColumnRole.NOTE_FORMAT, ColumnRole.DESCRIPTION);
        assertThat(preview.tree().getFirst().hasNote()).isTrue();

        Long restored = imports.confirmAsNewPlans("backup.csv", backup, ImportOptions.AUTO, "Restored", false, false)
                .plans().getFirst().planId();

        TreeNode m = items.tree(restored).getFirst();
        assertThat(m.description()).isEqualTo("desc");
        var moduleNote = notes.get(m.id());
        assertThat(moduleNote.content()).isEqualTo(HTML);
        assertThat(moduleNote.format()).isEqualTo(NoteFormat.HTML);
        var topicNote = notes.get(m.children().get(0).id());
        assertThat(topicNote.content()).isEqualTo(MARKDOWN.strip());
        assertThat(topicNote.format()).isEqualTo(NoteFormat.MARKDOWN);
        assertThat(notes.get(m.children().get(1).id()).content()).isNull();
    }

    @Test
    void importAsCopyIntoAnotherPlanKeepsNotes() {
        Long other = plans.create(new CreatePlanRequest("Copy", null)).id();
        imports.confirmIntoPlan(other, "copy.csv", withoutIds(), ImportOptions.AUTO, false);
        TreeNode m = items.tree(other).getFirst();
        assertThat(notes.get(m.children().getFirst().id()).content()).isEqualTo(MARKDOWN.strip());
    }

    @Test
    void syncRestoresChangedAndDeletedNotes() {
        byte[] backup = exported();
        notes.save(topic.id(), "rewritten by mistake", NoteFormat.MARKDOWN);
        notes.save(module.id(), "", NoteFormat.HTML); // deleted
        notes.save(bare.id(), "added after the backup", NoteFormat.MARKDOWN);

        SyncPreview preview = sync.preview(planId, "backup.csv", backup);
        List<SyncChange> updates = preview.changes().stream().filter(c -> c.kind() == ChangeKind.UPDATE).toList();
        assertThat(updates).hasSize(3);
        assertThat(updates).allSatisfy(c -> {
            assertThat(c.conflict()).isFalse();
            assertThat(c.fields()).extracting(SyncService.FieldChange::field).containsExactly("note");
        });

        sync.apply(planId, "backup.csv", backup, updates.stream().map(SyncChange::key).toList());
        assertThat(notes.get(topic.id()).content()).isEqualTo(MARKDOWN.strip());
        assertThat(notes.get(module.id()).content()).isEqualTo(HTML);
        assertThat(notes.get(bare.id()).content()).isNull();
        assertThat(sync.preview(planId, "backup.csv", backup).changes()).isEmpty();
    }

    @Test
    void fileWithoutNoteColumnNeverTouchesNotes() {
        // older exports end at the version column
        SyncPreview preview = sync.preview(planId, "old.csv", oldFormatExport().getBytes(StandardCharsets.UTF_8));
        assertThat(preview.errors()).isEmpty();
        assertThat(preview.changes()).isEmpty();
    }

    @Test
    void aLoneNotesColumnIsStillTheDescription() {
        ParsedSheet parsed = ImportParser.parse(new SheetData("s", List.of(
                List.of("Name", "Notes"), List.of("Topic", "read chapter 2"))), ImportOptions.AUTO);
        assertThat(parsed.roots().getFirst().description).isEqualTo("read chapter 2");
        assertThat(parsed.roots().getFirst().note).isNull();
    }

    /** Rebuilds an old-format export: the first 8 columns of every record. */
    private String oldFormatExport() {
        StringBuilder out = new StringBuilder();
        List<SheetData> sheets = SpreadsheetReader.read("x.csv", exported());
        for (List<String> row : sheets.getFirst().rows()) {
            out.append(String.join(",", row.subList(0, 8).stream()
                    .map(c -> "\"" + c.replace("\"", "\"\"") + "\"").toList())).append('\n');
        }
        return out.toString();
    }
}
