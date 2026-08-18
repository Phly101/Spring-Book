package com.phly101.library.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response")
public record ErrorResponseRecord(
        @Schema(description = "Error code identifying the type of error", example = "BOOK_NOT_FOUND")
        String errorCode,
        @Schema(description = "Human-readable error message", example = "Book with ISBN 978-0-13-110362-7 not found")
        String errorMessage) {
}
