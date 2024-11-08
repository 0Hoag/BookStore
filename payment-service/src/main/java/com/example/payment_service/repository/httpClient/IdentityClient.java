package com.example.payment_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.payment_service.dto.request.AuthenticationRequest;
import com.example.payment_service.dto.response.*;

@FeignClient(name = "identity-service", url = "http://localhost:7777/identity")
public interface IdentityClient {
    @GetMapping(value = "/auth/token", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<AuthenticationResponse> getToken(@RequestBody AuthenticationRequest request);

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getUser(
            @PathVariable("userId") String userId, @RequestHeader("Authorization") String token);

    @GetMapping(value = "/selectProduct/{SelectedId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<SelectedProductResponse> getSelectedProduct(
            @PathVariable("SelectedId") String SelectedId, @RequestHeader("Authorization") String token);

    @GetMapping(value = "/order/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<OrdersResponse> getOrder(
            @PathVariable("orderId") String orderId, @RequestHeader("Authorization") String token);
}
