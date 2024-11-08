package com.example.bookservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookRequest {
    String bookTitle;
    String author;
    double listedPrice;
    double price;
    double quantity;
    String description;
    String image;
}
