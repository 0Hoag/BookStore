package com.example.Interaction_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.Interaction_service.dto.response.ApiResponse;
import com.example.Interaction_service.dto.response.PostResponse;

@FeignClient(name = "post-service", url = "http://localhost:8083/post")
public interface PostClient {
    @GetMapping(value = "/activity/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<PostResponse> getPost(
            @PathVariable("postId") String postId, @RequestHeader("Authorization") String token);
}
