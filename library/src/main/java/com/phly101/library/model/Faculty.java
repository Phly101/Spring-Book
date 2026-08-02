package com.phly101.library.model;


import com.phly101.library.model.enums.FacultyRole;
import com.phly101.library.model.enums.MemberType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "faculties")
@PrimaryKeyJoinColumn(name = "member_id")
public class Faculty extends Member {

    @Column(nullable = false, name = "max_books_can_borrow")
    private int maxBooksAllowed;

    @Column(name = "loan_duration_days", nullable = false)
    private int loanDurationDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    FacultyRole facultyRole;

    protected Faculty() {
    }

    public Faculty(String name, String memberId) {
        this(name, memberId, 10, 60, MemberType.FACULTY);
    }

    public Faculty(String name, String memberId, int maxBooksAllowed, int loanDurationDays, MemberType type) {
        super(name, memberId, type);
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanDurationDays = loanDurationDays;
    }

    @Override
    public String toString() {
        return ("Faculty{Name:[%s], MemberId: [%s] LoanDurationDays: [%d]," +
                " MaxBooksAllowed: [%d] }")
                .formatted(super.getName(), super.getMemberId(),
                        this.loanDurationDays, this.maxBooksAllowed);
    }

    // getters
    @Override
    public int getBooks() {
        return this.maxBooksAllowed;
    }

    @Override
    public int getDuration() {
        return this.loanDurationDays;
    }

    public FacultyRole getFacultyRole() {
        return facultyRole;
    }
}
