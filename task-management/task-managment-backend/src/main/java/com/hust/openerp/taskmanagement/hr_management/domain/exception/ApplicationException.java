package com.hust.openerp.taskmanagement.hr_management.domain.exception;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApplicationException extends RuntimeException{
    private String message;
    private Long code;
    private Object error;

    public ApplicationException(Object error){
        this.message = ResponseCode.EXCEPTION_ERROR.getMessage();
        this.code = ResponseCode.EXCEPTION_ERROR.getCode();
        this.error = error;
    }

    public ApplicationException(ResponseCode responseCode){
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        error = null;
    }

    public ApplicationException(ResponseCode responseCode, Object error){
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.error = error;
    }
}

