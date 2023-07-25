package wms.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wms.common.constant.DefaultConst;
import wms.dto.facility.ExportFromFacilityDTO;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.dto.facility.ImportToFacilityDTO;
import wms.entity.ResultEntity;
import wms.service.facility.IFacilityService;
import wms.service.files.IFileService;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/facility")
@Slf4j
public class FacilityController extends BaseController{
    @Autowired
    private IFacilityService facilityService;
    @Autowired
    private IFileService fileService;
    @ApiOperation(value = "Thêm mới kho hàng từ file excel", notes = "{}")
    @PostMapping("/create-from-file")
    public ResponseEntity<?> createFacilityFromFile(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        try {
            facilityService.createFacilityFromFile(multipartFile, token);
            return response(new ResultEntity(1, "Import facilities successfully", null));
        } catch (Exception ex) {
            log.error("Import facilities from excel file failed!");
            return response(error(ex));
        }
    }
    @GetMapping(path = "/download-customer-template")
    public @ResponseBody
    ResponseEntity<InputStreamResource> processFacilityTemplateDownload() throws Exception {
        String templateSource = "/templates/Du_lieu_kho.xlsx";
        String templateName = "employee_template";
        File file = fileService.downloadTemplateExcelFile(templateSource, templateName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"))
                .contentLength(file.length()) //
                .body(new InputStreamResource(new FileInputStream(file)));
    }
    @ApiOperation(value = "Thêm mới kho hàng nhà phân phối")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody FacilityDTO facilityDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create facility successfully", facilityService.createFacility(facilityDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all facilities with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllFacilities(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "facilityName", required = false, defaultValue = DefaultConst.STRING) String facilityName,
            @RequestParam(value = "status", required = false, defaultValue = DefaultConst.STRING) String status,
            @RequestParam(value = "createdBy", required = false, defaultValue = DefaultConst.STRING) String createdBy,
            @RequestParam(value = "managedBy", required = false, defaultValue = DefaultConst.STRING) String managedBy,
            @RequestParam(value = "textSearch", required = false, defaultValue = DefaultConst.STRING) String textSearch
    ) {
        try {
            return response(new ResultEntity(1, "Get list facilities successfully", facilityService.getAllFacilities(page, pageSize, sortField, isSortAsc, facilityName, status, createdBy, managedBy, textSearch)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-all-without-paging")
    public ResponseEntity<?> getAllFacilities(
    ) {
        try {
            return response(new ResultEntity(1, "Get list facilities successfully", facilityService.getAllWithoutPaging()));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getFacilityByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get facility by id successfully", facilityService.getFacilityById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-by-code")
    public ResponseEntity<?> getFacilityByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get facility by code successfully", facilityService.getFacilityByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/get-inventory")
    public ResponseEntity<?> getFacilityInventory(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String facilityCode) {
        try {
            return response(new ResultEntity(1, "Get inventory item from facility with code " + facilityCode + " successfully", facilityService.getInventoryItems(page, pageSize, sortField, isSortAsc, facilityCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateFacility(@Valid @RequestBody FacilityUpdateDTO facilityDTO, @RequestParam("id") Long id) {
        try {
            return response(new ResultEntity(1, "Update facility successfully", facilityService.updateFacility(facilityDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFacilityById(@PathVariable("id") long id) {
        try {
            facilityService.deleteFacilityById(id);
            return response(new ResultEntity(1, "Delete facility successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Import item to facility with specific order, split bills")
    @PostMapping("/import-item")
    public ResponseEntity<?> importItemToFacilty(@Valid @RequestBody ImportToFacilityDTO importToFacilityDTO) {
        try {
            facilityService.importToFacilityWithOrder(importToFacilityDTO);
            return response(new ResultEntity(1, "Update facility successfully", null));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Export item from facility within specific order, split bills")
    @PostMapping("/export-item")
    public ResponseEntity<?> exportItemFromFacilty(@Valid @RequestBody ExportFromFacilityDTO exportFromFacilityDTO) {
        try {
            facilityService.exportFromFacility(exportFromFacilityDTO);
            return response(new ResultEntity(1, "Export item from facility successfully", null));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Assign staff to facility")
    @PostMapping("/assign-staff")
    public ResponseEntity<?> assignStaffToFacility(
            @RequestParam(value = "staffCode", required = true, defaultValue = DefaultConst.STRING) String staffCode,
            @RequestParam(value = "facilityCode", required = true, defaultValue = DefaultConst.STRING) String facilityCode
    ) {
        try {
            facilityService.assignStaff(staffCode, facilityCode);
            return response(new ResultEntity(1, "Assign staff to facility successfully", facilityCode));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Get all customers of a facility")
    @GetMapping("/get-customer-of-facility")
    public ResponseEntity<?> getCustomersOfFacility(
            @RequestParam(value = "facilityCode", required = true, defaultValue = DefaultConst.STRING) String facilityCode
    ) {
        try {
            return response(new ResultEntity(1, "Get list customers of facility " + facilityCode + " successfully", facilityService.getFacilityCustomer(facilityCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all facilities customers with pagination and sorting and some conditions")
    @GetMapping("/get-customer-of-facility-paging")
    public ResponseEntity<?> getFacilityCustomerPaging(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "facilityCode", required = true, defaultValue = DefaultConst.STRING) String facilityCode
    ) {
        try {
            return response(new ResultEntity(1, "Get list facilities successfully", facilityService.getAllFacilityCustomer(page, pageSize, sortField, isSortAsc, facilityCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
