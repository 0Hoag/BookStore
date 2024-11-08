package com.example.friend_service.dto.response;

import com.example.friend_service.enums.Condition;

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
