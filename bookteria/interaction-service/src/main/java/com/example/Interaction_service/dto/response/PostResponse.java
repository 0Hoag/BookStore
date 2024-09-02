package com.example.Interaction_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String postId;
    UserResponse userId;
    String content;
    Set<String> imageUrls;
    Set<String> videoUrls;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Set<LikeResponse> likes; // Danh sách ID của người dùng đã thích bài viết
    Set<CommentResponse> comments; // Danh sách ID của người dùng đã comment bài viết
}
