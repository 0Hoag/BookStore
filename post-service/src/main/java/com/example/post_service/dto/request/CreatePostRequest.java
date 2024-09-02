package com.example.post_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePostRequest {
//    String userId;
    String content;
    Set<String> imageUrls;
    Set<String> videoUrls;
    Set<String> likes; // Danh sách ID của người dùng đã thích bài viết
    Set<String> comments; // Danh sách ID của người dùng đã comment bài viết
}
