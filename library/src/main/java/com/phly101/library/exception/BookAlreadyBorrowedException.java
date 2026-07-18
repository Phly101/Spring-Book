package com.phly101.library.exception;

public class BookAlreadyBorrowedException extends MainException {

    public BookAlreadyBorrowedException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "ALREADY_BORROWED_BOOK";
    }

    @Override
    public String getErrorMessage() {
        return "Either wait for the due date or request to borrow another book";
    }
}
