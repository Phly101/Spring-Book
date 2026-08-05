package com.phly101.library.controller;

import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.dto.member.MemberResponse;
import com.phly101.library.dto.member.UpdateMemberRequest;
import com.phly101.library.mapper.MemberMapper;
import com.phly101.library.model.Member;
import com.phly101.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest createMemberRequest) {
        Member member = MemberMapper.toMemberEntity(createMemberRequest);
        memberService.registerMember(member);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{memberId}").buildAndExpand(member.getMemberId()).toUri();
        return ResponseEntity.created(location).body(MemberMapper.toMemberResponse(member));
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable("memberId") String memberId, @Valid @RequestBody UpdateMemberRequest updateMemberRequest) {
        Member updatedMember = memberService.updateMember(memberId, updateMemberRequest.name());
        return ResponseEntity.ok(MemberMapper.toMemberResponse(updatedMember));
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") String memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
