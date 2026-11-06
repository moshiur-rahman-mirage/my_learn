package com.mylearn.tracker.common.web;

import java.time.Instant;
import java.util.List;

/** The single JSON error shape returned by every endpoint. */
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<FieldError> fieldErrors,
        String path,
        Instant timestamp) {

    public record FieldError(String field, String message) {
    }
}
