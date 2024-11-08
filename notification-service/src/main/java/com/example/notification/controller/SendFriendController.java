package com.example.notification.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.notification.dto.ApiResponse;
import com.example.notification.dto.request.SendFriendRequest;
import com.example.notification.dto.response.SendFriendResponse;
import com.example.notification.service.SendFriendService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/SendFriend")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendFriendController {
    SendFriendService sendFriendService;

    @PostMapping("/create")
    public ApiResponse<SendFriendResponse> createSendFriend(
            @RequestBody SendFriendRequest request, @RequestHeader("Authorization") String token) {
        return ApiResponse.<SendFriendResponse>builder()
                .code(1000)
                .result(sendFriendService.createSendFriend(request, token))
                .build();
    }

    @GetMapping("/getSendFriendBySenderId/{senderId}")
    public ApiResponse<List<SendFriendResponse>> getSendFriendBySenderId(@PathVariable String senderId) {
        return ApiResponse.<List<SendFriendResponse>>builder()
                .code(1000)
                .result(sendFriendService.getSendFriendBySenderId(senderId))
                .build();
    }

    @GetMapping("/getSendFriend/{sendId}")
    public ApiResponse<SendFriendResponse> getSendFriend(@PathVariable String sendId) {
        return ApiResponse.<SendFriendResponse>builder()
                .code(1000)
                .result(sendFriendService.getSendFriend(sendId))
                .build();
    }

    @GetMapping("/getAllSendFriend")
    public ApiResponse<List<SendFriendResponse>> getAllSendFriend() {
        return ApiResponse.<List<SendFriendResponse>>builder()
                .code(1000)
                .result(sendFriendService.getAllSendFriend())
                .build();
    }

    @DeleteMapping("/deleteSendFriend/{sendId}")
    public ApiResponse<Void> deleteSendFriend(@PathVariable String sendId) {
        sendFriendService.deleteSendFriend(sendId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete send friend success!")
                .build();
    }
}
