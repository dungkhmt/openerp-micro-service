package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.category.CustomerTypeDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.product.ProductDTO;
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
    public ResponseEntity<?> create(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            return response(new ResultEntity(1, "Create new customer successfully", customerService.createNewCustomer(customerDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
//    @ApiOperation(value = "Get all product with pagination and sorting and some conditions")
//    @GetMapping("/get-all-product")
//    public ResponseEntity<?> getAllProducts(
//            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
//            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
//            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
//            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
//    ) {
//        try {
//            return response(new ResultEntity(1, "Get list product successfully", productService.getAllProducts(page, pageSize, sortField, isSortAsc)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @GetMapping("/get-product-by-id/{id}")
//    public ResponseEntity<?> getProductByID(@PathVariable("id") long id) {
//        try {
//            return response(new ResultEntity(1, "Get product by id successfully", productService.getProductById(id)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @GetMapping("/get-product-by-code")
//    public ResponseEntity<?> getProductByCode(
//            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
//        try {
//            return response(new ResultEntity(1, "Get product by code successfully", productService.getProductByCode(code)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @PutMapping("/update-product/{id}")
//    public ResponseEntity<?> updateProductCategory(@Valid @RequestBody ProductDTO productDTO, @PathVariable("id") long id) {
//        try {
//            return response(new ResultEntity(1, "Update product successfully", productService.updateProduct(productDTO, id)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @DeleteMapping("/delete-product/{id}")
//    public ResponseEntity<?> deleteProductCategoryById(@PathVariable("id") long id) {
//        try {
//            productService.deleteProductById(id);
//            return response(new ResultEntity(1, "Delete product successfully", id));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
}
