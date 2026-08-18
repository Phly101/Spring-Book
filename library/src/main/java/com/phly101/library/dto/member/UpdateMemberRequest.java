package com.phly101.library.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to update an existing member")
public record UpdateMemberRequest(
        @Schema(description = "The updated full name of the member", example = "Jane Doe", minLength = 5, maxLength = 100)
        @NotBlank(message = "name can't be empty")
        @Size(min = 5, max = 100)
        String name) {
}
