package com.phly101.library.mapper;

import com.phly101.library.dto.book.BookResponse;
import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.model.Book;

public class BookMapper {
    public static BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn()
        );
    }

    public static Book toEntity(CreateBookRequest request) {
        return new Book(
                request.title(),
                request.author(),
                request.isbn()
        );
    }
}
