package com.phly101.library.controller;

import com.phly101.library.dto.book.BookResponse;
import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.book.UpdateBookRequest;
import com.phly101.library.mapper.BookMapper;
import com.phly101.library.model.Book;
import com.phly101.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBooks(@Valid @RequestBody CreateBookRequest createBookRequest) {
        final Book newBook = BookMapper.toEntity(createBookRequest);
        bookService.addBook(newBook);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{isbn}").buildAndExpand(newBook.getIsbn()).toUri();
        return ResponseEntity.created(location).body(BookMapper.toBookResponse(newBook));

    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("isbn") String isbn) {
        return bookService.findBookByIsbn(isbn)
                .map(BookMapper::toBookResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable("isbn") String isbn, @Valid @RequestBody UpdateBookRequest updateBookRequest) {
        Book updatedBook = bookService.updateBook(isbn, updateBookRequest.title(), updateBookRequest.author());
        return ResponseEntity.ok(BookMapper.toBookResponse(updatedBook));
    }

    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable("isbn") String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

}
