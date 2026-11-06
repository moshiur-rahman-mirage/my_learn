package com.mylearn.tracker;

import com.mylearn.tracker.admin.BackupService;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.progress.Progress;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Starts the application, writes data, shuts it down completely, and starts it again on the same
 * data directory. This proves that data (including completion state) survives a restart, the way it
 * would after a laptop reboot. Not transactional: everything is really committed.
 */
class PersistenceAcrossRestartTest {

    private final Path dataDir = Path.of("target", "test-data", "restart-" + UUID.randomUUID());

    @Test
    void dataSurvivesARestartAndBackupsAreUsableDatabases() throws Exception {
        Long planId;
        try (ConfigurableApplicationContext app = start()) {
            PlanService plans = app.getBean(PlanService.class);
            ItemService items = app.getBean(ItemService.class);
            planId = plans.create(new CreatePlanRequest("Survives restart", "desc")).id();
            var module = items.create(planId, new CreateItemRequest("Module", ItemType.MODULE, null, null, null, null));
            var leaf = items.create(planId, new CreateItemRequest("Leaf", ItemType.TOPIC, module.id(), null, null, null));
            items.create(planId, new CreateItemRequest("Other", ItemType.TOPIC, module.id(), null, null, null));
            items.setCompletion(leaf.id(), true);
        }

        try (ConfigurableApplicationContext app = start()) {
            PlanService plans = app.getBean(PlanService.class);
            ItemService items = app.getBean(ItemService.class);

            assertThat(plans.get(planId).name()).isEqualTo("Survives restart");
            assertThat(plans.progressOf(planId)).isEqualTo(Progress.of(1, 2));
            List<TreeNode> tree = items.tree(planId);
            assertThat(tree.getFirst().children()).extracting(TreeNode::name).containsExactly("Leaf", "Other");
            assertThat(tree.getFirst().children().getFirst().completed()).isTrue();

            // Backup: a standalone SQLite file containing the same data.
            BackupService.BackupFile backup = app.getBean(BackupService.class).backupNow();
            assertThat(Files.size(Path.of(backup.path()))).isPositive();
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + backup.path());
                 ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM learning_item")) {
                assertThat(rs.getInt(1)).isEqualTo(3);
            }
            assertThat(app.getBean(BackupService.class).list()).hasSize(1);
        }
    }

    private ConfigurableApplicationContext start() {
        // Command-line args, not .properties(): those are mere defaults that application.yml overrides.
        return new SpringApplicationBuilder(LearningTrackerApplication.class)
                .web(WebApplicationType.NONE)
                .run("--app.data-dir=" + dataDir, "--app.backup.automatic=false");
    }
}
