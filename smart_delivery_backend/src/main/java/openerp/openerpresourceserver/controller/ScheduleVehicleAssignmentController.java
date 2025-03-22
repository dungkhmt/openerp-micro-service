package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.ScheduleVehicleAssignmentDto;
import openerp.openerpresourceserver.service.ScheduleVehicleAssignmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/schedule-assignments")
@RequiredArgsConstructor
public class ScheduleVehicleAssignmentController {

    private final ScheduleVehicleAssignmentService assignmentService;

    /**
     * Assign a vehicle to a schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PostMapping("/assign")
    public ResponseEntity<ScheduleVehicleAssignmentDto> assignVehicleToSchedule(@RequestBody Map<String, Object> request) {
        UUID scheduleId = UUID.fromString((String) request.get("scheduleId"));
        UUID vehicleId = UUID.fromString((String) request.get("vehicleId"));


        // Parse assignment date
        LocalDate assignmentDate;
        if (request.containsKey("assignmentDate") && request.get("assignmentDate") != null) {
            assignmentDate = LocalDate.parse((String) request.get("assignmentDate"));
        } else {
            assignmentDate = LocalDate.now(); // Default to today
        }

        return ResponseEntity.ok(assignmentService.assignVehicleToSchedule(
                scheduleId, vehicleId, assignmentDate));
    }

    /**
     * Update an assignment
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PutMapping("/{assignmentId}")
    public ResponseEntity<ScheduleVehicleAssignmentDto> updateAssignment(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody ScheduleVehicleAssignmentDto assignmentDto) {
        return ResponseEntity.ok(assignmentService.updateAssignment(assignmentId, assignmentDto));
    }

    /**
     * Unassign a vehicle from a schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> unassignVehicle(@PathVariable UUID assignmentId) {
        assignmentService.unassignVehicle(assignmentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all assignments for a schedule
     */
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<ScheduleVehicleAssignmentDto>> getAssignmentsBySchedule(
            @PathVariable UUID scheduleId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsBySchedule(scheduleId));
    }

    /**
     * Get all assignments for a vehicle
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ScheduleVehicleAssignmentDto>> getAssignmentsByVehicle(
            @PathVariable UUID vehicleId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByVehicle(vehicleId));
    }

    /**
     * Get all assignments for a driver
     */
    @GetMapping("/driver/get")
    public ResponseEntity<List<ScheduleVehicleAssignmentDto>> getAssignmentsByDriver(
            Principal principal) {

        return ResponseEntity.ok(assignmentService.getAssignmentsByDriverUsername(principal.getName()));
    }

    /**
     * Get all assignments for a specific date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ScheduleVehicleAssignmentDto>> getAssignmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByDate(date));
    }

    /**
     * Get all active assignments
     */
    @GetMapping("/active")
    public ResponseEntity<List<ScheduleVehicleAssignmentDto>> getAllActiveAssignments() {
        return ResponseEntity.ok(assignmentService.getAllActiveAssignments());
    }

    /**
     * Get assignment by ID
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<ScheduleVehicleAssignmentDto> getAssignmentById(
            @PathVariable UUID assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId));
    }

    /**
     * Get assignments for a route by day of week
     */
    @GetMapping("/route/{routeId}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleVehicleAssignmentDto>> getAssignmentsByRouteAndDay(
            @PathVariable UUID routeId,
            @PathVariable String dayOfWeek) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByRouteAndDay(routeId, dayOfWeek));
    }
}