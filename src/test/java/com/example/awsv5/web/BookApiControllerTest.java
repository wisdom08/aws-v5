package com.example.awsv5.web;

import com.example.awsv5.domain.Book;
import com.example.awsv5.domain.BookRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("dev")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepo bookRepo;

    @AfterEach
    void tearDown() throws Exception {
        bookRepo.deleteAll();
    }

    @Order(1)
    @Test
    void 책목록보기_테스트() {

        List<Book> books = Arrays.asList(
                new Book("제목1", "내용1", "wisdom"),
                new Book("제목2", "내용2", "wisdom"));
        bookRepo.saveAll(books);

        // 테스트 시작
        ResponseEntity<String> response = restTemplate.exchange("/api/book",
                HttpMethod.GET, null, String.class);
        System.out.println("================================================================================");
        System.out.println(response.getBody());
        System.out.println("================================================================================");
        // 테스트 검증
        DocumentContext dc = JsonPath.parse(response.getBody());
        String title1 = dc.read("$.[0].title");
        String title2 = dc.read("$.[1].title");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("제목1", title1);
        assertEquals("제목2", title2);
    }

    @Order(2)
    @Test
    void 책등록_테스트() throws Exception {

        // 데이터 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = new ObjectMapper().writeValueAsString(new Book("제목3", "내용3", "wisdom"));
        HttpEntity<String> request = new HttpEntity<String>(body, headers);

        // 테스트 시작
        ResponseEntity<String> response = restTemplate.exchange("/api/book", HttpMethod.POST, request, String.class);
        System.out.println("================================================================================");
        System.out.println(response.getBody());
        System.out.println("================================================================================");

        // 테스트 검증
        DocumentContext dc = JsonPath.parse(response.getBody());
        Long id = dc.read("$.id", Long.class);
        String title = dc.read("$.title");
        String content = dc.read("$.content");
        String author = dc.read("$.author");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(3L, id);
        assertEquals("제목3", title);
        assertEquals("내용3", content);
        assertEquals("wisdom", author);
    }

}
