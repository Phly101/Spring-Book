package com.phly101.library.repository;

import com.phly101.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Optional<Loan> findByMemberMemberIdAndBookIsbnAndReturnDateIsNull(String memberId, String isbn);
}
