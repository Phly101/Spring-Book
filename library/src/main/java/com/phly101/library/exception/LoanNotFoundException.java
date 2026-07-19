package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends MainException {

    public LoanNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHTTPStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorCode() {
        return "LOAN_WAS_NOT_FOUND";
    }

    @Override
    public String getErrorMessage() {
        return "Cannot access a none existent loan either request an already existing one or create a new loan";
    }
}
