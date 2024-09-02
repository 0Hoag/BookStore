package com.devteria.profile.exception;

import com.devteria.profile.dto.identity.KeyCloakError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ErrorKeyCloakException {
    private final ObjectMapper objectMapper;
    private final Map<String, ErrorCode> errorCodeMap;

    public ErrorKeyCloakException() {
        objectMapper = new ObjectMapper();
        errorCodeMap = new HashMap<>();

        errorCodeMap.put("User exists with same email", ErrorCode.EMAIL_EXISTED);
        errorCodeMap.put("User name is missing", ErrorCode.USERNAME_IS_MISSING);
        errorCodeMap.put("User exists with same username", ErrorCode.USERNAME_EXISTED);
    }

    public AppException handleErrorKeyCloak(FeignException exception) {
        try {
            log.warn("Cannot complete request", exception);
            var response = objectMapper.readValue(exception.contentUTF8(), KeyCloakError.class);

            if (Objects.nonNull(response.getErrorMessage()) &&
                    Objects.nonNull(errorCodeMap.get(response.getErrorMessage()))) {
                return new AppException(errorCodeMap.get(response.getErrorMessage()));
            }
        }catch (JsonProcessingException e) {
            log.error("Cannot deserialize content", e);
        }

        return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
}
