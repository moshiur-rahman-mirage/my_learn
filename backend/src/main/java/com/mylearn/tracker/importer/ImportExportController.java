package com.mylearn.tracker.importer;

import com.mylearn.tracker.importer.ExportService.Export;
import com.mylearn.tracker.importer.ImportDtos.ImportPreview;
import com.mylearn.tracker.importer.ImportDtos.ImportResult;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Import (multipart upload + parsing options as form fields) and CSV export.
 * {@code columnRoles} is a comma-separated list, e.g. {@code IGNORE,LEVEL,LEVEL,DESCRIPTION,COMPLETED}.
 */
@RestController
@RequestMapping("/api")
public class ImportExportController {

    private final ImportService imports;
    private final ExportService exports;
    private final SyncService sync;
    private final PlanBackupService planBackups;

    public ImportExportController(ImportService imports, ExportService exports, SyncService sync,
                                  PlanBackupService planBackups) {
        this.imports = imports;
        this.exports = exports;
        this.sync = sync;
        this.planBackups = planBackups;
    }

    // --- full plan backup (JSON) --------------------------------------------------------------

    /** Everything in the plan (notes, tags, links, time log, schedules, ratings) as one JSON file. */
    @GetMapping("/plans/{planId}/backup")
    public ResponseEntity<byte[]> backupPlan(@PathVariable Long planId) {
        Export export = planBackups.export(planId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(export.fileName(), StandardCharsets.UTF_8).build().toString())
                .body(export.content());
    }

    @PostMapping(path = "/import/backup/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PlanBackupService.BackupPreview previewBackup(@RequestPart("file") MultipartFile file) throws IOException {
        return planBackups.preview(file.getOriginalFilename(), file.getBytes());
    }

    /** Restores a plan backup as a new plan. */
    @PostMapping(path = "/import/backup/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult restoreBackup(@RequestPart("file") MultipartFile file,
                                      @RequestParam(required = false) String newPlanName) throws IOException {
        return planBackups.restore(file.getOriginalFilename(), file.getBytes(), newPlanName);
    }

    // --- sync an edited export back into its plan ---------------------------------------------

    @PostMapping(path = "/plans/{planId}/sync/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SyncService.SyncPreview syncPreview(@PathVariable Long planId, @RequestPart("file") MultipartFile file)
            throws IOException {
        return sync.preview(planId, file.getOriginalFilename(), file.getBytes());
    }

    /** @param selected keys of the changes to apply (repeat the parameter, one key each) */
    @PostMapping(path = "/plans/{planId}/sync/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SyncService.SyncResult syncApply(@PathVariable Long planId, @RequestPart("file") MultipartFile file,
                                            @RequestParam(required = false) List<String> selected) throws IOException {
        return sync.apply(planId, file.getOriginalFilename(), file.getBytes(), selected == null ? List.of() : selected);
    }

    // --- import into an existing plan ---------------------------------------------------------

    @PostMapping(path = "/plans/{planId}/import/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportPreview previewIntoPlan(@PathVariable Long planId, @RequestPart("file") MultipartFile file,
                                         @RequestParam(required = false) String sheet,
                                         @RequestParam(required = false) Integer headerRow,
                                         @RequestParam(required = false) List<ColumnRole> columnRoles) throws IOException {
        return imports.preview(planId, file.getOriginalFilename(), file.getBytes(),
                new ImportOptions(sheet, headerRow, columnRoles));
    }

    @PostMapping(path = "/plans/{planId}/import/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult confirmIntoPlan(@PathVariable Long planId, @RequestPart("file") MultipartFile file,
                                        @RequestParam(required = false) String sheet,
                                        @RequestParam(required = false) Integer headerRow,
                                        @RequestParam(required = false) List<ColumnRole> columnRoles,
                                        @RequestParam(defaultValue = "false") boolean allowDuplicate) throws IOException {
        return imports.confirmIntoPlan(planId, file.getOriginalFilename(), file.getBytes(),
                new ImportOptions(sheet, headerRow, columnRoles), allowDuplicate);
    }

    // --- import as new plan(s) ----------------------------------------------------------------

    @PostMapping(path = "/import/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportPreview preview(@RequestPart("file") MultipartFile file,
                                 @RequestParam(required = false) String sheet,
                                 @RequestParam(required = false) Integer headerRow,
                                 @RequestParam(required = false) List<ColumnRole> columnRoles) throws IOException {
        return imports.preview(null, file.getOriginalFilename(), file.getBytes(),
                new ImportOptions(sheet, headerRow, columnRoles));
    }

    @PostMapping(path = "/import/confirm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult confirm(@RequestPart("file") MultipartFile file,
                                @RequestParam(required = false) String sheet,
                                @RequestParam(required = false) Integer headerRow,
                                @RequestParam(required = false) List<ColumnRole> columnRoles,
                                @RequestParam(required = false) String newPlanName,
                                @RequestParam(defaultValue = "false") boolean allSheets,
                                @RequestParam(defaultValue = "false") boolean allowDuplicate) throws IOException {
        return imports.confirmAsNewPlans(file.getOriginalFilename(), file.getBytes(),
                new ImportOptions(sheet, headerRow, columnRoles), newPlanName, allSheets, allowDuplicate);
    }

    /** Blank template (header row only); {@code ?examples=true} adds sample rows. */
    @GetMapping("/import/template")
    public ResponseEntity<byte[]> template(@RequestParam(defaultValue = "false") boolean examples) {
        return download(exports.template(examples));
    }

    // --- export -------------------------------------------------------------------------------

    @GetMapping("/plans/{planId}/export")
    public ResponseEntity<byte[]> exportPlan(@PathVariable Long planId) {
        return download(exports.exportPlan(planId));
    }

    @GetMapping("/items/{itemId}/export")
    public ResponseEntity<byte[]> exportSubtree(@PathVariable Long itemId) {
        return download(exports.exportSubtree(itemId));
    }

    private static ResponseEntity<byte[]> download(Export export) {
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(export.fileName(), StandardCharsets.UTF_8).build().toString())
                .body(export.content());
    }
}
