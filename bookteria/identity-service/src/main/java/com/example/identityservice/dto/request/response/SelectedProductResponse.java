package com.example.identityservice.dto.request.response;

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
