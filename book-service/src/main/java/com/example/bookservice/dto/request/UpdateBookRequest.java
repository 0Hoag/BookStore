package com.example.bookservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBookRequest {
    String bookTitle;
    String author;
    String description;
    String listedPrice;
    String price;
    String quantity;
}
