package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wms.common.constant.DefaultConst;
import wms.dto.category.ProductCategoryDTO;
import wms.dto.product.ProductDTO;
import wms.entity.ResultEntity;
import wms.repo.ProductUnitRepo;
import wms.service.product.IProductService;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController extends BaseController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ProductUnitRepo productUnitRepo;

//    @ApiOperation(value = "Thêm mới sản phẩm từ file excel", notes = "{}")
//    @PostMapping("/create-from-file")
//    public ResponseEntity<?> createProductFromExcelFile(@RequestParam("file") MultipartFile multipartFile) {
//        try {
//            return response(new ResultEntity(1, "Import product successfully", productService.createProductFromExcel(multipartFile)));
//        } catch (Exception ex) {
//            log.error("Import products from excel file failed!");
//            return response(error(ex));
//        }
//    }
    @ApiOperation(value = "Thêm mới sản phẩm")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ProductDTO productDTO) {
        try {
            return response(new ResultEntity(1, "Create product successfully", productService.createProduct(productDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all product with pagination and sorting and some conditions")
    @GetMapping("/get-all-product")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list product successfully", productService.getAllProducts(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-product-by-id/{id}")
    public ResponseEntity<?> getProductByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get product by id successfully", productService.getProductById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-product-by-code")
    public ResponseEntity<?> getProductByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get product by code successfully", productService.getProductByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/update-product/{id}")
    public ResponseEntity<?> updateProductCategory(@Valid @RequestBody ProductDTO productDTO, @PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Update product successfully", productService.updateProduct(productDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProductCategoryById(@PathVariable("id") long id) {
        try {
            productService.deleteProductById(id);
            return response(new ResultEntity(1, "Delete product successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
