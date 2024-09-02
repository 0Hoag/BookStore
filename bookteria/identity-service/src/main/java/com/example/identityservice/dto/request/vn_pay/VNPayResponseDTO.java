package com.example.identityservice.dto.request.vn_pay;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayResponseDTO {
    String txnRef; // Mã tham chiếu giao dịch
    String orderInfo; // Thông tin đơn hàng
    String responseCode; // Mã phản hồi từ VNPay
    String transactionNo; // Mã giao dịch VNPay
    String payDate; // Ngày thanh toán
    String transactionStatus; // Trạng thái giao dịch
}
