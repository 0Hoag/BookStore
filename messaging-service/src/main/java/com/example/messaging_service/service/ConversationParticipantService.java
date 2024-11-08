package com.example.messaging_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.messaging_service.entity.ConversationParticipant;
import com.example.messaging_service.repository.ConversationParticipantRepository;
import com.example.messaging_service.repository.ConversationRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

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
