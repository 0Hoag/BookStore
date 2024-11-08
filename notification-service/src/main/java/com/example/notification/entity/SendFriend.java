package com.example.notification.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.example.notification.entity.enums.Condition;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document("send_friend")
public class SendFriend {
    @MongoId
    @Field(name = "send_id")
    String sendId;

    @Field(name = "sender_id")
    String senderId; // user send to

    @Field(name = "receiver_id")
    String receiverId;

    Condition condition; // status
}
