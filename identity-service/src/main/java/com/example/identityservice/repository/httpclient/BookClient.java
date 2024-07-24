package com.example.identityservice.repository.httpclient;

import com.example.identityservice.configuration.AuthenticationRequestInterceptor;
import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.response.AuthenticationResponse;
import com.example.identityservice.dto.request.response.BookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "book-service",
        url = "${app.services.book}"
)
public interface BookClient {
    @GetMapping(value = "/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<BookResponse> getBook(@PathVariable String bookId, @RequestHeader("Authorization") String token);
}
