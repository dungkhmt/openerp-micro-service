package wms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
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

    @ApiOperation(value = "Update shipment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/update")
    public ResponseEntity<?> updateShipmentInfo(@Valid @RequestBody ShipmentDTO shipmentDTO, @RequestParam(value = "id") Long id) {
        try {
            return response(new ResultEntity(1, "Create new shipment successfully", shipmentService.updateShipment(shipmentDTO, id)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Delete shipment")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteShipment(@RequestParam(value = "id") Long id) {
        try {
            shipmentService.deleteShipmentById(id);
            return response(new ResultEntity(1, "Delete shipment successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Phân đơn thành các shipment từ các spliting order")
    @PostMapping("/create-shipment-item")
    public ResponseEntity<?> createItem(@Valid @RequestBody ShipmentItemDTO shipmentItemDTO) {
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
            @RequestParam(value = "tripCode", required = true, defaultValue = DefaultConst.STRING) String tripCode
    ) {
        try {
            return response(new ResultEntity(1, "Get list shipment items successfully", shipmentService.getAllItemOfTrip(page, pageSize, sortField, isSortAsc, tripCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Assign splitted đơn (shipment item) cho các trip")
    @PutMapping("/assign-shipment-item")
    public ResponseEntity<?> assign(@Valid @RequestBody AssignedItemDTO assignedItemDTO) {
        try {
            shipmentService.assignShipmentItem(assignedItemDTO);
            return response(new ResultEntity(1, "Assign item to trip successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Unassign splitted đơn (shipment item) cho các trip")
    @PutMapping("/unassign-shipment-item")
    public ResponseEntity<?> unassign(@Valid @RequestBody AssignedItemDTO assignedItemDTO) {
        try {
            shipmentService.unassignShipmentItem(assignedItemDTO);
            return response(new ResultEntity(1, "Unassign item to trip successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
}
