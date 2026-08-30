package com.phly101.library.service;

import com.phly101.library.exception.BookAlreadyBorrowedException;
import com.phly101.library.exception.BookNotFoundException;
import com.phly101.library.exception.LoanNotFoundException;
import com.phly101.library.exception.MemberNotFoundException;
import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    @Mock
    private BookService bookService;
    @Mock
    private MemberService memberService;
    @Mock
    private LoanService loanService;

    @InjectMocks
    private LibraryService libraryService;


    //Helpers
    private Member addTestMember() {
        return new Student("tester1", "tester/123");
    }

    private Book addTestBook() {
        return new Book("Fallen Grace", "William Becett", "0987654321123",
                LocalDateTime.of(2020, 1, 15, 0, 0), 320, "https://example.com/fallen-grace.jpg");
    }

    @Nested
    class LibraryServiceBorrowBookTests {

        @Test
        void shouldReturnCorrectlyFormedLoan() {
            //arrange
            Member member = addTestMember();
            Book book = addTestBook();
            Loan expectedLoan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(member.getDuration()));
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.of(member));
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(loanService.createLoan(any(Loan.class))).thenReturn(expectedLoan);
            //act
            Loan result = libraryService.borrowBook(member.getMemberId(), book.getIsbn());

            //assert
            assertEquals("Fallen Grace", result.getBook().getTitle());
            assertEquals("tester/123", result.getMember().getMemberId());
        }

        @Test
        void shouldIncrementTotalTransactions() {
            //arrange
            Member member = addTestMember();
            Book book = addTestBook();
            Loan expectedLoan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(member.getDuration()));
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.of(member));
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(loanService.createLoan(any(Loan.class))).thenReturn(expectedLoan);
            //act
            libraryService.borrowBook(member.getMemberId(), book.getIsbn());
            int result = libraryService.getTotalTransactions();
            assertEquals(1, result);
        }

        @Test
        void shouldThrowWhenAlreadyBorrowed() {
            // arrange
            Member member = addTestMember();
            Book book = addTestBook();
            book.borrow();

            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.of(member));
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));

            //act+assert
            assertThrows(BookAlreadyBorrowedException.class, () ->
                    libraryService.borrowBook(member.getMemberId(), book.getIsbn()));
        }

        @Test
        void shouldThrowWhenMemberNotFound() {
            // arrange
            Member member = addTestMember();
            Book book = addTestBook();
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.empty());
            //act+assert
            assertThrows(MemberNotFoundException.class, () ->
                    libraryService.borrowBook(member.getMemberId(), book.getIsbn()));
        }

        @Test
        void shouldThrowWhenBookNotFound() {
            // arrange
            Member member = addTestMember();
            Book book = addTestBook();
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.of(member));
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.empty());

            //act+assert
            assertThrows(BookNotFoundException.class, () ->
                    libraryService.borrowBook(member.getMemberId(), book.getIsbn()));
        }

    }

    @Nested
    class LibraryServiceReturnBookTests {

        @Test
        void shouldReturnBookSuccessfully() {
            //arrange
            Member member = addTestMember();
            Book book = addTestBook();
            book.borrow();
            Loan expectedLoan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(member.getDuration()));
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.of(member));
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(loanService.findLoan(member.getMemberId(), book.getIsbn())).thenReturn(expectedLoan);
            //act
            libraryService.returnBook(book.getIsbn(), member.getMemberId());

            assertAll("Return outCome",
                    () -> assertTrue(book.isAvailable()),
                    () -> assertNotNull(expectedLoan.getReturnDate())
            );
        }
        @Test
        void shouldThrowWhenMemberNotFound() {
            // arrange
            Member member = addTestMember();
            Book book = addTestBook();
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.empty());
            //act+assert
            assertThrows(MemberNotFoundException.class, () ->
                    libraryService.returnBook(book.getIsbn(), member.getMemberId()));
        }

        @Test
        void shouldThrowWhenBookNotFound() {
            // arrange
            Member member = addTestMember();
            Book book = addTestBook();
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.empty());
            //act+assert
            assertThrows(BookNotFoundException.class, () ->
                    libraryService.returnBook(book.getIsbn(), member.getMemberId()));
        }

        @Test
        void shouldThrowWhenLoanNotFound() {
            // arrange
            Member member = addTestMember();
            Book book = addTestBook();
            when(memberService.findMemberById(member.getMemberId())).thenReturn(Optional.of(member));
            when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(loanService.findLoan(member.getMemberId(), book.getIsbn()))
                    .thenThrow(new LoanNotFoundException(book.getIsbn()));

            //act+assert
            assertThrows(LoanNotFoundException.class, () ->
                    libraryService.returnBook(book.getIsbn(), member.getMemberId()));
        }

    }
}
