package com.phly101.library.repository;

import com.phly101.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByIsbn(String isbn);

    List<Book> findByIsbnIn(List<String> isbn);
}
