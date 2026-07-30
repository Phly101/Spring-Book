package com.phly101.library.model;

import com.phly101.library.model.enums.MemberType;

public class Student extends Member {
    final int maxBooksAllowed;
    final int loanDurationDays;
    final MemberType type;

    public Student(String name, String memberId) {
        this(name, memberId, 3, 14, MemberType.STUDENT);
    }

    public Student(String name, String memberId, int maxBooksAllowed, int loanDurationDays, MemberType type) {
        super(name, memberId, type);
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanDurationDays = loanDurationDays;
        this.type = type;
    }

    @Override
    public String toString() {
        return ("Student{Name:[%s], MemberId: [%s] LoanDurationDays: [%d]," +
                " MaxBooksAllowed: [%d] }")
                .formatted(super.getName(), super.getMemberId(),
                        this.loanDurationDays, this.maxBooksAllowed);
    }

    @Override
    public int getBooks() {
        return maxBooksAllowed;
    }

    @Override
    public int getDuration() {
        return loanDurationDays;
    }

}
