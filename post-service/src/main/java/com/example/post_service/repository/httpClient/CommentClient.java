package com.example.post_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.post_service.dto.request.UpdateCommentRequest;
import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.CommentResponse;

@FeignClient(name = "comment-service", url = "http://localhost:8088/comment")
public interface CommentClient {
    @GetMapping(value = "/comments/activity/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CommentResponse> getComment(
            @PathVariable("commentId") String commentId, @RequestHeader("Authorization") String token);

    @DeleteMapping(value = "/comments/activity/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteComment(@PathVariable("commentId") String commentId, @RequestHeader("Authorization") String token);

    @PutMapping(value = "/comments/activity/updateComment/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CommentResponse> updateCommentResponse(
            @PathVariable("commentId") String commentId,
            @RequestBody UpdateCommentRequest request,
            @RequestHeader("Authorization") String token);
}
