package com.example.bookservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.bookservice.dto.request.CreateChapterRequest;
import com.example.bookservice.dto.request.UpdateChapterRequest;
import com.example.bookservice.dto.response.ApiResponse;
import com.example.bookservice.dto.response.CreateChapterResponse;
import com.example.bookservice.service.ChapterService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/truyen")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterController {
    ChapterService chapterService;

    @PostMapping("/registration")
    public ApiResponse<CreateChapterResponse> createChapter(@RequestBody CreateChapterRequest request) {
        return ApiResponse.<CreateChapterResponse>builder()
                .code(1000)
                .result(chapterService.createChapter(request))
                .build();
    }

    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<CreateChapterResponse> getChapter(@PathVariable String chapterId) {
        return ApiResponse.<CreateChapterResponse>builder()
                .code(1000)
                .result(chapterService.getChapter(chapterId))
                .build();
    }

    @GetMapping("/chapter/getAll")
    public ApiResponse<List<CreateChapterResponse>> getAllChapter() {
        return ApiResponse.<List<CreateChapterResponse>>builder()
                .code(1000)
                .result(chapterService.getAllChapter())
                .build();
    }

    @DeleteMapping("/chapter/{chapterId}")
    public ApiResponse<CreateChapterResponse> deleteChapter(@PathVariable String chapterId) {
        chapterService.deleteChapter(chapterId);
        return ApiResponse.<CreateChapterResponse>builder()
                .code(1000)
                .message("Delete chapter success")
                .build();
    }

    @PutMapping("/chapter/{chapterId}")
    public ApiResponse<CreateChapterResponse> updateChapter(
            @PathVariable String chapterId, @RequestBody UpdateChapterRequest request) {
        return ApiResponse.<CreateChapterResponse>builder()
                .code(1000)
                .result(chapterService.updateChapter(chapterId, request))
                .message("Update chapter success")
                .build();
    }
}
