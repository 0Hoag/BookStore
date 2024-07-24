package com.example.post_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String postId;
    UserProfileResponse profile;
    String activityType;
    String activityContent;
    LocalDate createdAt;
    BookResponse book;
}
