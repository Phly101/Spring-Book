package com.phly101.library.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to update an existing book")
public record UpdateBookRequest(
        @Schema(description = "The updated title of the book", example = "Clean Code", minLength = 5, maxLength = 100)
        @NotBlank(message = "Title can't be empty")
        @Size(min = 5, max = 100)
        String title,

        @Schema(description = "The updated author of the book", example = "Robert C. Martin")
        @NotBlank(message = "author can't be empty")
        String author
) {
}
