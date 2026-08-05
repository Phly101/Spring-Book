package com.phly101.library.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMemberRequest(
        @NotBlank(message = "name can't be empty")
        @Size(min = 5, max = 100)
        String name) {
}
