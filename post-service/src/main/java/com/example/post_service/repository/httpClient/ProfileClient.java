package com.example.post_service.repository.httpClient;

import com.example.post_service.dto.response.ApiResponse;
import com.example.post_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "profile-service",
        url = "http://localhost:8081/profile"
)
public interface ProfileClient {
    @GetMapping(value = "/users/{profileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String profileId, @RequestHeader("Authorization") String token);
}
