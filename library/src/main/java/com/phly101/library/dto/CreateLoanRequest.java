package com.phly101.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateLoanRequest(
        @NotBlank(message = "memebrId can't be empty")
        String memberId,
        @NotBlank(message = "isbn can't be empty")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
        String isbn
) {
}
