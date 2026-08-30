package com.phly101.library.controller;

import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.book.UpdateBookRequest;
import com.phly101.library.mapper.BookMapper;
import com.phly101.library.model.Book;
import com.phly101.library.service.BookService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class ValidationTests {
        @Test
        void BookController_CreateBook_WhenTitleBlank_ReturnsBadRequest() throws Exception {
            // arrange
            CreateBookRequest invalidBook = new CreateBookRequest("", "Robert Martin", "1234567890",
                    LocalDateTime.of(2008, 8, 1, 0, 0), 500, "https://example.com/clean-code.jpg");

            // act+assert
            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidBook)))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(bookService);
        }
    }
    @Nested
    class FindBookTests {
        @Test
        void BookController_findBookById_WhenBookExist() throws Exception {
            // arrange
            CreateBookRequest mockBook = new CreateBookRequest("Clean code", "Robert Martin", "1234567890",
                    LocalDateTime.of(2008, 8, 1, 0, 0), 500, "https://example.com/clean-code.jpg");
            when(bookService.findBookByIsbn("1234567890")).thenReturn(Optional.of(BookMapper.toEntity(mockBook)));
            //act + assert
            mockMvc.perform(get("/books/1234567890")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Clean code"))
                    .andExpect(jsonPath("$.author").value("Robert Martin"))
                    .andExpect(jsonPath("$.isbn").value("1234567890"));
        }

        @Test
        void BookController_findBookById_WhenBookDoesNotExist() throws Exception {
            // arrange
            when(bookService.findBookByIsbn("0000000000000")).thenReturn(Optional.empty());
            //act + assert
            mockMvc.perform(get("/books/0000000000000")).andExpect(status().isNotFound());

        }
    }

    @Nested
    class CreateBookTests {
        @Test
        void BookController_CreateBook_Successfully() throws Exception {
            // arrange
            CreateBookRequest mockBook = new CreateBookRequest("Clean code", "Robert Martin", "1234567890",
                    LocalDateTime.of(2008, 8, 1, 0, 0), 500, "https://example.com/clean-code.jpg");
            // act + assert
            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockBook)))
                    .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.title").value("Clean code"))
                    .andExpect(jsonPath("$.author").value("Robert Martin"))
                    .andExpect(jsonPath("$.isbn").value("1234567890"));
            verify(bookService).addBook(BookMapper.toEntity(mockBook));


        }
    }

    @Nested
    class UpdateBookTests {
        @Test
        void BookController_UpdateBook_Successfully() throws Exception {
            // arrange
            UpdateBookRequest updateBookRequest = new UpdateBookRequest("Balawdawdwawda", "Nonadwadwawwa");
            Book book = new Book(updateBookRequest.title(), updateBookRequest.author(), "1234567890111",
                    LocalDateTime.of(2012, 9, 10, 0, 0), 650, "https://example.com/update-book.jpg");
            when(bookService.updateBook("1234567890111", updateBookRequest.title(), updateBookRequest.author())).thenReturn(book);
            // act+assert
            mockMvc.perform(put("/books/1234567890111")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateBookRequest)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Balawdawdwawda"))
                    .andExpect(jsonPath("$.author").value("Nonadwadwawwa"))
                    .andExpect(jsonPath("$.isbn").value("1234567890111"));


        }
    }

    @Nested
    class DeleteBookTests {
        @Test
        void BookController_DeleteBook_Successfully() throws Exception {
            // arrange
            String isbn = "1234567890111";

            // act+assert
            mockMvc.perform(delete("/books/1234567890111")).andExpect(status().isNoContent());
            verify(bookService).deleteBook(isbn);


        }
    }

}
