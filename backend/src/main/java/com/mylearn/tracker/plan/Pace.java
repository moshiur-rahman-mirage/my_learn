package com.mylearn.tracker.plan;

import com.mylearn.tracker.progress.Progress;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * How a plan with a target date is doing.
 *
 * @param daysLeft           days from today to the target day, counting the target day (0 = overdue)
 * @param remaining          topics still to complete
 * @param perWeekNeeded      topics per week needed to finish on time (null when done or overdue)
 * @param perWeekRecent      topics per week you completed over the last {@value #RECENT_DAYS} days
 * @param projectedFinish    when you'd finish at the recent rate (null when the rate is 0 or done)
 */
public record Pace(LocalDate targetDate, long daysLeft, int remaining, Double perWeekNeeded, double perWeekRecent,
                   LocalDate projectedFinish, Status status) {

    public static final int RECENT_DAYS = 28;

    public enum Status {
        /** all topics completed */
        DONE,
        /** the recent rate finishes on time */
        ON_TRACK,
        /** the recent rate isn't enough */
        BEHIND,
        /** the target day has passed with topics left */
        OVERDUE,
        /** nothing to measure yet (no topics) */
        NO_TOPICS
    }

    /** @return null when the plan has no target date */
    public static Pace of(LocalDate targetDate, Progress progress, int completedRecently, LocalDate today) {
        if (targetDate == null) {
            return null;
        }
        long daysLeft = Math.max(0, ChronoUnit.DAYS.between(today, targetDate) + 1);
        int remaining = progress.remaining();
        double recent = round(completedRecently * 7.0 / RECENT_DAYS);
        if (progress.total() == 0) {
            return new Pace(targetDate, daysLeft, 0, null, recent, null, Status.NO_TOPICS);
        }
        if (remaining == 0) {
            return new Pace(targetDate, daysLeft, 0, null, recent, null, Status.DONE);
        }
        LocalDate projected = recent > 0 ? today.plusDays((long) Math.ceil(remaining / (recent / 7.0)) - 1) : null;
        if (daysLeft == 0) {
            return new Pace(targetDate, 0, remaining, null, recent, projected, Status.OVERDUE);
        }
        double needed = round(remaining * 7.0 / daysLeft);
        Status status = projected != null && !projected.isAfter(targetDate) ? Status.ON_TRACK : Status.BEHIND;
        return new Pace(targetDate, daysLeft, remaining, needed, recent, projected, status);
    }

    private static double round(double v) {
        return Math.round(v * 10) / 10.0;
    }
}
