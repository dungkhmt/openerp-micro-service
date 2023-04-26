package wms.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.shipment.ShipmentDTO;
import wms.entity.ResultEntity;
import wms.service.user.IUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;
//    @ApiOperation(value = "Thêm mới user")
//    @PostMapping("/create")
//    public ResponseEntity<?> create(@Valid @RequestBody ShipmentDTO shipmentDTO, JwtAuthenticationToken token) {
//        try {
//            return response(new ResultEntity(1, "Create new user successfully", shipmentService.createShipment(shipmentDTO, token)));
//        }
//        catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
    @ApiOperation(value = "Get all users with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsersWithPagination(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list users successfully", userService.getAllUserLogins(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Get all users without pagination and sorting and some conditions")
    @GetMapping("/get-all-exists")
    public ResponseEntity<?> getAllUsersExists(
    ) {
        try {
            return response(new ResultEntity(1, "Get list users successfully", userService.getAllUsers()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
