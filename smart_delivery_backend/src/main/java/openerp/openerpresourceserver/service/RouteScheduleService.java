package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.RouteScheduleDto;
import openerp.openerpresourceserver.dto.StartEndTimeDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RouteScheduleService {

    /**
     * Get all active route schedules
     */
    List<RouteScheduleDto> getAllActiveSchedules();

    /**
     * Create a single route schedule
     */
    RouteScheduleDto createSchedule(RouteScheduleDto scheduleDto);

    /**
     * Create weekly schedules for a route (multiple days)
     */
    List<RouteScheduleDto> createWeeklySchedule(
            UUID routeId,
            List<String> days,
            List<StartEndTimeDto> startEndTimeDtoList);

    /**
     * Update an existing route schedule
     */
    RouteScheduleDto updateSchedule(UUID scheduleId, RouteScheduleDto scheduleDto);

    /**
     * Get a schedule by ID
     */
    RouteScheduleDto getScheduleById(UUID scheduleId);

    /**
     * Delete a schedule
     */
    @Transactional
    void deleteSchedule(UUID scheduleId);

    /**
     * Get all schedules for a route
     */
    List<RouteScheduleDto> getSchedulesByRoute(UUID routeId);

    /**
     * Get a route's weekly schedule organized by day
     */
    Map<String, List<RouteScheduleDto>> getWeeklyScheduleForRoute(UUID routeId);

    /**
     * Get all active schedules for a specific day of the week
     */
    List<RouteScheduleDto> getActiveSchedulesByDay(DayOfWeek dayOfWeek);

    /**
     * Generate trips for today based on active schedules with vehicle assignments
     * Returns the number of trips created
     */
    @Transactional
    int generateTripsForToday();

    /**
     * Generate trips for a specific date based on active schedules
     * Returns the number of trips created
     */
    @Transactional
    int generateTripsForDate(DayOfWeek dayOfWeek);
}