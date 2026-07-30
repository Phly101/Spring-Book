package com.phly101.library.mapper;

import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.dto.member.MemberResponse;
import com.phly101.library.model.Faculty;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;

public class MemberMapper {
    public static MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(
                member.getType(),
                member.getName(),
                member.getMemberId()
        );
    }

    public static Member toMemberEntity(CreateMemberRequest request) {
        if (request.type().toString().equals("FACULTY")) {
            return new Faculty(
                    request.name(),
                    request.memberId()
            );
        } else {
            return new Student(
                    request.name(),
                    request.memberId())
                    ;
        }
    }

}
