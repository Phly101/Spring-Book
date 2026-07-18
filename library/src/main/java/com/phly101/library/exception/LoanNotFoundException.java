package com.phly101.library.exception;

public class LoanNotFoundException extends MainException {

    public LoanNotFoundException(String message) {
        super(message);
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
