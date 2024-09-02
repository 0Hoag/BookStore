package com.example.messaging_service.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("conversationparticipant")
public class ConversationParticipant {
    @MongoId
    String id;
    String conversationId;
    String userId;
    Instant joinedAt;
    String lastReadMessageId;
}
