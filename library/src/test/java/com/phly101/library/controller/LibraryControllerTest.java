package com.phly101.library.controller;

import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.mapper.BookMapper;
import com.phly101.library.mapper.MemberMapper;
import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import com.phly101.library.model.enums.MemberType;
import com.phly101.library.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LibraryController.class)
public class LibraryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LibraryService libraryService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
    }

    @Test
    void libraryController_findBookById_WhenBookExist() throws Exception {
        // arrange
        CreateBookRequest mockBook = new CreateBookRequest("Clean code", "Robert Martin", "1234567890");
        when(libraryService.findBookByIsbn("1234567890")).thenReturn(Optional.of(BookMapper.toEntity(mockBook)));
        //act + assert
        mockMvc.perform(get("/books/1234567890")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean code"))
                .andExpect(jsonPath("$.author").value("Robert Martin"))
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    void libraryController_findBookById_WhenBookDoesNotExist() throws Exception {
        // arrange
        when(libraryService.findBookByIsbn("0000000000000")).thenReturn(Optional.empty());
        //act + assert
        mockMvc.perform(get("/books/0000000000000")).andExpect(status().isNotFound());

    }

    @Test
    void libraryController_CreateBooks_whatMemberExist() throws Exception {
        // arrange
        CreateBookRequest mockBook = new CreateBookRequest("Clean code", "Robert Martin", "1234567890");
        // act + assert
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockBook)))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Clean code"))
                .andExpect(jsonPath("$.author").value("Robert Martin"))
                .andExpect(jsonPath("$.isbn").value("1234567890"));
        verify(libraryService).addBook(BookMapper.toEntity(mockBook));


    }

    @Test
    void libraryController_createMember_whenValid() throws Exception {
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
        verify(libraryService).registerMember(MemberMapper.toMemberEntity(mockMember));
    }

    @Test
    void libraryController_createLoan_whenValid() throws Exception {
        // arrange
        CreateLoanRequest mockLoan = new CreateLoanRequest("tester/123", "1234567891");
        Member member = new Student("tester1", "tester/123");
        Book book = new Book("Trail of the Tyrant", "Basel", "1234567891");
        Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14));
        when(libraryService.borrowBook(mockLoan.memberId(), mockLoan.isbn())).thenReturn(loan);
        // act+assert
        mockMvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockLoan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Trail of the Tyrant"))
                .andExpect(jsonPath("$.memberId").value("tester/123"))
                .andExpect(jsonPath("$.loanDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusDays(14).toString()));
    }

    @Test
    void libraryController_fetchTransaction_whenItExists() throws Exception {
        // arrange
        int transactionCount = 1;
        when(libraryService.getTotalTransactions()).thenReturn(transactionCount);
        // act+assert
        mockMvc.perform(get("/transactions/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(transactionCount));


    }

    @Test
    void libraryController_deleteLoan_whenValid() throws Exception {
        // arrange
        String isbn = "1234567890";
        // act+assert
        mockMvc.perform(delete("/loans/{isbn}", isbn)).andExpect(status().isNoContent());
        verify(libraryService).returnBook(isbn);


    }

}
