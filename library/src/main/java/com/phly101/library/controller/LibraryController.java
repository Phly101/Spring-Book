package com.phly101.library.controller;

import com.phly101.library.dto.*;
import com.phly101.library.model.*;
import com.phly101.library.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;


@RestController
public class LibraryController {
    final LibraryService libraryService;


    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping("/books")
    public ResponseEntity<Book> createBooks(@RequestBody CreateBookRequest createBookRequest) {
        final Book newBook = new Book(createBookRequest.title(), createBookRequest.author(), createBookRequest.isbn());
        libraryService.addBook(newBook);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{isbn}").buildAndExpand(newBook.getIsbn()).toUri();
        return ResponseEntity.created(location).body(newBook);

    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<Book> findBookById(@PathVariable("isbn") String isbn) {
        return libraryService.findBookByIsbn(isbn).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());


    }


    @PostMapping("/members")
    public ResponseEntity<Member> createMember(@RequestBody CreateMemberRequest createMemberRequest) {
        if (libraryService.findMemberById(createMemberRequest.memberId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
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
    public ResponseEntity<LoanResponse> createLoans(@RequestBody CreateLoanRequest createLoanRequest) {

        Member currentMember = libraryService.findMemberById(createLoanRequest.memberId()).orElse(null);
        Book currentBook = libraryService.findBookByIsbn(createLoanRequest.isbn()).orElse(null);
        if (currentBook == null || currentMember == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            Optional<Loan> loanResult = libraryService.borrowBook(currentMember, currentBook);
            if (loanResult.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Loan loan = loanResult.get();
            LoanResponse loanResponse = new LoanResponse(
                    loan.getBook().getTitle(),
                    loan.getMember().getMemberId(),
                    loan.getLoanDate(),
                    loan.getDueDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
        }
    }

    @GetMapping("/transactions/count")
    public ResponseEntity<TransactionCountResponse> fetchTransaction() {
        final int transactions = libraryService.getTotalTransactions();
        return ResponseEntity.status(HttpStatus.OK).body(new TransactionCountResponse(transactions));
    }

    @DeleteMapping("/loans/{isbn}")
    public ResponseEntity<Void> deleteBookLoan(@PathVariable("isbn") String isbn) {
        Book book = libraryService.findBookByIsbn(isbn).orElse(null);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (libraryService.returnBook(book)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
