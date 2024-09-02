package com.example.post_service.repository.httpClient;

import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.CommentResponse;
import com.example.post_service.dto.response.LikeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient
        (
                name = "interaction-service",
                url = "http://localhost:8089/interaction"
        )
public interface InteractionClient {
        @GetMapping(value = "/like/activity/{likeId}", produces = MediaType.APPLICATION_JSON_VALUE)
        ApiResponse<LikeResponse> getLike(@PathVariable("likeId") String likeId, @RequestHeader("Authorization") String token);

        @DeleteMapping(value = "/like/activity/{likeId}", produces = MediaType.APPLICATION_JSON_VALUE)
        void deleteLike(@PathVariable("likeId") String likeId, @RequestHeader("Authorization") String token);
}
