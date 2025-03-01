package com.example.post_service.entity;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaMetadata {
    String type;
    String publicId;
    String originalUrl;
    String thumbnailUrl;
    Map<String, String> versions = new HashMap<>();
}
