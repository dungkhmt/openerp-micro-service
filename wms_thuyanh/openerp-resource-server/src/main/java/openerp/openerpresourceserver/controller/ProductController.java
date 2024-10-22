package openerp.openerpresourceserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.model.request.ProductRequest;
//import openerp.openerpresourceserver.model.response.ProductDetailResponse;
import openerp.openerpresourceserver.model.response.ProductGeneralResponse;
import openerp.openerpresourceserver.service.ProductCategoryService;
import openerp.openerpresourceserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/wms/admin/product")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {

    private ProductService productService;
    private ProductCategoryService productCategoryService;

    @GetMapping
    public ResponseEntity<List<ProductGeneralResponse>> getProductGeneral() {
        return ResponseEntity.ok(productService.getAllProductGeneral());
    }

    @GetMapping("/without-image")
    public ResponseEntity<List<ProductGeneralResponse>> getProductGeneralWithoutImage() {
        log.info("Start get product with out images");
        return ResponseEntity.ok(productService.getAllProductGeneralWithoutImage());
    }

    @GetMapping("/list-no-img")
    public ResponseEntity<List<ProductGeneralResponse>> getListProductWithoutImage() {
        log.info("Start list product no images");
        return ResponseEntity.ok(productService.getListProductNoImage());
    }

    @PutMapping()
    public ResponseEntity<Product> createProduct(@RequestParam(required = false, name = "image") MultipartFile image,
                                                 @RequestParam("model") String model) {
        try {
            log.info("Model: " + model);
            log.info("Image: " + image);
            ObjectMapper mapper = new ObjectMapper();
            ProductRequest request = mapper.readValue(model, ProductRequest.class);
            request.setImage(image);
            log.info("Create product with request " + request);
            return ResponseEntity.ok(productService.createProduct(request));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<List<String>> delete(@RequestBody List<String> productIds) {
        return productService.deleteProducts(productIds) ?
                ResponseEntity.ok(productIds) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(productIds);
    }
/*
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getDetailByProductId(@PathVariable String id) {
        return ResponseEntity.ok(productService.getDetailByProductId(id));
    }
*/
    @GetMapping(path = "/category")
    public ResponseEntity<List<ProductCategory>> getAll() {
        return ResponseEntity.ok(productCategoryService.getAll());
    }

}
