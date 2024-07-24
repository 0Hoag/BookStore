package com.example.notification.controller;

import com.example.notification.entity.UserProfile;
import org.springframework.web.bind.annotation.*;

import com.example.notification.dto.request.ProfileCreationRequest;
import com.example.notification.dto.response.ApiResponse;
import com.example.notification.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    ApiResponse<UserProfile> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<UserProfile>builder()
                .code(1000)
                .result(userProfileService.createProfile(request))
                .build();
    }
}
