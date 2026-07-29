package com.phly101.library.service;

import com.phly101.library.exception.*;
import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        // activate
        libraryService = new LibraryService();
    }

    //Helpers
    private Member registerTestMember() {
        Member member = new Student("tester1", "tester/123");
        libraryService.registerMember(member);
        return member;
    }

    private Book addTestBook() {
        Book book = new Book("Trail of the Tyrant", "Basel", "1234567891");
        libraryService.addBook(book);
        return book;
    }

    private Loan borrowedBookSetup() {
        final Member member = registerTestMember();
        final Book book = addTestBook();
        return libraryService.borrowBook(member.getMemberId(), book.getIsbn());
    }

    @Nested
    class LibraryServiceRegisterMemberTests {

        @Test
        void shouldRegisterMemberSuccessfully() {
            // arrange
            final Member member = new Student("tester", "tester/123");

            //act
            libraryService.registerMember(member);
            //assert
            Optional<Member> found = libraryService.findMemberById(member.getMemberId());
            assertTrue(found.isPresent());
            assertEquals(member, found.get());
        }

        @Test
        void shouldThrowWhenDuplicateId() {
            // arrange
            final Member member1 = new Student("tester1", "tester/123");
            final Member member2 = new Student("tester2", "tester/123");
            // assert
            assertThrows(DuplicateMemberException.class, () -> {
                // act
                libraryService.registerMember(member1);
                libraryService.registerMember(member2);

            });

        }
    }

    @Nested
    class LibraryServiceBorrowBookTests {

        @Test
        void shouldReturnCorrectlyFormedLoan() {
            Loan loan = borrowedBookSetup();
            assertEquals("Trail of the Tyrant", loan.getBook().getTitle());
            assertEquals("tester/123", loan.getMember().getMemberId());
        }

        @Test
        void shouldIncrementTotalTransactions() {
            borrowedBookSetup();
            assertEquals(1, libraryService.getTotalTransactions());
        }

        @Test
        void shouldThrowWhenAlreadyBorrowed() {
            Loan loan = borrowedBookSetup();
            assertThrows(BookAlreadyBorrowedException.class, () ->
                    libraryService.borrowBook(loan.getMember().getMemberId(), loan.getBook().getIsbn())
            );
        }

        @Test
        void shouldThrowWhenMemberNotFound() {
            Book book = addTestBook();
            assertThrows(MemberNotFoundException.class, () ->
                    libraryService.borrowBook("9999999999", book.getIsbn())
            );
        }

        @Test
        void shouldThrowWhenBookNotFound() {
            Member member = registerTestMember();
            assertThrows(BookNotFoundException.class, () ->
                    libraryService.borrowBook(member.getMemberId(), "9999999999")
            );
        }

    }

    @Nested
    class LibraryServiceReturnBookTests {

        @Test
        void shouldReturnBookSuccessfully() {
            // arrange
            Loan loan = borrowedBookSetup();
            Book book = loan.getBook();
            //act
            libraryService.returnBook(book.getIsbn());
            //assert
            assertTrue(book.isAvailable());
        }

        @Test
        void shouldThrowWhenBookNotFound() {
            assertThrows(BookNotFoundException.class, () ->
                    libraryService.returnBook("awdawwadwada")
            );
        }

        @Test
        void shouldThrowWhenLoanNotFound() {
            Book book = addTestBook();
            assertThrows(LoanNotFoundException.class, () ->
                    libraryService.returnBook(book.getIsbn())
            );
        }

    }
}
