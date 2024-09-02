package com.example.messaging_service.dto.response;

import com.example.messaging_service.entity.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

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
    Message.MessageType messageType;
    String attachmentUrl;
    String replyToId;
    Message.DeliveryStatus deliveryStatus;
    Map<String, String> reactions;
}
