package com.phly101.library.service;

import com.phly101.library.exception.LoanNotFoundException;
import com.phly101.library.model.Loan;
import com.phly101.library.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan findLoan(String memberId, String isbn) {
        return loanRepository.findByMemberMemberIdAndBookIsbnAndReturnDateIsNull
                (memberId, isbn).orElseThrow(() -> new LoanNotFoundException(isbn));
    }

    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public boolean doesMemberHaveLoans(String memberId) {
        return loanRepository.existsByMemberMemberIdAndReturnDateIsNull(memberId);
    }

    public boolean isBookBorrowed(String isbn) {
        return loanRepository.existsByBookIsbnAndReturnDateIsNull(isbn);
    }


}
