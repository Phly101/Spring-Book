package com.phly101.library.exception;

import org.springframework.http.HttpStatus;

public class BookCurrentlyBorrowedException extends MainException {

    public BookCurrentlyBorrowedException(String isbn) {
        super("Book is currently borrowed: " + isbn);
    }

    @Override
    public HttpStatus getHttpstatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "BOOK_CURRENTLY_BORROWED";
    }

    @Override
    public String getErrorMessage() {
        return "return the book before deleting it";
    }
}