package com.example.bookservice.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.bookservice.dto.request.CreateBookRequest;
import com.example.bookservice.dto.response.BookResponse;
import com.example.bookservice.entity.BookProfile;
import com.example.bookservice.service.BookService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class BookControllerTest {
    @Autowired
    private MockBean mockBean;

    @MockBean
    private BookService bookService;

    private BookProfile bookProfile;

    private CreateBookRequest createBookRequest;

    private BookResponse response;

    @BeforeEach
    void initData() {
        createBookRequest = CreateBookRequest.builder().build();

        response = BookResponse.builder().build();
    }
}
