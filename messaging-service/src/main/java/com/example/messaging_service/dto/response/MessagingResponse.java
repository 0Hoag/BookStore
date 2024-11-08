package com.example.messaging_service.dto.response;

import java.time.Instant;
import java.util.Map;

import com.example.messaging_service.entity.enums.DeliveryStatus;
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
public class MessagingResponse {
    String id;
    String conversationId;
    String senderId;
    String content;
    Instant timestamp;
    boolean isRead;
    MessageType messageType;
    String attachmentUrl;
    String replyToId;
    DeliveryStatus deliveryStatus;
    Map<String, String> reactions;
}
