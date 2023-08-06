package wms.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import wms.algorithms.entity.Truck;
import wms.common.constant.DefaultConst;
import wms.dto.ReturnPaginationDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.customer.CustomerUpdateDTO;
import wms.dto.vehicle.DroneDTO;
import wms.dto.vehicle.TruckDTO;
import wms.dto.vehicle.TruckResponseDTO;
import wms.entity.ResultEntity;
import wms.entity.TruckEntity;
import wms.repo.TruckRepo;
import wms.service.vehicle.IVehicleService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vehicle")
@Slf4j
public class VehicleController extends BaseController {
    @Autowired
    private TruckRepo truckRepo;
    @Autowired
    private IVehicleService vehicleService;
    /**
     * Truck
     */
    @ApiOperation(value = "Thêm mới xe tải")
    @PostMapping("/truck/create")
    public ResponseEntity<?> create(@Valid @RequestBody TruckDTO truckDTO) {
        try {
            return response(new ResultEntity(1, "Create new truck successfully", vehicleService.createTruck(truckDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all trucks with pagination and sorting and some conditions")
    @GetMapping("/truck/get-all")
    public ResponseEntity<?> getAllTrucks(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            ReturnPaginationDTO<TruckResponseDTO> truckEntityList = vehicleService.getAllTrucks(page, pageSize, sortField, isSortAsc);
            return response(new ResultEntity(1, "Get list truck successfully", truckEntityList));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/truck/get-by-id/{id}")
    public ResponseEntity<?> getTruckByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get truck by id successfully", vehicleService.getTruckById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/truck/get-by-code")
    public ResponseEntity<?> getTruckByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get truck by code successfully", vehicleService.getTruckByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/truck/update")
    public ResponseEntity<?> updateTruck(@Valid @RequestBody TruckDTO truckDTO,
                                         @RequestParam(value = "id", required = true, defaultValue = DefaultConst.NUMBER) Long id
                                         ) {
        try {
            return response(new ResultEntity(1, "Update truck successfully", vehicleService.updateTruck(truckDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/truck/delete/{id}")
    public ResponseEntity<?> deleteTruckById(@PathVariable("id") long id) {
        try {
            vehicleService.deleteTruckById(id);
            return response(new ResultEntity(1, "Delete truck successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping(path = "/truck/remove")
    public ResponseEntity<?> deleteTruckByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code
    ) {
        try {
            vehicleService.deleteTruckByCode(code);
            return response(new ResultEntity(1, "Delete truck successfully", code));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    /**
     * Drone
     */
    @ApiOperation(value = "Thêm mới drone")
    @PostMapping("/drone/create")
    public ResponseEntity<?> create(@Valid @RequestBody DroneDTO droneDTO) {
        try {
            return response(new ResultEntity(1, "Create new drone successfully", vehicleService.createDrone(droneDTO)));
        }
        catch (Exception ex) {
            return response(error(ex));
        }
    }
    @ApiOperation(value = "Get all drones with pagination and sorting and some conditions")
    @GetMapping("/drone/get-all")
    public ResponseEntity<?> getAllDrones(
            @RequestParam(value = DefaultConst.PAGE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE) Integer page,
            @RequestParam(value = DefaultConst.PAGE_SIZE, required = false, defaultValue = DefaultConst.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = DefaultConst.SORT_TYPE, required = false, defaultValue = DefaultConst.STRING) String sortField,
            @RequestParam(value = "sort_asc", required = false, defaultValue = DefaultConst.BOOL) Boolean isSortAsc
    ) {
        try {
            return response(new ResultEntity(1, "Get list drone successfully", vehicleService.getAllDrones(page, pageSize, sortField, isSortAsc)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/drone/get-by-id/{id}")
    public ResponseEntity<?> getDroneByID(@PathVariable("id") long id) {
        try {
            return response(new ResultEntity(1, "Get drone by id successfully", vehicleService.getDroneById(id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @GetMapping("/drone/get-by-code")
    public ResponseEntity<?> getDroneByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code) {
        try {
            return response(new ResultEntity(1, "Get drone by code successfully", vehicleService.getDroneByCode(code)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @PutMapping("/drone/update")
    public ResponseEntity<?> updateDrone(@Valid @RequestBody DroneDTO droneDTO,
                                         @RequestParam(value = "id", required = true, defaultValue = DefaultConst.NUMBER) Long id
                                         ) {
        try {
            return response(new ResultEntity(1, "Update drone successfully", vehicleService.updateDrone(droneDTO, id)));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping("/drone/delete/{id}")
    public ResponseEntity<?> deleteDroneById(@PathVariable("id") long id) {
        try {
            vehicleService.deleteDroneById(id);
            return response(new ResultEntity(1, "Delete drone successfully", id));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
    @DeleteMapping(path = "/drone/remove")
    public ResponseEntity<?> deleteDroneByCode(
            @RequestParam(value = "code", required = true, defaultValue = DefaultConst.STRING) String code
    ) {
        try {
            vehicleService.deleteDroneByCode(code);
            return response(new ResultEntity(1, "Delete drone successfully", code));
        } catch (Exception ex) {
            return response(error(ex));
        }
    }
}
