package com.example.messaging_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZE_EXCEPTION(9999, "UNCATEGORIZE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "INVALID MESSAGE KEY", HttpStatus.BAD_REQUEST),
    USER_EXITSTED(1002, "USER EXITED", HttpStatus.BAD_REQUEST),
    SELECTED_PRODUCT_NOT_EXISTED(1003, "SELECTED_PRODUCT_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004, "USERNAME MUST AT LEES THAN {min} CHARACTER", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "PASSWORD NOT BE AT LEES THAN {min} CHARACTER", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZE(1008, "YOU DO NOT HAVE PREMISSION", HttpStatus.FORBIDDEN),
    //    INVALID_DOB(1009, "YOU AGE MUST BE AT least {min}", HttpStatus.BAD_REQUEST),
    //    BLOCK_LIST_NOT_EXISTED(1010, "BLOCKLIST_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    //    FRIENDS_REQUEST_NOT_EXISTED(1011, "FRIENDS_REQUEST_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    //    FRIENDS_SHIP_NOT_EXISTED(1011, "FRIENDS_SHIP_NOT_EXISTED", HttpStatus.BAD_REQUEST),
    //    FRIENDS_SHIP_NOT_VALIDATOR(1012, "Can not create friendShip because it not validator condition",
    // HttpStatus.BAD_REQUEST),
    CONVERSATION_NOT_EXISTED(1009, "Conversation not existed", HttpStatus.BAD_REQUEST),
    MESSAGE_NOT_EXISTED(1010, "Message not existed", HttpStatus.BAD_REQUEST),
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
