package com.hust.openerp.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public ApiException(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
