package com.phly101.library.service;

import java.time.LocalDate;
import java.util.*;

import com.phly101.library.exception.*;
import com.phly101.library.model.Loan;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.phly101.library.model.Book;
import com.phly101.library.model.Member;

@Service
public class LibraryService {

    private final BookService bookService;
    private final LoanService loanService;
    private final MemberService memberService;


    public LibraryService(BookService bookService, LoanService loanService, MemberService memberService) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.memberService = memberService;

    }


    private int totalTransactions = 0;

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public Book findBookByIsbn(String isbn) {
        return bookService.findBookByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Member findMemberById(String memberId) {
        return memberService.findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }


    @Transactional
    public void returnBook(String isbn, String memberId) {
        Book book = findBookByIsbn(isbn);
        Member member = findMemberById(memberId);
        Loan removedLoan = loanService.findLoan(member.getMemberId(), book.getIsbn());
        removedLoan.setReturnDate(LocalDate.now());
        book.returnItem();
    }

    private LocalDate getDueDate(final LocalDate loanDate, final int duePeriod) {
        return loanDate.plusDays(duePeriod);
    }

    @Transactional
    public Loan borrowBook(String memberId, String isbn) {
        Member member = findMemberById(memberId);
        Book book = findBookByIsbn(isbn);
        if (book.borrow()) {
            LocalDate today = LocalDate.now();
            Loan newLoan = new Loan(member, book, today, getDueDate(today, member.getDuration()));
            totalTransactions++;
            return loanService.createLoan(newLoan);
        } else {
            throw new BookAlreadyBorrowedException(book.getIsbn());
        }
    }


}
