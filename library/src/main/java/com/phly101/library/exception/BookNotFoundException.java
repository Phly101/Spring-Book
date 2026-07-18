package com.phly101.library.exception;

public class BookNotFoundException extends MainException {

    public BookNotFoundException(String message) {
        super(message);
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
