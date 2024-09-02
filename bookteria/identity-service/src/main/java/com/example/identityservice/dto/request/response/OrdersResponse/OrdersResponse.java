package com.example.identityservice.dto.request.response.OrdersResponse;

import java.math.BigDecimal;
import java.util.Set;

import com.example.identityservice.dto.request.response.SelectedProductResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrdersResponse {
    String orderId;

    String fullName;
    String phoneNumber;
    String country;
    String city;
    String district;
    String ward;
    String address;
    String paymentMethod;

    String vnpTxnRef;
    String vnpOrderInfo;
    BigDecimal vnpAmount;
    String vnpResponseCode;
    String vnpTransactionNo;
    String vnpPayDate;
    String vnpTransactionStatus;

    Set<SelectedProductResponse> selectedProducts;

    String paymentUrl; // Thêm trường này cho URL thanh toán VNPay
}
