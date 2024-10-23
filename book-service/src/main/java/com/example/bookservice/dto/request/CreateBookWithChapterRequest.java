package com.example.bookservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookWithChapterRequest {
    String bookTitle;
    String author;
    double listedPrice;
    double price;
    double quantity;
    String description;
    String image;

    Set<String> chapters;
}
