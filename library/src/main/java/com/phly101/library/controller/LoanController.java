package com.phly101.library.controller;

import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.loan.LoanResponse;
import com.phly101.library.dto.loan.TransactionCountResponse;
import com.phly101.library.mapper.LoanMapper;
import com.phly101.library.model.Loan;
import com.phly101.library.service.LibraryService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Loans", description = "Endpoints for managing book loans and transactions")
public class LoanController {
    private final LibraryService libraryService;

    public LoanController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Operation(
            summary = "Create a new book loan",
            description = "Records a book borrowing transaction for a library member. The book must exist and not already be borrowed by another member."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing/invalid memberId or ISBN)"),
            @ApiResponse(responseCode = "404", description = "Book or member not found"),
            @ApiResponse(responseCode = "409", description = "Book is already borrowed or member has active loans limit reached")
    })
    @PostMapping("/loans")
    public ResponseEntity<LoanResponse> createLoans(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Loan request containing member ID and book ISBN",
                    required = true
            )
            @Valid @RequestBody CreateLoanRequest createLoanRequest) {
        Loan loan = libraryService.borrowBook(createLoanRequest.memberId(), createLoanRequest.isbn());
        LoanResponse loanResponse = LoanMapper.toLoanResponse(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);

    }

    @Operation(
            summary = "Get total transaction count",
            description = "Retrieves the total number of loan transactions (borrows and returns) that have occurred in the library."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction count retrieved successfully")
    })
    @GetMapping("/transactions/count")
    public ResponseEntity<TransactionCountResponse> fetchTransaction() {
        final int transactions = libraryService.getTotalTransactions();
        return ResponseEntity.status(HttpStatus.OK).body(new TransactionCountResponse(transactions));
    }

    @Operation(
            summary = "Return a borrowed book",
            description = "Records the return of a previously borrowed book by a member. Both the book ISBN and member ID must be provided to identify the loan transaction."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book returned successfully"),
            @ApiResponse(responseCode = "400", description = "Missing required parameters (isbn or memberId)"),
            @ApiResponse(responseCode = "404", description = "Loan record not found for the given book and member"),
            @ApiResponse(responseCode = "409", description = "Cannot return book - loan already completed or other conflict")
    })
    @DeleteMapping("/loans")
    public ResponseEntity<Void> deleteBookLoan(
            @Parameter(description = "ISBN of the book being returned", example = "978-0-13-110362-7", required = true)
            @RequestParam("isbn") String isbn,
            @Parameter(description = "Member ID returning the book", example = "M123", required = true)
            @RequestParam("memberId") String memberId) {
        libraryService.returnBook(isbn, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
