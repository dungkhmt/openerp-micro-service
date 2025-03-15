package com.hust.openerp.taskmanagement.hr_management.domain.exception;

import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

public class UserNotExistException extends ApplicationException{
    public UserNotExistException(Object message) {
        super(ResponseCode.USER_NOT_EXIST, message);
    }
}
