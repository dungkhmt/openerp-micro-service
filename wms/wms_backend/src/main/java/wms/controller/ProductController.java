package wms.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wms.dto.product.ProductDTO;
import wms.entity.ResultEntity;
import wms.service.product.ProductServiceImpl;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {
    @Autowired
    private ProductServiceImpl productService;
    @ApiOperation(value = "Thêm mới sản phẩm")
    @PostMapping("/create")
    public ResponseEntity<?> create(Authentication authentication, @Valid @RequestBody ProductDTO productDTO) {
        try {
            return response(new ResultEntity(1, "Create product successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
}
