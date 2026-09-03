package com.phly101.library.controller

import com.phly101.library.dto.MemberProfileDto
import com.phly101.library.exception.MemberNotFoundException
import com.phly101.library.mapper.Mapper
import com.phly101.library.service.LoanService
import com.phly101.library.service.MemberService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class GraphQlController(
    private val memberService: MemberService,
    private val loanService: LoanService,
) {
    @QueryMapping
    fun memberProfile(@Argument memberId: String): MemberProfileDto {
        val member = memberService.findMemberById(memberId).orElseThrow { MemberNotFoundException(memberId) }
        val loans = loanService.getLoanHistory(memberId)
        return Mapper.toMemberProfileDto(member = member, loans = loans)
    }

}