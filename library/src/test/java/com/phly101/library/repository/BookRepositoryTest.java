package com.phly101.library.repository;

import com.phly101.library.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookRepository bookRepository;

    private Book addTestBook() {
        return new Book("Fallen Grace", "William Becett", "0987654321123",
                LocalDateTime.of(2020, 1, 15, 0, 0), 320, "https://example.com/fallen-grace.jpg");
    }

    @Test
    void findByIsbn_shouldReturnBook_WhenIsbnExists() {
        // arrange
        Book book = addTestBook();
        entityManager.persistAndFlush(book);

        // act
        Optional<Book> result = bookRepository.findByIsbn(book.getIsbn());

        // assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(result.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    void findByIsbn_shouldReturnEmpty_WhenIsbnDoesNotExist() {
        // arrange
        Book book = addTestBook();
        entityManager.persistAndFlush(book);
        //act
        Optional<Book> result = bookRepository.findByIsbn("1222334455667");

        // assert
        assertThat(result.isPresent()).isFalse();
    }
}
