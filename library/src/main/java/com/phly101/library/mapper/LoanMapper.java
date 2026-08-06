package com.phly101.library.mapper;

import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.loan.LoanResponse;
import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;

import java.time.LocalDate;

public class LoanMapper {
    public static LoanResponse toLoanResponse(Loan loan) {
        Book book = loan.getBook();
        Member member = loan.getMember();
        return new LoanResponse(
                book.getTitle(),
                member.getMemberId(),
                loan.getLoanDate(),
                loan.getDueDate()

        );

    }

    public static Loan toLoanEntity(Book book, Member member, LocalDate dueDate) {
        return new Loan(member, book, LocalDate.now(), dueDate);
    }
}
