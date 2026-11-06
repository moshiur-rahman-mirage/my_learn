package com.mylearn.tracker.progress;

/**
 * Leaf-based progress: {@code completed} of {@code total} checkable leaf topics.
 * {@code percent} is {@code null} when there are no leaves, and the UI shows "N/A".
 * It is never stored, only computed on read.
 */
public record Progress(int completed, int total, Double percent) {

    public static final Progress NONE = new Progress(0, 0, null);

    public static Progress of(int completed, int total) {
        if (total == 0) {
            return NONE;
        }
        double pct = Math.round(completed * 1000.0 / total) / 10.0; // one decimal place
        return new Progress(completed, total, pct);
    }

    public Progress plus(Progress other) {
        return of(completed + other.completed, total + other.total);
    }

    public int remaining() {
        return total - completed;
    }
}
