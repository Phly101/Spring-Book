package com.phly101.library.controller;

import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.dto.member.MemberResponse;
import com.phly101.library.dto.member.UpdateMemberRequest;
import com.phly101.library.mapper.MemberMapper;
import com.phly101.library.model.Member;
import com.phly101.library.service.MemberService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Members", description = "Endpoints for managing library members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(
            summary = "Register a new library member",
            description = "Creates a new member account in the library system. Members can be either Students or Faculty. Each member gets a unique member ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing/invalid name, email, or member type)"),
            @ApiResponse(responseCode = "409", description = "A member with this email already exists")
    })
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Member registration information",
                    required = true
            )
            @Valid @RequestBody CreateMemberRequest createMemberRequest) {
        Member member = MemberMapper.toMemberEntity(createMemberRequest);
        memberService.registerMember(member);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{memberId}").buildAndExpand(member.getMemberId()).toUri();
        return ResponseEntity.created(location).body(MemberMapper.toMemberResponse(member));
    }

    @Operation(
            summary = "Update an existing member",
            description = "Updates the name of an existing library member. The member ID is used to identify the member and cannot be changed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing/invalid name)"),
            @ApiResponse(responseCode = "404", description = "No member found with the provided member ID")
    })
    @PutMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "The unique identifier of the member to update", example = "M001", required = true)
            @PathVariable("memberId") String memberId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated member information",
                    required = true
            )
            @Valid @RequestBody UpdateMemberRequest updateMemberRequest) {
        Member updatedMember = memberService.updateMember(memberId, updateMemberRequest.name());
        return ResponseEntity.ok(MemberMapper.toMemberResponse(updatedMember));
    }

    @Operation(
            summary = "Delete a library member",
            description = "Removes a member account from the library system. Cannot delete members that have active book loans."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No member found with the provided member ID"),
            @ApiResponse(responseCode = "409", description = "Cannot delete member because they have active book loans")
    })
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "The unique identifier of the member to delete", example = "M001", required = true)
            @PathVariable("memberId") String memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
