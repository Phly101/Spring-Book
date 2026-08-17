package com.phly101.library.model;

import com.phly101.library.model.enums.MemberType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "member_id")
public class Student extends Member {

    @Column(nullable = false, name = "max_books_can_borrow")
    private int maxBooksAllowed;
    @Column(name = "loan_duration_days", nullable = false)
    private int loanDurationDays;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Student() {
    }

    public Student(String name, String memberId) {
        this(name, memberId, 3, 14, MemberType.STUDENT);
    }

    public Student(String name, String memberId, int maxBooksAllowed, int loanDurationDays, MemberType type) {
        super(name, memberId, type);
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanDurationDays = loanDurationDays;
    }

    @Override
    public String toString() {
        return ("Student{Name:[%s], MemberId: [%s] LoanDurationDays: [%d]," +
                " MaxBooksAllowed: [%d] }")
                .formatted(super.getName(), super.getMemberId(),
                        this.loanDurationDays, this.maxBooksAllowed);
    }
    // getters

    @Override
    public int getBooks() {
        return maxBooksAllowed;
    }

    @Override
    public int getDuration() {
        return loanDurationDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    private  void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
