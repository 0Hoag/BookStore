package com.example.identityservice.dto.request.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {
    String bookId;

    String bookTitle;
    String author;
    double listedPrice;
    double price;
    double quantity;
    String description;
    String image;
}
