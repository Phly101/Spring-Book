package com.phly101.library.dto;

import java.time.LocalDate;

public record LoanResponse(String bookTitle, String memberId, LocalDate loanDate, LocalDate dueDate) {
}
