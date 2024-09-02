package com.example.comment_service.repository.httpClient;

import com.example.comment_service.dto.response.ApiResponse;
import com.example.comment_service.dto.response.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient
        (
                name = "post-service",
                url = "http://localhost:8083/post"
        )
public interface PostClient {
        @GetMapping(value = "/activity/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
        ApiResponse<PostResponse> getPost(@PathVariable("postId") String postId, @RequestHeader("Authorization") String token);
}
