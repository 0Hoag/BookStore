package com.example.identityservice.dto.request;

import com.example.identityservice.enums.NotificationType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartNotificationRequest {
    String userId;
    String bookId;
    String bookTitle;
    String bookImage;
    double price;
    int quantity;
    NotificationType type;
    boolean idRead;
}
