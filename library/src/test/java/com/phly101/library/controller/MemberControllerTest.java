package com.phly101.library.controller;

import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.dto.member.UpdateMemberRequest;
import com.phly101.library.mapper.MemberMapper;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import com.phly101.library.model.enums.MemberType;
import com.phly101.library.service.MemberService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
    @MockitoBean
    private MemberService memberService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class ValidationTests {
        @Test
        void MemberController_createMember_WhenNameNotValid_ReturnBadRequest() throws Exception {
            // arrange
            CreateMemberRequest mockMember = new CreateMemberRequest(MemberType.STUDENT, "b", "tester123456");

            // act+assert
            mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockMember))).andExpect(status().isBadRequest());
            verifyNoInteractions(memberService);


        }
    }

    @Nested
    class CreateMemberTests {
        @Test
        void MemberController_createMember_whenValid() throws Exception {
            // arrange
            CreateMemberRequest mockMember = new CreateMemberRequest(MemberType.STUDENT, "Basel", "1234567890");

            // act+assert
            mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockMember)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.type").value(MemberType.STUDENT.name()))
                    .andExpect(jsonPath("$.name").value("Basel"))
                    .andExpect(jsonPath("$.memberId").value("1234567890"));
            verify(memberService).registerMember(MemberMapper.toMemberEntity(mockMember));
        }
    }

    @Nested
    class UpdateMemberTests {
        @Test
        void MemberController_UpdateMember_whenFound() throws Exception {
            // arrange
            UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest("bolaBolaBola");
            String memberId = "tester123456";
            Member member = new Student(updateMemberRequest.name(), memberId);
            when(memberService.updateMember(memberId, updateMemberRequest.name())).thenReturn(member);
            // act+assert
            mockMvc.perform(put("/members/{memberId}", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateMemberRequest)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("bolaBolaBola"))
                    .andExpect(jsonPath("$.memberId").value(memberId))
                    .andExpect(jsonPath("$.type").value(MemberType.STUDENT.name()));
        }
    }

    @Nested
    class DeleteMemberTests {
        @Test
        void MemberController_DeleteMember_whenFound() throws Exception {
            // arrange
            String memberId = "tester123456";
            // act+assert
            mockMvc.perform(delete("/members/tester123456")).andExpect(status().isNoContent());
            verify(memberService).deleteMember(memberId);


        }
    }
}
