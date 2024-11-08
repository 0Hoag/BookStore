package com.example.post_service.entity;

import java.util.Set;

import jakarta.persistence.Column;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "tag")
public class Tag {
    @Id
    @Column(name = "tag_id")
    String tagId;

    String name;

    Set<Post> postId;
}
