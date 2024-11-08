package com.example.Interaction_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.Interaction_service.dto.request.CreateLikeRequest;
import com.example.Interaction_service.dto.response.ApiResponse;
import com.example.Interaction_service.dto.response.LikeResponse;
import com.example.Interaction_service.service.LikeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/like")
public class LikeController {
    LikeService likeService;

    @PostMapping("/registration")
    ApiResponse<LikeResponse> createLike(@RequestBody CreateLikeRequest request) {
        return ApiResponse.<LikeResponse>builder()
                .code(1000)
                .result(likeService.createLike(request))
                .build();
    }

    @GetMapping("/activity/{likeId}")
    ApiResponse<LikeResponse> getLike(@PathVariable String likeId) {
        return ApiResponse.<LikeResponse>builder()
                .code(1000)
                .result(likeService.getLike(likeId))
                .build();
    }

    @GetMapping("/activity")
    ApiResponse<List<LikeResponse>> getAllLike() {
        return ApiResponse.<List<LikeResponse>>builder()
                .code(1000)
                .result(likeService.getAllLike())
                .build();
    }

    @DeleteMapping("/activity/{likeId}")
    ApiResponse<LikeResponse> deleteLike(@PathVariable String likeId) {
        likeService.deleteLike(likeId);
        return ApiResponse.<LikeResponse>builder()
                .code(1000)
                .message("Delete Like Success")
                .build();
    }

    @DeleteMapping("/activity/deleteAll")
    ApiResponse<LikeResponse> deleteAllLike() {
        likeService.deleteAllLike();
        return ApiResponse.<LikeResponse>builder()
                .code(1000)
                .message("Delete All success")
                .build();
    }
}
