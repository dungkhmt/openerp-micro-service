package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.VehicleScheduleDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.VehicleScheduleService;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleScheduleServiceImpl implements VehicleScheduleService {

    private final VehicleScheduleRepository vehicleScheduleRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final RouteVehicleRepository routeVehicleRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional
    public VehicleScheduleDto createSchedule(VehicleScheduleDto scheduleDto) {
        // Validate if route vehicle assignment exists
        RouteVehicle routeVehicle = routeVehicleRepository.findById(scheduleDto.getRouteVehicleId())
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        // Create schedule entity
        VehicleSchedule schedule = VehicleSchedule.builder()
                .routeVehicleId(scheduleDto.getRouteVehicleId())
                .dayOfWeek(scheduleDto.getDayOfWeek())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .numberOfTrips(scheduleDto.getNumberOfTrips())
                .isActive(scheduleDto.isActive())
                .build();

        // Save and return
        VehicleSchedule savedSchedule = vehicleScheduleRepository.save(schedule);

        return convertToDto(savedSchedule);
    }

    @Override
    @Transactional
    public List<VehicleScheduleDto> createWeeklySchedule(
            UUID routeVehicleId,
            List<String> days,
            int tripsPerDay,
            String startTime,
            String endTime) {

        // Validate route vehicle assignment
        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        // Parse time strings
        LocalTime startTimeObj = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTimeObj = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

        // Create a schedule for each day
        List<VehicleSchedule> schedules = new ArrayList<>();

        for (String day : days) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());

            // Check if a schedule already exists for this day and route vehicle
            List<VehicleSchedule> existingSchedules = vehicleScheduleRepository.findAll().stream()
                    .filter(s -> s.getRouteVehicleId().equals(routeVehicleId) && s.getDayOfWeek() == dayOfWeek)
                    .collect(Collectors.toList());

            // If exists, update it
            if (!existingSchedules.isEmpty()) {
                VehicleSchedule existingSchedule = existingSchedules.get(0);
                existingSchedule.setStartTime(startTimeObj);
                existingSchedule.setEndTime(endTimeObj);
                existingSchedule.setNumberOfTrips(tripsPerDay);
                existingSchedule.setActive(true);
                schedules.add(vehicleScheduleRepository.save(existingSchedule));
            } else {
                // Create new schedule
                VehicleSchedule schedule = VehicleSchedule.builder()
                        .routeVehicleId(routeVehicleId)
                        .dayOfWeek(dayOfWeek)
                        .startTime(startTimeObj)
                        .endTime(endTimeObj)
                        .numberOfTrips(tripsPerDay)
                        .isActive(true)
                        .build();

                schedules.add(vehicleScheduleRepository.save(schedule));
            }
        }

        return schedules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleScheduleDto updateSchedule(UUID scheduleId, VehicleScheduleDto scheduleDto) {
        // Check if schedule exists
        VehicleSchedule schedule = vehicleScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        // Validate if route vehicle assignment exists
        RouteVehicle routeVehicle = routeVehicleRepository.findById(scheduleDto.getRouteVehicleId())
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        // Update schedule
        schedule.setRouteVehicleId(scheduleDto.getRouteVehicleId());
        schedule.setDayOfWeek(scheduleDto.getDayOfWeek());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setNumberOfTrips(scheduleDto.getNumberOfTrips());
        schedule.setActive(scheduleDto.isActive());

        // Save and return
        VehicleSchedule updatedSchedule = vehicleScheduleRepository.save(schedule);

        return convertToDto(updatedSchedule);
    }

    @Override
    public VehicleScheduleDto getScheduleById(UUID scheduleId) {
        VehicleSchedule schedule = vehicleScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        return convertToDto(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID scheduleId) {
        if (!vehicleScheduleRepository.existsById(scheduleId)) {
            throw new NotFoundException("Schedule not found");
        }

        vehicleScheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<VehicleScheduleDto> getSchedulesByVehicle(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found");
        }

        return vehicleScheduleRepository.findByVehicleId(vehicleId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<VehicleScheduleDto>> getWeeklyScheduleForVehicle(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found");
        }

        List<VehicleScheduleDto> schedules = vehicleScheduleRepository.findByVehicleId(vehicleId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        Map<String, List<VehicleScheduleDto>> schedulesByDay = new HashMap<>();

        // Initialize all days
        for (DayOfWeek day : DayOfWeek.values()) {
            schedulesByDay.put(day.name(), new ArrayList<>());
        }

        // Group schedules by day
        for (VehicleScheduleDto schedule : schedules) {
            schedulesByDay.get(schedule.getDayOfWeek().name()).add(schedule);
        }

        return schedulesByDay;
    }

    @Override
    public List<VehicleScheduleDto> getActiveSchedulesByVehicle(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found");
        }

        return vehicleScheduleRepository.findByVehicleIdAndIsActiveTrue(vehicleId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleScheduleDto> getSchedulesByRoute(UUID routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new NotFoundException("Route not found");
        }

        return vehicleScheduleRepository.findByRouteId(routeId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleScheduleDto> getActiveSchedulesByDay(DayOfWeek dayOfWeek) {
        return vehicleScheduleRepository.findByDayOfWeekAndIsActiveTrue(dayOfWeek)
                .stream()
                .map(this::convertToDto)
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
        // Get all active schedules for today with available vehicles
        List<VehicleSchedule> schedules = vehicleScheduleRepository.findActiveSchedulesByDayForAvailableVehicles(dayOfWeek);

        if (schedules.isEmpty()) {
            log.info("No active schedules found for {}", dayOfWeek);
            return 0;
        }

        int tripsCreated = 0;
        LocalDate targetDate = getNextDateForDayOfWeek(dayOfWeek);

        for (VehicleSchedule schedule : schedules) {
            // Get the route vehicle assignment
            RouteVehicle routeVehicle = routeVehicleRepository.findById(schedule.getRouteVehicleId())
                    .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

            // Check if we already have trips for this vehicle and route for the target date
            List<Trip> existingTrips = tripRepository.findByRouteVehicleIdAndDateRange(
                    routeVehicle.getId(),
                    targetDate.atStartOfDay().toInstant(ZoneOffset.UTC),
                    targetDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));

            if (!existingTrips.isEmpty()) {
                log.info("Trips already exist for vehicle {} and route {} on {}",
                        routeVehicle.getVehicleId(), routeVehicle.getRouteId(), targetDate);
                continue;
            }

            // Calculate time slots for trips throughout the day
            List<LocalTime> tripStartTimes = calculateTripStartTimes(
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getNumberOfTrips());

            // Create trips for each time slot
            for (LocalTime startTime : tripStartTimes) {
                Instant tripStartInstant = convertToInstant(targetDate, startTime);

                // Create a new trip
                Trip trip = Trip.builder()
                        .routeVehicleId(routeVehicle.getId())
                        .driverId(null) // Will be assigned when driver starts the trip
                        .startTime(tripStartInstant)
                        .status("PLANNED")
                        .currentStopIndex(0)
                        .ordersPickedUp(0)
                        .ordersDelivered(0)
                        .build();

                tripRepository.save(trip);
                tripsCreated++;

                log.info("Created trip for vehicle {} and route {} at {} on {}",
                        routeVehicle.getVehicleId(), routeVehicle.getRouteId(), startTime, targetDate);
            }
        }

        return tripsCreated;
    }

    @Override
    public List<Map<String, Object>> getScheduledTripsForVehicle(UUID vehicleId, DayOfWeek dayOfWeek) {
        // Find the vehicle
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        // Find active schedules for this vehicle on the specified day
        List<VehicleSchedule> schedules = vehicleScheduleRepository.findActiveSchedulesByVehicleAndDay(vehicleId, dayOfWeek);

        List<Map<String, Object>> scheduledTrips = new ArrayList<>();

        for (VehicleSchedule schedule : schedules) {
            RouteVehicle routeVehicle = routeVehicleRepository.findById(schedule.getRouteVehicleId())
                    .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

            Route route = routeRepository.findById(routeVehicle.getRouteId())
                    .orElseThrow(() -> new NotFoundException("Route not found"));

            // Calculate trip start times
            List<LocalTime> tripTimes = calculateTripStartTimes(
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getNumberOfTrips());

            for (int i = 0; i < tripTimes.size(); i++) {
                LocalTime tripTime = tripTimes.get(i);

                Map<String, Object> tripInfo = new HashMap<>();
                tripInfo.put("tripNumber", i + 1);
                tripInfo.put("vehicleId", vehicle.getVehicleId());
                tripInfo.put("vehiclePlateNumber", vehicle.getPlateNumber());
                tripInfo.put("routeId", route.getRouteId());
                tripInfo.put("routeName", route.getRouteName());
                tripInfo.put("dayOfWeek", dayOfWeek.name());
                tripInfo.put("startTime", tripTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                // Calculate estimated end time (simplistic approach)
                int tripDurationMinutes = (int) Math.ceil(route.getEstimatedDuration() / schedule.getNumberOfTrips());
                LocalTime endTime = tripTime.plusMinutes(tripDurationMinutes);
                tripInfo.put("estimatedEndTime", endTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                scheduledTrips.add(tripInfo);
            }
        }

        // Sort by start time
        scheduledTrips.sort((t1, t2) -> {
            LocalTime time1 = LocalTime.parse((String) t1.get("startTime"), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime time2 = LocalTime.parse((String) t2.get("startTime"), DateTimeFormatter.ofPattern("HH:mm"));
            return time1.compareTo(time2);
        });

        return scheduledTrips;
    }

    // Helper methods

    private VehicleScheduleDto convertToDto(VehicleSchedule schedule) {
        RouteVehicle routeVehicle = routeVehicleRepository.findById(schedule.getRouteVehicleId())
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));

        Vehicle vehicle = vehicleRepository.findById(routeVehicle.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        Route route = routeRepository.findById(routeVehicle.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found"));

        return VehicleScheduleDto.builder()
                .id(schedule.getId())
                .routeVehicleId(schedule.getRouteVehicleId())
                .vehicleId(vehicle.getVehicleId())
                .vehiclePlateNumber(vehicle.getPlateNumber())
                .routeId(route.getRouteId())
                .routeName(route.getRouteName())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .numberOfTrips(schedule.getNumberOfTrips())
                .isActive(schedule.isActive())
                .build();
    }

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

    private List<LocalTime> calculateTripStartTimes(LocalTime startTime, LocalTime endTime, int numberOfTrips) {
        List<LocalTime> startTimes = new ArrayList<>();

        if (numberOfTrips <= 0) {
            return startTimes;
        }

        // If only one trip, schedule at the start time
        if (numberOfTrips == 1) {
            startTimes.add(startTime);
            return startTimes;
        }

        // Calculate time between trips
        long totalMinutes = Duration.between(startTime, endTime).toMinutes();
        long intervalMinutes = totalMinutes / (numberOfTrips - 1);

        for (int i = 0; i < numberOfTrips; i++) {
            LocalTime tripTime = startTime.plusMinutes(i * intervalMinutes);
            startTimes.add(tripTime);
        }

        return startTimes;
    }

    private Instant convertToInstant(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC);
    }

    @Override
    public List<VehicleScheduleDto> getAllActiveSchedules(){
        return vehicleScheduleRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }
}