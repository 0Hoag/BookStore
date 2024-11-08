package com.example.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.notification.dto.ApiResponse;
import com.example.notification.dto.request.SendFriendRequest;
import com.example.notification.dto.response.SendFriendResponse;
import com.example.notification.dto.response.UserInformationBasicResponse;
import com.example.notification.entity.SendFriend;
import com.example.notification.entity.enums.Condition;
import com.example.notification.exception.AppException;
import com.example.notification.exception.ErrorCode;
import com.example.notification.mapper.SendFriendMapper;
import com.example.notification.repository.SendFriendRepository;
import com.example.notification.repository.httpClient.IdentityClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendFriendService {
    SendFriendRepository sendFriendRepository;
    SendFriendMapper sendFriendMapper;
    IdentityClient identityClient;
    KafkaTemplate<String, SendFriend> kafkaTemplate;

    public SendFriendResponse createSendFriend(SendFriendRequest request, String token) {
        var sendFriend = sendFriendMapper.toSendFriend(request);

        if (getUserInformation(request.getSenderId(), token) == null) {
            return sendFriendMapper.toSendFriendResponse(sendFriend);
        }

        sendFriend.setCondition(Condition.PENDING);
        sendFriendRepository.save(sendFriend);

        kafkaTemplate.send("send-notifications", sendFriend);
        return sendFriendMapper.toSendFriendResponse(sendFriend);
    }

    public List<SendFriendResponse> getSendFriendBySenderId(String senderId) {
        return sendFriendRepository.findBySenderId(senderId).stream()
                .map(sendFriendMapper::toSendFriendResponse)
                .collect(Collectors.toList());
    }

    public SendFriendResponse getSendFriend(String sendId) {
        var sendFriend =
                sendFriendRepository.findById(sendId).orElseThrow(() -> new AppException(ErrorCode.SEND_NOT_EXISTED));
        return sendFriendMapper.toSendFriendResponse(sendFriend);
    }

    public List<SendFriendResponse> getAllSendFriend() {
        return sendFriendRepository.findAll().stream()
                .map(sendFriendMapper::toSendFriendResponse)
                .collect(Collectors.toList());
    }

    public void deleteSendFriend(String sendId) {
        sendFriendRepository.deleteById(sendId);
    }

    public UserInformationBasicResponse getUserInformation(String userId, String token) {
        ApiResponse<UserInformationBasicResponse> response = identityClient.getUserInformationBasic(userId, token);
        return response.getResult();
    }
}
