package com.example.messaging_service.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    private String id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastMessageAt;
    private List<String> participantIds;
    private MessagingResponse lastMessage;
}
