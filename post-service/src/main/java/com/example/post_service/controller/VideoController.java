package com.example.post_service.controller;

import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.ImageResponse;
import com.example.post_service.dto.response.VideoResponse;
import com.example.post_service.entity.Video;
import com.example.post_service.service.ImageService;
import com.example.post_service.service.VideoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoController {
    VideoService videoService;

    @PostMapping("/registration")
    public ApiResponse<Video> createVideo(@RequestParam("video") MultipartFile file) throws IOException {
        return ApiResponse.<Video>builder()
                .code(1000)
                .result(videoService.createVideo(file))
                .build();
    }

    @GetMapping("/viewVideo/{id}")
    public ApiResponse<VideoResponse> viewVideo(@PathVariable String id) {
        return ApiResponse.<VideoResponse>builder()
                .code(1000)
                .result(videoService.viewVideo(id))
                .build();
    }

    @GetMapping("/viewAll")
    public ApiResponse<List<VideoResponse>> viewAll() {
        return ApiResponse.<List<VideoResponse>>builder()
                .code(1000)
                .result(videoService.viewAllVideo())
                .build();
    }

    @DeleteMapping("/deleteVideo/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable String id) {
        videoService.deleteVideo(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete video success")
                .build();
    }
}
