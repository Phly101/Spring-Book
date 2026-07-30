package com.phly101.library.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record CreateBookRequest(
        @NotBlank(message = "Title can't be empty")
        @Size(min = 5, max = 100)
        String title,

        @NotBlank(message = "author can't be empty")
        String author,
        @NotBlank(message = "isbn can't be empty")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
        String isbn) {
}
