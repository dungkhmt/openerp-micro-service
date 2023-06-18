package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    @ApiOperation(value = "Thêm mới khách hàng từ file excel", notes = "{}")
    @PostMapping("/create-from-file")
    public ResponseEntity<?> createProductFromExcelFile(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        try {
            customerService.createCustomerFromFile(multipartFile, token);
            return response(new ResultEntity(1, "Import customers successfully", null));
        } catch (Exception ex) {
            log.error("Import products from excel file failed!");
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Import khách hàng từ file excel")
    @PostMapping("/import")
    public ResponseEntity<?> importCustomer(@Valid @RequestBody CustomerDTO customerDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Import new customers successfully", customerService.createNewCustomer(customerDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Thêm mới khách hàng")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CustomerDTO customerDTO, JwtAuthenticationToken token) {
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
            return response(new ResultEntity(1, "Get list customer successfully", customerService.getAllCustomers(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-all-without-paging")
    public ResponseEntity<?> getAllFacilities(
    ) {
        try {
            return response(new ResultEntity(1, "Get list facilities successfully", customerService.getAllWithoutPaging()));
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
    @PutMapping("/update")
    public ResponseEntity<?> updateCustomer(@Valid @RequestBody CustomerUpdateDTO customerDTO, @RequestParam(value = "id") Long id) {
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
