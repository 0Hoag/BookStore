package com.example.friend_service.entity;

import com.example.friend_service.enums.Condition;
import com.example.friend_service.enums.Status;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "friendRequest")
public class FriendRequest {
    @Id
    @Column(name = "request_id")
    String requestId;

    @Column(name = "sender_id")
    String senderId; //user send to

    @Column(name = "receiver_id")
    String receiverId;

    Condition condition; // status
}
