package com.phly101.library.model;


import com.phly101.library.model.enums.MemberType;

public class Faculty extends Member {
    final int maxBooksAllowed;
    final int loanDurationDays;
    final MemberType type;

    public Faculty(String name, String memberId) {
        this(name, memberId, 10, 60, MemberType.FACULTY);
    }

    public Faculty(String name, String memberId, int maxBooksAllowed, int loanDurationDays, MemberType type) {
        super(name, memberId);
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanDurationDays = loanDurationDays;
        this.type = type;
    }

    @Override
    public String toString() {
        return ("Faculty{Name:[%s], MemberId: [%s] LoanDurationDays: [%d]," +
                " MaxBooksAllowed: [%d] }")
                .formatted(super.getName(), super.getMemberId(),
                        this.loanDurationDays, this.maxBooksAllowed);
    }

    @Override
    public int getBooks() {
        return this.maxBooksAllowed;
    }

    @Override
    public int getDuration() {
        return this.loanDurationDays;
    }
}
