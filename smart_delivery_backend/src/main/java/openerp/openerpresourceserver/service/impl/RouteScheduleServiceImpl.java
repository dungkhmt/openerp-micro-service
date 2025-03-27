package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.RouteScheduleDto;
import openerp.openerpresourceserver.dto.StartEndTimeDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.mapper.RouteScheduleMapper;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.RouteScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteScheduleServiceImpl implements RouteScheduleService {

    private final RouteScheduleRepository routeScheduleRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final ScheduleVehicleAssignmentRepository scheduleVehicleAssignmentRepository;
    private final RouteScheduleMapper routeScheduleMapper = RouteScheduleMapper.INSTANCE;

    @Override
    public List<RouteScheduleDto> getAllActiveSchedules() {
        return routeScheduleRepository.findAll().stream()
                .filter(RouteSchedule::isActive)
                .map(routeSchedule -> {
                    Route route = routeRepository.findById(routeSchedule.getRouteId()).orElse(null);
                    RouteScheduleDto routeScheduleDto = RouteScheduleMapper.INSTANCE.routeScheduleToDto(routeSchedule);
                    if(route != null) {
                        routeScheduleDto.setRouteId(route.getRouteId());
                        routeScheduleDto.setRouteName(route.getRouteName());
                        routeScheduleDto.setRouteCode(route.getRouteCode());
                    }
                    return routeScheduleDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteScheduleDto createSchedule(RouteScheduleDto scheduleDto) {
        // Validate if route exists
        Route route = routeRepository.findById(scheduleDto.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found with ID: " + scheduleDto.getRouteId()));

        // Create schedule entity
        RouteSchedule schedule = routeScheduleMapper.dtoToRouteSchedule(scheduleDto);


        // Save and return
        RouteSchedule savedSchedule = routeScheduleRepository.save(schedule);
        return routeScheduleMapper.routeScheduleToDto(savedSchedule);
    }

    @Override
    @Transactional
    public List<RouteScheduleDto> createWeeklySchedule(
            UUID routeId,
            List<String> days,
            List<StartEndTimeDto> startEndTimeDtoList) {

        // Validate route
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("Route not found with ID: " + routeId));


        // Create a schedule for each day
        List<RouteSchedule> schedules = new ArrayList<>();

            for (String day : days) {
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
                for (StartEndTimeDto startEndTimeDto : startEndTimeDtoList) {
                    LocalTime startTime = LocalTime.parse(startEndTimeDto.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime endTime = LocalTime.parse(startEndTimeDto.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

                    // Check if a schedule already exists for this time and route vehicle

                    int existingSchedule = routeScheduleRepository.findExistedRouteSchedule(routeId, startTime, endTime, dayOfWeek);
                    if(existingSchedule > 0) throw new IllegalStateException("A schedule already exists conflict this time and route");

                    // create new routeschedule
                    RouteSchedule routeSchedule = RouteSchedule.builder()
                            .startTime(startTime)
                            .routeId(routeId)
                            .endTime(endTime)
                            .dayOfWeek(dayOfWeek)
                            .isActive(true)
                            .build();
                    schedules.add(routeScheduleRepository.save(routeSchedule));
                }
        }

        return schedules.stream()
                .map(routeScheduleMapper::routeScheduleToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteScheduleDto updateSchedule(UUID scheduleId, RouteScheduleDto scheduleDto) {
        // Check if schedule exists
        RouteSchedule schedule = routeScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found with ID: " + scheduleId));

        // Validate if route exists
        Route route = routeRepository.findById(scheduleDto.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found with ID: " + scheduleDto.getRouteId()));

        // Update schedule
        schedule.setRouteId(scheduleDto.getRouteId());
        schedule.setDayOfWeek(scheduleDto.getDayOfWeek());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setActive(scheduleDto.isActive());

        // Save and return
        RouteSchedule updatedSchedule = routeScheduleRepository.save(schedule);
        return routeScheduleMapper.routeScheduleToDto(updatedSchedule);
    }

    @Override
    public RouteScheduleDto getScheduleById(UUID scheduleId) {
        RouteSchedule schedule = routeScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found with ID: " + scheduleId));

        return routeScheduleMapper.routeScheduleToDto(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID scheduleId) {
        if (!routeScheduleRepository.existsById(scheduleId)) {
            throw new NotFoundException("Schedule not found with ID: " + scheduleId);
        }

        // Delete all vehicle assignments for this schedule first
        List<ScheduleVehicleAssignment> assignments = scheduleVehicleAssignmentRepository.findByRouteScheduleId(scheduleId);
        scheduleVehicleAssignmentRepository.deleteAll(assignments);

        // Delete the schedule
        routeScheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<RouteScheduleDto> getSchedulesByRoute(UUID routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new NotFoundException("Route not found with ID: " + routeId);
        }

        return routeScheduleRepository.findByRouteId(routeId).stream()
                .map(routeScheduleMapper::routeScheduleToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<RouteScheduleDto>> getWeeklyScheduleForRoute(UUID routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new NotFoundException("Route not found with ID: " + routeId);
        }

        List<RouteScheduleDto> schedules = routeScheduleRepository.findByRouteId(routeId).stream()
                .map(routeScheduleMapper::routeScheduleToDto)
                .collect(Collectors.toList());

        Map<String, List<RouteScheduleDto>> schedulesByDay = new HashMap<>();

        // Initialize all days
        for (DayOfWeek day : DayOfWeek.values()) {
            schedulesByDay.put(day.name(), new ArrayList<>());
        }

        // Group schedules by day
        for (RouteScheduleDto schedule : schedules) {
            schedulesByDay.get(schedule.getDayOfWeek().name()).add(schedule);
        }

        return schedulesByDay;
    }

    @Override
    public List<RouteScheduleDto> getActiveSchedulesByDay(DayOfWeek dayOfWeek) {
        return routeScheduleRepository.findByDayOfWeekAndIsActiveTrue(dayOfWeek).stream()
                .map(routeScheduleMapper::routeScheduleToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int generateTripsForToday() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return generateTripsForDate(today);
    }

    @Override
    @Transactional
    public int generateTripsForDate(DayOfWeek dayOfWeek) {
        // Get all active schedules for the specified day
        List<RouteSchedule> schedules = routeScheduleRepository.findByDayOfWeekAndIsActiveTrue(dayOfWeek);

        if (schedules.isEmpty()) {
            log.info("No active schedules found for {}", dayOfWeek);
            return 0;
        }

        int tripsCreated = 0;
        LocalDate targetDate = getNextDateForDayOfWeek(dayOfWeek);

        for (RouteSchedule schedule : schedules) {
            // Find vehicle assignments for this schedule
            List<ScheduleVehicleAssignment> vehicleAssignments =
                    scheduleVehicleAssignmentRepository.findByRouteScheduleIdAndAssignmentDateAndIsActiveTrue(
                            schedule.getId(), targetDate);

            if (vehicleAssignments.isEmpty()) {
                log.info("No vehicle assigned for schedule {} on {}", schedule.getId(), targetDate);
                continue;
            }

            // Calculate time slots for trips throughout the day
            List<LocalTime> tripStartTimes = calculateTripStartTimes(
                    schedule.getStartTime(),
                    schedule.getEndTime()
                    );

            // For each vehicle assignment, create trips
            for (ScheduleVehicleAssignment assignment : vehicleAssignments) {
                // Check if we already have trips for this vehicle and route for the target date
                List<Trip> existingTrips = tripRepository.findByRouteScheduleIdAndDateRange(
                        assignment.getId(), // Using assignment ID directly instead of RouteVehicle
                        targetDate.atStartOfDay().toInstant(ZoneOffset.UTC),
                        targetDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));

                if (!existingTrips.isEmpty()) {
                    log.info("Trips already exist for vehicle {} on schedule {} for {}",
                            assignment.getVehicleId(), schedule.getId(), targetDate);
                    continue;
                }

                // Create trips for each time slot
                for (LocalTime startTime : tripStartTimes) {
                    Instant tripStartInstant = convertToInstant(targetDate, startTime);

                    // Create a new trip
                    Trip trip = Trip.builder()
                            .routeScheduleId(assignment.getId()) // Using assignment ID directly
                            .driverId(assignment.getDriverId())
                            .startTime(tripStartInstant)
                            .status("PLANNED")
                            .currentStopIndex(0)
                            .ordersPickedUp(0)
                            .ordersDelivered(0)
                            .build();

                    tripRepository.save(trip);
                    tripsCreated++;

                    log.info("Created trip for vehicle {} on route {} at {} on {}",
                            assignment.getVehicleId(), schedule.getRouteId(), startTime, targetDate);
                }
            }
        }

        return tripsCreated;
    }

    // Helper methods

    private LocalDate getNextDateForDayOfWeek(DayOfWeek dayOfWeek) {
        LocalDate now = LocalDate.now();
        LocalDate next = now;

        if (now.getDayOfWeek() != dayOfWeek) {
            int daysToAdd = dayOfWeek.getValue() - now.getDayOfWeek().getValue();
            if (daysToAdd <= 0) {
                daysToAdd += 7; // Move to next week if the day has passed this week
            }
            next = now.plusDays(daysToAdd);
        }

        return next;
    }

    private List<LocalTime> calculateTripStartTimes(LocalTime startTime, LocalTime endTime) {
        List<LocalTime> startTimes = new ArrayList<>();


        return startTimes;
    }

    private Instant convertToInstant(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC);
    }
}