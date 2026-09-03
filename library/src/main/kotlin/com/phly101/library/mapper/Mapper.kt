package com.phly101.library.mapper

import com.phly101.library.dto.LoanHistoryItemDto
import com.phly101.library.dto.MemberProfileDto
import com.phly101.library.model.Loan
import com.phly101.library.model.Member

object Mapper {


    fun toMemberProfileDto(member: Member, loans: List<Loan>): MemberProfileDto {
        return MemberProfileDto(
            id = member.memberId,
            memberType = member.type,
            name = member.name,
            loanHistory = loans.map { toLoanHistoryDto(it) }

        )
    }

    private fun toLoanHistoryDto(loan: Loan): LoanHistoryItemDto {
        return LoanHistoryItemDto(
            loanId = loan.id.toString(),
            bookTitle = loan.book.title,
            bookIsbn = loan.book.isbn,
            loanDate = loan.loanDate.toString(),
            dueDate = loan.dueDate.toString(),
            returnDate = loan.returnDate?.toString()
        )
    }

}