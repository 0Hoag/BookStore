package com.example.bookservice.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "bookProfiles")
public class BookProfile {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String bookId;

    String bookTitle;
    String author;
    double listedPrice;
    double price;
    double quantity;
    String description;
    String image;

    @DBRef
    Set<Chapter> chapters;
}
