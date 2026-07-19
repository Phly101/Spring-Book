package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends MainException {

    public MemberNotFoundException(String memberId) {
        super("Member Not found: "+memberId);
    }

    @Override
    public HttpStatus getHTTPStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public  String getErrorCode() {
        return "MEMBER_WAS_NOT_FOUND";
    }

    @Override
    public String getErrorMessage() {
        return "Either you entered a wrong member id or made a typo check the id and try again";
    }
}
