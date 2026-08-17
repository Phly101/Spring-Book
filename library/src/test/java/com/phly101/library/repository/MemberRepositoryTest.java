package com.phly101.library.repository;


import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member addTestMember() {
        return new Student("tester1", "tester/123");
    }

    @Test
    void findMemberById_shouldReturnMember_WhenMemberIdExists() {

        // arrange
        Member member = addTestMember();
        entityManager.persistAndFlush(member);

        // act
        Optional<Member> result = memberRepository.findByMemberId(member.getMemberId());

        // assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(result.get().getName()).isEqualTo(member.getName());

    }
    @Test
    void findByMemberId_shouldReturnEmpty_WhenMemberIdDoesNotExist() {
        // arrange
        Member member = addTestMember();
        entityManager.persistAndFlush(member);

        // act
        Optional<Member> result = memberRepository.findByMemberId("tester2/444");

        // assert
        assertThat(result.isPresent()).isFalse();
    }
}
