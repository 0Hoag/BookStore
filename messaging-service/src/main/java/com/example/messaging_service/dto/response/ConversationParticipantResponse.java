package com.example.messaging_service.dto.response;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationParticipantResponse {
    String id;
    String conversationId;
    String userId;
    Instant joinedAt;
    String lastReadMessageId;
}
