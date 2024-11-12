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
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.entity.ProductInfoProjection;
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
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Tạo sản phẩm mới từ dữ liệu nhận được
        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
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

}

