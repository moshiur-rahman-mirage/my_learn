package com.mylearn.tracker.common.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Stores every {@link Instant} as fixed-width ISO-8601 UTC text, e.g. {@code 2026-11-06T07:47:28.123Z}.
 *
 * <p>Without this, the SQLite JDBC driver writes timestamps as epoch milliseconds. Fixed width
 * (always 3 fraction digits) keeps string ordering identical to chronological ordering, so
 * {@code ORDER BY updated_at} works in plain SQL, and the file stays readable in any SQLite tool.
 */
@Converter(autoApply = true)
public class InstantStringConverter implements AttributeConverter<Instant, String> {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    @Override
    public String convertToDatabaseColumn(Instant value) {
        return value == null ? null : FORMAT.format(value);
    }

    @Override
    public Instant convertToEntityAttribute(String value) {
        return value == null ? null : Instant.parse(value);
    }
}
