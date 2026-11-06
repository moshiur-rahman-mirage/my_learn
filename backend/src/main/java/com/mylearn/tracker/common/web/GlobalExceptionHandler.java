package com.mylearn.tracker.common.web;

import com.mylearn.tracker.common.exception.BusinessRuleException;
import com.mylearn.tracker.common.exception.ConflictException;
import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.common.persistence.Timestamps;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/** Maps exceptions to HTTP statuses and the common {@link ErrorResponse} body. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(NotFoundException e, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> noResource(NoResourceFoundException e, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, "No endpoint " + req.getRequestURI(), req);
    }

    /** Duplicate names, stale versions, invalid hierarchy changes. */
    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ErrorResponse> conflict(ConflictException e, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, e.getMessage(), req);
    }

    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ErrorResponse> businessRule(BusinessRuleException e, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), req);
    }

    /** A database constraint caught something the service layer didn't (e.g. a race on a unique name). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> integrity(DataIntegrityViolationException e, HttpServletRequest req) {
        log.warn("Constraint violation on {}: {}", req.getRequestURI(), e.getMostSpecificCause().getMessage());
        return error(HttpStatus.CONFLICT, "The change violates a data integrity rule", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<ErrorResponse.FieldError> fields = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorResponse.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Bad Request", "Validation failed",
                fields, req.getRequestURI(), Timestamps.now()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    ResponseEntity<ErrorResponse> badRequest(Exception e, HttpServletRequest req) {
        String message = e instanceof HttpMessageNotReadableException
                ? "Malformed request body"
                : e.getMessage();
        return error(HttpStatus.BAD_REQUEST, message, req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> method(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), req);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ErrorResponse> tooLarge(MaxUploadSizeExceededException e, HttpServletRequest req) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "The uploaded file is too large", req);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception e, HttpServletRequest req) {
        log.error("Unexpected error on {} {}", req.getMethod(), req.getRequestURI(), e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req);
    }

    private static ResponseEntity<ErrorResponse> error(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(),
                message, List.of(), req.getRequestURI(), Timestamps.now()));
    }
}
