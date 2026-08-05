package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class MemberHasActiveLoansException extends MainException {

    public MemberHasActiveLoansException(String memberId) {
        super("Member has active loans: " + memberId);
    }

    @Override
    public HttpStatus getHttpstatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "MEMBER_HAS_ACTIVE_LOANS";
    }

    @Override
    public String getErrorMessage() {
        return "return all borrowed books before deleting this member";
    }
}