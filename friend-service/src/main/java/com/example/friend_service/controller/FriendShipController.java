package com.example.friend_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.friend_service.dto.request.FriendShipRequest;
import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.FriendShipResponse;
import com.example.friend_service.service.FriendShipService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/ship")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendShipController {
    FriendShipService friendShipService;

    @PostMapping("/Registration")
    public ApiResponse<FriendShipResponse> createFriendShip(@RequestBody FriendShipRequest request) {
        return ApiResponse.<FriendShipResponse>builder()
                .code(1000)
                .result(friendShipService.createFriendShip(request))
                .build();
    }

    @GetMapping("/getFriendShip/{friendshipId}")
    public ApiResponse<FriendShipResponse> getFriendShip(@PathVariable String friendshipId) {
        return ApiResponse.<FriendShipResponse>builder()
                .code(1000)
                .result(friendShipService.getFriendShip(friendshipId))
                .build();
    }

    @GetMapping("/getAllFriend/{friendshipId}")
    public ApiResponse<List<FriendShipResponse>> getAllFriendShip() {
        return ApiResponse.<List<FriendShipResponse>>builder()
                .code(1000)
                .result(friendShipService.getAll())
                .build();
    }

    @DeleteMapping("/removeFriendShip/{friendshipId}")
    public ApiResponse<FriendShipResponse> removeFriendShip(@PathVariable String friendshipId) {
        friendShipService.deleteFriendShip(friendshipId);
        return ApiResponse.<FriendShipResponse>builder()
                .code(1000)
                .message("Remove friendShip success")
                .build();
    }

    @DeleteMapping("/removeAllFriendShip")
    public ApiResponse<FriendShipResponse> removeAllFriendShip() {
        friendShipService.deleteAllFriendShip();
        return ApiResponse.<FriendShipResponse>builder()
                .code(1000)
                .message("Remove all friendShip success")
                .build();
    }
}
