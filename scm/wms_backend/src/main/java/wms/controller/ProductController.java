package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wms.common.constant.DefaultConst;
import wms.dto.product.ProductDTO;
import wms.dto.product.ProductDiscountDTO;
import wms.dto.product.ProductPriceDTO;
import wms.entity.ResultEntity;
import wms.repo.ProductUnitRepo;
import wms.service.product.IProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController extends BaseController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ProductUnitRepo productUnitRepo;

    @ApiOperation(value = "Thêm mới sản phẩm từ file excel", notes = "{}")
    @PostMapping("/create-from-file")
    public ResponseEntity<?> createProductFromExcelFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            return response(new ResultEntity(1, "Import product successfully", productService.createProductFromExcel(multipartFile)));
        } catch (Exception ex) {
            log.error("Import products from excel file failed!");
            return response(error(ex));
        }
    }
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
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "productName", required = false, defaultValue = DefaultConst.STRING) String productName,
            @RequestParam(value = "status", required = false, defaultValue = DefaultConst.STRING) String status,
            @RequestParam(value = "categoryCode", required = false, defaultValue = DefaultConst.STRING) String category,
            @RequestParam(value = "unitCode", required = false, defaultValue = DefaultConst.STRING) String unit,
            @RequestParam(value = "textSearch", required = false, defaultValue = DefaultConst.STRING) String textSearch
    ) {
        try {
            return response(new ResultEntity(1, "Get list product successfully", productService.getAllProducts(page, pageSize, sortField, isSortAsc,
                    productName, status, category, unit, textSearch
                    )));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-all-without-paging")
    public ResponseEntity<?> getAllNoPaging(
    ) {
        try {
            return response(new ResultEntity(1, "Get list products successfully", productService.getAllWithoutPaging()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getProductByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get product by id successfully", productService.getProductById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-code")
    public ResponseEntity<?> getProductByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get product by code successfully", productService.getProductByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProductCategory(@Valid @RequestBody ProductDTO productDTO, @RequestParam(value = "id") Long id) {
        try {
            return response(new ResultEntity(1, "Update product successfully", productService.updateProduct(productDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductCategoryById(@PathVariable("id") long id) {
        try {
            productService.deleteProductById(id);
            return response(new ResultEntity(1, "Delete product successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Setup giá mua")
    @PostMapping("/set-purchase-price")
    public ResponseEntity<?> setPurchasePrice(@Valid @RequestBody List<ProductPriceDTO> productPriceDTO) {
        try {
            productService.setPurchasePrice(productPriceDTO);
            return response(new ResultEntity(1, "Set purchase price successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-all-sellin-price")
    public ResponseEntity<?> getSellinPrices() {
        try {
            return response(new ResultEntity(1, "Get all sellin prices successfully", productService.getAllSellinPrice()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/sellin-price/update")
    public ResponseEntity<?> updateProductSellinPrice(@RequestBody ProductPriceDTO priceDTO) {
        try {
            return response(new ResultEntity(1, "Update product successfully", productService.updateSellinPrice(priceDTO)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/sellin-price/delete")
    public ResponseEntity<?> deleteProductSellinPrice(@RequestParam(value = "id", required = false, defaultValue = DefaultConst.NUMBER) Long id) {
        try {
            productService.deleteSellinPrice(id);
            return response(new ResultEntity(1, "Delete price successfully", null));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Setup giá bán")
    @PostMapping("/set-sale-price")
    public ResponseEntity<?> setSalePrice(@Valid @RequestBody List<ProductDiscountDTO> productDiscountDTO) {
        try {
            productService.setSalePrice(productDiscountDTO);
            return response(new ResultEntity(1, "Set sale price successfully",null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-all-sellout-price")
    public ResponseEntity<?> getSelloutPrices() {
        try {
            return response(new ResultEntity(1, "Get all sellout prices successfully", productService.getAllSelloutPrice()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/sellout-price/update")
    public ResponseEntity<?> updateProductSellinPrice(@RequestBody ProductDiscountDTO discountDTO) {
        try {
            return response(new ResultEntity(1, "Update price successfully", productService.updateSelloutPrice(discountDTO)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/sellout-price/delete")
    public ResponseEntity<?> deleteProductSelloutPrice(@RequestParam(value = "id", required = false, defaultValue = DefaultConst.NUMBER) Long id) {
        try {
            productService.deleteSelloutPrice(id);
            return response(new ResultEntity(1, "Delete price successfully", null));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
