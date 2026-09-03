package com.phly101.library.dto

data class LoanHistoryItemDto(
    val loanId: String,
    val bookTitle: String,
    val bookIsbn: String,
    val loanDate: String,
    val dueDate: String,
    val returnDate: String?

)
