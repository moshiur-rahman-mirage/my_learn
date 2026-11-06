package com.mylearn.tracker.importer;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.importer.ImportDtos.ImportPreview;
import com.mylearn.tracker.importer.ImportDtos.ImportResult;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.progress.Progress;
import com.mylearn.tracker.support.IntegrationTest;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ImportExportIntegrationTest extends IntegrationTest {

    @Autowired
    ImportService imports;
    @Autowired
    ExportService exports;
    @Autowired
    PlanService plans;
    @Autowired
    ItemService items;
    @Autowired
    MockMvc mvc;

    @Test
    void xlsxWithSeveralSheetsCanBeImportedAsOnePlanPerSheet() throws IOException {
        byte[] xlsx = workbook();

        ImportPreview preview = imports.preview(null, "roadmaps.xlsx", xlsx, ImportOptions.AUTO);
        assertThat(preview.sheets()).containsExactly("Odoo", "Node JS", "Empty"); // the empty sheet is listed but skipped on import
        assertThat(preview.sheet()).isEqualTo("Odoo");
        assertThat(preview.canImport()).isTrue();

        ImportResult result = imports.confirmAsNewPlans("roadmaps.xlsx", xlsx, ImportOptions.AUTO, null, true, false);

        assertThat(result.plans()).extracting(ImportDtos.ImportedPlan::planName).containsExactly("Odoo", "Node JS");
        Long odoo = result.plans().get(0).planId();
        assertThat(plans.progressOf(odoo)).isEqualTo(Progress.of(1, 2));      // only leaf ticks count
        List<TreeNode> tree = items.tree(odoo);
        assertThat(tree.getFirst().name()).isEqualTo("PHASE 0");
        assertThat(tree.getFirst().children().getFirst().children()).extracting(TreeNode::name)
                .containsExactly("Editions", "Architecture");
        assertThat(plans.progressOf(result.plans().get(1).planId()).total()).isEqualTo(2);
    }

    @Test
    void importIntoExistingPlanAppendsWithoutTouchingExistingItems() {
        Long planId = plans.create(new CreatePlanRequest("Mine", null)).id();
        var existing = items.create(planId, new CreateItemRequest("Already here", ItemType.TOPIC, null, null, null, null));
        items.setCompletion(existing.id(), true);

        byte[] csv = "Name,Done\nNew one,TRUE\nNew two,FALSE\n".getBytes(StandardCharsets.UTF_8);
        imports.confirmIntoPlan(planId, "list.csv", csv, ImportOptions.AUTO, false);

        assertThat(items.tree(planId)).extracting(TreeNode::name).containsExactly("Already here", "New one", "New two");
        assertThat(items.tree(planId)).extracting(TreeNode::displayOrder).containsExactly(0, 1, 2);
        assertThat(plans.progressOf(planId)).isEqualTo(Progress.of(2, 3));    // existing progress preserved
    }

    @Test
    void duplicateImportNeedsExplicitConfirmation() {
        Long planId = plans.create(new CreatePlanRequest("Dup", null)).id();
        byte[] csv = "Name\nA\nB\n".getBytes(StandardCharsets.UTF_8);
        imports.confirmIntoPlan(planId, "a.csv", csv, ImportOptions.AUTO, false);

        assertThat(imports.preview(planId, "a.csv", csv, ImportOptions.AUTO).duplicateOf().planName()).isEqualTo("Dup");
        assertThatThrownBy(() -> imports.confirmIntoPlan(planId, "a.csv", csv, ImportOptions.AUTO, false))
                .isInstanceOf(ConflictException.class).hasMessageContaining("already imported");

        imports.confirmIntoPlan(planId, "a.csv", csv, ImportOptions.AUTO, true);
        assertThat(items.tree(planId)).hasSize(4);
    }

    @Test
    void sheetWithErrorsCannotBeConfirmed() {
        byte[] csv = "Phase,Topic,Description\n,,orphan description\n".getBytes(StandardCharsets.UTF_8);

        assertThat(imports.preview(null, "bad.csv", csv, ImportOptions.AUTO).canImport()).isFalse();
        assertThatThrownBy(() -> imports.confirmAsNewPlans("bad.csv", csv, ImportOptions.AUTO, "Bad", false, false))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void exportThenReimportReproducesTheTree_andExistingIdsAreRefused() {
        Long planId = plans.create(new CreatePlanRequest("Source", null)).id();
        var phase = items.create(planId, new CreateItemRequest("Phase, with comma", ItemType.MODULE, null, "Line \"quoted\"", null, null));
        var topic = items.create(planId, new CreateItemRequest("Topic", ItemType.TOPIC, phase.id(), null, com.mylearn.tracker.item.Priority.MUST, null));
        items.create(planId, new CreateItemRequest("বাংলা টপিক", ItemType.TOPIC, topic.id(), null, null, null));
        items.create(planId, new CreateItemRequest("Empty module", ItemType.MODULE, phase.id(), null, null, null));

        byte[] csv = exports.exportPlan(planId).content();
        String text = new String(csv, StandardCharsets.UTF_8);
        assertThat(text).startsWith("﻿id,level,type,name,description,priority,completed");

        // Re-importing the untouched export is refused: its ids already exist (use sync instead).
        assertThat(imports.preview(null, "source.csv", csv, ImportOptions.AUTO).canImport()).isFalse();

        // With the id column cleared it imports as a faithful copy.
        String withoutIds = text.lines().map(l -> l.replaceFirst("^(﻿?)[0-9a-f-]{36}", "$1")).reduce((a, b) -> a + "\n" + b).orElseThrow();
        ImportResult copy = imports.confirmAsNewPlans("source.csv", withoutIds.getBytes(StandardCharsets.UTF_8),
                ImportOptions.AUTO, "Copy", false, false);
        List<TreeNode> copied = items.tree(copy.plans().getFirst().planId());
        assertThat(copied.getFirst().name()).isEqualTo("Phase, with comma");
        assertThat(copied.getFirst().description()).isEqualTo("Line \"quoted\"");
        assertThat(copied.getFirst().children()).extracting(TreeNode::name).containsExactly("Topic", "Empty module");
        assertThat(copied.getFirst().children().get(1).type()).isEqualTo(ItemType.MODULE);
        assertThat(copied.getFirst().children().getFirst().children().getFirst().name()).isEqualTo("বাংলা টপিক");
    }

    @Test
    void httpEndpointsForPreviewConfirmTemplateAndExport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "plan.csv", "text/csv",
                "Phase,Topic,Done\nP1,,\n,T1,TRUE\n".getBytes(StandardCharsets.UTF_8));

        mvc.perform(multipart("/api/import/preview").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canImport").value(true))
                .andExpect(jsonPath("$.tree[0].name").value("P1"))
                .andExpect(jsonPath("$.tree[0].children[0].completed").value(true))
                .andExpect(jsonPath("$.columns[2].role").value("COMPLETED"));
        String body = mvc.perform(multipart("/api/import/confirm").file(file).param("newPlanName", "From HTTP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plans[0].itemsCreated").value(2))
                .andReturn().getResponse().getContentAsString();
        String planId = body.replaceAll(".*\"planId\":(\\d+).*", "$1");

        mvc.perform(get("/api/plans/" + planId + "/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("from-http.csv")))
                .andExpect(content -> assertThat(content.getResponse().getContentAsString(StandardCharsets.UTF_8)).contains("T1"));
        mvc.perform(get("/api/import/template")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("learning-plan-template.csv")));
        mvc.perform(get("/api/import/template").param("examples", "true")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("learning-plan-example.csv")));
        mvc.perform(multipart("/api/import/preview").file(new MockMultipartFile("file", "x.pdf", "application/pdf", new byte[]{1})))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void utf8TextIsKept_andWindows1252IsTheFallback() {
        // escapes, so the test doesn't depend on the source-file encoding: em dash, é, Bangla
        String name = "Phase 1 — Café বাংলা";
        byte[] utf8 = ("level,type,name\n1,TOPIC," + name + "\n").getBytes(StandardCharsets.UTF_8);
        assertThat(imports.preview(null, "pasted.csv", utf8, ImportOptions.AUTO).tree().get(0).name()).isEqualTo(name);

        byte[] legacy = "level,type,name\n1,TOPIC,Café\n".getBytes(java.nio.charset.Charset.forName("windows-1252"));
        assertThat(imports.preview(null, "old.csv", legacy, ImportOptions.AUTO).tree().get(0).name()).isEqualTo("Café");
    }

    @Test
    void blankTemplateHasOnlyTheHeader_andImportsOnceFilledIn() {
        String blank = new String(exports.template(false).content(), StandardCharsets.UTF_8);
        assertThat(blank).isEqualTo("\uFEFFlevel,type,name,description,priority,completed\r\n");

        // what a user would type below the header in Excel
        String filled = blank
                + "1,MODULE,Spring Core,,MUST,\r\n"
                + "2,TOPIC,Dependency injection,\"@Autowired, constructors\",MUST,TRUE\r\n"
                + "2,TOPIC,Bean scopes,,SHOULD,\r\n"
                + "1,TOPIC,Read the docs,,NICE,FALSE\r\n";
        ImportPreview preview = imports.preview(null, "my-plan.csv", filled.getBytes(StandardCharsets.UTF_8), ImportOptions.AUTO);
        assertThat(preview.errors()).isEmpty();
        assertThat(preview.warnings()).isEmpty();

        ImportResult result = imports.confirmAsNewPlans("my-plan.csv", filled.getBytes(StandardCharsets.UTF_8),
                ImportOptions.AUTO, "Spring", false, false);
        List<TreeNode> tree = items.tree(result.plans().getFirst().planId());
        assertThat(tree).extracting(TreeNode::name).containsExactly("Spring Core", "Read the docs");
        assertThat(tree.getFirst().children()).extracting(TreeNode::name).containsExactly("Dependency injection", "Bean scopes");
        assertThat(tree.getFirst().children().getFirst().description()).isEqualTo("@Autowired, constructors");
        assertThat(tree.getFirst().progress()).isEqualTo(Progress.of(1, 2));
    }

    @Test
    void exampleTemplateImportsCleanly() {
        byte[] example = exports.template(true).content();
        ImportPreview preview = imports.preview(null, "example.csv", example, ImportOptions.AUTO);
        assertThat(preview.errors()).isEmpty();
        assertThat(preview.stats().modules()).isEqualTo(3);
        assertThat(preview.stats().leafTopics()).isEqualTo(4);
    }

    /** Two sheets mimicking the user's workbook: a title row, a header on row 2, boolean checkbox cells. */
    private static byte[] workbook() throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet odoo = wb.createSheet("Odoo");
            odoo.createRow(0).createCell(5).setCellValue(13.09);
            row(odoo, 1, "Phase", "Topic", "Subtopic", "What to Learn", "Priority").createCell(5).setCellValue(false);
            row(odoo, 2, "PHASE 0").createCell(5).setCellValue(true);
            row(odoo, 3, "", "■ What is Odoo").createCell(5).setCellValue(false);
            row(odoo, 4, "", "", "• Editions", "Community vs Enterprise", "Must").createCell(5).setCellValue(true);
            row(odoo, 5, "", "", "• Architecture", "3-tier", "Must").createCell(5).setCellValue(false);

            XSSFSheet node = wb.createSheet("Node JS");
            row(node, 0, "Serial", "Name").createCell(2).setCellValue(false);
            XSSFRow r1 = row(node, 1, "", "Intro");
            r1.createCell(0).setCellValue(1.0);
            r1.createCell(2).setCellValue(false);
            row(node, 2, "", "Modules").createCell(2).setCellValue(false);

            wb.createSheet("Empty");
            wb.write(out);
            return out.toByteArray();
        }
    }

    private static XSSFRow row(XSSFSheet sheet, int index, String... values) {
        XSSFRow row = sheet.createRow(index);
        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                row.createCell(i).setCellValue(values[i]);
            }
        }
        return row;
    }
}
