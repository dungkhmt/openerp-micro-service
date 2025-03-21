package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.VehicleScheduleDto;
import openerp.openerpresourceserver.entity.VehicleSchedule;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VehicleScheduleService {
    List<VehicleScheduleDto> getAllActiveSchedules();
    /**
     * Create a single vehicle schedule
     */
    VehicleScheduleDto createSchedule(VehicleScheduleDto scheduleDto);

    /**
     * Create a weekly schedule for a vehicle (multiple days)
     */
    List<VehicleScheduleDto> createWeeklySchedule(
            UUID routeVehicleId,
            List<String> days,
            int tripsPerDay,
            String startTime,
            String endTime);

    /**
     * Update an existing vehicle schedule
     */
    VehicleScheduleDto updateSchedule(UUID scheduleId, VehicleScheduleDto scheduleDto);

    /**
     * Get a vehicle schedule by ID
     */
    VehicleScheduleDto getScheduleById(UUID scheduleId);

    /**
     * Delete a vehicle schedule
     */
    void deleteSchedule(UUID scheduleId);

    /**
     * Get all schedules for a vehicle
     */
    List<VehicleScheduleDto> getSchedulesByVehicle(UUID vehicleId);

    /**
     * Get a vehicle's weekly schedule organized by day
     */
    Map<String, List<VehicleScheduleDto>> getWeeklyScheduleForVehicle(UUID vehicleId);

    /**
     * Get all active schedules for a vehicle
     */
    List<VehicleScheduleDto> getActiveSchedulesByVehicle(UUID vehicleId);

    /**
     * Get all schedules for a route
     */
    List<VehicleScheduleDto> getSchedulesByRoute(UUID routeId);

    /**
     * Get all active schedules for a specific day of the week
     */
    List<VehicleScheduleDto> getActiveSchedulesByDay(DayOfWeek dayOfWeek);

    /**
     * Create trips for today based on active schedules
     * Returns the number of trips created
     */
    int generateTripsForToday();

    /**
     * Create trips for a specific date based on active schedules
     * Returns the number of trips created
     */
    int generateTripsForDate(DayOfWeek dayOfWeek);

    /**
     * Get scheduled trip details for a vehicle on a specific day
     */
    List<Map<String, Object>> getScheduledTripsForVehicle(UUID vehicleId, DayOfWeek dayOfWeek);
}