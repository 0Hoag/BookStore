package com.example.messaging_service.dto.response;

import com.example.messaging_service.dto.identity.UserResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

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
