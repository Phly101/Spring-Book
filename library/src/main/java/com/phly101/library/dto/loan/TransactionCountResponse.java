package com.phly101.library.dto.loan;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the total number of loan transactions")
public record TransactionCountResponse(
        @Schema(description = "The total count of all loan transactions (borrows and returns)", example = "42")
        int transactionCount) {
}
