package com.example.post_service.exception;

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
    USER_NOT_EXISTED(1005, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    LIKE_NOT_EXISTED(1006, "LIKE_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_EXISTED(1007, "COMMENT_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXISTED(1014, "Image not existed", HttpStatus.BAD_REQUEST),
    VIDEO_NOT_EXISTED(1014, "Video not existed", HttpStatus.BAD_REQUEST),

    UPLOAD_FILE_FAIL(1015, "Upload file to fail!", HttpStatus.BAD_REQUEST),
    REMOVE_FILE_FAIL(1016, "Remove file to fail!", HttpStatus.BAD_REQUEST);
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
