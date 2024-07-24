package com.example.payment_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SelectedProductResponse {
    String selectedId;
    int quantity;
    BookResponse bookId;
}
