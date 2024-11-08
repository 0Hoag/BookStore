package com.example.notification.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.example.notification.entity.enums.NotificationType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document("cart_notification")
public class CartNotification {
    @MongoId
    @Field(name = "notification_id")
    String notificationId;

    @Field(name = "user_id")
    String userId;

    @Field(name = "book_id")
    String bookId;

    @Field(name = "book_title")
    String bookTitle;

    @Field(name = "book_image")
    String bookImage;

    double price;
    int quantity;
    NotificationType type;

    @Field(name = "create_at")
    @CreatedDate
    LocalDateTime timestamp;

    @Field(value = "text")
    String message;

    @Field(name = "is_read")
    boolean idRead;

    public void generateMessage() {
        switch (type) {
            case ADD_TO_CART:
                this.message = String.format("Add %d copies of '%s' to cart", quantity, bookTitle);
                break;

            case REMOVE_FROM_CART:
                this.message = String.format("Remove '%s' form cart", bookTitle);
                break;

            case UPDATE_QUANTITY:
                this.message = String.format("Update quantity of '%s' to %d", bookTitle, quantity);
                break;

            case CLEAR_CART:
                this.message = "Cart has been cleared";
                break;

            default:
                this.message = "Cart has been modified";
        }
    }
}
