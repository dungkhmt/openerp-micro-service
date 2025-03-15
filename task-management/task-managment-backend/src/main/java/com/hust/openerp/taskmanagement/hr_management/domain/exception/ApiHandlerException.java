package com.hust.openerp.taskmanagement.hr_management.domain.exception;

import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

public class ApiHandlerException extends ApplicationException{
    public ApiHandlerException(Object message) {
        super(ResponseCode.API_HANDLER_EXCEPTION, message);
    }
}
