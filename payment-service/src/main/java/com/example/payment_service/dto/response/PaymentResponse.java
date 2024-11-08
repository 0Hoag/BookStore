package com.example.payment_service.dto.response;

import java.time.LocalDateTime;

import com.example.payment_service.entity.enumclass.PaymentMethod;
import com.example.payment_service.entity.enumclass.PaymentStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String paymentId;
    OrdersResponse orderId;
    String userId;

    PaymentMethod paymentMethod;

    String bank;
    String accountNumber;
    String bankName;
    String accountHolderName;
    String transactionId;
    double amount;

    PaymentStatus paymentStatus;

    LocalDateTime paymentDate;
    LocalDateTime dueDate;
    String currency;
    String notes;
}
