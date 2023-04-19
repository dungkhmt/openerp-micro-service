package wms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import wms.common.response.IErrorResult;
import wms.entity.ResultEntity;

@Component
public class BaseController implements IErrorResult {
    public BaseController() {

    }
    protected ResponseEntity response(ResultEntity entity) {
        return new ResponseEntity(entity, HttpStatus.OK);
    }
}
