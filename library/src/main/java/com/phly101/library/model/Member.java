package com.phly101.library.model;

import com.phly101.library.model.enums.MemberType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "members")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Member {

    @Id
    @GeneratedValue
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private MemberType memberType;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 20, unique = true, name = "member_id")
    private String memberId;

    protected Member() {
    }

    protected Member(String name, String memberId, MemberType memberType) {
        this.memberId = memberId;
        this.name = name;
        this.memberType = memberType;
    }

    // getters
    public UUID getId() {
        return id;
    }

    public abstract int getBooks();

    public abstract int getDuration();

    public MemberType getType() {
        return this.memberType;
    }

    public String getName() {
        return this.name;
    }

    public String getMemberId() {
        return this.memberId;
    }
    // setters


    public void setName(String name) {
        this.name = name;
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
