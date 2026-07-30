package com.phly101.library.controller;

import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.loan.TransactionCountResponse;
import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.loan.LoanResponse;
import com.phly101.library.dto.member.CreateMemberRequest;
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
    public ResponseEntity<Book> createBooks(@Valid @RequestBody CreateBookRequest createBookRequest) {
        final Book newBook = new Book(createBookRequest.title(), createBookRequest.author(), createBookRequest.isbn());
        libraryService.addBook(newBook);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{isbn}").buildAndExpand(newBook.getIsbn()).toUri();
        return ResponseEntity.created(location).body(newBook);

    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<Book> findBookById(@PathVariable("isbn") String isbn) {
        return libraryService.findBookByIsbn(isbn).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/members")
    public ResponseEntity<Member> createMember(@Valid @RequestBody CreateMemberRequest createMemberRequest) {
        switch (createMemberRequest.type()) {
            case STUDENT -> {
                Student student = new Student(createMemberRequest.name(), createMemberRequest.memberId());
                libraryService.registerMember(student);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{memberId}").buildAndExpand(student.getMemberId()).toUri();
                return ResponseEntity.created(location).body(student);
            }
            case FACULTY -> {
                Faculty faculty = new Faculty(createMemberRequest.name(), createMemberRequest.memberId());
                libraryService.registerMember(faculty);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{memberId}").buildAndExpand(faculty.getMemberId()).toUri();
                return ResponseEntity.created(location).body(faculty);
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }

        }

    }

    @PostMapping("/loans")
    public ResponseEntity<LoanResponse> createLoans(@Valid @RequestBody CreateLoanRequest createLoanRequest) {
        Loan loan = libraryService.borrowBook(createLoanRequest.memberId(), createLoanRequest.isbn());
        LoanResponse loanResponse = new LoanResponse(loan.getBook().getTitle(), loan.getMember().getMemberId(), loan.getLoanDate(), loan.getDueDate());
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
