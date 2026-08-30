package com.phly101.library.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(description = "Request payload to create a new book in the catalog")
public record CreateBookRequest(
        @Schema(description = "The title of the book", example = "Clean Code", minLength = 5, maxLength = 100)
        @NotBlank(message = "Title can't be empty")
        @Size(min = 5, max = 100)
        String title,

        @Schema(description = "The author of the book", example = "Robert C. Martin")
        @NotBlank(message = "author can't be empty")
        String author,
        @Schema(description = "The ISBN identifier (must be 10 or 13 digits)", example = "978-0-13-110362-7")
        @NotBlank(message = "isbn can't be empty")
        @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits")
        String isbn,
        @Schema(description = "The publish date of the book", example = "3/9/2003")
        LocalDateTime publish_date,
        @Schema(description = "The number of book pages", example = "250")
        int number_of_pages,
        @Schema(description = "The Cover image of the book", example = "https://coverimageurl")
        String cover_image

) {
}
