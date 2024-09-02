package com.example.friend_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRelationshipInfo {
    List<FriendResponse> sentRequests;
    List<FriendResponse> receivedRequests;
    List<FriendShipResponse> friendships;
    List<BlockListResponse> blockedUsers;
    List<BlockListResponse> blockedByUsers;
}
