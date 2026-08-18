package com.phly101.library.controller;
import com.phly101.library.dto.book.BookResponse;
import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.book.UpdateBookRequest;
import com.phly101.library.mapper.BookMapper;
import com.phly101.library.model.Book;
import com.phly101.library.service.BookService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Books", description = "Endpoints for managing the library's book catalog")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Add a new book to the catalog",
            description = "Creates a new book record with a unique ISBN. Fails if a book with the same ISBN already exists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing/invalid title, author, or ISBN)"),
            @ApiResponse(responseCode = "409", description = "A book with this ISBN already exists")
    })

    @PostMapping("/books")
    public ResponseEntity<BookResponse> createBooks(@Valid @RequestBody CreateBookRequest createBookRequest) {
        final Book newBook = BookMapper.toEntity(createBookRequest);
        bookService.addBook(newBook);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{isbn}").buildAndExpand(newBook.getIsbn()).toUri();
        return ResponseEntity.created(location).body(BookMapper.toBookResponse(newBook));

    }

    @Operation(
            summary = "Retrieve a book by ISBN",
            description = "Fetches a specific book record from the catalog using its ISBN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "No book found with the provided ISBN")
    })
    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> findBookById(
            @Parameter(description = "The ISBN of the book to retrieve", example = "978-0-13-110362-7")
            @PathVariable("isbn") String isbn) {
        return bookService.findBookByIsbn(isbn)
                .map(BookMapper::toBookResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing book",
            description = "Updates the title and/or author of an existing book in the catalog. The ISBN is used to identify the book and cannot be changed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing/invalid title or author)"),
            @ApiResponse(responseCode = "404", description = "No book found with the provided ISBN")
    })
    @PutMapping("/books/{isbn}")
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "The ISBN of the book to update", example = "978-0-13-110362-7")
            @PathVariable("isbn") String isbn,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated book information",
                    required = true
            )
            @Valid @RequestBody UpdateBookRequest updateBookRequest) {
        Book updatedBook = bookService.updateBook(isbn, updateBookRequest.title(), updateBookRequest.author());
        return ResponseEntity.ok(BookMapper.toBookResponse(updatedBook));
    }

    @Operation(
            summary = "Delete a book from the catalog",
            description = "Removes a book record from the catalog using its ISBN. Cannot delete books that are currently borrowed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No book found with the provided ISBN"),
            @ApiResponse(responseCode = "409", description = "Cannot delete book because it is currently borrowed")
    })
    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "The ISBN of the book to delete", example = "978-0-13-110362-7")
            @PathVariable("isbn") String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

}
