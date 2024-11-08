package com.example.post_service.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.post_service.dto.PageResponse;
import com.example.post_service.dto.request.*;
import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.service.PostService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
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

    //    @PutMapping("/updateImageToPost/{postId}")
    //    ApiResponse<PostResponse> updateImageToPost(@PathVariable String postId, @RequestBody AddImageToPostRequest
    // request) {
    //        return ApiResponse.<PostResponse>builder()
    //                .code(1000)
    //                .result(postService.updateImageToPost(postId, request))
    //                .build();
    //    }

    @PostMapping("/updateMediaToPost/{postId}")
    public ApiResponse<PostResponse> uploadMediaToPost(
            @PathVariable String postId, @RequestPart("file") MultipartFile file) throws IOException {
        log.info(
                "Received file upload request for postId: {}, file name: {}, file size: {}, content type: {}",
                postId,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType());

        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.uploadMediaToPost(postId, file))
                .build();
    }

    @GetMapping("/my-posts")
    ApiResponse<PageResponse<PostResponse>> getMyPost(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
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
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(1000)
                .result(postService.getPostWithUserId(userId, page, size))
                .build();
    }

    @GetMapping("/activity")
    ApiResponse<PageResponse<PostResponse>> getAllPost(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(1000)
                .result(postService.getAll(page, size))
                .build();
    }

    @PostMapping("/addCommentToPost/{postId}")
    ApiResponse<PostResponse> addCommentToPost(
            @PathVariable String postId, @RequestBody AddCommentToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.addCommentToPost(postId, request))
                .build();
    }

    @DeleteMapping("/removeCommentToPost/{postId}")
    ApiResponse<PostResponse> removeCommentToPost(
            @PathVariable String postId, @RequestBody RemoveCommentToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.removeCommentToPost(postId, request))
                .build();
    }

    @PutMapping("/updateCommentToPost/{postId}/{commentId}")
    ApiResponse<PostResponse> updateCommentToPost(
            @PathVariable String postId, @PathVariable String commentId, @RequestBody UpdateCommentRequest request) {
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
    ApiResponse<PostResponse> removeLikeToPost(
            @PathVariable String postId, @RequestBody RemoveLikeToPostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.removeLikeToPost(postId, request))
                .build();
    }

    @PutMapping("/activity/update")
    ApiResponse<PostResponse> updatePost(
            @RequestParam(value = "postId", required = false) String postId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "medias", required = false) Set<String> medias) {
        return ApiResponse.<PostResponse>builder()
                .code(1000)
                .result(postService.updatePost(postId, content, medias))
                .build();
    }

    @DeleteMapping("/activity/{postId}")
    ApiResponse<PostResponse> deletePost(@PathVariable String postId) {
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
