package com.example.messaging_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("conversationlistitem")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationListItem {
    String id;
    String title;
    String lastMessageContent;
    Instant lastMessageTime;
    String lastMessageSenderId;
}
