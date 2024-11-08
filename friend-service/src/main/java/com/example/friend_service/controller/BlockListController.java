package com.example.friend_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.friend_service.dto.request.BlockListRequest;
import com.example.friend_service.dto.request.BlockListUpdateRequest;
import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.BlockListResponse;
import com.example.friend_service.service.BlockListService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/blockList")
public class BlockListController {
    BlockListService blockListService;

    @PostMapping("/registration")
    public ApiResponse<BlockListResponse> createBlockList(@RequestBody BlockListRequest request) {
        return ApiResponse.<BlockListResponse>builder()
                .code(1000)
                .result(blockListService.createBlockList(request))
                .build();
    }

    @GetMapping("/getBlockList/{blockId}")
    public ApiResponse<BlockListResponse> getBlockList(@PathVariable String blockId) {
        return ApiResponse.<BlockListResponse>builder()
                .code(1000)
                .result(blockListService.getBlockList(blockId))
                .build();
    }

    @GetMapping("/getBlockListAll")
    public ApiResponse<List<BlockListResponse>> getAllBlockList() {
        return ApiResponse.<List<BlockListResponse>>builder()
                .code(1000)
                .result(blockListService.getAllBlockList())
                .build();
    }

    @DeleteMapping("/deleteBlockList/{blockId}")
    public ApiResponse<BlockListResponse> removeBlockList(@PathVariable String blockId) {
        blockListService.deleteBlockList(blockId);
        return ApiResponse.<BlockListResponse>builder()
                .code(1000)
                .message("You delete BlockList success")
                .build();
    }

    @DeleteMapping("/deleteAll")
    public ApiResponse<BlockListResponse> removeAllBlockList() {
        blockListService.deleteAll();
        return ApiResponse.<BlockListResponse>builder()
                .code(1000)
                .message("You delete BlockList success")
                .build();
    }

    @PutMapping("/updateBlockList/{blockId}")
    public ApiResponse<BlockListResponse> UpdateAllBlockList(
            @PathVariable String blockId, @RequestBody BlockListUpdateRequest request) {
        return ApiResponse.<BlockListResponse>builder()
                .code(1000)
                .result(blockListService.updateBlockList(blockId, request))
                .build();
    }
}
