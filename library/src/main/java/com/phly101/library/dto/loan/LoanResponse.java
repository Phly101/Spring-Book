package com.phly101.library.dto.loan;

import java.time.LocalDate;

public record LoanResponse(String bookTitle, String memberId, LocalDate loanDate, LocalDate dueDate) {
}
