package com.mylearn.tracker.admin;

import com.mylearn.tracker.admin.BackupService.BackupFile;
import com.mylearn.tracker.admin.BackupService.BackupSettings;
import com.mylearn.tracker.admin.FolderBrowser.FolderListing;
import com.mylearn.tracker.admin.RestoreService.RestoreResult;
import com.mylearn.tracker.common.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BackupService backups;
    private final RestoreService restore;
    private final FolderBrowser folders;

    public AdminController(BackupService backups, RestoreService restore, FolderBrowser folders) {
        this.backups = backups;
        this.restore = restore;
        this.folders = folders;
    }

    @PostMapping("/backup")
    @ResponseStatus(HttpStatus.CREATED)
    public BackupFile backup() {
        return backups.backupNow();
    }

    @GetMapping("/backups")
    public List<BackupFile> list() {
        return backups.list();
    }

    @GetMapping("/backup-settings")
    public BackupSettings settings() {
        return backups.settings();
    }

    @PutMapping("/backup-settings")
    public BackupSettings changeSettings(@RequestBody BackupSettingsRequest request) {
        return backups.changeDirectory(request.directory());
    }

    /** Second folder every backup is also copied to; blank directory = stop copying. */
    @PutMapping("/backup-mirror")
    public BackupSettings changeMirror(@RequestBody BackupSettingsRequest request) {
        return backups.changeMirror(request.directory());
    }

    /** Clean-up rule: newest N, plus one per day / week / month going back. */
    @PutMapping("/backup-retention")
    public BackupSettings changeRetention(@RequestBody AppSettings.Retention retention) {
        return backups.changeRetention(retention);
    }

    /** Is everything fine with backups (recent, checked, copied)? Shown on the dashboard. */
    @GetMapping("/backup-status")
    public BackupService.BackupStatus status() {
        return backups.status();
    }

    /** Re-runs the integrity check of one backup. */
    @PostMapping("/backups/{fileName}/check")
    public BackupService.BackupCheck check(@PathVariable String fileName) {
        return backups.checkFile(fileName);
    }

    /** {@code ?dryRun=true} lists what the clean-up rule would delete without deleting it. */
    @PostMapping("/backups/cleanup")
    public List<BackupFile> cleanup(@RequestParam(defaultValue = "false") boolean dryRun) {
        return dryRun ? backups.cleanupPreview() : backups.cleanup();
    }

    @GetMapping("/folders")
    public FolderListing folders(@RequestParam(required = false) String path) {
        return folders.list(path);
    }

    /** Restore one of the backups listed by {@code GET /backups}. */
    @PostMapping("/backups/{fileName}/restore")
    public RestoreResult restoreBackup(@PathVariable String fileName) {
        return restore.restoreBackup(fileName);
    }

    @DeleteMapping("/backups/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBackup(@PathVariable String fileName) {
        backups.delete(fileName);
    }

    /** Restore from a .db file chosen on this computer (e.g. a copy kept on a USB drive). */
    @PostMapping("/restore")
    public RestoreResult restoreUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessRuleException("The chosen file is empty.");
        }
        try (InputStream in = file.getInputStream()) {
            return restore.restoreUpload(file.getOriginalFilename(), in);
        }
    }

    /** @param directory absolute folder path; blank = default ({@code <data-dir>/backups}) */
    public record BackupSettingsRequest(String directory) {
    }
}
