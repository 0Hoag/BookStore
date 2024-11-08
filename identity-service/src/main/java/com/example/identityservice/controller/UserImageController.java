package com.example.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.UserImageRequest;
import com.example.identityservice.dto.request.UserImageUpdateRequest;
import com.example.identityservice.dto.request.response.UserImageResponse;
import com.example.identityservice.entity.UserImage;
import com.example.identityservice.service.UserImageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/userImage")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserImageController {
    UserImageService userImageService;

    @PostMapping("/registration")
    public ApiResponse<UserImage> toCreateUserImage(@RequestBody UserImageRequest request) {
        return ApiResponse.<UserImage>builder()
                .code(1000)
                .result(userImageService.toCreateUserImage(request))
                .build();
    }

    @GetMapping("/getUserImage/{imageId}")
    public ApiResponse<UserImageResponse> getUserImage(@PathVariable String imageId) {
        return ApiResponse.<UserImageResponse>builder()
                .code(1000)
                .result(userImageService.getUserImage(imageId))
                .build();
    }

    @GetMapping("/getAllUserImage")
    public ApiResponse<List<UserImage>> getAllUserImage() {
        return ApiResponse.<List<UserImage>>builder()
                .code(1000)
                .result(userImageService.getAll())
                .build();
    }

    @PutMapping("/updateUserImage/{imageId}")
    public ApiResponse<UserImageResponse> updateImage(
            @PathVariable String imageId, @RequestBody UserImageUpdateRequest request) {
        return ApiResponse.<UserImageResponse>builder()
                .code(1000)
                .result(userImageService.updateUserImage(imageId, request))
                .build();
    }

    @DeleteMapping("/deleteUserImage/{imageId}")
    public ApiResponse<Void> deleteUserImage(@PathVariable String imageId) {
        userImageService.deleteUserImage(imageId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete user image success!")
                .build();
    }
}
