package com.phly101.library.model;

public interface Borrowable {
    boolean borrow();

    void returnItem();

    boolean isAvailable();
}
