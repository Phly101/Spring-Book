package com.phly101.library.dto.member;

import com.phly101.library.model.enums.MemberType;

public record MemberResponse(MemberType type, String name, String memberId) {
}
