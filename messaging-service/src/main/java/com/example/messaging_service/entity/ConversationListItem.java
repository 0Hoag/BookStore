package com.example.messaging_service.entity;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
