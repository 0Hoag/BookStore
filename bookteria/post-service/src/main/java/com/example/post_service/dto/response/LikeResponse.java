package com.example.post_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikeResponse {
    String likeId;
    String userId;
    LocalDateTime createdAt; // Ngày thích bài viết
}
