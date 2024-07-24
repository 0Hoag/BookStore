package com.example.post_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class PostStatusBook {
        @Id
        @GeneratedValue(generatorClass = UUIDStringGenerator.class)
        String postId;
        @Property(value = "profileId")
        String profileId;

        String activityType;
        String activityContent;
        LocalDate createdAt;
        @Property(value = "bookId")
        String bookId;
    }
