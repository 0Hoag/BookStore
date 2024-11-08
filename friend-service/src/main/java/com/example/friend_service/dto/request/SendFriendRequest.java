package com.example.friend_service.dto.request;

import com.example.friend_service.enums.Condition;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendFriendRequest {
    String senderId; // user send to

    String receiverId;

    Condition condition; // status
}
