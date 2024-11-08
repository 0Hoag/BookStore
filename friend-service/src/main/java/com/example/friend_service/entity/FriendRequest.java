package com.example.friend_service.entity;

import jakarta.persistence.Column;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.example.friend_service.enums.Condition;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "friendRequest")
public class FriendRequest {
    @MongoId
    @Column(name = "request_id")
    String requestId;

    @Column(name = "sender_id")
    String senderId; // user send to

    @Column(name = "receiver_id")
    String receiverId;

    Condition condition; // status
}
