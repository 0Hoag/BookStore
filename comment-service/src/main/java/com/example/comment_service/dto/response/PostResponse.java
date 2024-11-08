package com.example.comment_service.dto.response;

import java.time.Instant;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String postId;
    String userId;
    String content;
    String created;
    Instant createdAt;
    Instant updatedAt;
    Set<LikeResponse> likes; // Danh sách ID của người dùng đã thích bài viết
    Set<CommentResponse> comments; // Danh sách ID của người dùng đã comment bài viết
}
