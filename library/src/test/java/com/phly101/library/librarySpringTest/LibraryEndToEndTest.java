package com.phly101.library.librarySpringTest;

import com.phly101.library.dto.book.BookResponse;
import com.phly101.library.dto.book.CreateBookRequest;
import com.phly101.library.dto.loan.CreateLoanRequest;
import com.phly101.library.dto.loan.LoanResponse;
import com.phly101.library.dto.loan.TransactionCountResponse;
import com.phly101.library.dto.member.CreateMemberRequest;
import com.phly101.library.dto.member.MemberResponse;
import com.phly101.library.model.enums.MemberType;
import com.phly101.library.repository.BookRepository;
import com.phly101.library.repository.LoanRepository;
import com.phly101.library.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class LibraryEndToEndTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    LoanRepository loanRepository;


    @AfterEach
    void clean() {
        loanRepository.deleteAll();
        memberRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void FullMemberLifeCycleShouldRegisterAddBorrowAndReturn() {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                MemberType.STUDENT, "Tester", "tester/001");

        ResponseEntity<MemberResponse> responseMember =
                restTemplate.postForEntity("/members", createMemberRequest, MemberResponse.class);


        assertThat(responseMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseMember.getBody()).isNotNull();
        assertThat(responseMember.getBody().memberId()).isEqualTo("tester/001");
        assertThat(responseMember.getBody().name()).isEqualTo("Tester");

        CreateBookRequest bookRequest = new CreateBookRequest("BolaBook", "Basel", "0987654321000");

        ResponseEntity<BookResponse> responseBook =
                restTemplate.postForEntity("/books", bookRequest, BookResponse.class);

        assertThat(responseBook.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseBook.getBody()).isNotNull();
        assertThat(responseBook.getBody().title()).isEqualTo("BolaBook");
        assertThat(responseBook.getBody().author()).isEqualTo("Basel");
        assertThat(responseBook.getBody().isbn()).isEqualTo("0987654321000");

        CreateLoanRequest createLoanRequest = new CreateLoanRequest(createMemberRequest.memberId(), bookRequest.isbn());

        ResponseEntity<LoanResponse> responseLoan =
                restTemplate.postForEntity("/loans", createLoanRequest, LoanResponse.class);

        assertThat(responseLoan.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseLoan.getBody()).isNotNull();
        assertThat(responseLoan.getBody().memberId()).isEqualTo(createMemberRequest.memberId());
        assertThat(responseLoan.getBody().bookTitle()).isEqualTo(bookRequest.title());
        restTemplate.delete("/loans?isbn={isbn}&memberId={memberId}",
                bookRequest.isbn(), createMemberRequest.memberId());
        assertThat(loanRepository
                .findByMemberMemberIdAndBookIsbnAndReturnDateIsNull
                        (createMemberRequest.memberId(), bookRequest.isbn()).isPresent()).isFalse();


    }

    @Test
    void multipleBooksAndLoans_shouldPersistAndTrackTransactionsCorrectly() {


        CreateMemberRequest createMemberRequest = new CreateMemberRequest(
                MemberType.STUDENT, "Multi Tester", "tester/multi001");
        ResponseEntity<MemberResponse> memberResponse =
                restTemplate.postForEntity("/members", createMemberRequest, MemberResponse.class);
        assertThat(memberResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        CreateBookRequest book1 = new CreateBookRequest("Book One", "Author A", "1111111111111");
        CreateBookRequest book2 = new CreateBookRequest("Book Two", "Author B", "2222222222222");
        CreateBookRequest book3 = new CreateBookRequest("Book Three", "Author C", "3333333333333");

        ResponseEntity<BookResponse> book1Response = restTemplate.postForEntity("/books", book1, BookResponse.class);
        ResponseEntity<BookResponse> book2Response = restTemplate.postForEntity("/books", book2, BookResponse.class);
        ResponseEntity<BookResponse> book3Response = restTemplate.postForEntity("/books", book3, BookResponse.class);

        assertThat(book1Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(book2Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(book3Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        CreateLoanRequest loan1Request = new CreateLoanRequest(createMemberRequest.memberId(), book1.isbn());
        CreateLoanRequest loan2Request = new CreateLoanRequest(createMemberRequest.memberId(), book2.isbn());

        ResponseEntity<LoanResponse> loan1Response = restTemplate.postForEntity("/loans", loan1Request, LoanResponse.class);
        ResponseEntity<LoanResponse> loan2Response = restTemplate.postForEntity("/loans", loan2Request, LoanResponse.class);

        assertThat(loan1Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(loan2Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        ResponseEntity<BookResponse> fetchedBook1 =
                restTemplate.getForEntity("/books/{isbn}", BookResponse.class, book1.isbn());
        ResponseEntity<BookResponse> fetchedBook2 =
                restTemplate.getForEntity("/books/{isbn}", BookResponse.class, book2.isbn());
        ResponseEntity<BookResponse> fetchedBook3 =
                restTemplate.getForEntity("/books/{isbn}", BookResponse.class, book3.isbn());

        assertThat(fetchedBook1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchedBook1.getBody()).isNotNull();
        assertThat(fetchedBook1.getBody().title()).isEqualTo("Book One");
        assertThat(fetchedBook2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchedBook2.getBody()).isNotNull();
        assertThat(fetchedBook2.getBody().title()).isEqualTo("Book Two");
        assertThat(fetchedBook3.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchedBook3.getBody()).isNotNull();
        assertThat(fetchedBook3.getBody().title()).isEqualTo("Book Three");

        assertThat(loanRepository.existsByBookIsbnAndReturnDateIsNull(book1.isbn())).isTrue();
        assertThat(loanRepository.existsByBookIsbnAndReturnDateIsNull(book2.isbn())).isTrue();
        assertThat(loanRepository.existsByBookIsbnAndReturnDateIsNull(book3.isbn())).isFalse();


        ResponseEntity<TransactionCountResponse> transactionCountResponse =
                restTemplate.getForEntity("/transactions/count", TransactionCountResponse.class);
        assertThat(transactionCountResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(transactionCountResponse.getBody()).isNotNull();
        assertThat(transactionCountResponse.getBody().transactionCount()).isEqualTo(2);
    }
}
