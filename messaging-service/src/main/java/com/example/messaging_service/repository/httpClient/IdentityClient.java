package com.example.messaging_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.messaging_service.dto.identity.AuthenticationRequest;
import com.example.messaging_service.dto.identity.AuthenticationResponse;
import com.example.messaging_service.dto.identity.UserResponse;
import com.example.messaging_service.dto.response.ApiResponse;

@FeignClient(name = "identity-service", url = "http://localhost:7777/identity")
public interface IdentityClient {
    @GetMapping(value = "/auth/token", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<AuthenticationResponse> getToken(@RequestBody AuthenticationRequest request);

    @GetMapping(value = "/users/getUserInformationBasic/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserResponse> getUserInformationBasic(
            @PathVariable("userId") String userId, @RequestHeader("Authorization") String token);
}
