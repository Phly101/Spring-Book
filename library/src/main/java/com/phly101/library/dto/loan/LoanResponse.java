package com.phly101.library.dto.loan;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing loan transaction details")
public record LoanResponse(
        @Schema(description = "The title of the borrowed book", example = "Clean Code")
        String bookTitle,
        @Schema(description = "The unique identifier of the member who borrowed the book", example = "M001")
        String memberId,
        @Schema(description = "The date when the book was borrowed", example = "2026-08-18")
        LocalDate loanDate,
        @Schema(description = "The date when the book is due to be returned", example = "2026-09-01")
        LocalDate dueDate) {
}
