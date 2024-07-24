package com.example.payment_service.mapper;

import com.example.payment_service.dto.request.CreatePaymentRequest;
import com.example.payment_service.dto.request.UpdatePaymentRequest;
import com.example.payment_service.dto.response.OrdersResponse;
import com.example.payment_service.dto.response.PaymentResponse;
import com.example.payment_service.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPayment(CreatePaymentRequest request);

    PaymentResponse toPaymentResponse(Payment entity);

    // Thay đổi từ này
    void updatePayment(@MappingTarget Payment payment, UpdatePaymentRequest request);

    default OrdersResponse map(String orderId) {
        if (orderId == null) {
            return null;
        }
        return OrdersResponse.builder().orderId(orderId).build();
    }
}