package com.mylearn.tracker.dashboard;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.item.Priority;
import com.mylearn.tracker.progress.ProgressQueries;
import com.mylearn.tracker.progress.ProgressQueries.CompletedTopic;
import com.mylearn.tracker.progress.ProgressQueries.NameAndParent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Topics you completed in a date range, grouped by day (newest first) and then by plan. */
@Service
@Transactional(readOnly = true)
public class CompletedHistoryService {

    static final int MAX_RANGE_DAYS = 3660;

    private final ProgressQueries progress;

    public CompletedHistoryService(ProgressQueries progress) {
        this.progress = progress;
    }

    /** @param from first day, inclusive; @param to last day, inclusive (days in {@code zone}) */
    public CompletedHistory completed(LocalDate from, LocalDate to, ZoneId zone, Long planId) {
        if (to.isBefore(from)) {
            throw new BusinessRuleException("'from' must be on or before 'to'");
        }
        if (ChronoUnit.DAYS.between(from, to) >= MAX_RANGE_DAYS) {
            throw new BusinessRuleException("The range can be at most " + MAX_RANGE_DAYS + " days");
        }
        Instant start = from.atStartOfDay(zone).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(zone).toInstant();
        List<CompletedTopic> rows = progress.completedBetween(start, end, planId);

        Set<Long> planIds = rows.stream().map(CompletedTopic::planId).collect(Collectors.toSet());
        Map<Long, NameAndParent> names = progress.namesOfPlans(planIds);

        // day (newest first) -> plan id (first seen order) -> topics
        Map<LocalDate, Map<Long, PlanGroup>> byDay = new TreeMap<>(Collections.reverseOrder());
        for (CompletedTopic r : rows) {
            LocalDate day = r.completedAt().atZone(zone).toLocalDate();
            PlanGroup group = byDay.computeIfAbsent(day, d -> new LinkedHashMap<>())
                    .computeIfAbsent(r.planId(), id -> new PlanGroup(id, r.planName(), new ArrayList<>()));
            group.topics().add(new CompletedItem(r.itemId(), r.name(), priorityOf(r.priority()),
                    pathOf(r.parentId(), names), r.completedAt()));
        }

        List<CompletedDay> days = new ArrayList<>();
        byDay.forEach((day, plans) -> {
            List<PlanGroup> groups = new ArrayList<>(plans.values());
            groups.sort((a, b) -> a.planName().compareToIgnoreCase(b.planName()));
            days.add(new CompletedDay(day, groups.stream().mapToInt(g -> g.topics().size()).sum(), groups));
        });
        return new CompletedHistory(from, to, zone.getId(), rows.size(), days);
    }

    private static List<String> pathOf(Long parentId, Map<Long, NameAndParent> names) {
        List<String> path = new ArrayList<>();
        Long id = parentId;
        int guard = 0;
        while (id != null && guard++ < 100) {
            NameAndParent n = names.get(id);
            if (n == null) {
                break;
            }
            path.add(n.name());
            id = n.parentId();
        }
        Collections.reverse(path);
        return path;
    }

    private static Priority priorityOf(String p) {
        if (p == null) {
            return null;
        }
        try {
            return Priority.valueOf(p);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** @param path ancestor names, root first */
    public record CompletedItem(Long itemId, String name, Priority priority, List<String> path, Instant completedAt) {
    }

    public record PlanGroup(Long planId, String planName, List<CompletedItem> topics) {
    }

    public record CompletedDay(LocalDate date, int count, List<PlanGroup> plans) {
    }

    public record CompletedHistory(LocalDate from, LocalDate to, String zone, int total, List<CompletedDay> days) {
    }
}
