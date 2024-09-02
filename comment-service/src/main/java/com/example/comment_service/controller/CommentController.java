package com.example.comment_service.controller;

import com.example.comment_service.dto.request.AddCommentToPostRequest;
import com.example.comment_service.dto.request.CreateCommentRequest;
import com.example.comment_service.dto.request.UpdateCommentRequest;
import com.example.comment_service.dto.response.ApiResponse;
import com.example.comment_service.dto.response.CommentResponse;
import com.example.comment_service.dto.response.PostResponse;
import com.example.comment_service.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/comments")
public class CommentController {
    CommentService commentService;

    @PostMapping("/registration")
    ApiResponse<CommentResponse> createLike(@RequestBody CreateCommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/activity/{commentId}")
    ApiResponse<CommentResponse> getComment(@PathVariable String commentId) {
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.getComment(commentId))
                .build();
    }

    @GetMapping("/activity")
    ApiResponse<List<CommentResponse>> getAllComment() {
        return ApiResponse.<List<CommentResponse>>builder()
                .code(1000)
                .result(commentService.getAllComment())
                .build();
    }

    @PutMapping("/activity/updateComment/{commentId}")
    ApiResponse<CommentResponse> updateCommentResponse(@PathVariable String commentId, @RequestBody UpdateCommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .result(commentService.updateCommentResponse(commentId, request))
                .build();
    }

    @DeleteMapping("/activity/{commentId}")
    ApiResponse<Void> deleteComment(@PathVariable String commentId){
        commentService.deleteComment(commentId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete Like Success")
                .build();
    }

    @DeleteMapping("/activity/deleteAll")
    ApiResponse<CommentResponse> deleteAllLike() {
        commentService.deleteAllComment();
        return ApiResponse.<CommentResponse>builder()
                .code(1000)
                .message("Delete All success")
                .build();
    }
}
