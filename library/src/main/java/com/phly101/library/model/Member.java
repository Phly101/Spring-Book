package com.phly101.library.model;

import java.util.Objects;

public abstract class Member {
    private final String name;
    private final String memberId;

    protected Member(final String name, final String memberId) {
        this.memberId = memberId;
        this.name = name;
    }

    public abstract int getBooks();

    public abstract int getDuration();

    public String getName() {
        return this.name;
    }

    public String getMemberId() {
        return this.memberId;
    }

    @Override
    public String toString() {
        return "Member: " + this.name + " MemberId: " + this.memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
