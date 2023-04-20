package wms.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.dto.product.ProductDTO;
import wms.entity.ResultEntity;
import wms.service.facility.IFacilityService;

import javax.validation.Valid;

@RestController
@RequestMapping("/facility")
@Slf4j
public class FacilityController extends BaseController{
    @Autowired
    private IFacilityService facilityService;
    @ApiOperation(value = "Thêm mới nhà phân phối")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody FacilityDTO facilityDTO) {
        try {
            return response(new ResultEntity(1, "Create facility successfully", facilityService.createFacility(facilityDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all facilities with pagination and sorting and some conditions")
    @GetMapping("/get-all-facility")
    public ResponseEntity<?> getAllFacilities(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list facilities successfully", facilityService.getAllFacilities(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-facility-by-id/{id}")
    public ResponseEntity<?> getFacilityByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get facility by id successfully", facilityService.getFacilityById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-facility-by-code")
    public ResponseEntity<?> getFacilityByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get facility by code successfully", facilityService.getFacilityByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/update-facility/{id}")
    public ResponseEntity<?> updateProductCategory(@Valid @RequestBody FacilityUpdateDTO facilityDTO, @PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Update facility successfully", facilityService.updateFacility(facilityDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete-facility/{id}")
    public ResponseEntity<?> deleteFacilityById(@PathVariable("id") long id) {
        try {
            facilityService.deleteFacilityById(id);
            return response(new ResultEntity(1, "Delete facility successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
