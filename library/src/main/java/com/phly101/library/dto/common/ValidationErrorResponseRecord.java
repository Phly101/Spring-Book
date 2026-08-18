package com.phly101.library.dto.common;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Validation error response with field-level error messages")
public record ValidationErrorResponseRecord(
        @Schema(description = "Error code indicating validation failure", example = "VALIDATION_ERROR")
        String errorCode,
        @Schema(description = "Map of field names to their corresponding validation error messages", example = "{\"title\": \"Title can't be empty\", \"isbn\": \"ISBN must be 10 or 13 digits\"}")
        Map<String, String> errorMessages) {
}