package wms.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.entity.ResultEntity;
import wms.service.user.IUserService;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;
    @ApiOperation(value = "Get all users with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsersWithPagination(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "role", required = false, defaultValue = DefaultConst.STRING) String role,
            @RequestParam(value = "textSearch", required = false, defaultValue = DefaultConst.STRING) String textSearch
            ) {
        try {
            return response(new ResultEntity(1, "Get list users successfully", userService.getAllUsersPaging(page, pageSize, sortField, isSortAsc, role, textSearch)));
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
    @ApiOperation(value = "Get all users by roles")
    @GetMapping("/get-all-by-roles")
    public ResponseEntity<?> getAllUsersExists(
            @RequestParam(value = "role", required = false, defaultValue = DefaultConst.STRING) String role
    ) {
        try {
            return response(new ResultEntity(1, "Get list users successfully", userService.getAllUsersByRole(role)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
