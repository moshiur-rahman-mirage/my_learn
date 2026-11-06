package com.mylearn.tracker.common.exception;

/** The request clashes with the current state, e.g. a duplicate name (HTTP 409). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
