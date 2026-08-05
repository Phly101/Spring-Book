package com.phly101.library.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBookRequest(
        @NotBlank(message = "Title can't be empty")
        @Size(min = 5, max = 100)
        String title,

        @NotBlank(message = "author can't be empty")
        String author
) {
}
