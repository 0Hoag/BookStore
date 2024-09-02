package com.example.post_service.entity;

import com.example.post_service.dto.response.CommentResponse;
import com.example.post_service.dto.response.LikeResponse;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "post")
public class Post {
    @MongoId
    @Column(name = "post_id")
    String postId;

    @Property(value = "user_id")
    String userId;

    String content;

    @Column(name = "image_urls")
    Set<String> imageUrls;

    @Column(name = "video_urls")
    Set<String> videoUrls;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "update_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<LikeResponse> likes; // Danh sách ID của người dùng đã thích bài viết

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Set<CommentResponse> comments; // Danh sách ID của người dùng đã comment bài viết

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", imageUrls='" + imageUrls + '\'' +
                ", videoUrls='" + videoUrls + '\'' +
                ", createdAt=" + createdAt + '\'' +
                ", updatedAt=" + updatedAt + '\'' +
                ", likes=" + (likes != null ? likes.size() : 0) +
                ", comments=" + (comments != null ? comments.size() : 0) +
                '}';
    }
}
