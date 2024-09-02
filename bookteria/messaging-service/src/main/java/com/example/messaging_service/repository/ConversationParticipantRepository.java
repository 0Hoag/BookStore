package com.example.messaging_service.repository;

import com.example.messaging_service.dto.identity.UserResponse;
import com.example.messaging_service.dto.response.ConversationParticipantResponse;
import com.example.messaging_service.dto.response.ConversationResponse;
import com.example.messaging_service.entity.Conversation;
import com.example.messaging_service.entity.ConversationParticipant;
import com.example.messaging_service.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends MongoRepository<ConversationParticipant, String> {
    List<ConversationParticipant> findByConversationId(String conversationId);

    List<ConversationParticipant> findByUserId(String userId);

    void deleteByConversationIdAndUserId(String conversationId, String userId);
}
