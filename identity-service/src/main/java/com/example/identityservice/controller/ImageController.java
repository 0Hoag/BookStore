package com.example.identityservice.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.response.ImageResponse;
import com.example.identityservice.service.ImageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageController {
    ImageService imageService;

    @PostMapping("/registration")
    public ApiResponse<ImageResponse> createImage(@RequestParam("image") MultipartFile file)
            throws SQLException, IOException {
        return ApiResponse.<ImageResponse>builder()
                .code(1000)
                .result(imageService.createImage(file))
                .build();
    }

    @GetMapping("/viewImage/{id}")
    public ApiResponse<ImageResponse> viewImage(@PathVariable String id) {
        return ApiResponse.<ImageResponse>builder()
                .code(1000)
                .result(imageService.viewImage(id))
                .build();
    }

    @GetMapping("/viewAll")
    public ApiResponse<List<ImageResponse>> createImage() {
        return ApiResponse.<List<ImageResponse>>builder()
                .code(1000)
                .result(imageService.viewAllImage())
                .build();
    }

    @DeleteMapping("/deleteImage/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable String id) {
        imageService.deleteImage(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete image success")
                .build();
    }
}
