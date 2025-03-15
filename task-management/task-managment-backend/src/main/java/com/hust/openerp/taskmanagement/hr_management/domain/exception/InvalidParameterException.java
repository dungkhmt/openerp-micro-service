package com.hust.openerp.taskmanagement.hr_management.domain.exception;


import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

public class InvalidParameterException extends ApplicationException {
    public InvalidParameterException(Object message) {
        super(ResponseCode.PARAMETER_VALUE_IS_INVALID, message);
    }
}
