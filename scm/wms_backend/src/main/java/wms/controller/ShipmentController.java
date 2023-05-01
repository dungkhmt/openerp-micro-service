package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.shipment.AssignedItemDTO;
import wms.dto.shipment.ShipmentDTO;
import wms.dto.shipment.ShipmentItemDTO;
import wms.entity.ResultEntity;
import wms.service.shipment.IShipmentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/shipment")
@Slf4j
public class ShipmentController extends BaseController {
    @Autowired
    private IShipmentService shipmentService;
    @ApiOperation(value = "Thêm mới kế hoạch (đợt) giao hàng")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ShipmentDTO shipmentDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create new shipment successfully", shipmentService.createShipment(shipmentDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all shipments with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllShipments(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list shipments successfully", shipmentService.getAllShipments(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

//    @GetMapping("/get-by-id/{id}")
//    public ResponseEntity<?> getCustomerByID(@PathVariable("id") long id) {
//        try {
//            return response(new ResultEntity(1, "Get customer by id successfully", customerService.getCustomerById(id)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @GetMapping("/get-by-code")
//    public ResponseEntity<?> getCustomerByCode(
//            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
//        try {
//            return response(new ResultEntity(1, "Get customer by code successfully", customerService.getCustomerByCode(code)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @PutMapping("/update/{id}")
//    public ResponseEntity<?> updateCustomer(@Valid @RequestBody CustomerUpdateDTO customerDTO, @PathVariable("id") long id) {
//        try {
//            return response(new ResultEntity(1, "Update customer successfully", customerService.updateCustomerInfo(customerDTO, id)));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> deleteCustomerById(@PathVariable("id") long id) {
//        try {
//            customerService.deleteCustomerById(id);
//            return response(new ResultEntity(1, "Delete customer successfully", id));
//        } catch (Exception ex) {
//            return response(error(ex));
//        }
//    }

    @ApiOperation(value = "Phân đơn thành các shipment từ các spliting order")
    @PostMapping("/create-shipment-item")
    public ResponseEntity<?> createItem(@Valid @RequestBody ShipmentItemDTO shipmentItemDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create new shipment item successfully", shipmentService.createShipmentItem(shipmentItemDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all shipment items with pagination and sorting and some conditions")
    @GetMapping("/get-all-item")
    public ResponseEntity<?> getAllShipmentItems(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list shipment items successfully", shipmentService.getAllShipmentItems(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Get all shipment items of a trip with pagination and sorting and some conditions")
    @GetMapping("/get-item-of-trip")
    public ResponseEntity<?> getAllItemOfTrip(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "", required = true, defaultValue = DefaultConst.STRING) String tripCode
    ) {
        try {
            return response(new ResultEntity(1, "Get list shipment items successfully", shipmentService.getAllItemOfTrip(page, pageSize, sortField, isSortAsc, tripCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Assign splitted đơn (shipment item) cho các trip")
    @PutMapping("/assign-shipment-item")
    public ResponseEntity<?> create(@Valid @RequestBody AssignedItemDTO assignedItemDTO, JwtAuthenticationToken token) {
        try {
            shipmentService.assignShipmentItem(assignedItemDTO);
            return response(new ResultEntity(1, "Assign item to trip successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
}
