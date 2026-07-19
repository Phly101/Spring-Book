package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class BookNotFoundException extends MainException {

    public BookNotFoundException(String isbn) {
        super("Book was not found: "+isbn);
    }

    @Override
    public HttpStatus getHTTPStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorCode() {
        return "BOOK_WAS_NOT_FOUND";
    }

    @Override
    public String getErrorMessage() {
        return "Book was not found, either not created or a wrong isbn was provided!";
    }
}
