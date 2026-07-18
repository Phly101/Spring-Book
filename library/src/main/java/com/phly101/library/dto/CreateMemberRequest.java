package com.phly101.library.dto;
import com.phly101.library.model.enums.MemberType;

public record CreateMemberRequest(MemberType type, String name, String memberId) {
}
