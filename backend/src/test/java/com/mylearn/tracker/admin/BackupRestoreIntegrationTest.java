package com.mylearn.tracker.admin;

import com.mylearn.tracker.admin.BackupService.BackupFile;
import com.mylearn.tracker.admin.RestoreService.RestoreResult;
import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.PlanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Not transactional (unlike {@code IntegrationTest}): restore needs the single pooled connection
 * for itself, and backups (VACUUM INTO) can't run inside a transaction. Own data folder.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BackupRestoreIntegrationTest {

    private static final Path DATA_DIR = Path.of("target", "test-data", "restore-" + UUID.randomUUID()).toAbsolutePath();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("app.data-dir", DATA_DIR::toString);
        registry.add("app.backup.automatic", () -> "false");
    }

    @Autowired
    BackupService backups;
    @Autowired
    RestoreService restore;
    @Autowired
    PlanService plans;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    MockMvc mvc;

    @BeforeEach
    void clean() {
        backups.changeDirectory(null);
        jdbc.update("DELETE FROM learning_plan");
    }

    @Test
    void restoreFromListedBackup_replacesDataAndKeepsASafetyBackup() {
        plans.create(new CreatePlanRequest("Kept", null));
        BackupFile backup = backups.backupNow();
        plans.create(new CreatePlanRequest("Added later", null));

        RestoreResult result = restore.restoreBackup(backup.fileName());

        assertThat(plans.search(null, null)).extracting(PlanResponse::name).containsExactly("Kept");
        assertThat(result.plans()).isEqualTo(1);
        // the safety backup holds the state from just before the restore, so it can be undone
        restore.restoreBackup(result.safetyBackup());
        assertThat(plans.search(null, null)).extracting(PlanResponse::name).containsExactlyInAnyOrder("Kept", "Added later");
        // the app keeps working normally afterwards
        plans.create(new CreatePlanRequest("After restore", null));
        assertThat(plans.search(null, null)).hasSize(3);
    }

    @Test
    void restoreFromUploadedFile() throws Exception {
        plans.create(new CreatePlanRequest("From USB", null));
        byte[] file = Files.readAllBytes(Path.of(backups.backupNow().path()));
        jdbc.update("DELETE FROM learning_plan");

        mvc.perform(multipart("/api/admin/restore").file(new MockMultipartFile("file", "copy.db", null, file)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restoredFrom").value("copy.db"))
                .andExpect(jsonPath("$.plans").value(1));
        assertThat(plans.search(null, null)).extracting(PlanResponse::name).containsExactly("From USB");
    }

    @Test
    void rejectsFilesThatAreNotOurBackups_andLeavesDataAlone() throws Exception {
        plans.create(new CreatePlanRequest("Untouched", null));

        assertThatThrownBy(() -> restore.restoreUpload("notes.txt", new ByteArrayInputStream("hello".getBytes())))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("isn't a Learning Tracker backup");

        Path otherDb = DATA_DIR.resolve("other.sqlite");
        Files.deleteIfExists(otherDb);
        new JdbcTemplate(new org.springframework.jdbc.datasource.DriverManagerDataSource("jdbc:sqlite:" + otherDb))
                .execute("CREATE TABLE something (id INTEGER)");
        assertThatThrownBy(() -> restore.restoreUpload("other.sqlite", Files.newInputStream(otherDb)))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("not a Learning Tracker backup");

        mvc.perform(multipart("/api/admin/restore").file(new MockMultipartFile("file", "x.db", null, "nope".getBytes())))
                .andExpect(status().isUnprocessableEntity());
        assertThat(plans.search(null, null)).extracting(PlanResponse::name).containsExactly("Untouched");
    }

    @Test
    void backupFromAnOlderVersionIsUpgradedAfterRestore() throws Exception {
        plans.create(new CreatePlanRequest("Old times", null));
        Path old = Path.of(backups.backupNow().path());
        // turn it into a backup made before notes (V2) and the learning features (V3) existed: schema V1
        JdbcTemplate oldDb = new JdbcTemplate(new org.springframework.jdbc.datasource.DriverManagerDataSource("jdbc:sqlite:" + old));
        for (String trigger : new String[]{"trg_search_item_insert", "trg_search_item_update", "trg_search_item_delete",
                "trg_search_note_delete"}) {
            oldDb.execute("DROP TRIGGER " + trigger);
        }
        for (String table : new String[]{"note_ref", "flashcard", "item_confidence", "search_index", "item_review", "item_tag", "tag", "time_log", "item_link", "item_note"}) {
            oldDb.execute("DROP TABLE " + table);
        }
        oldDb.execute("ALTER TABLE learning_plan DROP COLUMN target_date");
        // every later migration (V2 notes, V3 features, V4 note format, …) is undone by the drops above
        oldDb.update("DELETE FROM flyway_schema_history WHERE version IS NOT NULL AND version <> '1'");

        restore.restoreUpload("v1.db", Files.newInputStream(old));

        assertThat(plans.search(null, null)).extracting(PlanResponse::name).containsExactly("Old times");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sqlite_master WHERE name = 'item_note'", Integer.class)).isEqualTo(1);
        // the search index is built from the restored data
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM search_index WHERE search_index MATCH 'times'", Integer.class))
                .isEqualTo(0); // plans aren't indexed, items are
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sqlite_master WHERE name = 'search_index'", Integer.class)).isEqualTo(1);
    }

    @Test
    void backupNamesCantEscapeTheFolder() {
        assertThatThrownBy(() -> restore.restoreBackup("../learning-tracker.db")).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> restore.restoreBackup("whatever.db")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteBackup_removesOnlyThatFile_andCantEscapeTheFolder() throws Exception {
        BackupFile keep = backups.backupNow();
        BackupFile gone = backups.backupNow();

        mvc.perform(delete("/api/admin/backups/{name}", gone.fileName())).andExpect(status().isNoContent());

        assertThat(Files.exists(Path.of(gone.path()))).isFalse();
        assertThat(backups.list()).extracting(BackupFile::fileName).contains(keep.fileName()).doesNotContain(gone.fileName());
        mvc.perform(delete("/api/admin/backups/{name}", gone.fileName())).andExpect(status().isNotFound());
        assertThatThrownBy(() -> backups.delete("../learning-tracker.db")).isInstanceOf(NotFoundException.class);    }

    @Test
    void everyBackupIsChecked_andADamagedFileFailsItsCheck() throws Exception {
        BackupFile b = backups.backupNow();
        assertThat(b.check()).isNotNull();
        assertThat(b.check().ok()).isTrue();
        assertThat(b.check().plans()).isGreaterThanOrEqualTo(0);
        assertThat(backups.status().warning()).isNull();
        assertThat(backups.status().lastCheck().ok()).isTrue();

        // damage the file: the re-check fails, and the dashboard status says so
        Path file = Path.of(b.path());
        byte[] bytes = Files.readAllBytes(file);
        for (int i = 4096; i < bytes.length; i += 97) {
            bytes[i] = (byte) 0x5A;
        }
        Files.write(file, bytes);
        BackupService.BackupCheck check = backups.checkFile(b.fileName());
        assertThat(check.ok()).isFalse();
        assertThat(backups.list().getFirst().check().ok()).isFalse(); // remembered next to the file
        assertThat(backups.status().warning()).contains("failed its check");
        backups.delete(b.fileName());
        assertThat(Path.of(b.path() + ".check")).doesNotExist();
    }

    @Test
    void cleanupKeepsRecentPlusOnePerDayWeekAndMonth() throws Exception {
        Path dir = DATA_DIR.resolve("retention-" + UUID.randomUUID());
        Files.createDirectories(dir);
        List<Path> files = new java.util.ArrayList<>();
        java.time.LocalDateTime t = java.time.LocalDateTime.of(2026, 11, 11, 12, 0);
        // three today, then one per day back to ~4 months ago
        for (int h = 0; h < 3; h++) {
            files.add(dir.resolve("learning-tracker-" + t.minusHours(h).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")) + ".db"));
        }
        for (int d = 1; d <= 120; d++) {
            files.add(dir.resolve("learning-tracker-" + t.minusDays(d).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")) + ".db"));
        }
        var keep = BackupService.toKeep(files, new AppSettings.Retention(2, 7, 4, 3));
        // files.get(2 + d) is d days back. Recent: 0, 1. Days: Nov 11 (0) and Nov 10–5 (3–8).
        // ISO weeks (Mon–Sun): Nov 9–15 (0), Nov 2–8 (5), Oct 26–Nov 1 (12), Oct 19–25 (19).
        // Months: Nov (0), Oct 31 (13), Sep 30 (44).
        assertThat(keep).containsExactlyInAnyOrder(files.get(0), files.get(1), files.get(3), files.get(4), files.get(5),
                files.get(6), files.get(7), files.get(8), files.get(12), files.get(19), files.get(13), files.get(44));
    }

    @Test
    void cleanupNowRemovesWhatTheRuleDoesntKeep() {
        var before = backups.settings().retention();
        for (int i = 0; i < 4; i++) {
            backups.backupNow();
        }
        backups.changeRetention(new AppSettings.Retention(2, 1, 1, 1));
        List<BackupFile> preview = backups.cleanupPreview();
        int total = backups.list().size();
        assertThat(preview).hasSize(total - 2);
        assertThat(backups.cleanup()).hasSize(total - 2);
        assertThat(backups.list()).hasSize(2);
        assertThatThrownBy(() -> backups.changeRetention(new AppSettings.Retention(0, 1, 1, 1)))
                .isInstanceOf(BusinessRuleException.class);
        backups.changeRetention(before);
    }

    @Test
    void backupsAreAlsoCopiedToTheSecondFolder_andAFailedCopyNeverFailsTheBackup() throws Exception {
        Path mirror = DATA_DIR.resolve("usb-" + UUID.randomUUID());
        BackupFile first = backups.backupNow();
        backups.changeMirror(mirror.toString());
        assertThat(mirror.resolve(first.fileName())).exists(); // newest backup copied right away

        BackupFile second = backups.backupNow();
        assertThat(mirror.resolve(second.fileName())).exists();
        assertThat(backups.settings().mirrorDirectory()).isEqualTo(mirror.toString());
        assertThat(backups.settings().mirrorError()).isNull();
        assertThatThrownBy(() -> backups.changeMirror(backups.settings().directory()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("different folder");

        // the "drive" disappears: the backup still works, the problem is reported
        try (var files = Files.list(mirror)) {
            for (Path f : files.toList()) {
                Files.delete(f);
            }
        }
        Files.delete(mirror);
        Files.writeString(mirror, "not a folder any more");
        BackupFile third = backups.backupNow();
        assertThat(Path.of(third.path())).exists();
        assertThat(backups.settings().mirrorError()).contains("failed");

        Files.delete(mirror);
        backups.changeMirror("");
        assertThat(backups.settings().mirrorDirectory()).isNull();
    }

    @Test
    void chosenBackupFolderIsUsed_persisted_andValidated() throws Exception {
        Path custom = DATA_DIR.resolve("my backups").resolve("nested");
        backups.changeDirectory(custom.toString());

        BackupFile b = backups.backupNow();
        assertThat(Path.of(b.path()).getParent()).isEqualTo(custom);
        assertThat(backups.list()).extracting(BackupFile::fileName).contains(b.fileName());
        assertThat(Files.readString(DATA_DIR.resolve("settings.properties"))).contains("backup.dir");
        assertThat(new AppSettings(DATA_DIR.toString()).backupDir()).isEqualTo(custom); // survives a restart
        assertThat(backups.settings().custom()).isTrue();

        assertThatThrownBy(() -> backups.changeDirectory("relative/folder"))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("full path");
        Path aFile = Files.writeString(DATA_DIR.resolve("a-file.txt"), "x");
        assertThatThrownBy(() -> backups.changeDirectory(aFile.toString()))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("is a file");

        backups.changeDirectory("");
        assertThat(backups.settings().custom()).isFalse();
        assertThat(backups.settings().directory()).isEqualTo(DATA_DIR.resolve("backups").toString());
    }
}
