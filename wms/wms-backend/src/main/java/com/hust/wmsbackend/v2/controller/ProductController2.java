package com.hust.wmsbackend.v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.wmsbackend.management.entity.ProductCategory;
import com.hust.wmsbackend.management.entity.Product;
import com.hust.wmsbackend.management.model.request.ProductPriceRequest;
import com.hust.wmsbackend.management.model.request.ProductRequest;
import com.hust.wmsbackend.management.model.response.ProductDetailResponse;
import com.hust.wmsbackend.management.model.response.ProductGeneralResponse;
import com.hust.wmsbackend.management.model.response.ProductPriceResponse;
import com.hust.wmsbackend.management.service.ProductCategoryService;
import com.hust.wmsbackend.v2.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

// By Diep
@RestController
@RequestMapping("/wmsv3/admin/product")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductController2 {

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

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getDetailByProductId(@PathVariable String id) {
        return ResponseEntity.ok(productService.getDetailByProductId(id));
    }

    @GetMapping(path = "/category")
    public ResponseEntity<List<ProductCategory>> getAll() {
        return ResponseEntity.ok(productCategoryService.getAll());
    }

    @PutMapping(path = "/price-config")
    public ResponseEntity<String> createPriceConfig(@Valid @RequestBody ProductPriceRequest request) {
        return productService.createProductPrice(request) ?
                ResponseEntity.ok("OK") :
                new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/price-config")
    public ResponseEntity<List<ProductPriceResponse>> getAllProductPrices() {
        return ResponseEntity.ok(productService.getAllProductPrices());
    }

    @DeleteMapping(path = "/price-config/{priceIds}")
    public ResponseEntity<String> deleteProductPriceById(@PathVariable String[] priceIds) {
        return productService.deleteProductPriceById(priceIds) ?
                ResponseEntity.ok("OK") :
                new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path="/sale")
    public ResponseEntity<List<ProductGeneralResponse>> getProductGeneralForSale() {
        return ResponseEntity.ok(productService.getAllProductForSale());
    }

}
