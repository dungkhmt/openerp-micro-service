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
import wms.dto.vehicle.DroneDTO;
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
    public ResponseEntity<?> createTrip(@Valid @RequestBody DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) {
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
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc,
            @RequestParam(value = "shipmentCode", required = false, defaultValue = DefaultConst.STRING) String shipmentCode
    ) {
        try {
            return response(new ResultEntity(1, "Get list delivery trips successfully", deliveryTripService.getAllDeliveryTrips(page, pageSize, sortField, isSortAsc, shipmentCode)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateDeliveryTrip(@Valid @RequestBody DeliveryTripDTO deliveryTripDTO,
                                         @RequestParam(value = "id", required = true, defaultValue = DefaultConst.NUMBER) Long id
    ) {
        try {
            return response(new ResultEntity(1, "Update drone successfully", deliveryTripService.updateDeliveryTrip(deliveryTripDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Delete trip")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTrip(@RequestParam(value = "id") Long id) {
        try {
            deliveryTripService.deleteDeliveryTrip(id);
            return response(new ResultEntity(1, "Delete trip successfully", null));
        }
        catch (Exception ex) {
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
    @GetMapping("/get-trip-route")
    public ResponseEntity<?> getTripRoute(
            @RequestParam(value = "tripCode", required = true, defaultValue = DefaultConst.STRING) String tripCode
    ) {
        try {

            return response(new ResultEntity(1, "Get trip route successfully", deliveryTripService.getTripRoute(tripCode)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/delete-trip-route")
    public ResponseEntity<?> deleteTripRoute(
            @RequestParam(value = "tripCode", required = true, defaultValue = DefaultConst.STRING) String tripCode
    ) {
        try {
            deliveryTripService.deleteTripRoute(tripCode);
            return response(new ResultEntity(1, "Delete trip route successfully", null));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
}
