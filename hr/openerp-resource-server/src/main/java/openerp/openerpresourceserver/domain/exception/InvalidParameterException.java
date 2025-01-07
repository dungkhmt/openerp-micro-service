package openerp.openerpresourceserver.domain.exception;

import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

public class InvalidParameterException extends ApplicationException {
    public InvalidParameterException(Object message) {
        super(ResponseCode.PARAMETER_VALUE_IS_INVALID, message);
    }
}
