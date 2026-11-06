package com.mylearn.tracker.common.persistence;

import java.time.Instant;

/** The stored text form of an {@link Instant}, for plain-SQL parameters (see {@link InstantStringConverter}). */
public final class InstantText {

    private static final InstantStringConverter CONVERTER = new InstantStringConverter();

    private InstantText() {
    }

    public static String format(Instant instant) {
        return CONVERTER.convertToDatabaseColumn(instant);
    }
}
