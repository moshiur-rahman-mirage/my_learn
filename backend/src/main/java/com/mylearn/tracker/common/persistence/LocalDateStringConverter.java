package com.mylearn.tracker.common.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

/** Stores every {@link LocalDate} as ISO text ({@code 2026-11-06}) instead of the driver's epoch millis. */
@Converter(autoApply = true)
public class LocalDateStringConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate value) {
        return value == null ? null : value.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}
