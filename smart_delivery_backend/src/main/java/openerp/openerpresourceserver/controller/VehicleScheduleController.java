package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.VehicleScheduleDto;
import openerp.openerpresourceserver.service.VehicleScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/scheduler")
@RequiredArgsConstructor
public class VehicleScheduleController {

    private final VehicleScheduleService vehicleScheduleService;

    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @GetMapping("/schedules")
    public ResponseEntity<List<VehicleScheduleDto>> getAllSchedules() {
        return ResponseEntity.ok(vehicleScheduleService.getAllActiveSchedules());
    }

    /**
     * Create a new weekly schedule for a vehicle
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PostMapping("/weekly-schedule")
    public ResponseEntity<List<VehicleScheduleDto>> createWeeklySchedule(@Valid @RequestBody Map<String, Object> request) {
        UUID routeVehicleId = UUID.fromString((String) request.get("routeVehicleId"));
        int tripsPerDay = (Integer) request.get("tripsPerDay");
        String startTime = (String) request.get("startTime");
        String endTime = (String) request.get("endTime");

        @SuppressWarnings("unchecked")
        List<String> daysOfWeek = (List<String>) request.get("days");

        List<VehicleScheduleDto> schedules = vehicleScheduleService.createWeeklySchedule(
                routeVehicleId, daysOfWeek, tripsPerDay, startTime, endTime);

        return ResponseEntity.ok(schedules);
    }

    /**
     * Update an existing schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<VehicleScheduleDto> updateSchedule(
            @PathVariable UUID scheduleId,
            @Valid @RequestBody VehicleScheduleDto scheduleDto) {
        return ResponseEntity.ok(vehicleScheduleService.updateSchedule(scheduleId, scheduleDto));
    }

    /**
     * Get all schedules for a vehicle
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleScheduleDto>> getSchedulesByVehicle(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(vehicleScheduleService.getSchedulesByVehicle(vehicleId));
    }

    /**
     * Get the weekly schedule for a vehicle
     */
    @GetMapping("/vehicle/{vehicleId}/weekly")
    public ResponseEntity<Map<String, List<VehicleScheduleDto>>> getWeeklyScheduleForVehicle(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(vehicleScheduleService.getWeeklyScheduleForVehicle(vehicleId));
    }

    /**
     * Delete a schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID scheduleId) {
        vehicleScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Generate trips for today based on schedules
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PostMapping("/generate-trips/today")
    public ResponseEntity<Integer> generateTripsForToday() {
        int tripsCreated = vehicleScheduleService.generateTripsForToday();
        return ResponseEntity.ok(tripsCreated);
    }

    /**
     * Get scheduled trips for a specific vehicle on a specific day
     */
    @GetMapping("/trips/{vehicleId}/{dayOfWeek}")
    public ResponseEntity<List<Map<String, Object>>> getScheduledTripsForVehicle(
            @PathVariable UUID vehicleId,
            @PathVariable DayOfWeek dayOfWeek) {
        return ResponseEntity.ok(vehicleScheduleService.getScheduledTripsForVehicle(vehicleId, dayOfWeek));
    }
}