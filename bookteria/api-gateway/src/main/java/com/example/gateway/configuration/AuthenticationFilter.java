package com.example.gateway.configuration;

import com.example.gateway.dto.response.ApiResponse;
import com.example.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEnpoint = {
            "/identity/auth/.*",
            "/identity/users/registration",
            "/profile/register", //update 30/07
            "/identity/cartItem/registration",
            "/identity/selectProduct/registration",
            "/identity/order/registration",
            "/notification/email/send",
            "/book/registration",
            "/book/registration/ManyChapter",
            "/book/truyen/registration",
            "/friend/Request/Registration",
            "/friend/ship/Registration",
            "/friend/blockList/registration",
            "/post/registration",
            "/post/my-posts",
            "/comment/comments/registration",
            "/interaction/like/registration",
            "/messaging/mess/registration",
            "/messaging/messenger/registration",

            //KeyCloak
            "/profile/.*",
    };

    @NonFinal
    @Value("${app.api-prefix}")
    private String apiPrefix;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter....");

        if (isPulicEnpoint(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        // Get token from authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unAuthenticated(exchange.getResponse());

        String token = authHeader.getFirst().replace("Bearer", "");
        log.info("Token: {}", token);

        //Check authentiated
        return identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
            if (introspectResponseApiResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unAuthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unAuthenticated(exchange.getResponse()));
    }

    public boolean isPulicEnpoint(ServerHttpRequest request) {
        return Arrays.stream(publicEnpoint)
                .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
    }

    @Override
    public int getOrder() {
        return -1; //you input -1 it start first all
    }

    Mono<Void> unAuthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                        .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
