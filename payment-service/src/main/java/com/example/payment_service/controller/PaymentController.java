package com.example.payment_service.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.example.payment_service.dto.request.CreatePaymentRequest;
import com.example.payment_service.dto.request.UpdatePaymentRequest;
import com.example.payment_service.dto.response.ApiResponse;
import com.example.payment_service.dto.response.PaymentResponse;
import com.example.payment_service.service.PaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @PostMapping("/registration")
    public ApiResponse<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .code(1000)
                .result(paymentService.createPayment(request))
                .build();
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable String paymentId) {
        return ApiResponse.<PaymentResponse>builder()
                .code(1000)
                .result(paymentService.getPayment(paymentId))
                .build();
    }

    @GetMapping("/getAllPayment")
    public ApiResponse<Set<PaymentResponse>> getAllPayment() {
        return ApiResponse.<Set<PaymentResponse>>builder()
                .code(1000)
                .result(paymentService.getAllPayment())
                .build();
    }

    @GetMapping("/getPaymentByUser/{userId}")
    public ApiResponse<Set<PaymentResponse>> getPaymentByUser(@PathVariable String userId) {
        return ApiResponse.<Set<PaymentResponse>>builder()
                .code(1000)
                .result(paymentService.getPaymenByUserId(userId))
                .build();
    }

    @DeleteMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> deletePayment(@PathVariable String paymentId) {
        paymentService.deletePayment(paymentId);
        return ApiResponse.<PaymentResponse>builder()
                .code(1000)
                .message("Delete payment Success fully")
                .build();
    }

    @PutMapping("/updatePayment/{paymentId}")
    public ApiResponse<PaymentResponse> updatePayment(
            @PathVariable String paymentId, @RequestBody UpdatePaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .code(1000)
                .result(paymentService.updatePayment(paymentId, request))
                .build();
    }
}
