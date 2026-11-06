package com.mylearn.tracker.dashboard;

import com.mylearn.tracker.progress.Progress;
import com.mylearn.tracker.progress.ProgressQueries.LeafTimes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Day-by-day history, derived from the creation and completion times of the current leaf topics
 * (nothing extra is stored). A topic that is un-ticked again simply drops out of the history.
 *
 * <p>Topics that were imported already ticked count toward progress, but not as "completed by
 * you" on the import day: their completion time equals their creation time.
 */
public final class ActivityCalculator {

    /** Completed within this time of being created = came in ticked (import). */
    static final Duration IMPORTED_TOLERANCE = Duration.ofSeconds(5);
    public static final int MAX_DAYS = 3660;

    private ActivityCalculator() {
    }

    /**
     * @param days number of days ending today; 0 = from the first topic's creation day
     */
    static Activity compute(List<LeafTimes> leaves, LocalDate today, ZoneId zone, int days) {
        Map<LocalDate, Integer> created = new HashMap<>();
        Map<LocalDate, Integer> doneByYou = new HashMap<>();
        Map<LocalDate, Integer> doneImported = new HashMap<>();
        LocalDate first = today;

        for (LeafTimes leaf : leaves) {
            LocalDate c = leaf.createdAt().atZone(zone).toLocalDate();
            created.merge(c, 1, Integer::sum);
            if (c.isBefore(first)) {
                first = c;
            }
            if (leaf.completedAt() != null) {
                LocalDate d = leaf.completedAt().atZone(zone).toLocalDate();
                boolean imported = Duration.between(leaf.createdAt(), leaf.completedAt()).abs()
                        .compareTo(IMPORTED_TOLERANCE) <= 0;
                (imported ? doneImported : doneByYou).merge(d, 1, Integer::sum);
            }
        }

        LocalDate earliestAllowed = today.minusDays(MAX_DAYS - 1L);
        LocalDate start = days <= 0 ? first : today.minusDays(Math.min(days, MAX_DAYS) - 1L);
        if (start.isBefore(earliestAllowed)) {
            start = earliestAllowed;
        }

        // totals as of the day before the range
        int total = sumBefore(created, start);
        int completed = sumBefore(doneByYou, start) + sumBefore(doneImported, start);

        List<ActivityDay> series = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            int you = doneByYou.getOrDefault(d, 0);
            int imported = doneImported.getOrDefault(d, 0);
            total += created.getOrDefault(d, 0);
            completed += you + imported;
            series.add(new ActivityDay(d, you, imported, total, completed, Progress.of(completed, total).percent()));
        }

        TreeSet<LocalDate> activeDays = new TreeSet<>(doneByYou.keySet());
        return new Activity(zone.getId(), series,
                doneByYou.getOrDefault(today, 0),
                sumBetween(doneByYou, today.minusDays(6), today),
                sumBetween(doneByYou, today.minusDays(29), today),
                currentStreak(activeDays, today),
                bestStreak(activeDays));
    }

    /** Consecutive days with a completion, ending today (or yesterday: today isn't over yet). */
    static int currentStreak(TreeSet<LocalDate> activeDays, LocalDate today) {
        LocalDate d = activeDays.contains(today) ? today : today.minusDays(1);
        int streak = 0;
        while (activeDays.contains(d)) {
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }

    static int bestStreak(TreeSet<LocalDate> activeDays) {
        int best = 0;
        int run = 0;
        LocalDate previous = null;
        for (LocalDate d : activeDays) {
            run = previous != null && previous.plusDays(1).equals(d) ? run + 1 : 1;
            best = Math.max(best, run);
            previous = d;
        }
        return best;
    }

    private static int sumBefore(Map<LocalDate, Integer> counts, LocalDate day) {
        return counts.entrySet().stream().filter(e -> e.getKey().isBefore(day)).mapToInt(Map.Entry::getValue).sum();
    }

    private static int sumBetween(Map<LocalDate, Integer> counts, LocalDate from, LocalDate to) {
        return counts.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(from) && !e.getKey().isAfter(to))
                .mapToInt(Map.Entry::getValue).sum();
    }

    /**
     * @param completedByYou   topics ticked on this day
     * @param completedImported topics that arrived already ticked (import) on this day
     * @param totalLeaves      topics existing at the end of the day
     * @param completedLeaves  of those, completed by the end of the day
     * @param percent          null on days before the first topic existed
     */
    public record ActivityDay(LocalDate date, int completedByYou, int completedImported,
                       int totalLeaves, int completedLeaves, Double percent) {
    }

    public record Activity(String timezone, List<ActivityDay> days, int completedToday, int completedLast7Days,
                    int completedLast30Days, int currentStreak, int bestStreak) {
    }
}
