package com.example.payment_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePaymentRequest {
    String orderId;
    String userId;
    String bank;
    String accountNumber;
    String bankName;
    String accountHolderName;
    String transactionId;
    double amount;
    String status;
    LocalDateTime paymentDate;
    String paymentType;
    String currency;
}
