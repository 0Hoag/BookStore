package com.example.friend_service.dto.response;

import java.time.LocalDateTime;

import com.example.friend_service.enums.RelationShip;

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
public class FriendShipResponse {
    String friendshipId;

    String userId1;
    String userId2;

    LocalDateTime since;
    RelationShip relationShip;
}
