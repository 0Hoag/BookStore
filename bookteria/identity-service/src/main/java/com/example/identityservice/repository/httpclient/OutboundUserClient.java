package com.example.identityservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.identityservice.dto.request.response.OuboundUserResponse;

@FeignClient(name = "outbound-user-client", url = "YOU_URL")
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    OuboundUserResponse getUser(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
