package com.example.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.CartNotificationRequest;
import com.example.identityservice.dto.request.response.CartNotificationResponse;

@FeignClient(name = "notification-service", url = "${app.services.notification}")
public interface NotificationClient {
    @PostMapping(value = "/cartNotification/create", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CartNotificationResponse> createCartNotification(
            @RequestBody CartNotificationRequest request, @RequestHeader("Authorization") String token);

    @GetMapping(value = "/cartNotification/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CartNotificationResponse> getUserNotification(@PathVariable("userId") String userId);

    @GetMapping(value = "/cartNotification/user/{userId}/unread", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CartNotificationResponse> getUnreadNotification(@PathVariable("userId") String userId);

    @PutMapping(value = "/cartNotification/{notificationId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CartNotificationResponse> markAsRead(@PathVariable("notificationId") String notificationId);

    @PutMapping(value = "/cartNotification/user/{userId}/read-all", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<CartNotificationResponse> markAllAsRead(@PathVariable("userId") String userId);
}
