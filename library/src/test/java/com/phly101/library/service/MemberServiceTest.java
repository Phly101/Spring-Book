package com.phly101.library.service;

import com.phly101.library.exception.DuplicateMemberException;
import com.phly101.library.exception.MemberHasActiveLoansException;
import com.phly101.library.exception.MemberNotFoundException;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import com.phly101.library.repository.MemberRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LoanService loanService;

    @InjectMocks
    private MemberService memberService;

    private Member addTestMember() {
        return new Student("tester1", "tester/123");
    }

    @Nested
    class FindMemberByIdTests {

        @Test
        void MemberService_FindMemberByIdWhenExists_Test() {
            Member member = addTestMember();
            String memberId = member.getMemberId();
            when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.of(member));
            Optional<Member> result = memberService.findMemberById(memberId);
            assertEquals(member, result.orElse(null));
        }

        @Test
        void MemberService_FindMemberByIdDoesNotExist_Test() {
            String memberId = "tester/123";
            when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
            Optional<Member> result = memberService.findMemberById(memberId);
            assertTrue(result.isEmpty());
        }

        @Test
        void MemberService_FindMemberByIdWhenMemberIdIsNull_Test() {
            Optional<Member> result = memberService.findMemberById(null);
            assertTrue(result.isEmpty());
            verify(memberRepository, never()).findByMemberId(any());
        }
    }

    @Nested
    class RegisterMemberTests {

        @Test
        void MemberService_RegisterMemberSuccessfully_Test() {
            Member member = addTestMember();
            when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.empty());
            when(memberRepository.save(member)).thenReturn(member);
            Member result = memberService.registerMember(member);
            assertEquals(member, result);
        }

        @Test
        void MemberService_RegisterDuplicateMember_Test() {
            Member member = addTestMember();
            when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));
            assertThrows(DuplicateMemberException.class, () -> memberService.registerMember(member));
            verify(memberRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateMemberTests {

        @Test
        void MemberService_UpdateMemberWhenFound_Test() {
            Member member = addTestMember();
            when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));
            Member result = memberService.updateMember(member.getMemberId(), "New Name");
            assertEquals("New Name", result.getName());
        }

        @Test
        void MemberService_UpdateMemberWhenNotFound_Test() {
            String memberId = "tester/123";
            when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
            assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberId, "New Name"));
        }
    }

    @Nested
    class DeleteMemberTests {

        @Test
        void MemberService_DeleteMemberWhenFoundWithNoLoans_Test() {
            Member member = addTestMember();
            when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));
            when(loanService.doesMemberHaveLoans(member.getMemberId())).thenReturn(false);
            memberService.deleteMember(member.getMemberId());
            verify(memberRepository, times(1)).delete(member);
        }

        @Test
        void MemberService_DeleteMemberWhenNotFound_Test() {
            String memberId = "tester/123";
            when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
            assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(memberId));
            verify(memberRepository, never()).delete(any());
        }

        @Test
        void MemberService_DeleteMemberWhenHasLoans_Test() {
            Member member = addTestMember();
            when(memberRepository.findByMemberId(member.getMemberId())).thenReturn(Optional.of(member));
            when(loanService.doesMemberHaveLoans(member.getMemberId())).thenReturn(true);
            assertThrows(MemberHasActiveLoansException.class, () -> memberService.deleteMember(member.getMemberId()));
            verify(memberRepository, never()).delete(any());
        }
    }
}