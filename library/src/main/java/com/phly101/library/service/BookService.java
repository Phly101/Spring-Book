package com.phly101.library.service;

import com.phly101.library.exception.BookAlreadyExistsException;
import com.phly101.library.exception.BookCurrentlyBorrowedException;
import com.phly101.library.exception.BookNotFoundException;
import com.phly101.library.model.Book;
import com.phly101.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LoanService loanService;

    public BookService(BookRepository bookRepository,LoanService loanService) {
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

    public Book addBook(Book book) {
        if (findBookByIsbn(book.getIsbn()).isPresent()) {
            throw new BookAlreadyExistsException(book.getIsbn());
        }
        return bookRepository.save(book);
    }

    public List<Book> addBooks(Book... books) {
        for (Book book : books) {
            if (findBookByIsbn(book.getIsbn()).isPresent()) {
                throw new BookAlreadyExistsException(book.getIsbn());
            }
        }
        return bookRepository.saveAll(Arrays.asList(books));
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
