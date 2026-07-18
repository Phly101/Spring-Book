package com.phly101.library.model;

import java.time.LocalDate;


public record Loan(Member member, Book book, LocalDate loanDate, LocalDate dueDate) {
    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
