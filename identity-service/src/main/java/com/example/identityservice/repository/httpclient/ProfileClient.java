package com.example.identityservice.repository.httpclient;

import java.util.List;

import com.example.identityservice.dto.request.response.ProfileExistedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.identityservice.configuration.AuthenticationRequestInterceptor;
import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.ProfileCreationRequest;
import com.example.identityservice.dto.request.response.UserProfileResponse;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserProfileResponse> createProfile(@RequestBody ProfileCreationRequest request);

    @DeleteMapping(value = "/users/userId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteProfileUserId(@PathVariable String userId);

    @DeleteMapping(value = "/users/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    List<UserProfileResponse> deleteAllProfile();

    @GetMapping(value = "/users/existed/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileExistedResponse profileExists(@PathVariable String userId);
}

