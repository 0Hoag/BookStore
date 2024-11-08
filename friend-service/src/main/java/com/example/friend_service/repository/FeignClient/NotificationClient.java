package com.example.friend_service.repository.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.friend_service.dto.request.SendFriendRequest;
import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.SendFriendResponse;

@FeignClient(name = "notification-service", url = "${app.services.notification}")
public interface NotificationClient {
    @PostMapping(value = "/SendFriend/create", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<SendFriendResponse> createSendFriendNotification(
            @RequestBody SendFriendRequest request, @RequestHeader("Authorization") String token);

    @GetMapping(value = "/SendFriend/getSendFriend/{sendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<SendFriendResponse> getUserNotification(@PathVariable("sendId") String sendId);

    //    @GetMapping(value = "/SendFriend/user/{userId}/unread", produces = MediaType.APPLICATION_JSON_VALUE)
    //    ApiResponse<SendFriendResponse> getUnreadNotification(@PathVariable("userId") String userId);
    //
    //    @PutMapping(value = "/SendFriend/{notificationId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    //    ApiResponse<SendFriendResponse> markAsRead(@PathVariable("notificationId") String notificationId);
    //
    //    @PutMapping(value = "/SendFriend/user/{userId}/read-all", produces = MediaType.APPLICATION_JSON_VALUE)
    //    ApiResponse<SendFriendResponse> markAllAsRead(@PathVariable("userId") String userId);
}
