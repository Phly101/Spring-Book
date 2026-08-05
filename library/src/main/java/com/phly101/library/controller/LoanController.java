package com.phly101.library.controller;

import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.loan.LoanResponse;
import com.phly101.library.dto.loan.TransactionCountResponse;
import com.phly101.library.mapper.LoanMapper;
import com.phly101.library.model.Loan;
import com.phly101.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoanController {
    private final LibraryService libraryService;

    public LoanController(LibraryService libraryService) {
        this.libraryService = libraryService;
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

    @DeleteMapping("/loans")
    public ResponseEntity<Void> deleteBookLoan(@RequestParam("isbn") String isbn, @RequestParam("memberId") String memberId) {
        libraryService.returnBook(isbn, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
