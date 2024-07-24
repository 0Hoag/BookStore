package com.example.identityservice.dto.request.vn_pay;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateVNPayInfoRequest {
    String orderId;
    String vnpTxnRef;
    String vnpOrderInfo;
    BigDecimal vnpAmount;
    String vnpResponseCode;
    String vnpTransactionNo;
    String vnpPayDate;
    String vnpTransactionStatus;
}
