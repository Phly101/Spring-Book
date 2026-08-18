package com.phly101.library.dto.member;

import com.phly101.library.model.enums.MemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to register a new library member")
public record CreateMemberRequest(
        @Schema(description = "The type of library member (STUDENT or FACULTY)", example = "STUDENT")
        @NotNull(message = "Type can't be empty")
        MemberType type,
        @Schema(description = "The full name of the member", example = "John Doe", minLength = 5, maxLength = 100)
        @NotBlank(message = "name can't be empty")
        @Size(min = 5, max = 100)
        String name,
        @Schema(description = "The unique identifier assigned to the member", example = "M001")
        @NotBlank(message = "memebrId can't be empty")
        String memberId) {
}
