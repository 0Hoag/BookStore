package com.example.comment_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "comment")
public class Comment {
    @Id
    @Column(name = "comment_id")
    String commentId;

    @Column(name = "user_id")
    String userId; // ID của người dùng đã bình luận

    String content;
    LocalDateTime createdAt;
    String post; // ID của bài viết mà bình luận thuộc về
}
