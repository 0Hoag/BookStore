package com.example.messaging_service.entity;

import java.time.Instant;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.example.messaging_service.entity.enums.DeliveryStatus;
import com.example.messaging_service.entity.enums.MessageType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Document("messaging")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @MongoId
    String id;

    @Column(name = "conversation_id", nullable = false)
    String conversationId;

    @Column(name = "sender_id", nullable = false)
    String senderId;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(nullable = false)
    Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    MessageType messageType;

    @Column(name = "attachment_url")
    String attachmentUrl;

    @Column(name = "reply_to_id")
    Long replyToId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    DeliveryStatus deliveryStatus;

    Map<String, String> reactions;
}
