package com.tkeburia.testRest.exception;

public class MissingPropertyException extends RuntimeException {
    public MissingPropertyException(String message) {
        super(message);
    }
}
