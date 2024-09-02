package com.example.identityservice.dto.request.vn_pay;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayDTO {
    String txnRef; // Mã tham chiếu giao dịch
    String orderInfo; // Thông tin đơn hàng
    BigDecimal amount; // Số tiền giao dịch
    String paymentUrl; // URL thanh toán VNPay
}
