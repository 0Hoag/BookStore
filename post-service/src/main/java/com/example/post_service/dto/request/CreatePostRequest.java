package com.example.post_service.dto.request;

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
public class CreatePostRequest {
    //    String userId;
    String content;
    Set<String> medias;
    Map<String, MediaMetadata> mediaMetadata;
    Set<String> likes;
    Set<String> comments;
}
