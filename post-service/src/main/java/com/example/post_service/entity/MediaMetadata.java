package com.example.post_service.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaMetadata {
    String type;
    String publicId;
    String originalUrl;
    String thumbnailUrl;
    Map<String, String> versions = new HashMap<>();
}
