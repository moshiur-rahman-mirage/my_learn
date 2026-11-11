package com.mylearn.tracker;

import com.mylearn.tracker.confidence.ConfidenceService;
import com.mylearn.tracker.flashcard.FlashcardService;
import com.mylearn.tracker.flashcard.FlashcardService.DueCard;
import com.mylearn.tracker.importer.ImportDtos.ImportResult;
import com.mylearn.tracker.importer.PlanBackupService;
import com.mylearn.tracker.importer.PlanBackupService.BackupPreview;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.link.LinkService;
import com.mylearn.tracker.note.NoteFormat;
import com.mylearn.tracker.note.NoteLinks;
import com.mylearn.tracker.note.NoteService;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.review.ReviewService;
import com.mylearn.tracker.support.IntegrationTest;
import com.mylearn.tracker.tag.TagService;
import com.mylearn.tracker.timelog.TimeLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Flashcards, [[links]], confidence ratings and the JSON plan backup. */
class LearningAidsIntegrationTest extends IntegrationTest {

    @Autowired PlanService plans;
    @Autowired ItemService items;
    @Autowired NoteService notes;
    @Autowired FlashcardService cards;
    @Autowired NoteLinks links;
    @Autowired ConfidenceService confidence;
    @Autowired ReviewService reviews;
    @Autowired PlanBackupService planBackups;
    @Autowired TagService tags;
    @Autowired LinkService webLinks;
    @Autowired TimeLogService time;
    @Autowired JdbcTemplate jdbc;

    Long planId;
    ItemResponse module;
    ItemResponse di;
    ItemResponse beans;

    @BeforeEach
    void plan() {
        planId = plans.create(new CreatePlanRequest("Aids " + System.nanoTime(), null)).id();
        module = items.create(planId, new CreateItemRequest("Spring", ItemType.MODULE, null, null, null, null));
        di = items.create(planId, new CreateItemRequest("Dependency Injection", ItemType.TOPIC, module.id(), null, null, null));
        beans = items.create(planId, new CreateItemRequest("Beans", ItemType.TOPIC, module.id(), null, null, null));
    }

    private List<DueCard> dueHere() {
        return cards.due(planId);
    }

    @Test
    void flashcardsFollowTheNote_andKeepTheirScheduleWhenAnswersChange() {
        notes.save(di.id(), "Q: What is DI?\nA: Passing dependencies in.\n\nQ: Who creates beans?\nA: The container.", NoteFormat.MARKDOWN);
        assertThat(dueHere()).extracting(DueCard::question).containsExactly("What is DI?", "Who creates beans?");

        Long first = dueHere().getFirst().id();
        var outcome = cards.review(first, true);
        assertThat(outcome.nextDue()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(dueHere()).hasSize(1);

        // edit the answer, drop the second card, add a third
        notes.save(di.id(), "Q: what is  DI?\nA: Inversion of control for dependencies.\n\nQ: New one?\nA: yes", NoteFormat.MARKDOWN);
        assertThat(cards.forItem(di.id())).hasSize(2);
        assertThat(dueHere()).extracting(DueCard::question).containsExactly("New one?"); // the reviewed card is still scheduled
        assertThat(items.tree(planId).getFirst().children().getFirst().flashcards()).isEqualTo(2);

        cards.review(dueHere().getFirst().id(), false); // "again" keeps it due today
        assertThat(dueHere()).hasSize(1);

        notes.save(di.id(), "", NoteFormat.MARKDOWN); // deleting the note deletes its cards
        assertThat(cards.forItem(di.id())).isEmpty();
    }

    @Test
    void wikiLinksGiveBacklinks() {
        notes.save(beans.id(), "Beans are created with [[dependency   injection|DI]] and [[Missing]].", NoteFormat.MARKDOWN);
        notes.save(module.id(), "<p>See [[Dependency Injection]]</p>", NoteFormat.HTML);
        assertThat(links.backlinks(di.id())).extracting(NoteLinks.Backlink::name).containsExactly("Spring", "Beans");
        assertThat(links.backlinks(beans.id())).isEmpty();

        notes.save(beans.id(), "no links now", NoteFormat.MARKDOWN);
        assertThat(links.backlinks(di.id())).extracting(NoteLinks.Backlink::name).containsExactly("Spring");
    }

    @Test
    void shakyRatingOfACompletedTopicBringsItsReviewForward() {
        items.setCompletion(di.id(), true);
        reviews.review(di.id(), true); // next review in 7 days
        var rated = confidence.rate(di.id(), 2);
        assertThat(rated.reviewOn()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(jdbc.queryForObject("SELECT due_on FROM item_review WHERE item_id = ?", String.class, di.id()))
                .isEqualTo(LocalDate.now().plusDays(1).toString());
        assertThat(confidence.shaky()).extracting(ConfidenceService.Shaky::itemId).contains(di.id());

        assertThat(confidence.rate(beans.id(), 5).reviewOn()).isNull();
        TreeNode diNode = items.tree(planId).getFirst().children().getFirst();
        assertThat(diNode.confidence()).isEqualTo(2);

        confidence.rate(di.id(), null);
        assertThat(items.tree(planId).getFirst().children().getFirst().confidence()).isNull();
    }

    @Test
    void jsonBackupRestoresEverythingAsANewPlan() {
        notes.save(di.id(), "# DI\n\nQ: What is DI?\nA: Injection.\n\nSee [[Beans]]", NoteFormat.MARKDOWN);
        items.setCompletion(di.id(), true);
        cards.review(dueHere().getFirst().id(), true);
        tags.setTags(di.id(), List.of("core", "interview"));
        webLinks.add(di.id(), "https://example.com/di", "Docs");
        time.log(di.id(), 25);
        confidence.rate(di.id(), 4);
        String diExternalId = items.tree(planId).getFirst().children().getFirst().externalId();

        var export = planBackups.export(planId);
        String text = new String(export.content(), StandardCharsets.UTF_8);
        assertThat(text).contains("\"format\" : \"learning-tracker-plan\"").contains("What is DI?").contains("interview");

        BackupPreview preview = planBackups.preview(export.fileName(), export.content());
        assertThat(preview.nameTaken()).isTrue();
        assertThat(preview.suggestedName()).endsWith("(restored)");
        assertThat(preview.topics()).isEqualTo(2);
        assertThat(preview.notes()).isEqualTo(1);
        assertThat(preview.flashcards()).isEqualTo(1);

        ImportResult result = planBackups.restore(export.fileName(), export.content(), preview.suggestedName());
        Long restored = result.plans().getFirst().planId();
        assertThat(result.plans().getFirst().itemsCreated()).isEqualTo(3);

        TreeNode copy = items.tree(restored).getFirst().children().getFirst();
        assertThat(copy.name()).isEqualTo("Dependency Injection");
        assertThat(copy.completed()).isTrue();
        assertThat(copy.externalId()).isNotEqualTo(diExternalId); // in use by the original plan
        assertThat(copy.tags()).containsExactly("core", "interview");
        assertThat(copy.links()).extracting(l -> l.url()).containsExactly("https://example.com/di");
        assertThat(copy.minutes()).isEqualTo(25);
        assertThat(copy.confidence()).isEqualTo(4);
        assertThat(copy.reviewDueOn()).isNotNull();
        assertThat(notes.get(copy.id()).content()).startsWith("# DI");
        assertThat(cards.forItem(copy.id())).singleElement()
                .satisfies(c -> assertThat(c.dueOn()).isEqualTo(LocalDate.now().plusDays(1))); // schedule restored
        assertThat(cards.due(restored)).isEmpty();

        // after the original is deleted, a restore keeps the original ids
        plans.delete(planId);
        Long again = planBackups.restore(export.fileName(), export.content(), null).plans().getFirst().planId();
        assertThat(items.tree(again).getFirst().children().getFirst().externalId()).isEqualTo(diExternalId);
    }
}
