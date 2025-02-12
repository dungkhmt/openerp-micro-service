package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
import openerp.openerpresourceserver.model.request.ProductDto;
import openerp.openerpresourceserver.service.ProductCategoryService;
import openerp.openerpresourceserver.service.ProductService;

@RestController
@RequestMapping("/admin/product")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {

    private ProductService productService;
    private ProductCategoryService productCategoryService;

    @GetMapping
    public ResponseEntity<List<ProductInfoProjection>> getProductGeneral() {
        return ResponseEntity.ok(productService.getAllProductGeneral());
    }
    
    @PostMapping("/create-product")
    public ResponseEntity<String> createProduct(
            @RequestParam("productData") String productData,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Deserialize product data
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.readValue(productData, ProductDto.class);

            // Tạo mới sản phẩm
            boolean isCreated = productService.createProduct(productDto, imageFile);

            if (isCreated) {
                return ResponseEntity.ok("Product created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product");
        }
    }
    
    @PostMapping("/delete-product")
    public ResponseEntity<String> deleteProduct(@RequestBody Map<String, Object> requestBody) {
        try {
            String id = (String) requestBody.get("id");
            UUID uuid = UUID.fromString(id); // Convert the string to UUID
            boolean isDeleted = productService.deleteProductById(uuid);
            if (isDeleted) {
                return ResponseEntity.ok("Product deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        }
    }

    @GetMapping(path = "/category")
    public ResponseEntity<List<ProductCategory>> getAll() {
        return ResponseEntity.ok(productCategoryService.getAll());
    }
    
    @PostMapping("/get-product-detail")
    public ResponseEntity<?> getProductDetail(@RequestParam("id") String id) {
        try {
            UUID productId = UUID.fromString(id);
            
            // Fetch product details from the service
            Product product = productService.getProductById(productId);
            
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        }
    }
    
    @PostMapping("/update-product")
    public ResponseEntity<String> updateProduct(
            @RequestParam("productData") String productData,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Deserialize product data
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.readValue(productData, ProductDto.class);

            // Cập nhật sản phẩm
            boolean isUpdated = productService.updateProduct(productDto, imageFile);

            if (isUpdated) {
                return ResponseEntity.ok("Product updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product");
        }
    }




}

