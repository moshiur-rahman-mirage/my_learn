package com.mylearn.tracker.common.exception;

/** The requested resource does not exist (HTTP 404). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Object id) {
        super("%s %s not found".formatted(resource, id));
    }
}
