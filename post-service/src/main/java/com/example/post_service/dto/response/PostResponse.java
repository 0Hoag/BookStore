package com.example.post_service.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import com.example.post_service.entity.MediaMetadata;

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
    Set<String> medias;
    Map<String, MediaMetadata> mediaMetadata;
    String created;
    Instant createdAt;
    Instant updatedAt;
    Set<LikeResponse> likes; // Danh sách ID của người dùng đã thích bài viết
    Set<CommentResponse> comments; // Danh sách ID của người dùng đã comment bài viết
}
