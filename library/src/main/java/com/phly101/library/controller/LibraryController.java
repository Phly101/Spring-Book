package com.phly101.library.controller;

import com.phly101.library.dto.book.BookResponse;
import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.loan.TransactionCountResponse;
import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.loan.LoanResponse;
import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.dto.member.MemberResponse;
import com.phly101.library.mapper.BookMapper;
import com.phly101.library.mapper.LoanMapper;
import com.phly101.library.mapper.MemberMapper;
import com.phly101.library.model.*;
import com.phly101.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;


@RestController
public class LibraryController {
    final LibraryService libraryService;


    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBooks(@Valid @RequestBody CreateBookRequest createBookRequest) {
        final Book newBook = BookMapper.toEntity(createBookRequest);
        libraryService.addBook(newBook);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{isbn}").buildAndExpand(newBook.getIsbn()).toUri();
        return ResponseEntity.created(location).body(BookMapper.toBookResponse(newBook));

    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("isbn") String isbn) {
        return libraryService.findBookByIsbn(isbn)
                .map(BookMapper::toBookResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest createMemberRequest) {
        Member member = MemberMapper.toMemberEntity(createMemberRequest);
        libraryService.registerMember(member);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{memberId}").buildAndExpand(member.getMemberId()).toUri();
        return ResponseEntity.created(location).body(MemberMapper.toMemberResponse(member));
    }

    @PostMapping("/loans")
    public ResponseEntity<LoanResponse> createLoans(@Valid @RequestBody CreateLoanRequest createLoanRequest) {
        Loan loan = libraryService.borrowBook(createLoanRequest.memberId(), createLoanRequest.isbn());
        LoanResponse loanResponse = LoanMapper.toLoanResponse(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);

    }

    @GetMapping("/transactions/count")
    public ResponseEntity<TransactionCountResponse> fetchTransaction() {
        final int transactions = libraryService.getTotalTransactions();
        return ResponseEntity.status(HttpStatus.OK).body(new TransactionCountResponse(transactions));
    }

    @DeleteMapping("/loans/{isbn}")
    public ResponseEntity<Void> deleteBookLoan(@PathVariable("isbn") String isbn) {
        libraryService.returnBook(isbn);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
