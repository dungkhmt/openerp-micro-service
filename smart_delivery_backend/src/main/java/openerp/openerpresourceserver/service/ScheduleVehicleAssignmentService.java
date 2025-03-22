package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.ScheduleVehicleAssignmentDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleVehicleAssignmentService {

    /**
     * Assign a vehicle to a route schedule
     */
    @Transactional
    ScheduleVehicleAssignmentDto assignVehicleToSchedule(UUID scheduleId, UUID vehicleId, LocalDate assignmentDate);

    /**
     * Update a vehicle assignment
     */
    @Transactional
    ScheduleVehicleAssignmentDto updateAssignment(UUID assignmentId, ScheduleVehicleAssignmentDto assignmentDto);

    /**
     * Unassign a vehicle from a schedule
     */
    @Transactional
    void unassignVehicle(UUID assignmentId);

    /**
     * Get all vehicle assignments for a route schedule
     */
    List<ScheduleVehicleAssignmentDto> getAssignmentsBySchedule(UUID scheduleId);

    /**
     * Get all assignments for a vehicle
     */
    List<ScheduleVehicleAssignmentDto> getAssignmentsByVehicle(UUID vehicleId);

    /**
     * Get all assignments for a driver
     */
    List<ScheduleVehicleAssignmentDto> getAssignmentsByDriver(UUID driverId);

    /**
     * Get all assignments for a specific date
     */
    List<ScheduleVehicleAssignmentDto> getAssignmentsByDate(LocalDate date);

    /**
     * Get all active assignments
     */
    List<ScheduleVehicleAssignmentDto> getAllActiveAssignments();

    /**
     * Get assignment by ID
     */
    ScheduleVehicleAssignmentDto getAssignmentById(UUID assignmentId);

    /**
     * Get assignments for a route by day of week
     */
    List<ScheduleVehicleAssignmentDto> getAssignmentsByRouteAndDay(UUID routeId, String dayOfWeek);

    List<ScheduleVehicleAssignmentDto> getAssignmentsByDriverUsername(String name);
}