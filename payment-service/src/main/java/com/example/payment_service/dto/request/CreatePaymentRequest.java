package com.example.payment_service.dto.request;

import com.example.payment_service.entity.enumclass.PaymentMethod;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
    String orderId;
    String userId;

    PaymentMethod paymentMethod; // Thêm trường này

    String bank;
    String accountNumber;
    String bankName;
    String accountHolderName;

    double amount;

    String currency;
    String notes; // Thêm trường này
}
