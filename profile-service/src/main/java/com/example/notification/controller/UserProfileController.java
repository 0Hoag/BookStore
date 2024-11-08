package com.example.notification.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.notification.dto.request.ProfileUpdateRequest;
import com.example.notification.dto.response.ApiResponse;
import com.example.notification.dto.response.ProfileExistedResponse;
import com.example.notification.dto.response.UserProfileResponse;
import com.example.notification.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/{profileId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String profileId) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(1000)
                .result(userProfileService.getProfile(profileId))
                .build();
    }

    @GetMapping("/getUserId/{userId}")
    ApiResponse<UserProfileResponse> getUser(@PathVariable String userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(1000)
                .result(userProfileService.getProfileByUserId(userId))
                .build();
    }

    @GetMapping("/existed/{userId}")
    ApiResponse<ProfileExistedResponse> profileExists(@PathVariable String userId) {
        return ApiResponse.<ProfileExistedResponse>builder()
                .code(1000)
                .result(userProfileService.profileExists(userId))
                .build();
    }

    @GetMapping("/getAllProfile")
    ApiResponse<List<UserProfileResponse>> getAllProfile() {
        return ApiResponse.<List<UserProfileResponse>>builder()
                .code(1000)
                .result(userProfileService.getAllProfile())
                .build();
    }

    @DeleteMapping("/{profileId}")
    ApiResponse<Void> deleteProfile(@PathVariable String profileId) {
        userProfileService.deleteProfile(profileId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete Profile Success")
                .build();
    }

    //    @GetMapping("/my-info")
    //    ApiResponse<UserProfileResponse> getMyInfoProfile() {
    //        return ApiResponse.<UserProfileResponse>builder()
    //                .code(1000)
    //                .result(userProfileService.getMyInfo())
    //                .build();
    //    }

    @DeleteMapping("/userId/{userId}")
    ApiResponse<Void> deleteProfileToUserId(@PathVariable String userId) {
        userProfileService.deleteProfileByUserId(userId);
        return ApiResponse.<Void>builder()
                .message("Delete Profile to UserId Success")
                .build();
    }

    @PutMapping("/{profileId}")
    ApiResponse<UserProfileResponse> updateProfile(
            @PathVariable String profileId, @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(1000)
                .result(userProfileService.updateProfile(profileId, request))
                .build();
    }

    @DeleteMapping("/deleteAll")
    ApiResponse<UserProfileResponse> deleteAllProfile() {
        userProfileService.deleteAllProfile();
        return ApiResponse.<UserProfileResponse>builder()
                .code(1000)
                .message("Delete All Profile success")
                .build();
    }
}
