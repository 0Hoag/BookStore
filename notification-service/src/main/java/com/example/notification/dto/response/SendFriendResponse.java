package com.example.notification.dto.response;

import com.example.notification.entity.enums.Condition;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendFriendResponse {
    String sendId;

    String senderId; // user send to

    String receiverId;

    Condition condition; // status
}
