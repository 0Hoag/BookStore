package com.example.identityservice.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.request.response.UserInformationBasicResponse;
import com.example.identityservice.dto.request.response.UserResponse;
import com.example.identityservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request)
            throws SQLException, IOException {
        log.info("Controller: create user");
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/create-password/{userId}")
    ApiResponse<Void> createPassword(
            @PathVariable("userId") String userId, @RequestBody @Valid PasswordCreationRequest request) {
        userService.createPassword(userId, request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Password has been created, you could use it to log-in")
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid PasswordChangeRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Password has been change, you could use it to log-in")
                .build();
    }

    @PutMapping("/verify-password")
    public ApiResponse<Void> verifyPassword(@RequestBody @Valid PasswordVerifyRequest request) {
        userService.verifyPassword(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Password has been change, you could use it to log-in")
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUser() {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authenticated.getName());
        authenticated.getAuthorities().stream().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .result(userService.getAllUser())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        log.info("Controller: getUser");
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/getAllUserInformationBasic")
    ApiResponse<List<UserInformationBasicResponse>> getAllUserInformationBasic() {
        return ApiResponse.<List<UserInformationBasicResponse>>builder()
                .code(1000)
                .result(userService.getAllUserInformationBasic())
                .build();
    }

    @GetMapping("/getUserInformationBasic/{userId}")
    ApiResponse<UserInformationBasicResponse> getUserInformationBasic(@PathVariable String userId) {
        return ApiResponse.<UserInformationBasicResponse>builder()
                .code(1000)
                .result(userService.getUserInformationBasic(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/updateInformationUser")
    ApiResponse<UserResponse> updateInformationUser(@RequestBody UpdateInformationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.updateInformationUser(request))
                .build();
    }

    @PostMapping("/uploadImageUserProfile/{userId}")
    ApiResponse<Void> uploadImageUserProfile(
            @PathVariable String userId,
            @RequestPart("image") MultipartFile file)
            throws SQLException, IOException {
        userService.uploadImageUserProfile(userId, file);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Update image photo success!")
                .build();
    }

    @PostMapping("/uploadImageUserCover/{userId}")
    ApiResponse<Void> uploadImageUserCover(
            @PathVariable String userId,
            @RequestPart("image") MultipartFile file)
            throws SQLException, IOException {
        userService.uploadImageUserCover(userId, file);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Update image photo success!")
                .build();
    }

    @DeleteMapping("/deleteUserImage/{userId}")
    ApiResponse<Void> removeUserImage(
            @PathVariable String userId,
            @RequestBody RemoveUserImage image) {
        userService.removeImageUser(userId, image);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Remove user image success!")
                .build();
    }

    @PutMapping("{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        log.info("Controller: updateUser");
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("User has been delete")
                .build();
    }

    @DeleteMapping("deleteAllUser")
    ApiResponse<Void> deleteAllUser() {
        userService.deleteAllUser();
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete All User Success")
                .build();
    }

    @GetMapping("/userExisted/{userId}")
    ApiResponse<Void> userExisted(@PathVariable String userId) {
        userService.userExisted(userId);
        return ApiResponse.<Void>builder().code(1000).message("User existed").build();
    }
}
