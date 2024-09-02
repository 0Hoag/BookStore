package com.example.messaging_service.service;

import com.example.messaging_service.dto.response.ConversationParticipantResponse;
import com.example.messaging_service.dto.response.ConversationResponse;
import com.example.messaging_service.entity.Conversation;
import com.example.messaging_service.entity.ConversationParticipant;
import com.example.messaging_service.exception.AppException;
import com.example.messaging_service.exception.ErrorCode;
import com.example.messaging_service.mapper.ConversationMapper;
import com.example.messaging_service.mapper.ConversationParticipantMapper;
import com.example.messaging_service.repository.ConversationParticipantRepository;
import com.example.messaging_service.repository.ConversationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationParticipantService {
    ConversationRepository conversationRepository;
    ConversationParticipantRepository conversationParticipantRepository;

    public List<ConversationParticipant> getParticipantsForConversation(String conversationId) {
        return conversationParticipantRepository.findByConversationId(conversationId);
    }
}
