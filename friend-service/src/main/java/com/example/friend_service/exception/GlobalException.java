package com.example.friend_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.friend_service.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalException {
    private static final String MIN_ATTRIBUTES = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handRuntimeException(RuntimeException exception) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZE_EXCEPTION;
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlRuntimeException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    //    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    //    ResponseEntity<ApiResponse> hanlMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    //        String enumKey = exception.getFieldError().getDefaultMessage();
    //
    //        if (enumKey.isEmpty()) {
    //            throw new NullPointerException();
    //        }
    //
    //        ErrorCode errorCode = ErrorCode.INVALID_KEY;
    //        Map<String, Object> attributes = null;
    //        try {
    //            errorCode = ErrorCode.valueOf(enumKey);
    //
    //            var constrainViolation =
    //                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
    //
    //            attributes = constrainViolation.getConstraintDescriptor().getAttributes();
    //
    //            log.info(attributes.toString());
    //
    //        } catch (IllegalArgumentException e) {
    //            throw new IllegalArgumentException();
    //        }
    //
    //        ApiResponse apiResponse = new ApiResponse();
    //
    //        apiResponse.setCode(errorCode.getCode());
    //        apiResponse.setMessage(
    //                Objects.nonNull(attributes)
    //                        ? mapAttribute(errorCode.getMessage(), attributes)
    //                        : errorCode.getMessage());
    //
    //        return ResponseEntity.badRequest().body(apiResponse);
    //    }
    //
    //    private String mapAttribute(String message, Map<String, Object> attributes) {
    //        String minValues = String.valueOf(attributes.get(MIN_ATTRIBUTES));
    //        return message.replace("{" + MIN_ATTRIBUTES + "}", minValues);
    //    }
    //
    //    @ExceptionHandler(value = AccessDeniedException.class)
    //    ResponseEntity<ApiResponse> responseEntity(AccessDeniedException exception) {
    //        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
    //        return ResponseEntity.status(errorCode.getStatusCode())
    //                .body(ApiResponse.builder()
    //                        .code(errorCode.getCode())
    //                        .message(errorCode.getMessage())
    //                        .build());
    //    }
}
