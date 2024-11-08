package com.example.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.notification.dto.ApiResponse;
import com.example.notification.dto.request.CartNotificationRequest;
import com.example.notification.dto.response.CartNotificationResponse;
import com.example.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/cartNotification")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CartNotificationController {
    NotificationService notificationService;

    @Autowired
    public CartNotificationController(
            NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
    }

    @PostMapping("/create")
    public ApiResponse<CartNotificationResponse> createCartNotification(@RequestBody CartNotificationRequest request) {
        return ApiResponse.<CartNotificationResponse>builder()
                .code(1000)
                .result(notificationService.createNotification(request))
                .build();
    }

    @GetMapping("/getAllNotification/{userId}")
    public ApiResponse<List<CartNotificationResponse>> getNotificationByUser(@PathVariable String userId) {
        return ApiResponse.<List<CartNotificationResponse>>builder()
                .code(1000)
                .result(notificationService.getNotificationUser(userId))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<CartNotificationResponse>> getUserNotification(@PathVariable String userId) {
        return ApiResponse.<List<CartNotificationResponse>>builder()
                .code(1000)
                .result(notificationService.getUserReadNotification(userId))
                .build();
    }

    @GetMapping("/user/unread/{userId}")
    public List<CartNotificationResponse> getUnreadNotification(@PathVariable String userId) {
        return notificationService.getUserUnReadNotification(userId);
    }

    @PutMapping("/read/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PutMapping("/user/read-all/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
    }

    @DeleteMapping("/deleteNotification/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
