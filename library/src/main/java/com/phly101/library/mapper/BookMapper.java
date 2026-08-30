package com.phly101.library.mapper;

import com.phly101.library.dto.book.BookResponse;
import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.model.Book;

import java.util.List;

public class BookMapper {
    public static BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublishDate(),
                book.getNumberOfPages(),
                book.getCoverImage()
        );
    }

    public static Book toEntity(CreateBookRequest request) {
        return new Book(
                request.title(),
                request.author(),
                request.isbn(),
                request.publish_date(),
                request.number_of_pages(),
                request.cover_image()
        );

    }

    public static List<Book> toEntities(List<CreateBookRequest> requests) {
        return requests.stream()
                .map(BookMapper::toEntity)
                .toList();
    }

    public static List<BookResponse> toBooksResponse(List<Book> books) {
        return books.stream()
                .map(book -> new BookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPublishDate(),
                        book.getNumberOfPages(),
                        book.getCoverImage()
                ))
                .toList();
    }

}
