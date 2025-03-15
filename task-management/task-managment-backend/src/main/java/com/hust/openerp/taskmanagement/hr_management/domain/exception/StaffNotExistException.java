package com.hust.openerp.taskmanagement.hr_management.domain.exception;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

public class StaffNotExistException extends ApplicationException{
    public StaffNotExistException(Object message) {
        super(ResponseCode.STAFF_NOT_EXIST, message);
    }
}
