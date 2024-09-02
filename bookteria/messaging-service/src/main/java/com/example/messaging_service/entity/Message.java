package com.example.messaging_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Builder
@Document("messaging")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message  {
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

    @Column(name = "is_read")
    boolean isRead;

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

    Map<String, String> reactions; // miss in toString()
    public enum MessageType {
        TEXT, IMAGE, FILE, AUDIO, VIDEO
    }

    public enum DeliveryStatus {
        SENT, DELIVERED, READ
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", messageType=" + messageType +
                ", attachmentUrl='" + attachmentUrl + '\'' +
                ", replyToId=" + replyToId +
                ", deliveryStatus=" + deliveryStatus +
                ", reactions=" + reactions +
                '}';
    }
}

