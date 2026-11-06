package com.mylearn.tracker.common.exception;

/**
 * A syntactically valid request that breaks a domain rule, e.g. a parent from another plan
 * or a position outside the sibling range (HTTP 422).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
