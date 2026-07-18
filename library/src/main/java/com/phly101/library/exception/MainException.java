package com.phly101.library.exception;

// Use an abstract class instead of an interface
public abstract class MainException extends RuntimeException {

    public MainException(String message) {
        super(message);
    }

    public MainException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getErrorCode();

    public abstract String getErrorMessage();
}

