package com.example.post_service.dto.request;

import com.example.post_service.entity.MediaMetadata;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

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
