package com.phly101.library.service;

import com.phly101.library.exception.BookAlreadyExistsException;
import com.phly101.library.exception.BookCurrentlyBorrowedException;
import com.phly101.library.exception.BookNotFoundException;
import com.phly101.library.model.Book;
import com.phly101.library.repository.BookRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanService loanService;

    @InjectMocks
    private BookService bookService;


    private Book addTestBook() {
        return new Book("Fallen Grace", "William Becett", "0987654321123",
                LocalDateTime.of(2020, 1, 15, 0, 0), 320, "https://example.com/fallen-grace.jpg");
    }

    private List<Book> addTestBooks() {
        List<Book> books = new ArrayList<>();
        books.add(addTestBook());
        books.add(new Book("Tyrant's return", "David Daniub ", "098765437784",
                LocalDateTime.of(2021, 5, 10, 0, 0), 420, "https://example.com/tyrants-return.jpg"));
        return books;
    }

    @Nested
    class FindBookByIsbnTests {

        @Test
        void BookService_FindBookByIsbnWhenExists_Test() {
            // arrange
            Book book = addTestBook();
            String isbn = book.getIsbn();
            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
            //act
            Book result = bookService.findBookByIsbn(isbn).orElse(null);
            //assert
            assertEquals(result, book);
        }

        @Test
        void BookService_FindBookByIsbnDoesNotExist_Test() {
            // arrange
            Book book = addTestBook();
            String isbn = book.getIsbn();
            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
            //act
            Optional<Book> result = bookService.findBookByIsbn(isbn);
            //assert
            assertTrue(result.isEmpty());
        }

        @Test
        void BookService_FindBookByIsbnWhenIsbnIsNull_Test() {
            Optional<Book> result = bookService.findBookByIsbn(null);
            assertTrue(result.isEmpty());
            verify(bookRepository, never()).findByIsbn(any());
        }

    }

    @Nested
    class AddBookTests {
        @Test
        void BookService_AddBookSuccessfully_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
            when(bookRepository.save(book)).thenReturn(book);
            //act
            Book result = bookService.addBook(book);
            //assert
            assertEquals(result, book);
        }

        @Test
        void BookService_AddDuplicateBook_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            //act+assert
            assertThrows(BookAlreadyExistsException.class, () -> bookService.addBook(book));
            verify(bookRepository, never()).save(any());
        }

    }

    @Nested
    class AddBooksTests {
        @Test
        void BookService_AddBooksSuccessfully_Test() {
            // arrange
            List<Book> books = addTestBooks();
            for (Book book : books) {
                when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
            }
            when(bookRepository.saveAll(anyList())).thenReturn(books);
            //act
            List<Book> result = bookService.addBooks(books);
            //assert
            assertEquals(books, result);
        }

        @Test
        void BookService_AddDuplicateBooks_Test() {
            // arrange
            List<Book> books = addTestBooks();
            // first book is new, second book already exists
            when(bookRepository.findByIsbn(books.get(0).getIsbn())).thenReturn(Optional.empty());
            when(bookRepository.findByIsbn(books.get(1).getIsbn())).thenReturn(Optional.of(books.get(1)));
            //act+assert
            assertThrows(BookAlreadyExistsException.class, () ->
                    bookService.addBooks(books));
            verify(bookRepository, never()).saveAll(any());
        }
    }

    @Nested
    class UpdateBookTests {

        @Test
        void BookService_UpdateBookWhenFound_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            //act
            Book result = bookService.updateBook(book.getIsbn(), "bla bla", "Bola");
            //assert
            assertAll("Book details",
                    () -> assertEquals("bla bla", result.getTitle()),
                    () -> assertEquals("Bola", result.getAuthor())
            );
        }

        @Test
        void BookService_UpdateBookWhenNotFound_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
            //act+ assert
            assertThrows(BookNotFoundException.class, () -> bookService.updateBook(book.getIsbn(), book.getTitle(), book.getAuthor()));

        }
    }

    @Nested
    class DeleteBookTests {
        @Test
        void BookService_DeleteBookWhenFoundWithNoLoan_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(loanService.isBookBorrowed(book.getIsbn())).thenReturn(false);
            //act
            bookService.deleteBook(book.getIsbn());
            //assert
            verify(bookRepository, times(1)).delete(book);
        }

        @Test
        void BookService_DeleteBookWhenNotFound_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
            //act+ assert
            assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(book.getIsbn()));
            verify(bookRepository, never()).delete(any());

        }

        @Test
        void BookService_DeleteBookWhenHaveLoan_Test() {
            // arrange
            Book book = addTestBook();
            when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
            when(loanService.isBookBorrowed(book.getIsbn())).thenReturn(true);
            //act+assert
            assertThrows(BookCurrentlyBorrowedException.class, () -> bookService.deleteBook(book.getIsbn()));
            verify(bookRepository, never()).delete(any());

        }
    }

}
