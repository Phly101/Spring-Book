package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class BookAlreadyExistsException extends MainException {

    public BookAlreadyExistsException(String isbn) {
        super("Book already exists: " + isbn);
    }

    @Override
    public HttpStatus getHttpstatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "ALREADY_EXISTS_BOOK";
    }

    @Override
    public String getErrorMessage() {
        return "try and add a new book with different isbn";
    }
}
