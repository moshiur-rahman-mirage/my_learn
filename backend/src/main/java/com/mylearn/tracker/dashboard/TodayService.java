package com.mylearn.tracker.dashboard;

import com.mylearn.tracker.item.LearningItemRepository;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.item.TreeBuilder;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.LearningPlan;
import com.mylearn.tracker.plan.LearningPlanRepository;
import com.mylearn.tracker.plan.PlanStatus;
import com.mylearn.tracker.review.ReviewService;
import com.mylearn.tracker.review.ReviewService.DueReview;
import com.mylearn.tracker.timelog.TimeLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** What to do today: reviews that are due and the next topics of every active plan. */
@Service
@Transactional(readOnly = true)
public class TodayService {

    static final int NEXT_PER_PLAN = 3;

    private final LearningPlanRepository plans;
    private final LearningItemRepository items;
    private final ReviewService reviews;
    private final TimeLogService time;

    public TodayService(LearningPlanRepository plans, LearningItemRepository items, ReviewService reviews,
                        TimeLogService time) {
        this.plans = plans;
        this.items = items;
        this.reviews = reviews;
        this.time = time;
    }

    public Today today(ZoneId zone) {
        List<PlanNext> next = new ArrayList<>();
        List<LearningPlan> active = plans.findAll().stream()
                .filter(p -> p.getStatus() == PlanStatus.ACTIVE)
                .sorted(Comparator.comparing(p -> p.getName().toLowerCase()))
                .toList();
        for (LearningPlan plan : active) {
            List<NextTopic> topics = nextTopics(TreeBuilder.build(items.findAllByPlan(plan.getId())));
            if (!topics.isEmpty()) {
                next.add(new PlanNext(plan.getId(), plan.getName(), topics));
            }
        }
        Instant startOfToday = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        return new Today(reviews.due(), next, time.minutesSince(startOfToday),
                time.minutesSince(Instant.now().minus(Duration.ofDays(7))));
    }

    /**
     * The first unfinished topics in roadmap order; MUST topics come before the others, then
     * SHOULD, no priority and NICE, keeping roadmap order within each group.
     */
    static List<NextTopic> nextTopics(List<TreeNode> roots) {
        List<NextTopic> open = new ArrayList<>();
        collect(roots, new ArrayList<>(), open);
        return open.stream()
                .sorted(Comparator.comparingInt(t -> rank(t.priority())))
                .limit(NEXT_PER_PLAN)
                .toList();
    }

    private static void collect(List<TreeNode> nodes, List<String> path, List<NextTopic> out) {
        for (TreeNode n : nodes) {
            if (n.checkable() && !n.completed()) {
                out.add(new NextTopic(n.id(), n.name(), n.priority(), List.copyOf(path)));
            }
            if (!n.children().isEmpty()) {
                path.add(n.name());
                collect(n.children(), path, out);
                path.removeLast();
            }
        }
    }

    private static int rank(Priority p) {
        if (p == null) {
            return 2;
        }
        return switch (p) {
            case MUST -> 0;
            case SHOULD -> 1;
            case NICE -> 3;
        };
    }

    /** @param path ancestor names, root first */
    public record NextTopic(Long itemId, String name, Priority priority, List<String> path) {
    }

    public record PlanNext(Long planId, String planName, List<NextTopic> topics) {
    }

    public record Today(List<DueReview> dueReviews, List<PlanNext> nextUp, int minutesToday, int minutesLast7Days) {
    }
}
