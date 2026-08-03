package com.phly101.library.service;

import java.time.LocalDate;
import java.util.*;

import com.phly101.library.exception.*;
import com.phly101.library.model.Loan;
import com.phly101.library.repository.BookRepository;
import com.phly101.library.repository.MemberRepository;
import org.springframework.stereotype.Service;

import com.phly101.library.model.Book;
import com.phly101.library.model.Member;

@Service
public class LibraryService {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final Map<String, Loan> loans;

    public LibraryService(MemberRepository memberRepository, BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loans = new HashMap<>();
    }


    private int totalTransactions = 0;

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public Book addBook(Book book) {
        if (findBookByIsbn(book.getIsbn()).isPresent()) {
            throw new BookAlreadyExistsException(book.getIsbn());
        }
        return bookRepository.save(book);
    }

    public List<Book> addBooks(Book... books) {
        for (Book book : books) {
            if (findBookByIsbn(book.getIsbn()).isPresent()) {
                throw new BookAlreadyExistsException(book.getIsbn());
            }
        }
        return bookRepository.saveAll(Arrays.asList(books));
    }

    public Member registerMember(Member member) {
        if (findMemberById(member.getMemberId()).isPresent()) {
            throw new DuplicateMemberException(member.getMemberId());
        }
        return memberRepository.save(member);

    }

    public Optional<Member> findMemberById(String memberId) {
        if (memberId != null) {
            return memberRepository.findByMemberId(memberId);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        if (isbn != null) {
            return bookRepository.findByIsbn(isbn);
        } else {
            return Optional.empty();
        }
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
