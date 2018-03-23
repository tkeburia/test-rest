package com.tkeburia.testRest.exception;

public class MissingPropertyException extends RuntimeException {
    public MissingPropertyException() {
        super();
    }

    public MissingPropertyException(String message) {
        super(message);
    }

    public MissingPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingPropertyException(Throwable cause) {
        super(cause);
    }

    protected MissingPropertyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
