package com.example.notification.dto.response;

import java.time.LocalDateTime;

import com.example.notification.entity.enums.NotificationType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartNotificationResponse {
    String notificationId;
    String userId;
    String bookId;
    String bookTitle;
    String bookImage;
    double price;
    int quantity;
    NotificationType type;
    LocalDateTime timestamp;
    String message;
    boolean idRead;
}
