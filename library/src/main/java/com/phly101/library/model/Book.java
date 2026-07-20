package com.phly101.library.model;

import java.util.Objects;

public class Book implements Borrowable {
    final private String title;
    final private String author;
    final private String isbn;
    private boolean available = true;

    // constructor
    public Book(final String title, final String author, final String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }
    //Getters

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
}
