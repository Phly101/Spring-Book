package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends MainException {
    final String isbn;

    public LoanNotFoundException(String isbn) {
        super("Loan was not Found! with book isbn: " + isbn);
        this.isbn = isbn;
    }

    @Override
    public HttpStatus getHttpstatus() {
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
