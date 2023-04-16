package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.category.ProductUnitDTO;
import wms.entity.ResultEntity;
import wms.service.category.ICategoryService;

import javax.validation.Valid;

@Controller
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Autowired
    private ICategoryService categoryService;
    @PostMapping(path = "/create-unit")
    public ResponseEntity<?> createUnit(@Valid @RequestBody ProductUnitDTO productUnitDTO) {
        try {
            return response(new ResultEntity(1, "Create product unit successfully", categoryService.createProductUnit(productUnitDTO)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all product units with pagination and sorting and some conditions")
    @GetMapping(path = "/get-all-unit")
    public ResponseEntity<?> getAllUnits(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list product units successfully", categoryService.getAllProductUnit(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping(path = "/get-unit-by-id/{id}")
    public ResponseEntity<?> getUnitById(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get unit by id successfully", categoryService.getProductUnitById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping(path = "/get-unit-by-code")
    public ResponseEntity<?> getUnitByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get unit by code successfully", categoryService.getProductUnitByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
//    @PutMapping(name = "/update-unit")
//    public ResponseEntity<?> getUnitByCode(@Valid @RequestBody ProductUnitDTO productUnitDTO) {
//        try {
//            return response(new ResultEntity(1, "Create unit successfully", categoryService.createProductUnit(productUnitDTO)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @DeleteMapping(name = "/remove-unit")
//    public ResponseEntity<?> getUnitByCode(@Valid @RequestBody ProductUnitDTO productUnitDTO) {
//        try {
//            return response(new ResultEntity(1, "Create unit successfully", categoryService.createProductUnit(productUnitDTO)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
}
