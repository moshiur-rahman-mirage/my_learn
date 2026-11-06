package com.mylearn.tracker;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.dashboard.TodayService;
import com.mylearn.tracker.dashboard.TodayService.NextTopic;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.link.LinkService;
import com.mylearn.tracker.note.NoteService;
import com.mylearn.tracker.plan.Pace;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.plan.dto.PlanResponse;
import com.mylearn.tracker.review.ReviewService;
import com.mylearn.tracker.review.ReviewService.DueReview;
import com.mylearn.tracker.search.SearchService;
import com.mylearn.tracker.search.SearchService.SearchHit;
import com.mylearn.tracker.support.IntegrationTest;
import com.mylearn.tracker.tag.TagService;
import com.mylearn.tracker.tag.TagService.TagCount;
import com.mylearn.tracker.timelog.TimeLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.mylearn.tracker.item.ItemType.MODULE;
import static com.mylearn.tracker.item.ItemType.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Target dates, search, tags, links, time spent, reviews and the Today view. */
class LearningFeaturesIntegrationTest extends IntegrationTest {

    @Autowired PlanService plans;
    @Autowired ItemService items;
    @Autowired NoteService notes;
    @Autowired SearchService search;
    @Autowired TagService tags;
    @Autowired LinkService links;
    @Autowired TimeLogService time;
    @Autowired ReviewService reviews;
    @Autowired TodayService today;
    @Autowired JdbcTemplate jdbc;

    Long planId;

    @BeforeEach
    void plan() {
        planId = plans.create(new CreatePlanRequest("Spring Roadmap", null)).id();
    }

    private ItemResponse create(String name, com.mylearn.tracker.item.ItemType type, Long parentId, Priority priority) {
        return items.create(planId, new CreateItemRequest(name, type, parentId, null, priority, null));
    }

    private TreeNode node(Long id) {
        return find(items.tree(planId), id);
    }

    private static TreeNode find(List<TreeNode> nodes, Long id) {
        for (TreeNode n : nodes) {
            if (n.id().equals(id)) {
                return n;
            }
            TreeNode found = find(n.children(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // --- target date & pace -------------------------------------------------------------------

    @Test
    void targetDateIsStoredAndPaceIsCalculated() {
        LocalDate target = LocalDate.now().plusDays(13); // 14 days including today
        PlanResponse plan = plans.create(new CreatePlanRequest("Exam prep", null, target));
        assertThat(plan.targetDate()).isEqualTo(target);
        Long id = plan.id();
        for (int i = 0; i < 4; i++) {
            items.create(id, new CreateItemRequest("Topic " + i, TOPIC, null, null, null, null));
        }

        Pace pace = plans.get(id).pace();
        assertThat(pace.daysLeft()).isEqualTo(14);
        assertThat(pace.remaining()).isEqualTo(4);
        assertThat(pace.perWeekNeeded()).isEqualTo(2.0);
        assertThat(pace.status()).isEqualTo(Pace.Status.BEHIND); // nothing done recently yet

        assertThat(plans.get(planId).pace()).isNull(); // no target date
    }

    @Test
    void paceStatuses() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        var halfDone = com.mylearn.tracker.progress.Progress.of(5, 10);
        // 5 left, 7 per week recently: finishes Jan 5; target Jan 10 -> on track
        assertThat(Pace.of(LocalDate.of(2026, 1, 10), halfDone, 28, today).status()).isEqualTo(Pace.Status.ON_TRACK);
        assertThat(Pace.of(LocalDate.of(2026, 1, 10), halfDone, 28, today).projectedFinish()).isEqualTo(LocalDate.of(2026, 1, 5));
        // 1 per week recently: 35 days -> behind
        assertThat(Pace.of(LocalDate.of(2026, 1, 10), halfDone, 4, today).status()).isEqualTo(Pace.Status.BEHIND);
        assertThat(Pace.of(LocalDate.of(2025, 12, 31), halfDone, 4, today).status()).isEqualTo(Pace.Status.OVERDUE);
        assertThat(Pace.of(LocalDate.of(2025, 12, 31), com.mylearn.tracker.progress.Progress.of(3, 3), 0, today).status())
                .isEqualTo(Pace.Status.DONE);
        assertThat(Pace.of(LocalDate.of(2026, 1, 1), halfDone, 0, today).daysLeft()).isEqualTo(1); // due today
    }

    // --- search -------------------------------------------------------------------------------

    @Test
    void searchFindsNamesDescriptionsAndNoteText_byWordPrefix() {
        ItemResponse boot = create("Spring Boot basics", TOPIC, null, null);
        ItemResponse di = items.create(planId, new CreateItemRequest("Core container", TOPIC, null,
                "Beans and the application context", null, null));
        notes.save(di.id(), "<p><strong>Dependency</strong> injection &amp; inversion of control</p>");

        assertThat(search.search("sprin boo", null).hits()).extracting(SearchHit::itemId).containsExactly(boot.id());
        SearchHit noteHit = search.search("inject", null).hits().getFirst();
        assertThat(noteHit.itemId()).isEqualTo(di.id());
        assertThat(noteHit.matchedIn()).isEqualTo("note");
        assertThat(noteHit.snippet()).anyMatch(s -> s.hit() && s.text().equalsIgnoreCase("injection"));
        assertThat(search.search("applicat", null).hits().getFirst().matchedIn()).isEqualTo("description");
        // HTML markup is not searchable, and operators typed by the user can't break the query
        assertThat(search.search("strong", null).hits()).isEmpty();
        assertThat(search.search("\"OR( * AND", null).hits()).isEmpty();
        assertThat(search.search("   ", null).hits()).isEmpty();

        // renames and deletes keep the index current (also for items deleted with their parent)
        items.update(boot.id(), new com.mylearn.tracker.item.dto.UpdateItemRequest("Kotlin basics", TOPIC, null, null,
                node(boot.id()).version()));
        assertThat(search.search("spring", null).hits()).isEmpty();
        ItemResponse module = create("Messaging", MODULE, null, null);
        create("Kafka streams", TOPIC, module.id(), null);
        assertThat(search.search("kafka", null).hits()).hasSize(1);
        items.delete(module.id());
        assertThat(search.search("kafka", null).hits()).isEmpty();
    }

    @Test
    void searchShowsWhereAnItemSits_andFiltersByTag() {
        ItemResponse module = create("Data", MODULE, null, null);
        ItemResponse jpa = create("JPA relations", TOPIC, module.id(), null);
        ItemResponse jdbcTopic = create("JDBC template", TOPIC, module.id(), null);
        tags.setTags(jpa.id(), List.of("interview"));

        SearchHit hit = search.search("jpa", null).hits().getFirst();
        assertThat(hit.path()).containsExactly("Data");
        assertThat(hit.planName()).isEqualTo("Spring Roadmap");
        assertThat(hit.checkable()).isTrue();

        assertThat(search.search(null, "INTERVIEW").hits()).extracting(SearchHit::itemId).containsExactly(jpa.id());
        assertThat(search.search("j", "interview").hits()).extracting(SearchHit::itemId).containsExactly(jpa.id());
        assertThat(search.search("jdbc", "interview").hits()).isEmpty();
        assertThat(jdbcTopic.id()).isNotNull();
    }

    // --- tags ---------------------------------------------------------------------------------

    @Test
    void tagsAreNormalizedSharedAndRemovedWhenUnused() {
        ItemResponse a = create("A", TOPIC, null, null);
        ItemResponse b = create("B", TOPIC, null, null);

        assertThat(tags.setTags(a.id(), List.of(" Revise ", "#interview", "revise", ""))).containsExactly("interview", "Revise");
        assertThat(tags.setTags(b.id(), List.of("REVISE"))).containsExactly("Revise"); // existing spelling wins
        assertThat(tags.all()).extracting(TagCount::name, TagCount::items)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("interview", 1), org.assertj.core.groups.Tuple.tuple("Revise", 2));
        assertThat(node(a.id()).tags()).containsExactly("interview", "Revise");

        tags.setTags(a.id(), List.of());
        assertThat(tags.all()).extracting(TagCount::name).containsExactly("Revise");
        assertThatThrownBy(() -> tags.setTags(a.id(), List.of("x".repeat(41)))).isInstanceOf(BusinessRuleException.class);
    }

    // --- links --------------------------------------------------------------------------------

    @Test
    void linksAreWebLinksOnly_andShownInTheTree() {
        ItemResponse topic = create("Security", TOPIC, null, null);
        var link = links.add(topic.id(), "docs.spring.io/spring-security", "Reference");
        assertThat(link.url()).isEqualTo("https://docs.spring.io/spring-security");
        links.add(topic.id(), "https://youtu.be/abc", "  ");

        assertThat(node(topic.id()).links()).extracting(l -> l.title()).containsExactly("Reference", null);
        assertThatThrownBy(() -> links.add(topic.id(), "javascript:alert(1)", null)).isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> links.add(topic.id(), "file:///C:/secret.txt", null)).isInstanceOf(BusinessRuleException.class);

        links.delete(link.id());
        assertThat(links.list(topic.id())).hasSize(1);
    }

    // --- time spent ---------------------------------------------------------------------------

    @Test
    void timeIsLoggedPerItemAndRolledUp() {
        ItemResponse module = create("Testing", MODULE, null, null);
        ItemResponse unit = create("Unit tests", TOPIC, module.id(), null);
        ItemResponse it = create("Integration tests", TOPIC, module.id(), null);
        time.log(unit.id(), 25);
        time.log(unit.id(), 10);
        var entry = time.log(it.id(), 50);

        assertThat(node(unit.id()).minutes()).isEqualTo(35);
        assertThat(node(module.id()).minutes()).isZero();
        assertThat(node(module.id()).totalMinutes()).isEqualTo(85);
        assertThat(time.forItem(unit.id()).totalMinutes()).isEqualTo(35);
        assertThat(today.today(ZoneId.systemDefault()).minutesToday()).isGreaterThanOrEqualTo(85);

        time.delete(entry.id());
        assertThat(node(module.id()).totalMinutes()).isEqualTo(35);
        assertThatThrownBy(() -> time.log(unit.id(), 0)).isInstanceOf(BusinessRuleException.class);
    }

    // --- reviews ------------------------------------------------------------------------------

    @Test
    void completedTopicsComeBackForReview_after1_7_and30Days() {
        ItemResponse topic = create("Transactions", TOPIC, null, null);
        items.setCompletion(topic.id(), true);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertThat(node(topic.id()).reviewDueOn()).isEqualTo(tomorrow);
        assertThat(reviews.due()).isEmpty();

        makeDueToday(topic.id());
        DueReview due = reviews.due().getFirst();
        assertThat(due.itemId()).isEqualTo(topic.id());
        assertThat(due.review()).isEqualTo(1);

        assertThat(reviews.review(topic.id(), true).nextDue()).isEqualTo(LocalDate.now().plusDays(7));
        assertThat(reviews.review(topic.id(), false).nextDue()).isEqualTo(tomorrow); // forgot: start over
        reviews.review(topic.id(), true);
        reviews.review(topic.id(), true);
        assertThat(reviews.review(topic.id(), true).finished()).isTrue();
        assertThat(node(topic.id()).reviewDueOn()).isNull();
    }

    @Test
    void untickingOrAddingASubItemCancelsReviews() {
        ItemResponse a = create("A", TOPIC, null, null);
        ItemResponse b = create("B", TOPIC, null, null);
        items.setCompletion(a.id(), true);
        items.setCompletion(b.id(), true);

        items.setCompletion(a.id(), false);
        create("B.1", TOPIC, b.id(), null);

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM item_review", Integer.class)).isZero();
    }

    private void makeDueToday(Long itemId) {
        jdbc.update("UPDATE item_review SET due_on = ? WHERE item_id = ?", LocalDate.now().toString(), itemId);
    }

    // --- today --------------------------------------------------------------------------------

    @Test
    void nextUpListsUnfinishedTopicsMustFirst() {
        ItemResponse module = create("Basics", MODULE, null, null);
        create("Intro", TOPIC, module.id(), null);
        ItemResponse done = create("Setup", TOPIC, module.id(), Priority.MUST);
        create("History", TOPIC, module.id(), Priority.NICE);
        create("Core idea", TOPIC, module.id(), Priority.MUST);
        create("Tooling", TOPIC, module.id(), Priority.SHOULD);
        items.setCompletion(done.id(), true);

        var next = today.today(ZoneId.systemDefault()).nextUp().stream()
                .filter(p -> p.planId().equals(planId)).findFirst().orElseThrow();
        assertThat(next.topics()).extracting(NextTopic::name).containsExactly("Core idea", "Tooling", "Intro");
        assertThat(next.topics().getFirst().path()).containsExactly("Basics");
    }
}
