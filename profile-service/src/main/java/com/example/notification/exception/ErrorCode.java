package com.example.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZE_EXCEPTION(9999, "UNCATEGORIZE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_NOT_EXISTED(1001, "USERID_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    PROFILE_EXISTED(1002, "PROFILE_EXISTED", HttpStatus.BAD_REQUEST),
    USER_EXITSTED(1002, "USER EXITED", HttpStatus.BAD_REQUEST),
    PROFILE_DELETEALL_EXISTED(1003, "PROFILE_DELETEALL_EXISTED", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1004, "YOU AGE MUST BE AT least {min}", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1005, "INVALID MESSAGE KEY", HttpStatus.BAD_REQUEST),
    UNAUTHORIZATION(1006, "YOU DON'T HAVE PERMISSION", HttpStatus.FORBIDDEN);
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
