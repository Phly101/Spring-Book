package com.phly101.library.dto

import com.phly101.library.model.enums.MemberType

data class MemberProfileDto(
    val id: String,
    val name: String,
    val memberType: MemberType,
    val loanHistory: List<LoanHistoryItemDto>
)
