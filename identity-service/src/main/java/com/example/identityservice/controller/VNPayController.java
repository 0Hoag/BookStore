package com.example.identityservice.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.vn_pay.VNPayDTO;
import com.example.identityservice.dto.request.vn_pay.VNPayResponseDTO;
import com.example.identityservice.entity.Orders;
import com.example.identityservice.service.OrdersService;
import com.example.identityservice.service.VNPayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
@Slf4j
public class VNPayController {
    private final VNPayService vnPayService;
    private final OrdersService ordersService;

    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<VNPayDTO>> createPayment(
            @RequestParam String orderId, HttpServletRequest request) {
        log.info("Received create payment request for order: {}", orderId);
        try {
            Orders order = ordersService.getOrderId(orderId);
            String ipAddress = vnPayService.getClientIpAddress(request);
            VNPayDTO vnpayment = vnPayService.createPaymentUrl(order, ipAddress);

            ApiResponse<VNPayDTO> response =
                    ApiResponse.<VNPayDTO>builder().code(1000).result(vnpayment).build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment: ", e);
            ApiResponse<VNPayDTO> errorResponse = ApiResponse.<VNPayDTO>builder()
                    .code(5000)
                    .message("An error occurred while creating the payment: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/payment-return")
    public ResponseEntity<ApiResponse<VNPayResponseDTO>> handlePaymentReturn(
            @RequestParam Map<String, String> queryParams) {
        try {
            VNPayResponseDTO responseDTO = vnPayService.processReturnUrl(queryParams);

            boolean isSuccess =
                    "00".equals(responseDTO.getResponseCode()) && "00".equals(responseDTO.getTransactionStatus());
            String statusMessage = isSuccess ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";

            // Cập nhật thông tin thanh toán vào đơn hàng
            ordersService.updateVNPayResponse(responseDTO.getTxnRef(), responseDTO);

            ApiResponse<VNPayResponseDTO> response = ApiResponse.<VNPayResponseDTO>builder()
                    .code(isSuccess ? 1000 : 1001)
                    .result(responseDTO)
                    .message(statusMessage)
                    .build();

            if (statusMessage == "PAYMENT_FAILED") {
                ordersService.deleteOrders(responseDTO.getTxnRef());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing payment return: ", e);
            ApiResponse<VNPayResponseDTO> errorResponse = ApiResponse.<VNPayResponseDTO>builder()
                    .code(5000)
                    .message("An error occurred while processing the payment return: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
