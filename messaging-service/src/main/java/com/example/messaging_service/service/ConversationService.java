package com.example.messaging_service.service;

import com.example.messaging_service.dto.identity.UserResponse;
import com.example.messaging_service.dto.request.CreateConversationRequest;
import com.example.messaging_service.dto.request.UpdateConversationRequest;
import com.example.messaging_service.dto.response.ConversationResponse;
import com.example.messaging_service.entity.Conversation;
import com.example.messaging_service.entity.ConversationParticipant;
import com.example.messaging_service.exception.AppException;
import com.example.messaging_service.exception.ErrorCode;
import com.example.messaging_service.mapper.ConversationMapper;
import com.example.messaging_service.repository.ConversationParticipantRepository;
import com.example.messaging_service.repository.ConversationRepository;
import com.example.messaging_service.repository.httpClient.IdentityClient;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ConversationService {
    ConversationMapper conversationMapper;
    ConversationRepository conversationRepository;
    ConversationParticipantRepository participantRepository;
    MessagingService messagingService;
    IdentityClient identityClient;

    public ConversationResponse createConversation(CreateConversationRequest request) {
        List<Conversation> existedParticipantIdBoth = conversationRepository.findByParticipantIdContainingBoth(request.getParticipantIds());

        for (Conversation conversation: existedParticipantIdBoth) {
            if (conversation.getParticipantIds().size() == 2) {
                return conversationMapper.toConversationResponse(conversation);
            }
        }

        var convers = conversationMapper.toConversation(request);
        conversationRepository.save(convers);

        convers.setCreateAt(Instant.now());
        convers.setUpdateAt(Instant.now());
        convers.setLastMessageAt(Instant.now());

        log.info("convers: {}", convers);
        request.getParticipantIds().forEach(userId -> {
            ConversationParticipant participant = new ConversationParticipant();
            participant.setConversationId(convers.getId());
            participant.setUserId(userId);
            participant.setJoinedAt(Instant.now());
            participantRepository.save(participant);
        });

        return conversationMapper.toConversationResponse(convers);
    }

    public List<ConversationResponse> getUserConversations(String userId) {
        List<String> conversationIds = participantRepository.findByUserId(userId)
                .stream()
                .map(ConversationParticipant::getConversationId)
                .collect(Collectors.toList());
        log.info("conversationIds: {}", conversationIds);

        List<Conversation> conversations = conversationRepository.findAllById(conversationIds);

        return conversations.stream()
                .map(conversation -> {
                    var conver = conversationMapper.toConversationResponse(conversation);
                    conver.setLastMessage(messagingService.getLastMessage(conversation.getId()));
                    return conver;
                })
                .sorted(Comparator.comparing(ConversationResponse::getLastMessageAt).reversed())
                .collect(Collectors.toList());
    }

    public ConversationResponse updateConversation(String conversationId, UpdateConversationRequest request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));

        conversation.setName(request.getName());
        conversation.setUpdateAt(Instant.now());

        Conversation updatedConversation = conversationRepository.save(conversation);
        return conversationMapper.toConversationResponse(updatedConversation);
    }

    public void addParticipant(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));

        if (!conversation.getParticipantIds().contains(userId)) {
            conversation.getParticipantIds().add(userId);
            conversationRepository.save(conversation);

            ConversationParticipant participant = new ConversationParticipant();
            participant.setConversationId(conversationId);
            participant.setUserId(userId);
            participant.setJoinedAt(Instant.now());
            participantRepository.save(participant);
        }
    }

    public void removeParticipant(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_EXISTED));

        if (conversation.getParticipantIds().contains(userId)) {
            conversation.getParticipantIds().remove(userId);
            conversationRepository.save(conversation);

            participantRepository.deleteByConversationIdAndUserId(conversationId, userId);
        }
    }
}
