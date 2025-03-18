package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.DriverVehicleAssignmentDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.service.DriverVehicleAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/driver-vehicle")
@RequiredArgsConstructor
public class DriverVehicleAssignmentController {

    private final DriverVehicleAssignmentService driverVehicleAssignmentService;

    /**
     * Get all driver-vehicle assignments
     */
    @GetMapping("/assignments")
    public ResponseEntity<List<DriverVehicleAssignmentDto>> getAllAssignments() {
        return ResponseEntity.ok(driverVehicleAssignmentService.getAllDriverVehicleAssignments());
    }

    /**
     * Assign a driver to a vehicle
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @PostMapping("/assign")
    public ResponseEntity<Void> assignDriverToVehicle(@RequestBody Map<String, String> request) {
        UUID driverId = UUID.fromString(request.get("driverId"));
        UUID vehicleId = UUID.fromString(request.get("vehicleId"));

        if (driverId == null || vehicleId == null) {
            return ResponseEntity.badRequest().build();
        }

        driverVehicleAssignmentService.assignDriverToVehicle(driverId, vehicleId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unassign a driver from a vehicle
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @PostMapping("/unassign/{vehicleId}")
    public ResponseEntity<Void> unassignDriverFromVehicle(@PathVariable UUID vehicleId) {
        driverVehicleAssignmentService.unassignDriverFromVehicle(vehicleId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get the vehicle assigned to a driver
     */
    @GetMapping("/driver/{driverId}/vehicle")
    public ResponseEntity<VehicleDto> getVehicleForDriver(@PathVariable UUID driverId) {
        VehicleDto vehicleDto = driverVehicleAssignmentService.getVehicleForDriver(driverId);
        return vehicleDto != null ? ResponseEntity.ok(vehicleDto) : ResponseEntity.notFound().build();
    }

    /**
     * Get all unassigned vehicles
     */
    @GetMapping("/vehicles/unassigned")
    public ResponseEntity<List<VehicleDto>> getUnassignedVehicles() {
        return ResponseEntity.ok(driverVehicleAssignmentService.getUnassignedVehicles());
    }
}