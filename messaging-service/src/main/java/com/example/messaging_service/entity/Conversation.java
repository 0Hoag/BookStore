package com.example.messaging_service.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("conversation")
public class Conversation {
    String id;
    String name;
    Instant createAt;
    Instant updateAt;
    Instant lastMessageAt; // update
    List<String> participantIds; // update
}
