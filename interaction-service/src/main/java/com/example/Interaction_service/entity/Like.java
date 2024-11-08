package com.example.Interaction_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "like")
public class Like {
    @Id
    @Column(name = "like_id")
    String likeId;

    @Column(name = "user_id")
    String userId;

    LocalDateTime createdAt; // Ngày thích bài viết

    @JoinColumn(name = "post_id")
    String post; // ID của bài viết mà bình thích thuộc về
}
