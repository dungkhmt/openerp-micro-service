package wms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import wms.common.response.IErrorResult;
import wms.entity.ResultEntity;

/**
 * Rules:
 * GET and POST must not be used to tunnel other request methods
 * GET must be used to retrieve a representation of a resource (contain headers but no body)
 * HEAD should be used to retrieve response headers
 * PUT must be used to both insert and update a stored resource
 * PUT must be used to update mutable resources
 * POST must be used to create a new resource in a collection
 * POST must be used to execute controllers
 * DELETE must be used to remove a resource from its parent
 */
@Component
public class BaseController implements IErrorResult {
    public BaseController() {

    }
    protected ResponseEntity response(ResultEntity entity) {
        return new ResponseEntity(entity, HttpStatus.OK);
    }
}
