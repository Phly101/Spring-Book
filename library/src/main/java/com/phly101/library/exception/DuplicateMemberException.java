package com.phly101.library.exception;


import org.springframework.http.HttpStatus;

public class DuplicateMemberException extends MainException {
    private final String memberId;

    public DuplicateMemberException(String memberId) {
        super("Duplicate member: " + memberId);
        this.memberId = memberId;
    }

    @Override
    public HttpStatus getHttpstatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "ALREADY_CREATED";
    }

    @Override
    public String getErrorMessage() {
        return "The member ID '" + memberId + "' is already registered. " +
                "Either enter a valid new member or just use your ID for the task.";
    }
}
