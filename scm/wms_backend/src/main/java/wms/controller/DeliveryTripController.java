package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.common.constant.DefaultConst;
import wms.dto.delivery_trip.DeliveryTripDTO;
import wms.dto.delivery_trip.TripRouteDTO;
import wms.entity.ResultEntity;
import wms.service.delivery_trip.IDeliveryTripService;

import javax.validation.Valid;

@RestController
@RequestMapping("/delivery-trip")
@Slf4j
public class DeliveryTripController extends BaseController {
    @Autowired
    private IDeliveryTripService deliveryTripService;
    @ApiOperation(value = "Thêm mới chuyến giao hàng")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Create new trip successfully", deliveryTripService.createDeliveryTrip(deliveryTripDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all delivery trips with pagination and sorting and some conditions")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllDeliveryTrips(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list delivery trips successfully", deliveryTripService.getAllDeliveryTrips(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get delivery trip that a bill can be assign to")
    @GetMapping("/get-trip-to-assign")
    public ResponseEntity<?> getTripToAssign(
            @RequestParam(value = "billCode", required = true, defaultValue = DefaultConst.STRING) String billCode
    ) {
        try {
            return response(new ResultEntity(1, "Get delivery trip to assign successfully", deliveryTripService.getTripToAssignBill(billCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Phân đơn cho chuyến")
    @PostMapping("/assign-bill-to-trip")
    public ResponseEntity<?> assignBillToTrip(@Valid @RequestBody DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) {
        try {
            return response(new ResultEntity(1, "Assign to trip successfully", deliveryTripService.assignBillToTrip(deliveryTripDTO, token)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }

    @ApiOperation(value = "Tạo lộ trình di chuyển")
    @PostMapping("/create-trip-route")
    public ResponseEntity<?> createTripRoute(
          @RequestBody TripRouteDTO tripRouteDTO
    ) {
        try {
            deliveryTripService.createTripRoute(tripRouteDTO);
            return response(new ResultEntity(1, "Create trip route successfully", tripRouteDTO.getTripCode()));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
}
