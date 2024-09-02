package com.example.payment_service.entity;

import com.example.payment_service.entity.enumclass.PaymentMethod;
import com.example.payment_service.entity.enumclass.PaymentStatus;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    String paymentId;

    String orderId;
    String userId;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    String bank; // Có thể để null nếu là COD
    String accountNumber; // Có thể để null nếu là COD
    String bankName; // Có thể để null nếu là COD
    String accountHolderName; // Có thể để null nếu là COD
    String transactionId; // Có thể để null nếu là COD hoặc chưa thanh toán

    double amount;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    LocalDateTime paymentDate; // Ngày thanh toán thực tế
    LocalDateTime dueDate; // Ngày dự kiến thanh toán (cho COD)

    String currency;
    String notes;
}

