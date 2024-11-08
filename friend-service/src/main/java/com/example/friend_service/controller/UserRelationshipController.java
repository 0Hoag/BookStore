package com.example.friend_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.UserRelationshipInfo;
import com.example.friend_service.entity.FriendShip;
import com.example.friend_service.service.UserRelationshipService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/relationship")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRelationshipController {
    UserRelationshipService userRelationshipService;

    @GetMapping("/{userId}")
    public ApiResponse<UserRelationshipInfo> getUserRelationShip(@PathVariable String userId) {
        return ApiResponse.<UserRelationshipInfo>builder()
                .code(1000)
                .result(userRelationshipService.getUserRelationShip(userId))
                .build();
    }

    @GetMapping("/friends/{userId}")
    public ApiResponse<List<String>> getFriends(@PathVariable String userId) {
        return ApiResponse.<List<String>>builder()
                .code(1000)
                .result(userRelationshipService.getFriendIds(userId))
                .build();
    }

    @GetMapping("/friendships/{userId}")
    public ApiResponse<List<FriendShip>> getAllFriendShip(@PathVariable String userId) {
        return ApiResponse.<List<FriendShip>>builder()
                .code(1000)
                .result(userRelationshipService.getAllFriendShip(userId))
                .build();
    }
}
