package com.phly101.library.repository;

import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    private Member registerTestMember() {
        return new Student("tester1", "tester/123");
    }

    private Book addTestBook() {
        return new Book("Trail of the Tyrant", "Basel", "0987654321123");

    }


    @Test
    void findLoanByMemberIdAndIsbn_shouldReturnLoan_WhenBothExists() {
        // arrange
        Member member = registerTestMember();
        Book book = addTestBook();
        entityManager.persistAndFlush(member);
        entityManager.persistAndFlush(book);
        Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14));
        entityManager.persistAndFlush(loan);

        // act
        Optional<Loan> result = loanRepository
                .findByMemberMemberIdAndBookIsbnAndReturnDateIsNull(
                        loan.getMember().getMemberId(),
                        loan.getBook().getIsbn());

        // assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getReturnDate()).isEqualTo(loan.getReturnDate());
        assertThat(result.get().getBook().getIsbn()).isEqualTo(loan.getBook().getIsbn());
        assertThat(result.get().getMember().getMemberId()).isEqualTo(loan.getMember().getMemberId());
    }

    @Test
    void findLoanByMemberIdAndIsbn_shouldReturnEmpty_WhenBothDoesNotExists() {
        // arrange
        Member member = registerTestMember();
        Book book = addTestBook();
        entityManager.persistAndFlush(member);
        entityManager.persistAndFlush(book);
        Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14));
        entityManager.persistAndFlush(loan);

        // act
        Optional<Loan> result = loanRepository
                .findByMemberMemberIdAndBookIsbnAndReturnDateIsNull("tester2/332", "1222334455667");

        // assert
        assertThat(result.isPresent()).isFalse();
    }
    @Test
    void existsByMemberMemberIdAndReturnDateIsNull_shouldReturnTrue_WhenMemberHasActiveLoan() {
        // arrange
        Member member = registerTestMember();
        Book book = addTestBook();
        entityManager.persistAndFlush(member);
        entityManager.persistAndFlush(book);
        Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14));
        entityManager.persistAndFlush(loan);

        // act
        boolean result = loanRepository.existsByMemberMemberIdAndReturnDateIsNull(member.getMemberId());

        // assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByMemberMemberIdAndReturnDateIsNull_shouldReturnFalse_WhenMemberHasNoActiveLoan() {
        // arrange
        Member member = registerTestMember();
        entityManager.persistAndFlush(member);
        // no loan persisted at all

        // act
        boolean result = loanRepository.existsByMemberMemberIdAndReturnDateIsNull(member.getMemberId());

        // assert
        assertThat(result).isFalse();
    }

    @Test
    void existsByBookIsbnAndReturnDateIsNull_shouldReturnTrue_WhenBookIsCurrentlyLoaned() {
        // arrange
        Member member = registerTestMember();
        Book book = addTestBook();
        entityManager.persistAndFlush(member);
        entityManager.persistAndFlush(book);
        Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14));
        entityManager.persistAndFlush(loan);

        // act
        boolean result = loanRepository.existsByBookIsbnAndReturnDateIsNull(book.getIsbn());

        // assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByBookIsbnAndReturnDateIsNull_shouldReturnFalse_WhenBookIsNotCurrentlyLoaned() {
        // arrange
        Book book = addTestBook();
        entityManager.persistAndFlush(book);
        // no loan persisted at all

        // act
        boolean result = loanRepository.existsByBookIsbnAndReturnDateIsNull(book.getIsbn());

        // assert
        assertThat(result).isFalse();
    }


}
