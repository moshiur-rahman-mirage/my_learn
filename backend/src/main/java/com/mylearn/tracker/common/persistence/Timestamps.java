package com.mylearn.tracker.common.persistence;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Single source of "now", truncated to the millisecond precision we store. */
public final class Timestamps {

    private Timestamps() {
    }

    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
