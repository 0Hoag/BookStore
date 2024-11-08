package com.example.messaging_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.messaging_service.entity.ConversationParticipant;

@Repository
public interface ConversationParticipantRepository extends MongoRepository<ConversationParticipant, String> {
    List<ConversationParticipant> findByConversationId(String conversationId);

    List<ConversationParticipant> findByUserId(String userId);

    void deleteByConversationIdAndUserId(String conversationId, String userId);
}
