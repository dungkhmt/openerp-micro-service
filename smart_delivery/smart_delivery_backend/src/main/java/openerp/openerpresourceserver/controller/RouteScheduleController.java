package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.RouteScheduleDto;
import openerp.openerpresourceserver.dto.StartEndTimeDto;
import openerp.openerpresourceserver.service.RouteScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/smdeli/route-scheduler")
@RequiredArgsConstructor
public class RouteScheduleController {

    private final RouteScheduleService routeScheduleService;

    @GetMapping("/schedules")
    public ResponseEntity<List<RouteScheduleDto>> getAllSchedules() {
        return ResponseEntity.ok(routeScheduleService.getAllActiveSchedules());
    }

    /**
     * Create a new schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PostMapping("/schedule")
    public ResponseEntity<List<RouteScheduleDto>> createWeeklySchedule(@Valid @RequestBody Map<String, Object> request) {
        UUID routeId = UUID.fromString((String) request.get("routeId"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> timeSlots = (List<Map<String, String>>) request.get("timeSlots");

        // Convert to your DTOs
        List<StartEndTimeDto> startEndTimes = timeSlots.stream()
                .map(slot -> new StartEndTimeDto(slot.get("start"), slot.get("end")))
                .collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        List<String> daysOfWeek = (List<String>) request.get("days");

        List<RouteScheduleDto> schedules = routeScheduleService.createWeeklySchedule(
                routeId, daysOfWeek, startEndTimes);

        return ResponseEntity.ok(schedules);
    }

    /**
     * Update an existing schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PutMapping("/schedule/{scheduleId}")
    public ResponseEntity<RouteScheduleDto> updateSchedule(
            @PathVariable UUID scheduleId,
            @Valid @RequestBody RouteScheduleDto scheduleDto) {
        return ResponseEntity.ok(routeScheduleService.updateSchedule(scheduleId, scheduleDto));
    }

    /**
     * Get schedule by ID
     */
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<RouteScheduleDto> getScheduleById(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(routeScheduleService.getScheduleById(scheduleId));
    }

    /**
     * Delete a schedule
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID scheduleId) {
        routeScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all schedules for a route
     */
    @GetMapping("/route/{routeId}/schedules")
    public ResponseEntity<List<RouteScheduleDto>> getSchedulesByRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeScheduleService.getSchedulesByRoute(routeId));
    }

    /**
     * Get the weekly schedule for a route
     */
    @GetMapping("/route/{routeId}/weekly")
    public ResponseEntity<Map<String, List<RouteScheduleDto>>> getWeeklyScheduleForRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeScheduleService.getWeeklyScheduleForRoute(routeId));
    }

    /**
     * Get active schedules for a specific day of the week
     */
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<RouteScheduleDto>> getActiveSchedulesByDay(@PathVariable String dayOfWeek) {
        return ResponseEntity.ok(routeScheduleService.getActiveSchedulesByDay(DayOfWeek.valueOf(dayOfWeek.toUpperCase())));
    }

    /**
     * Generate trips for today based on schedules
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PostMapping("/generate-trips/today")
    public ResponseEntity<Integer> generateTripsForToday() {
        int tripsCreated = routeScheduleService.generateTripsForToday();
        return ResponseEntity.ok(tripsCreated);
    }

    /**
     * Generate trips for a specific day of the week
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ROUTE_MANAGER', 'SCHEDULER')")
    @PostMapping("/generate-trips/day/{dayOfWeek}")
    public ResponseEntity<Integer> generateTripsForDay(@PathVariable String dayOfWeek) {
        int tripsCreated = routeScheduleService.generateTripsForDate(DayOfWeek.valueOf(dayOfWeek.toUpperCase()));
        return ResponseEntity.ok(tripsCreated);
    }
}