package com.phly101.library.repository;

import com.phly101.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Optional<Loan> findByMemberMemberIdAndBookIsbnAndReturnDateIsNull(String memberId, String isbn);

    boolean existsByMemberMemberIdAndReturnDateIsNull(String memberId);

    boolean existsByBookIsbnAndReturnDateIsNull(String isbn);

     List<Loan> findByMemberMemberIdOrderByLoanDateDesc(String memberId);
}
