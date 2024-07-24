package com.example.post_service.controller;

import com.example.post_service.dto.request.CreatePostRequest;
import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.entity.PostStatusBook;
import com.example.post_service.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping("/registration")
    ApiResponse<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/activity/{postId}")
    ApiResponse<PostResponse> getPost(@PathVariable String postId) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.getPost(postId))
                .build();
    }

    @GetMapping("/activity")
    ApiResponse<List<PostResponse>> getAllPost() {
        return ApiResponse.<List<PostResponse>>builder()
                .code(1000)
                .result(postService.getAllPost())
                .build();
    }

    @DeleteMapping("/activity/{postId}")
    ApiResponse<PostResponse> deletePost(@PathVariable String postId){
        postService.deletePost(postId);
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .message("Delete Post Success")
                .build();
    }

    @DeleteMapping("/activity/deleteAll")
    ApiResponse<PostResponse> deleteAllPost() {
        postService.deleteAllPost();
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .message("Delete All success")
                .build();
    }
}
