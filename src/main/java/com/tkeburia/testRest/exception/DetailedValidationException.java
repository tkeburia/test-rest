package com.tkeburia.testRest.exception;

import org.everit.json.schema.ValidationException;

import java.util.HashSet;
import java.util.Set;

public class DetailedValidationException extends RuntimeException {

    public DetailedValidationException(ValidationException e) {
        this(e.getMessage(), new HashSet<>(e.getAllMessages()));
    }

    private DetailedValidationException(String baseMessage, Set<String> violations) {
        this(String.format("%s. %d violations found: %s", baseMessage, violations.size(), violations.toString()));
    }

    private DetailedValidationException(String message) {
        super(message);
    }
}
