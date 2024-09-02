package com.example.friend_service.controller;

import com.example.friend_service.dto.request.CreateFriendRequest;
import com.example.friend_service.dto.request.UpdateFriendStatus;
import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.FriendResponse;
import com.example.friend_service.service.FriendRequestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/Request")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendRequestController {
    FriendRequestService friendRequestService;

    @PostMapping("/Registration")
    public ApiResponse<FriendResponse> createFriendRequest(@RequestBody CreateFriendRequest request) {
        return ApiResponse.<FriendResponse>builder()
                .code(1000)
                .result(friendRequestService.createRequest(request))
                .build();
    }

    @GetMapping("/getFriend/{requestId}")
    public ApiResponse<FriendResponse> getFriendRequest(@RequestParam String requestId) {
        return ApiResponse.<FriendResponse>builder()
                .code(1000)
                .result(friendRequestService.getRequest(requestId))
                .build();
    }

    @GetMapping("/getAllFriend")
    public ApiResponse<List<FriendResponse>> getAllFriendRequest() {
        return ApiResponse.<List<FriendResponse>>builder()
                .code(1000)
                .result(friendRequestService.getAll())
                .build();
    }

    @PutMapping("/updateFriendRequest/{requestId}")
    ApiResponse<FriendResponse> updateFriendStatus(@PathVariable String requestId, @RequestBody UpdateFriendStatus status) {
        return ApiResponse.<FriendResponse>builder()
                .code(1000)
                .result(friendRequestService.updateFriendCondition(requestId, status))
                .build();
    }

    @DeleteMapping("/deleteFriendRequest/{requestId}")
    public ApiResponse<FriendResponse> removeFriendRequest(@PathVariable String requestId) {
        friendRequestService.deleteRequest(requestId);
        return ApiResponse.<FriendResponse>builder()
                .code(1000)
                .message("Remove request success")
                .build();
    }
}
