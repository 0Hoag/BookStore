package com.example.friend_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

import com.example.friend_service.enums.RelationShip;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "friendShip")
public class FriendShip {
    @Id
    @Column(name = "friendship_id")
    String friendshipId;

    String userId1;
    String userId2;

    LocalDateTime since;
    RelationShip relationShip;
}
