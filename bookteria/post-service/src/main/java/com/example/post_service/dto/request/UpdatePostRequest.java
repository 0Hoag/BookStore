package com.example.post_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePostRequest {
    String content;
    Set<String> imageUrls;
    Set<String> videoUrls;
}
