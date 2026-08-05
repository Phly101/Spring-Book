package com.phly101.library.service;

import com.phly101.library.exception.DuplicateMemberException;
import com.phly101.library.exception.MemberHasActiveLoansException;
import com.phly101.library.exception.MemberNotFoundException;
import com.phly101.library.model.Member;
import com.phly101.library.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final LoanService loanService;

    public MemberService(MemberRepository memberRepository, LoanService loanService) {
        this.memberRepository = memberRepository;
        this.loanService = loanService;
    }


    public Optional<Member> findMemberById(String memberId) {
        if (memberId != null) {
            return memberRepository.findByMemberId(memberId);
        } else {
            return Optional.empty();
        }
    }


    public Member registerMember(Member member) {
        if (findMemberById(member.getMemberId()).isPresent()) {
            throw new DuplicateMemberException(member.getMemberId());
        }
        return memberRepository.save(member);

    }

    @Transactional
    public Member updateMember(String memberId, String name) {
        Member member = findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        member.setName(name);
        return member;
    }

    @Transactional
    public void deleteMember(String memberId) {
        Member member = findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        if (loanService.doesMemberHaveLoans(member.getMemberId())) {
            throw new MemberHasActiveLoansException(memberId);
        }
        memberRepository.delete(member);
    }
}
