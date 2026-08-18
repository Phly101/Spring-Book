package com.phly101.library.dto.member;

import com.phly101.library.model.enums.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing member information")
public record MemberResponse(
        @Schema(description = "The type of library member (STUDENT or FACULTY)", example = "STUDENT")
        MemberType type,
        @Schema(description = "The full name of the member", example = "John Doe")
        String name,
        @Schema(description = "The unique identifier of the member", example = "M001")
        String memberId) {
}
