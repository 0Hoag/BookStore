package com.example.notification.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.notification.dto.ApiResponse;
import com.example.notification.dto.response.UserInformationBasicResponse;

@FeignClient(name = "identity-service", url = "${app.service.identity-service}")
public interface IdentityClient {
    @GetMapping(value = "/users/getUserInformationBasic/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserInformationBasicResponse> getUserInformationBasic(
            @PathVariable("userId") String userId, @RequestHeader("Authorization") String token);
}
