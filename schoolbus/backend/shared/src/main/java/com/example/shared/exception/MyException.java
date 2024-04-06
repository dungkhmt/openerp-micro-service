package com.example.shared.exception;

import com.example.shared.response.CommonResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MyException extends RuntimeException {
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public MyException(Throwable cause, String code, String message, HttpStatus httpStatus) {
        super(cause);
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public MyException(Throwable cause, ErrorCodeList errorCode, HttpStatus httpStatus ) {
        super(cause);
        this.code = errorCode.toCode();
        this.message = errorCode.toString();
        this.httpStatus = httpStatus;
    }

    public CommonResponse<String> toMyCommonResponse() {
        return new CommonResponse<>(
                this.code,
                this.message,
                null
        );
    }
}
