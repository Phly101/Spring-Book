package com.phly101.library.dto.loan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to create a new book loan")
public record CreateLoanRequest(
        @Schema(description = "The unique identifier of the library member borrowing the book", example = "M001")
        @NotBlank(message = "memebrId can't be empty")
        String memberId,
        @Schema(description = "The ISBN of the book to borrow (10 or 13 digits)", example = "978-0-13-110362-7")
        @NotBlank(message = "isbn can't be empty")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
        String isbn
) {
}
