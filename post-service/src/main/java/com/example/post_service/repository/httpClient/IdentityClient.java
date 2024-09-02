package com.example.post_service.repository.httpClient;

import com.example.post_service.dto.request.AuthenticationRequest;
import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.AuthenticationResponse;
import com.example.post_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient
        (
                name = "identity-service",
                url = "http://localhost:7777/identity"
        )
public interface IdentityClient {
        @GetMapping(value = "/auth/token", produces = MediaType.APPLICATION_JSON_VALUE)
        ApiResponse<AuthenticationResponse> getToken(@RequestBody AuthenticationRequest request);

        @GetMapping(value = "/users/getUserInformationBasic/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
        ApiResponse<UserResponse> getUserInformationBasic(@PathVariable("userId") String userId, @RequestHeader("Authorization") String token);
}
