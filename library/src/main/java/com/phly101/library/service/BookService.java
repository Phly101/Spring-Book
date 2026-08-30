package com.phly101.library.service;

import com.phly101.library.exception.BookAlreadyExistsException;
import com.phly101.library.exception.BookCurrentlyBorrowedException;
import com.phly101.library.exception.BookNotFoundException;
import com.phly101.library.model.Book;
import com.phly101.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LoanService loanService;

    public BookService(BookRepository bookRepository, LoanService loanService) {
        this.bookRepository = bookRepository;
        this.loanService = loanService;
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        if (isbn != null) {
            return bookRepository.findByIsbn(isbn);
        } else {
            return Optional.empty();
        }
    }

    public List<Book> findAllBooksByIsbn(List<String> isbn) {
        if (isbn != null && !isbn.isEmpty()) {
            return bookRepository.findByIsbnIn(isbn);
        } else {
            return Collections.emptyList();
        }
    }

    public Book addBook(Book book) {
        if (findBookByIsbn(book.getIsbn()).isPresent()) {
            throw new BookAlreadyExistsException(book.getIsbn());
        }
        return bookRepository.save(book);
    }

    @Transactional
    public List<Book> addBooks(List<Book> books) {
        List<String> requestedIsbns = books.stream().map(Book::getIsbn).toList();
        List<Book> existingBooks = findAllBooksByIsbn(requestedIsbns);
        if (!existingBooks.isEmpty()) {
            List<String> duplicateIsbns = existingBooks.stream().map(Book::getIsbn).toList();
            throw new BookAlreadyExistsException(duplicateIsbns.toString());
        }
        return bookRepository.saveAll(books);
    }

    @Transactional
    public Book updateBook(String isbn, String title, String author) {
        Book book = findBookByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        book.setTitle(title);
        book.setAuthor(author);
        return book;
    }

    @Transactional
    public void deleteBook(String isbn) {
        Book book = findBookByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        if (loanService.isBookBorrowed(isbn)) {
            throw new BookCurrentlyBorrowedException(isbn);
        }
        bookRepository.delete(book);
    }
}
