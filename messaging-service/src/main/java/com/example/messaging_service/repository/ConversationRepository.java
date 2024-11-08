package com.example.messaging_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.messaging_service.entity.Conversation;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    @Query("{'participantIds': { $all: ?0 } }") // choose values participantIds and we can check if it existed
    List<Conversation> findByParticipantIdContainingBoth(List<String> participantIds);

    @Query("{'participantIds': ?0}")
    Page<Conversation> findByParticipantIdsContaining(String userId, Pageable pageable);

    // if you need a method get value it not pagination
    List<Conversation> findByParticipantIdsContaining(String userId);
}
