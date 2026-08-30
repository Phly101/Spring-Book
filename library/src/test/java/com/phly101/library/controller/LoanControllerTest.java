package com.phly101.library.controller;

import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.model.Book;
import com.phly101.library.model.Loan;
import com.phly101.library.model.Member;
import com.phly101.library.model.Student;
import com.phly101.library.service.LibraryService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
public class LoanControllerTest {
    @MockitoBean
    private LibraryService libraryService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class ValidationTests {
        @Test
        void LoanController_CreateLoan_WhenMemberIdBlank_ReturnBadRequest() throws Exception {
            // arrange
            CreateLoanRequest mockLoan = new CreateLoanRequest("", "1234567891");
            // act+assert
            mockMvc.perform(post("/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockLoan)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(libraryService);


        }
    }

    @Nested
    class CreateLoanTests {
        @Test
        void LoanController_createLoan_whenValid() throws Exception {
            // arrange
            CreateLoanRequest mockLoan = new CreateLoanRequest("tester/123", "1234567891");
            Member member = new Student("tester1", "tester/123");
            Book book = new Book("Trail of the Tyrant", "Basel", "1234567891",
                    LocalDateTime.of(2019, 6, 20, 0, 0), 410, "https://example.com/trail-of-the-tyrant.jpg");
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
    }

    @Nested
    class TransactionTests {
        @Test
        void LoanController_fetchTransaction_whenItExists() throws Exception {
            // arrange
            int transactionCount = 1;
            when(libraryService.getTotalTransactions()).thenReturn(transactionCount);
            // act+assert
            mockMvc.perform(get("/transactions/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactionCount").value(transactionCount));


        }
    }

    @Nested
    class DeleteLoanTests {

        @Test
        void LoanController_deleteLoan_whenValid() throws Exception {
            // arrange
            String isbn = "1234567890";
            String memberId = "tester/123";
            // act+assert
            mockMvc.perform(delete("/loans").param("isbn", isbn).param("memberId", memberId))
                    .andExpect(status().isNoContent());
            verify(libraryService).returnBook(isbn, memberId);


        }
    }

}
