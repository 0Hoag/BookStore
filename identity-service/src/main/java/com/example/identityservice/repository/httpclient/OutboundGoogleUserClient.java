package com.example.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.identityservice.dto.request.response.OuboundGoogleUserResponse;

@FeignClient(name = "outbound-user-client", url = "https://www.googleapis.com")
public interface OutboundGoogleUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OuboundGoogleUserResponse getUser(
            @RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
