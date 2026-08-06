package com.phly101.library.service;

import com.phly101.library.exception.LoanNotFoundException;
import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import com.phly101.library.repository.LoanRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;


    private Member registerTestMember() {
        return new Student("tester1", "tester/123");
    }

    private Book addTestBook() {
        return new Book("Trail of the Tyrant", "Basel", "0987654321123");

    }

    private Loan addTestLoan() {
        Book book = addTestBook();
        Member member = registerTestMember();
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(14);
        return new Loan(member, book, loanDate, dueDate);
    }

    @Nested
    class FindLoanTests {
        @Test
        void LoanService_FindLoanWhenExist_Test() {
            // arrange
            Loan loan = addTestLoan();
            when(loanRepository.findByMemberMemberIdAndBookIsbnAndReturnDateIsNull(
                    loan.getMember().getMemberId(), loan.getBook().getIsbn()))
                    .thenReturn(Optional.of(loan));
            //act
            Loan result = loanService.findLoan(loan.getMember().getMemberId(), loan.getBook().getIsbn());

            //assert
            assertEquals(loan, result);
        }

        @Test
        void LoanService_FindLoanWhenDoseNotExist_Test() {
            // arrange
            Loan loan = addTestLoan();
            when(loanRepository.findByMemberMemberIdAndBookIsbnAndReturnDateIsNull(
                    loan.getMember().getMemberId(), loan.getBook().getIsbn())).thenReturn(Optional.empty());
            //act+assert
            assertThrows(LoanNotFoundException.class, () -> loanService.findLoan(loan.getMember().getMemberId(), loan.getBook().getIsbn()));
        }
    }

    @Nested
    class CreateLoanTests {
        @Test
        void loanService_createLoanSuccessfully_Test() {
            // arrange
            Loan loan = addTestLoan();
            when(loanRepository.save(loan)).thenReturn(loan);
            //act
            Loan result = loanService.createLoan(loan);

            //assert
            assertEquals(loan, result);
        }
    }

    @Nested
    class DoesMemberHaveLoansTests {
        @Test
        void loanService_MemberHasLoans_Test() {
            // arrange
            String memberId = "tester/123";
            when(loanRepository.existsByMemberMemberIdAndReturnDateIsNull(memberId)).thenReturn(true);
            //act
            boolean result = loanService.doesMemberHaveLoans(memberId);

            //assert
            assertTrue(result);
        }
        @Test
        void loanService_MemberDoesNotHaveLoans_Test() {
            // arrange
            String memberId = "tester/123";
            when(loanRepository.existsByMemberMemberIdAndReturnDateIsNull(memberId)).thenReturn(false);
            //act
            boolean result = loanService.doesMemberHaveLoans(memberId);

            //assert
            assertFalse(result);
        }
    }

    @Nested
    class IsBookBorrowedTests {
        @Test
        void loanService_BookIsBorrowed_Test() {
            // arrange
            String isbn = "0987654321123";
            when(loanRepository.existsByBookIsbnAndReturnDateIsNull(isbn)).thenReturn(true);
            //act
            boolean result = loanService.isBookBorrowed(isbn);

            //assert
            assertTrue(result);
        }
        @Test
        void loanService_BookIsNotBorrowed_Test() {
            // arrange
            String isbn = "0987654321123";
            when(loanRepository.existsByBookIsbnAndReturnDateIsNull(isbn)).thenReturn(false);
            //act
            boolean result = loanService.isBookBorrowed(isbn);

            //assert
            assertFalse(result);
        }
    }
}