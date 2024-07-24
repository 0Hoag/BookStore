package com.example.post_service.dto.response;

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
    String description;
    String listedPrice;
    String price;
    String quantity;
}
