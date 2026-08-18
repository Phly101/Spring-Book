package com.phly101.library.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing book information")
public record BookResponse(
        @Schema(description = "The title of the book", example = "Clean Code")
        String title,
        @Schema(description = "The author of the book", example = "Robert C. Martin")
        String author,
        @Schema(description = "The unique ISBN identifier (10 or 13 digits)", example = "978-0-13-110362-7")
        String isbn) {
}
