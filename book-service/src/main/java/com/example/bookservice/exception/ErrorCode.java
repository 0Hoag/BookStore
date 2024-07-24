package com.example.bookservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZE_EXCEPTION(9999, "UNCATEGORIZE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOK_NOT_EXISTED(1001, "BOOKID_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    CHAPTER_NOT_EXISTED(1002, "CHAPTER_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAILED(1005, "Failed to upload image to Dropbox", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1002, "YOU AGE MUST BE AT least {min}", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1003, "INVALID MESSAGE KEY", HttpStatus.BAD_REQUEST),
    UNAUTHORIZATION(1004, "YOU DON'T HAVE PERMISSION", HttpStatus.FORBIDDEN);
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
