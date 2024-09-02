package com.example.messaging_service.service;

import com.example.messaging_service.dto.identity.AuthenticationRequest;
import com.example.messaging_service.dto.identity.AuthenticationResponse;
import com.example.messaging_service.dto.identity.UserResponse;
import com.example.messaging_service.dto.request.CreateMessagingRequest;
import com.example.messaging_service.dto.response.ApiResponse;
import com.example.messaging_service.dto.response.MessagingResponse;
import com.example.messaging_service.dto.response.PageResponse;
import com.example.messaging_service.entity.Conversation;
import com.example.messaging_service.entity.ConversationListItem;
import com.example.messaging_service.entity.Message;
import com.example.messaging_service.exception.AppException;
import com.example.messaging_service.exception.ErrorCode;
import com.example.messaging_service.mapper.MessagingMapper;
import com.example.messaging_service.repository.ConversationRepository;
import com.example.messaging_service.repository.MessagingRepository;
import com.example.messaging_service.repository.httpClient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MessagingService {

    MessagingMapper messagingMapper;

    ConversationRepository conversationRepository;
    MessagingRepository messagingRepository;
    IdentityClient identityClient;

    public MessagingResponse sendMessage(CreateMessagingRequest request) {

        String token = generationToken();

        var mess = messagingMapper.toMessaging(request);

        mess.setTimestamp(Instant.now()); // update 08/31

        UserResponse user = getUserInformationBasic(request.getSenderId(), token);
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));

        return messagingMapper.toMessagingResponse(messagingRepository.save(mess));
    }

    public List<MessagingResponse> getAllMessage() {
        return messagingRepository.findAll()
                .stream()
                .map(messagingMapper::toMessagingResponse)
                .collect(Collectors.toList());
    }

    public List<ConversationListItem> getUserConversationList(String userId, int limit) {
        // Lấy danh sách cuộc trò chuyện của người dùng
        Page<Conversation> conversationsPage = conversationRepository.findByParticipantIdsContaining(
                userId,
                PageRequest.of(0, limit, Sort.by("lastMessageAt").descending()));
        log.info("conversationsPage {}", conversationsPage);
        // Lấy ID của tất cả cuộc trò chuyện
        List<String> conversationIds = conversationsPage.stream().map(Conversation::getId).collect(Collectors.toList());
        log.info("conversationIds {}", conversationIds);

        // Lấy tin nhắn cuối cùng cho tất cả cuộc trò chuyện trong một truy vấn
        List<Message> lastMessages = messagingRepository.findLastMessagesByConversationIdsAggregation(conversationIds);
        Map<String, Message> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(Message::getConversationId, Function.identity()));
        log.info("lastMessages {}", lastMessages);
        log.info("lastMessageMap {}", lastMessageMap);


        // Tạo danh sách kết quả
        return conversationsPage.stream().map(conversation -> {
            Message lastMessage = lastMessageMap.get(conversation.getId());
            String title = getConversationTitle(conversation, userId, lastMessage);
            return new ConversationListItem(
                    conversation.getId(),
                    title,
                    lastMessage != null ? lastMessage.getContent() : "",
                    lastMessage != null ? lastMessage.getTimestamp() : conversation.getCreateAt(),
                    lastMessage != null ? lastMessage.getSenderId() : null
            );
        }).collect(Collectors.toList());
    }

    private String getConversationTitle(Conversation conversation, String currentUserId, Message firstMessage) {
        if (conversation.getName() != null && !conversation.getName().isEmpty()) {
            return conversation.getName();
        }
        if (conversation.getParticipantIds().size() == 2) {
            String otherUserId = conversation.getParticipantIds().stream()
                    .filter(id -> !id.equals(currentUserId))
                    .findFirst()
                    .orElse(null);
            return otherUserId != null ? otherUserId : "Unknown User";
        }
        if (firstMessage != null) {
            return firstMessage.getSenderId() + " and others";
        }
        return "Unnamed Conversation";
    }

    public PageResponse<MessagingResponse> getMessagesForConversation(String conversationId, int page, int size) {
        Sort sort = Sort.by("timestamp").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = messagingRepository.findByConversationId(conversationId, pageable);
        log.info("pageData {}", pageData);
        return PageResponse.<MessagingResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent()
                        .stream()
                        .map(message -> {
                                        var messageResponse = messagingMapper.toMessagingResponse(message);
                                        messagingRepository.findById(messageResponse.getId()).orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_EXISTED));
                                        conversationRepository.findById(messageResponse.getConversationId()).orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));
                                        UserResponse userResponse = getUserInformationBasic(message.getSenderId(), generationToken());
                                        if (userResponse == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
                                        return messageResponse;
                        }).collect(Collectors.toList()))
                .build();
    }

    public MessagingResponse getLastMessage(String conversationId) {
        Message lastMessage = messagingRepository.findFirstByConversationIdOrderByTimestampDesc(conversationId);
        return lastMessage != null ? messagingMapper.toMessagingResponse(lastMessage) : new MessagingResponse(); // Return an empty response instead of null
    }

    public void addReaction(String messageId, String userId, String reaction) {
        Message message = messagingRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_EXISTED));

        if (message.getReactions() == null) {
            message.setReactions(new HashMap<>());
        }
        message.getReactions().put(userId, reaction);
        messagingRepository.save(message);
    }

    public void removeReaction(String messageId, String userId) {
        Message message = messagingRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_EXISTED));

        if (message.getReactions() != null) {
            message.getReactions().remove(userId);
            messagingRepository.save(message);
        }
    }

    public String generationToken() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");
        ApiResponse<AuthenticationResponse> getToken = identityClient.getToken(authenticationRequest);
        return getToken.getResult().getToken();
    }
    private UserResponse getUserInformationBasic(String userId, String token) {
        ApiResponse<UserResponse> userResponse = identityClient.getUserInformationBasic(userId, "Bearer " + token);
        return userResponse.getResult();
    }
}
