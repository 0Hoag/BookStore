package com.example.messaging_service.dto.request;

import com.example.messaging_service.entity.enums.MessageType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMessagingRequest {
    String conversationId;
    String senderId;
    String content;
    MessageType messageType;
    String attachmentUrl;
    Long replyToId;
}
