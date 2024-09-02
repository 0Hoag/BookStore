package com.example.gateway.service;

import com.example.gateway.dto.request.IntrospectRequest;
import com.example.gateway.dto.response.ApiResponse;
import com.example.gateway.dto.response.IntrospectResponse;
import com.example.gateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {
        return identityClient.introspectResponse(IntrospectRequest.builder()
                        .token(token)
                .build());
    }
}
