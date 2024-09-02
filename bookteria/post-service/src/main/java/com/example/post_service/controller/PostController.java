package com.example.post_service.controller;

import com.example.post_service.dto.PageResponse;
import com.example.post_service.dto.request.*;
import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.entity.Post;
import com.example.post_service.service.B2StorageService;
import com.example.post_service.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import okhttp3.Request;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;
    B2StorageService b2StorageService;

    @PostMapping("/registration")
    ApiResponse<PostResponse> createPost(
//            @RequestParam String userId,
//            @RequestParam String content,
//            @RequestParam List<String> likes,
//            @RequestParam List<String> comments,
//            @RequestParam("imageUrls") List<MultipartFile> images,
//            @RequestParam("videoUrls") List<MultipartFile> videos
            @RequestBody CreatePostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
//                .result(postService.createPost(userId, content, likes, comments, images, videos))
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/my-posts")
    ApiResponse<PageResponse<PostResponse>> getMyPost(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
                                                      ) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(1000)
                .result(postService.getMyPost(page, size))
                .build();
    }

    @GetMapping("/activity/{postId}")
    ApiResponse<PostResponse> getPost(@PathVariable String postId) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.getPost(postId))
                .build();
    }

    @GetMapping("/activity/getPostWithUser")
    ApiResponse<PageResponse<PostResponse>> getPostWithUser(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(1000)
                .result(postService.getPostWithUserId(userId, page, size))
                .build();
    }

    @GetMapping("/getToken")
    ApiResponse<String> authorizeAccount() throws IOException {
        return ApiResponse.<String>builder()
                .code(1000)
                .result(b2StorageService.authorizeAccount())
                .build();
    }

    @GetMapping("/activity")
    ApiResponse<PageResponse<PostResponse>> getAllPost(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(1000)
                .result(postService.getAll(page, size))
                .build();
    }

    @PostMapping("/addCommentToPost/{postId}")
    ApiResponse<PostResponse> addCommentToPost(@PathVariable String postId, @RequestBody AddCommentToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.addCommentToPost(postId, request))
                .build();
    }

    @DeleteMapping("/removeCommentToPost/{postId}")
    ApiResponse<PostResponse> removeCommentToPost(@PathVariable String postId, @RequestBody RemoveCommentToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.removeCommentToPost(postId, request))
                .build();
    }

    @PutMapping("/updateCommentToPost/{postId}/{commentId}")
    ApiResponse<PostResponse> updateCommentToPost(@PathVariable String postId, @PathVariable String commentId, @RequestBody UpdateCommentRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.updateCommentToPost(postId, commentId, request))
                .build();
    }

    @PostMapping("/addLikeToPost/{postId}")
    ApiResponse<PostResponse> addLikeToPost(@PathVariable String postId, @RequestBody AddLikeToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.addLikeToPost(postId, request))
                .build();
    }

    @DeleteMapping("/removeLikeToPost/{postId}") // none test
    ApiResponse<PostResponse> removeLikeToPost(@PathVariable String postId, @RequestBody RemoveLikeToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.removeLikeToPost(postId, request))
                .build();
    }

    @PutMapping("/activity/update/{postId}")
    ApiResponse<PostResponse> updatePost(@PathVariable String postId, @RequestBody UpdatePostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.updatePost(postId, request))
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
