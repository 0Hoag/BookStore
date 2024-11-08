package com.example.payment_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.dto.response.BookResponse;

@FeignClient(name = "book-service", url = "http://localhost:8084/book")
public interface BookClient {
    @GetMapping(value = "/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<BookResponse> getBook(@PathVariable String bookId, @RequestHeader("Authorization") String token);
}
