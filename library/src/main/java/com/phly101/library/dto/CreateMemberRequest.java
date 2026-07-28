package com.phly101.library.dto;

import com.phly101.library.model.enums.MemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
        @NotNull(message = "Type can't be empty")
        MemberType type,
        @NotBlank(message = "name can't be empty")
        @Size(min = 5, max = 100)
        String name,
        @NotBlank(message = "memebrId can't be empty")
        String memberId) {
}
