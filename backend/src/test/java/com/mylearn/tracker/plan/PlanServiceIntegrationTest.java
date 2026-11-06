package com.mylearn.tracker.plan;

import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.exception.StaleUpdateException;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.PlanResponse;
import com.mylearn.tracker.plan.dto.UpdatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanServiceIntegrationTest extends IntegrationTest {

    @Autowired
    PlanService plans;
    @Autowired
    ItemService items;
    @Autowired
    LearningItemRepository itemRepository;

    @Test
    void createAndReadBack() {
        PlanResponse created = plans.create(new CreatePlanRequest("  Spring Boot Mastery  ", "   "));

        PlanResponse read = plans.get(created.id());
        assertThat(read.name()).isEqualTo("Spring Boot Mastery"); // trimmed
        assertThat(read.description()).isNull();                  // blank -> null
        assertThat(read.status()).isEqualTo(PlanStatus.ACTIVE);
        assertThat(read.version()).isZero();
        assertThat(read.createdAt()).isNotNull().isEqualTo(read.updatedAt());
    }

    @Test
    void duplicateNameIsRejectedIgnoringCase() {
        plans.create(new CreatePlanRequest("Core Java", null));

        assertThatThrownBy(() -> plans.create(new CreatePlanRequest("core JAVA", null)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void updateChangesFieldsAndIncrementsVersion() {
        PlanResponse plan = plans.create(new CreatePlanRequest("AWS", null));

        PlanResponse updated = plans.update(plan.id(),
                new UpdatePlanRequest("AWS Certification", "SAA-C03", PlanStatus.ARCHIVED, plan.version()));

        assertThat(updated.name()).isEqualTo("AWS Certification");
        assertThat(updated.description()).isEqualTo("SAA-C03");
        assertThat(updated.status()).isEqualTo(PlanStatus.ARCHIVED);
        assertThat(updated.version()).isEqualTo(plan.version() + 1);
    }

    @Test
    void archiveAndActivateRoundTrip() {
        PlanResponse plan = plans.create(new CreatePlanRequest("MBA", null));
        PlanResponse archived = plans.update(plan.id(), new UpdatePlanRequest("MBA", null, PlanStatus.ARCHIVED, plan.version()));
        PlanResponse active = plans.update(plan.id(), new UpdatePlanRequest("MBA", null, PlanStatus.ACTIVE, archived.version()));

        assertThat(active.status()).isEqualTo(PlanStatus.ACTIVE);
    }

    @Test
    void secondEditBasedOnTheSameVersionIsStale() {
        PlanResponse plan = plans.create(new CreatePlanRequest("System Design", null));
        plans.update(plan.id(), new UpdatePlanRequest("System Design I", null, PlanStatus.ACTIVE, plan.version()));

        // Another tab still holds the original version
        assertThatThrownBy(() -> plans.update(plan.id(),
                new UpdatePlanRequest("System Design II", null, PlanStatus.ACTIVE, plan.version())))
                .isInstanceOf(StaleUpdateException.class);
    }

    @Test
    void renamingToAnotherPlansNameIsRejectedButKeepingOwnNameIsFine() {
        plans.create(new CreatePlanRequest("Python", null));
        PlanResponse java = plans.create(new CreatePlanRequest("Java", null));

        assertThatThrownBy(() -> plans.update(java.id(), new UpdatePlanRequest("PYTHON", null, PlanStatus.ACTIVE, java.version())))
                .isInstanceOf(ConflictException.class);
        assertThat(plans.update(java.id(), new UpdatePlanRequest("JAVA", null, PlanStatus.ACTIVE, java.version())).name())
                .isEqualTo("JAVA");
    }

    @Test
    void searchMatchesNameOrDescriptionAndFiltersByStatus() {
        plans.create(new CreatePlanRequest("Odoo Developer Roadmap", "ORM, views, OWL"));
        plans.create(new CreatePlanRequest("Core Java", "JVM and collections"));
        PlanResponse aws = plans.create(new CreatePlanRequest("AWS", "100% cloud"));
        plans.update(aws.id(), new UpdatePlanRequest("AWS", "100% cloud", PlanStatus.ARCHIVED, aws.version()));

        assertThat(plans.search("odoo", null)).extracting(PlanResponse::name).containsExactly("Odoo Developer Roadmap");
        assertThat(plans.search("jvm", null)).extracting(PlanResponse::name).containsExactly("Core Java");
        assertThat(plans.search("%", null)).extracting(PlanResponse::name).containsExactly("AWS"); // % is literal
        assertThat(plans.search(null, PlanStatus.ACTIVE)).extracting(PlanResponse::name)
                .containsExactly("Core Java", "Odoo Developer Roadmap"); // sorted by name
        assertThat(plans.search("  ", PlanStatus.ARCHIVED)).extracting(PlanResponse::name).containsExactly("AWS");
    }

    @Test
    void deletingAPlanDeletesItsItems() {
        PlanResponse plan = plans.create(new CreatePlanRequest("Throwaway", null));
        var module = items.create(plan.id(), new CreateItemRequest("Module", ItemType.MODULE, null, null, null, null));
        items.create(plan.id(), new CreateItemRequest("Topic", ItemType.TOPIC, module.id(), null, null, null));

        plans.delete(plan.id());

        assertThatThrownBy(() -> plans.get(plan.id())).isInstanceOf(NotFoundException.class);
        assertThat(itemRepository.countByPlan_Id(plan.id())).isZero();
    }
}
