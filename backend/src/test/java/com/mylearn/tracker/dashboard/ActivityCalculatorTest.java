package com.mylearn.tracker.dashboard;

import com.mylearn.tracker.dashboard.ActivityCalculator.Activity;
import com.mylearn.tracker.dashboard.ActivityCalculator.ActivityDay;
import com.mylearn.tracker.progress.ProgressQueries.LeafTimes;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityCalculatorTest {

    private static final ZoneId DHAKA = ZoneId.of("Asia/Dhaka"); // UTC+6
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 6);

    private static Instant at(String local) {
        return java.time.LocalDateTime.parse(local).atZone(DHAKA).toInstant();
    }

    private static LeafTimes leaf(String created, String completed) {
        return new LeafTimes(at(created), completed == null ? null : at(completed));
    }

    @Test
    void dailyCountsCumulativeTotalsAndPercent() {
        List<LeafTimes> leaves = List.of(
                leaf("2026-11-01T09:00:00", "2026-11-03T10:00:00"),
                leaf("2026-11-01T09:00:00", "2026-11-05T23:30:00"), // late evening local = still the 5th
                leaf("2026-11-01T09:00:00", null),
                leaf("2026-11-04T09:00:00", null));

        Activity a = ActivityCalculator.compute(leaves, TODAY, DHAKA, 7);

        assertThat(a.days()).hasSize(7);
        assertThat(a.days().getFirst().date()).isEqualTo(LocalDate.of(2026, 10, 31));
        assertThat(a.days().getFirst().percent()).isNull(); // nothing existed yet
        ActivityDay nov3 = a.days().get(3);
        assertThat(nov3).isEqualTo(new ActivityDay(LocalDate.of(2026, 11, 3), 1, 0, 3, 1, 33.3));
        ActivityDay nov6 = a.days().getLast();
        assertThat(nov6.totalLeaves()).isEqualTo(4);
        assertThat(nov6.completedLeaves()).isEqualTo(2);
        assertThat(nov6.percent()).isEqualTo(50.0);
        assertThat(a.days().get(5).completedByYou()).isEqualTo(1);
        assertThat(a.completedLast7Days()).isEqualTo(2);
        assertThat(a.completedToday()).isZero();
        assertThat(a.timezone()).isEqualTo("Asia/Dhaka");
    }

    @Test
    void topicsImportedAsDoneCountForProgressButNotAsYourActivity() {
        Instant importTime = at("2026-11-05T12:00:00");
        List<LeafTimes> leaves = List.of(
                new LeafTimes(importTime, importTime.plusMillis(3)),
                new LeafTimes(importTime, null));

        ActivityDay nov5 = ActivityCalculator.compute(leaves, TODAY, DHAKA, 2).days().getFirst();

        assertThat(nov5.completedByYou()).isZero();
        assertThat(nov5.completedImported()).isEqualTo(1);
        assertThat(nov5.percent()).isEqualTo(50.0);
    }

    @Test
    void historyBeforeTheRangeIsCarriedIn() {
        List<LeafTimes> leaves = List.of(leaf("2026-01-01T09:00:00", "2026-02-01T09:00:00"), leaf("2026-01-01T09:00:00", null));
        ActivityDay first = ActivityCalculator.compute(leaves, TODAY, DHAKA, 7).days().getFirst();
        assertThat(first.totalLeaves()).isEqualTo(2);
        assertThat(first.completedLeaves()).isEqualTo(1);
    }

    @Test
    void allHistoryStartsAtTheFirstTopic() {
        List<LeafTimes> leaves = List.of(leaf("2026-11-02T09:00:00", null));
        Activity a = ActivityCalculator.compute(leaves, TODAY, DHAKA, 0);
        assertThat(a.days()).hasSize(5);
        assertThat(ActivityCalculator.compute(List.of(), TODAY, DHAKA, 0).days()).hasSize(1);
    }

    @Test
    void streaks() {
        TreeSet<LocalDate> days = new TreeSet<>(List.of(
                TODAY.minusDays(10), TODAY.minusDays(9), TODAY.minusDays(8), TODAY.minusDays(7),
                TODAY.minusDays(2), TODAY.minusDays(1)));
        assertThat(ActivityCalculator.currentStreak(days, TODAY)).isEqualTo(2); // today not done yet: still counts
        assertThat(ActivityCalculator.bestStreak(days)).isEqualTo(4);
        days.add(TODAY);
        assertThat(ActivityCalculator.currentStreak(days, TODAY)).isEqualTo(3);
        assertThat(ActivityCalculator.currentStreak(new TreeSet<>(List.of(TODAY.minusDays(3))), TODAY)).isZero();
    }
}
