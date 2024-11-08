package com.example.friend_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.friend_service.dto.request.CreateFriendRequest;
import com.example.friend_service.dto.request.SendFriendRequest;
import com.example.friend_service.dto.request.UpdateFriendStatus;
import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.FriendResponse;
import com.example.friend_service.dto.response.SendFriendResponse;
import com.example.friend_service.entity.FriendRequest;
import com.example.friend_service.enums.Condition;
import com.example.friend_service.exception.AppException;
import com.example.friend_service.exception.ErrorCode;
import com.example.friend_service.mapper.FriendRequestMapper;
import com.example.friend_service.repository.FeignClient.NotificationClient;
import com.example.friend_service.repository.FriendRequestRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FriendRequestService {
    FriendRequestMapper friendRequestMapper;
    FriendRequestRepository friendRequestRepository;
    NotificationClient notificationClient;

    public FriendResponse createRequest(CreateFriendRequest request, String token) {
        var friendRequest = friendRequestMapper.toFriendRequest(request);
        friendRequest.setCondition(Condition.PENDING);
        friendRequestRepository.save(friendRequest);

        SendFriendRequest sendFriendRequest = SendFriendRequest.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .condition(friendRequest.getCondition())
                .build();
        createSendFriend(sendFriendRequest, token);

        return friendRequestMapper.toFriendResponse(friendRequest);
    }

    public FriendResponse getRequest(String requestId) {
        var friendRequest = friendRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIENDS_REQUEST_NOT_EXISTED));
        return friendRequestMapper.toFriendResponse(friendRequest);
    }

    public List<FriendResponse> getAll() {
        return friendRequestRepository.findAll().stream()
                .map(friendRequestMapper::toFriendResponse)
                .toList();
    }

    public FriendResponse updateFriendCondition(String requestId, UpdateFriendStatus status) {
        FriendRequest request = friendRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIENDS_REQUEST_NOT_EXISTED));
        friendRequestMapper.updateFriendStatus(request, status);
        return friendRequestMapper.toFriendResponse(friendRequestRepository.save(request));
    }

    public void deleteRequest(String requestId) {
        friendRequestRepository.deleteById(requestId);
    }

    public SendFriendResponse createSendFriend(SendFriendRequest request, String token) {
        ApiResponse<SendFriendResponse> sendFriendResponseApiResponse =
                notificationClient.createSendFriendNotification(request, token);
        return sendFriendResponseApiResponse.getResult();
    }
}
