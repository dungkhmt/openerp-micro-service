package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.customer.CustomerDTO;
import wms.dto.customer.CustomerUpdateDTO;
import wms.entity.ResultEntity;
import wms.service.customer.ICustomerService;

import javax.validation.Valid;

@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController extends BaseController{
    @Autowired
    private ICustomerService customerService;
    @ApiOperation(value = "Thêm mới khách hàng")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CustomerDTO customerDTO,  JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create new customer successfully", customerService.createNewCustomer(customerDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all customer with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list product successfully", customerService.getAllCustomers(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getCustomerByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get customer by id successfully", customerService.getCustomerById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-code")
    public ResponseEntity<?> getCustomerByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get customer by code successfully", customerService.getCustomerByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCustomer(@Valid @RequestBody CustomerUpdateDTO customerDTO, @PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Update customer successfully", customerService.updateCustomerInfo(customerDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable("id") long id) {
        try {
            customerService.deleteCustomerById(id);
            return response(new ResultEntity(1, "Delete customer successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
