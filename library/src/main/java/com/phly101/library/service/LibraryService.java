package com.phly101.library.service;

import java.time.LocalDate;
import java.util.*;

import com.phly101.library.exception.*;
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
        for (Member member : members) {
            if (findMemberById(member.getMemberId()).isPresent()) {
                throw new DuplicateMemberException(member.getMemberId());
            }
        }
        this.members.addAll(Arrays.asList(members));
    }

    public Optional<Member> findMemberById(String memberId) {
        if (memberId != null) {
            return members
                    .stream()
                    .filter(member -> member.getMemberId().equals(memberId))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    public void returnBook(String isbn) {
        Book book = findBookByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        Loan removedLoan = loans.remove(isbn);
        if (removedLoan != null) {
            book.returnItem();
        } else {
            throw new LoanNotFoundException(isbn);
        }
    }

    private LocalDate getDueDate(final LocalDate loanDate, final int duePeriod) {
        return loanDate.plusDays(duePeriod);
    }

    public Loan borrowBook(String memberId, String isbn) {
        Member member = findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        Book book = findBookByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        if (book.borrow()) {
            LocalDate today = LocalDate.now();
            Loan newLoan = new Loan(member, book, today, getDueDate(today, member.getDuration()));
            loans.put(book.getIsbn(), newLoan);
            totalTransactions++;
            return newLoan;
        } else {
            throw new BookAlreadyBorrowedException(book.getIsbn());
        }
    }
}
