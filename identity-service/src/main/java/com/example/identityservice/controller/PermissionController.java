package com.example.identityservice.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.request.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .code(1000)
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping("/{permission}")
    ApiResponse<PermissionResponse> getPermision(@PathVariable String permission) {
        return ApiResponse.<PermissionResponse>builder()
                .code(1000)
                .result(permissionService.getPermission(permission))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authenticated.getName());
        authenticated.getAuthorities().stream().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<PermissionResponse>>builder()
                .code(1000)
                .result(permissionService.getALL())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("permission has been delete")
                .build();
    }
}
