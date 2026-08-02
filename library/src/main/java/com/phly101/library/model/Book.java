package com.phly101.library.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "books")
public class Book implements Borrowable {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, length = 13, unique = true)
    private String isbn;

    @Column(nullable = false)
    private boolean available = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // required by JPA
    protected Book() {
    }

    // constructor
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }
    //Getters

    public UUID getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getIsbn() {
        return this.isbn;
    }

    //------------------------------------------------------------
    @Override
    public boolean borrow() {
        if (!this.available) {
            return false;
        }
        this.available = false;
        return true;
    }

    @Override
    public void returnItem() {
        this.available = true;

    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public String toString() {
        return "Book{title='" + title + "', author='" + author + "', isbn='" + isbn + "', available=" + available + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
