package com.tkeburia.testRest.exception;

import org.everit.json.schema.ValidationException;

import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public class DetailedValidationException extends RuntimeException {

    public DetailedValidationException(ValidationException e) {
        this(e.getMessage(), e.getCausingExceptions().stream().map(ValidationException::getMessage).collect(toSet()));
    }

    private DetailedValidationException(String baseMessage, Set<String> violations) {
        this(baseMessage + " " + violations.stream().collect(joining(", ")));
    }

    private DetailedValidationException(String message) {
        super(message);
    }
}
