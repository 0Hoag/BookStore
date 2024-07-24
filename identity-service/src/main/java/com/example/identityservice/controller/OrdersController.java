package com.example.identityservice.controller;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.OrdersRequest.AddSelectedProductWithOrdersWithUserRequest;
import com.example.identityservice.dto.request.OrdersRequest.CreateOrdersRequest;
import com.example.identityservice.dto.request.OrdersRequest.RemoveSelectedProductWithOrdersWithUserRequest;
import com.example.identityservice.dto.request.OrdersRequest.UpdateOrdersRequest;
import com.example.identityservice.dto.request.response.OrdersResponse.OrdersResponse;
import com.example.identityservice.dto.request.vn_pay.VNPayDTO;
import com.example.identityservice.entity.Orders;
import com.example.identityservice.mapper.OrdersMapper;
import com.example.identityservice.service.OrdersService;
import com.example.identityservice.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersController {
    OrdersService ordersService;
    OrdersMapper ordersMapper;
    VNPayService vnPayService;

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<OrdersResponse>> createOrders(@RequestBody CreateOrdersRequest request, HttpServletRequest httpRequest) {
        Orders orders = ordersService.createOrders(request);
        OrdersResponse orderResponse = ordersMapper.toOrdersResponse(orders);


        if ("VNPAY".equals(request.getPaymentMethod())) {
            String idAddress = vnPayService.getClientIpAddress(httpRequest);
            VNPayDTO vnPayDTO = vnPayService.createPaymentUrl(orders, idAddress);

            //set url payment OrderResponse
            orderResponse.setPaymentUrl(vnPayDTO.getPaymentUrl());
        }

        return ResponseEntity.ok(ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(orderResponse)
                .build());
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrdersResponse> getOrders(@PathVariable String orderId) {
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(ordersService.getOrders(orderId))
                .build();
    }

    @GetMapping("/getAllOrders")
    public ApiResponse<Set<OrdersResponse>> getAllOrders() {
        return ApiResponse.<Set<OrdersResponse>>builder()
                .code(1000)
                .result(ordersService.getAllOrders())
                .build();
    }

    @PutMapping("/updateOrder/{orderId}")
    public ApiResponse<OrdersResponse> updateOrder(@PathVariable String orderId, @RequestBody UpdateOrdersRequest request) {
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .result(ordersService.updateOrders(orderId,request))
                .build();
    }

    @PostMapping("/addSelectedProductWithOrdersWithUser/{userId}")
    public ApiResponse<Void> addSelectedProductWithOrdersWithUser(@PathVariable String userId, @RequestBody AddSelectedProductWithOrdersWithUserRequest request) {
        ordersService.addSelectedProductWithOrdersWithUser(userId, request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Add SelectedProductWithOrdersWithUser Success")
                .build();
    }

    @DeleteMapping("/removeSelectedProductWithOrdersWithUser/{userId}")
    public ApiResponse<Void> removeSelectedProductWithOrdersWithUser(@PathVariable String userId, @RequestBody RemoveSelectedProductWithOrdersWithUserRequest request) {
        ordersService.removeSelectedProductWithOrdersWithUser(userId, request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Remove SelectedProductWithOrdersWithUser Success")
                .build();
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<OrdersResponse> deleteOrders(@PathVariable String orderId) {
        ordersService.deleteOrders(orderId);
        return ApiResponse.<OrdersResponse>builder()
                .code(1000)
                .message("Delete Orders Success")
                .build();
    }
}
