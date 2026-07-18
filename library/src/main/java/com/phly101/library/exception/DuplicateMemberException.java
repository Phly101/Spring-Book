package com.phly101.library.exception;

public class DuplicateMemberException extends MainException {

    public DuplicateMemberException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "ALREADY_CREATED";
    }

    @Override
    public String getErrorMessage() {
        return "Either enter a valid new member or just use your id for the task you need done.";
    }
}
