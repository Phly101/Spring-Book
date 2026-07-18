package com.phly101.library.service;

import java.time.LocalDate;
import java.util.*;

import com.phly101.library.model.Loan;
import org.springframework.stereotype.Service;

import com.phly101.library.model.Book;
import com.phly101.library.model.Member;

@Service
public class LibraryService {
    private final List<Member> members;
    private final List<Book> books;
    private final Map<String, Loan> loans;

    public LibraryService() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.loans = new HashMap<>();
    }


    private int totalTransactions = 0;

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void addBook(Book... booksToAdd) {
        this.books.addAll(Arrays.asList(booksToAdd));
    }

    public void registerMember(Member... members) {
        this.members.addAll(Arrays.asList(members));
    }

    public Optional<Member> findMemberById(String memberId) {
        return members
                .stream()
                .filter(member -> member.getMemberId().equals(memberId))
                .findFirst();
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    public boolean returnBook(Book book) {
        Loan removedLoan = loans.remove(book.getIsbn());
        if (removedLoan != null) {
            book.returnItem();
            return true;
        } else {
            return false;
        }
    }

    private LocalDate getDueDate(final LocalDate loanDate, final int duePeriod) {
        return loanDate.plusDays(duePeriod);
    }

    public Optional<Loan> borrowBook(Member member, Book book) {

        if (book.borrow()) {
            LocalDate today = LocalDate.now();
            Loan newLoan = new Loan(member, book, today, getDueDate(today, member.getDuration()));
            loans.put(book.getIsbn(), newLoan);
            totalTransactions++;
            return Optional.of(newLoan);
        } else {
            return Optional.empty();
        }

    }
}
