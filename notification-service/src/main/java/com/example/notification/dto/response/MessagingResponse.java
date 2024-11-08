package com.example.notification.dto.response;

import java.time.Instant;
import java.util.Map;

import com.example.notification.entity.enums.DeliveryStatus;
import com.example.notification.entity.enums.MessageType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
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
