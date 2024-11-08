package com.example.post_service.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "file")
public class File {
    @MongoId
    String fileId;

    String fileName;
    String fileType;
    Long fileSize;
    byte[] data;
    LocalDateTime createAt;
}
