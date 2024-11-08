package com.example.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.identityservice.dto.request.response.GithubUserResponse;

@FeignClient(name = "github-user-client", url = "https://api.github.com")
public interface OutboundGithubUserClient {
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    GithubUserResponse getUser(@RequestHeader("Authorization") String bearerToken);
}
