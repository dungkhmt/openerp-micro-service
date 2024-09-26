package com.example.shared.exception;

import com.example.shared.response.CommonResponse;
import org.springframework.http.HttpStatus;

public class InvalidParamsSendException extends MyException{
    private Object data;

    public InvalidParamsSendException(Object data) {
        super(null, ErrorCodeList.INVALID_PARAMETER, HttpStatus.BAD_REQUEST);
        this.data = data;
    }

    public CommonResponse<Object> toResponse() {
        return CommonResponse.badRequest(ErrorCodeList.INVALID_PARAMETER, this.data);
    }
}
