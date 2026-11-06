package com.mylearn.tracker.common;

public final class Text {

    private Text() {
    }

    /** Trims; turns {@code null}, empty and whitespace-only input into {@code null}. */
    public static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
