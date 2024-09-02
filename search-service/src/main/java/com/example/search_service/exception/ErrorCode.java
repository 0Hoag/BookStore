package com.example.search_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZE_EXCEPTION(9999, "UNCATEGORIZE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    POST_NOT_EXISTED(1001, "POSTID_NOT_EXISTED", HttpStatus.BAD_REQUEST),

    INVALID_DOB(1002, "YOU AGE MUST BE AT least {min}", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1003, "INVALID MESSAGE KEY", HttpStatus.BAD_REQUEST),
    UNAUTHORIZATION(1004, "YOU DON'T HAVE PERMISSION", HttpStatus.FORBIDDEN),
    USER_NOT_EXISTED(1005, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    LIKE_NOT_EXISTED(1006, "LIKE_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_EXISTED(1007, "COMMENT_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    ;
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
