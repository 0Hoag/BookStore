package com.example.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.identityservice.dto.request.ExchangeGithubTokenRequest;
import com.example.identityservice.dto.request.response.ExchangeGithubTokenResponse;

import feign.QueryMap;

@FeignClient(name = "github-identity", url = "https://github.com")
public interface OutboundGithubIdentityClient {
    @PostMapping(
            value = "/login/oauth/access_token",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ExchangeGithubTokenResponse exchangeToken(@QueryMap ExchangeGithubTokenRequest request);
}
