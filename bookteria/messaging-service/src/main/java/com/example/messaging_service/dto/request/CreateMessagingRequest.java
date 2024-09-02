package com.example.messaging_service.dto.request;

import com.example.messaging_service.entity.Message;
import com.example.messaging_service.entity.Message;
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
public class CreateMessagingRequest {
    String conversationId;
    String senderId;
    String content;
    Message.MessageType messageType;
    String attachmentUrl;
    Long replyToId;
}
