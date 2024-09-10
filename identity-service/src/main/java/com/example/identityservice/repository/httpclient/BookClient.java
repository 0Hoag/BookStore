package com.example.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.response.BookResponse;

@FeignClient(name = "book-service", url = "${app.services.book}")
public interface BookClient {
    @GetMapping(value = "/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<BookResponse> getBook(@PathVariable String bookId, @RequestHeader("Authorization") String token);
}