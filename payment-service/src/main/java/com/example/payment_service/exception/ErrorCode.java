package com.example.payment_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZE_EXCEPTION(9999, "UNCATEGORIZE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    POST_NOT_EXISTED(1001, "POSTID_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1002, "YOU AGE MUST BE AT least {min}", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1003, "INVALID MESSAGE KEY", HttpStatus.BAD_REQUEST),
    UNAUTHORIZATION(1004, "YOU DON'T HAVE PERMISSION", HttpStatus.FORBIDDEN),
    PAYMENT_NOT_EXISTED(1005, "PAYMENT_NOT_EXISTED", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    ErrorCode() {}
}
