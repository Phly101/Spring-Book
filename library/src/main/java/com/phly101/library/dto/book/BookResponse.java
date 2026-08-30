package com.phly101.library.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing book information")
public record BookResponse(
        @Schema(description = "The title of the book", example = "Clean Code")
        String title,
        @Schema(description = "The author of the book", example = "Robert C. Martin")
        String author,
        @Schema(description = "The unique ISBN identifier (10 or 13 digits)", example = "978-0-13-110362-7")
        String isbn,
        @Schema(description = "The publish date of the book", example = "3/9/2003")
        LocalDateTime publish_date,
        @Schema(description = "The number of book pages", example = "250")
        int number_of_pages,
        @Schema(description = "The Cover image of the book", example = "https://coverimageurl")
        String cover_image

) {
}
