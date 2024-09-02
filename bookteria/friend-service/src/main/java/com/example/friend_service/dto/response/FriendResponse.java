package com.example.friend_service.dto.response;

import com.example.friend_service.enums.Condition;
import com.example.friend_service.enums.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendResponse {
    String requestId;
    String senderId; //userId
    String receiverId; //userId send Object user
    Condition condition; //default: PENDING
}
