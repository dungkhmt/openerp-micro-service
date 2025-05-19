package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final OrderRepo orderRepo;
    private final DriverRepo driverRepo;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final HubRepo hubRepo;
    private final VehicleRepository vehicleRepository;
    private final VehicleDriverRepository vehicleDriverRepository;
    private final TripOrderRepository tripOrderRepository;
    private final RouteScheduleRepository routeScheduleRepository;
    private final ScheduleVehicleAssignmentRepository scheduleVehicleAssignmentRepository;
    private final OrderItemRepo orderItemRepo;
    /**
     * Get all trips for a driver categorized as active, scheduled, or completed
     */
    @Override
    public List<TripDTO> getAllTripsForDriver(String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Get all trips assigned to this driver
        List<Trip> allTrips = tripRepository.findByDriverId(driver.getId());

        // Convert and categorize trips
        return allTrips.stream().map(this::convertToTripDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> getAllTripsForDriverToday(String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Get all trips assigned to this driver
        List<Trip> allTrips = tripRepository.findByDriverIdAndDate(driver.getId(),LocalDate.now());

        // Convert and categorize trips
        return allTrips.stream().map(this::convertToTripDTO)
                .collect(Collectors.toList());
    }


    /**
     * Get detailed information about a specific trip
     */
    @Override
    public TripDetailsDTO getTripDetailsForDriver(UUID tripId, String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Get the trip
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        // Verify driver is assigned to this trip
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }

        // Find the route for this trip
        UUID routeScheduleId = trip.getRouteScheduleId(); // This now represents the route ID directly
        RouteSchedule routeSchedule = routeScheduleRepository.findById(routeScheduleId)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        Route route = routeRepository.findById(routeSchedule.getRouteId()).orElseThrow(() -> new NotFoundException());
        // Get route stops
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());
        List<TripOrder> tripOrders = tripOrderRepository.findByTripId(tripId);

        int orderCount = tripOrders.size();

        // Convert to DTOs and set status based on current stop index
        List<TripStopDTO> stopDTOs = new ArrayList<>();
        for (int i = 0; i < routeStops.size(); i++) {
            RouteStop stop = routeStops.get(i);
            Hub hub = hubRepo.findById(stop.getHubId())
                    .orElseThrow(() -> new NotFoundException("Hub not found"));

            String status;
            if (i < trip.getCurrentStopIndex() || i == trip.getCurrentStopIndex() && "COMPLETED".equals(trip.getStatus())) {
                status = "COMPLETED";
            } else if (i == trip.getCurrentStopIndex()) {
                status = "CURRENT";
            } else {
                status = "PENDING";
            }

            // Count orders for this stop

            // Calculate estimated arrival time (this would be more complex in a real system)
            LocalTime baseTime = LocalTime.of(8, 0); // Assume 8:00 AM start
            LocalTime estimatedTime = baseTime.plusMinutes(stop.getStopSequence() * 45); // 45 min between stops

            TripStopDTO stopDTO = TripStopDTO.builder()
                    .id(stop.getId())
                    .hubId(hub.getHubId())
                    .hubName(hub.getName())
                    .address(hub.getAddress())
                    .latitude(hub.getLatitude())
                    .longitude(hub.getLongitude())
                    .stopSequence(stop.getStopSequence())
                    .estimatedArrivalTime(estimatedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .status(status)
                    .orderCount(orderCount)
                    .build();

            stopDTOs.add(stopDTO);
        }
        List<OrderSummaryDTO> orderDtos = tripOrders.stream()
                .map(tripOrder -> {
                    Order order = orderRepo.findById(tripOrder.getOrderId())
                            .orElseThrow(() -> new NotFoundException("Order not found"));
                    return new OrderSummaryDTO(order); // You need this conversion step
                })
                .collect(Collectors.toList());
        // Count completed orders
        int ordersDelivered = tripOrderRepository.findByTripIdAndDeliveredIsTrue(tripId).size();

        return TripDetailsDTO.builder()
                .id(trip.getId())
                .routeScheduleId(routeScheduleId)
                .routeName(route.getRouteName())
                .status(trip.getStatus())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .currentStopIndex(trip.getCurrentStopIndex())
                .totalStops(routeStops.size())
                .ordersCount(orderCount)
                .ordersDelivered(ordersDelivered)
                .stops(stopDTOs)
                .orders(orderDtos)
                .build();
    }

    /**
     * Start a scheduled trip
     */
    @Transactional
    @Override
    public TripDetailsDTO startTrip(UUID tripId, String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Get the trip
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        // Verify driver is assigned to this trip
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }

        // Verify trip is in PLANNED status
        if (!"PLANNED".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not in PLANNED status");
        }

        // Update trip status
        trip.setStatus(TripStatus.CAME_FIRST_STOP);
        trip.setStartTime(Instant.now());
        trip.setCurrentStopIndex(0); // Start at first stop

        // Update vehicle status if needed
        // Find the vehicle assigned to this driver
        VehicleDriver vehicleDriver = vehicleDriverRepository.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleDriver.getVehicleId())
                    .orElse(null);
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.TRANSITING);
                vehicleRepository.save(vehicle);
            }
        }

        // Save trip
        Trip updatedTrip = tripRepository.save(trip);

        // Return detailed trip info
        return getTripDetailsForDriver(updatedTrip.getId(), username);
    }

    /**
     * Advance to next stop in a trip
     */
    @Transactional
    @Override
    public TripDetailsDTO advanceToNextStop(UUID tripId, String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Get the trip
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId()).orElseThrow(() -> new NotFoundException("Route not found"));

        // Verify driver is assigned to this trip
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }

        // Verify trip is in progress
        if (!"IN_PROGRESS".equals(trip.getStatus()) && !"CAME_FIRST_STOP".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not in progress");
        }

        // Get route stops to check if we're at the last stop
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());

        // Check if already at last stop
        if (trip.getCurrentStopIndex() >= stops.size() - 1) {
            throw new IllegalStateException("Already at the last stop");
        }

        // Process orders at current stop - update statuses as needed
        RouteStop currentStop = stops.get(trip.getCurrentStopIndex());
        RouteStop nextStop = stops.get(trip.getCurrentStopIndex() + 1);

//        // For orders that are picked up at this stop, update status
//        List<Order> ordersToUpdate = orderRepo.findByRouteVehicleId(trip.getRouteScheduleId()).stream()
//                .filter(order -> {
//                    // If this is a pickup stop for the order
//                    return order.getOriginHubId() != null &&
//                            order.getOriginHubId().equals(currentStop.getHubId()) &&
//                            order.getStatus() == OrderStatus.COLLECTED_HUB;
//                })
//                .collect(Collectors.toList());
//
//        // Update orders status to DELIVERING
//        for (Order order : ordersToUpdate) {
//            order.setStatus(OrderStatus.DELIVERING);
//        }

        // For orders that are being delivered to this stop, update status
        List<Order> deliveredOrders = orderRepo.findByTripId(trip.getId()).stream()
                .filter(order -> {
                    // If this is a delivery stop for the order
                    return order.getFinalHubId() != null &&
                            order.getFinalHubId().equals(currentStop.getHubId()) &&
                            order.getStatus() == OrderStatus.DELIVERING;
                })
                .collect(Collectors.toList());

//        // Update orders status to DELIVERED
//        for (Order order : deliveredOrders) {
//            order.setStatus(OrderStatus.DELIVERED);
//        }
//
//        // Save all updated orders
//        orderRepo.saveAll(ordersToUpdate);
//        orderRepo.saveAll(deliveredOrders);

//        // Update trip stats
//        trip.setOrdersPickedUp((trip.getOrdersPickedUp() != null ? trip.getOrdersPickedUp() : 0) + ordersToUpdate.size());
//        trip.setOrdersDelivered((trip.getOrdersDelivered() != null ? trip.getOrdersDelivered() : 0) + deliveredOrders.size());

        // Advance to next stop
        trip.setCurrentStopIndex(trip.getCurrentStopIndex() + 1);
        trip.setLastStopArrivalTime(Instant.now());

        // Save updated trip
        Trip updatedTrip = tripRepository.save(trip);

        // Return updated trip details
        return getTripDetailsForDriver(updatedTrip.getId(), username);
    }

    /**
     * Complete a trip
     */
    @Transactional
    @Override
    public TripSummaryDTO completeTrip(UUID tripId, String username, String completionNotes) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Get the trip
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        // Verify driver is assigned to this trip
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }

        // Verify trip is in progress
        if (!"CONFIRMED_IN".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not confirmed in!");
        }

        // Update any remaining orders
        List<Order> remainingOrders = orderRepo.findByTripId(trip.getId()).stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERING)
                .collect(Collectors.toList());

        // Update orders to delivered status
        for (Order order : remainingOrders) {
            order.setStatus(OrderStatus.DELIVERED_FAILED);
        }
        orderRepo.saveAll(remainingOrders);

        // Find associated vehicle and update status
        VehicleDriver vehicleDriver = vehicleDriverRepository.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleDriver.getVehicleId())
                    .orElse(null);
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }
        }
        RouteStop lastStop = getCurrentRouteStop(trip.getId());

        // Complete the trip
        Instant endTime = Instant.now();
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(endTime);
        trip.setCompletionNotes(completionNotes);
        trip.setOrdersDelivered(orderRepo.findByTripId(trip.getId()).size());

        Trip completedTrip = tripRepository.save(trip);

        // Calculate summary data
        long durationMinutes = 0;
        if (completedTrip.getStartTime() != null && completedTrip.getEndTime() != null) {
            durationMinutes = Duration.between(completedTrip.getStartTime(), completedTrip.getEndTime()).toMinutes();
        }

        // Get route stops
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(trip.getRouteScheduleId());

        // Count failed orders if any
        int ordersFailed = 0; // In a real implementation, track failed deliveries

        // Build and return summary
        return TripSummaryDTO.builder()
                .tripId(completedTrip.getId())
                .startTime(completedTrip.getStartTime())
                .endTime(completedTrip.getEndTime())
                .durationMinutes(durationMinutes)
                .totalStops(stops.size())
                .ordersDelivered(completedTrip.getOrdersDelivered())
                .ordersFailed(ordersFailed)
                .completionNotes(completedTrip.getCompletionNotes())
                .build();
    }

    /**
     * Create a new trips for week
     */
    @Transactional
    @Override
    public List<Trip> createTripThisWeek() {
        List<RouteSchedule> routeSchedules = routeScheduleRepository.findAllByIsActiveIsTrue();
        if (routeSchedules.isEmpty()) {
            throw new NotFoundException("No route schedules found");
        }
        List<Trip> trips = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int currentDayOfWeek = today.getDayOfWeek().getValue();

        for (RouteSchedule routeSchedule : routeSchedules) {
            List<ScheduleVehicleAssignment> scheduleVehicleAssignments = scheduleVehicleAssignmentRepository.findAllByRouteScheduleIdAndUnassignedAtIsNull(routeSchedule.getId());
            if (scheduleVehicleAssignments.isEmpty()) {
                continue;
            }
            int scheduleDayOfWeek = routeSchedule.getDayOfWeek().getValue();
            LocalDate tripDate = today.plusDays(scheduleDayOfWeek - currentDayOfWeek);
            if (tripDate.isBefore(today)) {
                continue;
            }

            for (ScheduleVehicleAssignment scheduleVehicleAssignment : scheduleVehicleAssignments) {
                VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleIdAndUnassignedAtIsNull(scheduleVehicleAssignment.getVehicleId());
                if (vehicleDriver == null) {
                    throw new NotFoundException("Not assignment found with username: " + scheduleVehicleAssignment.getVehicleId());
                }

                // Create new trip
                Trip trip = Trip.builder()
                        .routeScheduleId(routeSchedule.getId())
                        .vehicleId(vehicleDriver.getVehicleId())
                        .driverId(vehicleDriver.getDriverId())
                        .date(tripDate)
                        .status(TripStatus.PLANNED)
                        .currentStopIndex(0)
                        .ordersPickedUp(0)
                        .ordersDelivered(0)
                        .build();
                trips.add(trip);
            }

        }


            // Save and return the trip
            return tripRepository.saveAll(trips);
        }


    @Transactional
    @Override
    public List<Trip> createTripFromTo(LocalDate startDate, LocalDate endDate) {
        List<RouteSchedule> routeSchedules = routeScheduleRepository.findAllByIsActiveIsTrue();
        if (routeSchedules.isEmpty()) {
            throw new NotFoundException("No route schedules found");
        }
        List<Trip> trips = new ArrayList<>();
        LocalDate today = LocalDate.now();
//        if (startDate.isBefore(today)) {
//            throw new IllegalStateException("Start date must be after today");
//        }
        int startDateDayOfWeek = startDate.getDayOfWeek().getValue();
        for (RouteSchedule routeSchedule : routeSchedules) {
            List<ScheduleVehicleAssignment> scheduleVehicleAssignments = scheduleVehicleAssignmentRepository.findAllByRouteScheduleIdAndUnassignedAtIsNull(routeSchedule.getId());
            if (scheduleVehicleAssignments.isEmpty()) {
                continue;
            }
            int scheduleDayOfWeek = routeSchedule.getDayOfWeek().getValue();

            LocalDate tripDate = startDate.plusDays(scheduleDayOfWeek - startDateDayOfWeek);
            if (tripDate.isBefore(today)) {
                tripDate = tripDate.plusDays(7);
            }
            while (!tripDate.isAfter(endDate)) {
                for (ScheduleVehicleAssignment scheduleVehicleAssignment : scheduleVehicleAssignments) {
                    VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleIdAndUnassignedAtIsNull(scheduleVehicleAssignment.getVehicleId());
                    if (vehicleDriver == null) {
                        log.warn("Not assignment found with username: " + scheduleVehicleAssignment.getVehicleId());
                        continue;
                    }

                    // Create new trip
                    Trip trip = Trip.builder()
                            .routeScheduleId(routeSchedule.getId())
                            .vehicleId(vehicleDriver.getVehicleId())
                            .driverId(vehicleDriver.getDriverId())
                            .date(tripDate)
                            .status(TripStatus.PLANNED)
                            .currentStopIndex(0)
                            .ordersPickedUp(0)
                            .ordersDelivered(0)
                            .build();
                    trips.add(trip);
                }
                tripDate = tripDate.plusDays(7);
            }
        }
        return tripRepository.saveAll(trips);
    }




    /**
     * Create a new trip for today
     */
    @Transactional
    @Override
    public Trip createTripForToday(UUID routeScheduleId, String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Check if there's already an active trip for this route vehicle
        Optional<Trip> existingTrip = tripRepository.findActiveByRouteScheduleId(routeScheduleId);
        if (existingTrip.isPresent()) {
            return existingTrip.get();
        }

        // Create new trip
        Trip trip = Trip.builder()
                .routeScheduleId(routeScheduleId)
                .driverId(driver.getId())
                .status(TripStatus.PLANNED)
                .currentStopIndex(0)
                .ordersPickedUp(0)
                .ordersDelivered(0)
                .build();

        // Save and return the trip
        return tripRepository.save(trip);
    }

    @Override
    public RouteStop getCurrentRouteStop(UUID tripId){
        // Get the trip
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId()).orElseThrow(() -> new NotFoundException("Route not found"));

        // Get route stops to check if we're at the last stop
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());



        // Process orders at current stop - update statuses as needed
        return stops.get(trip.getCurrentStopIndex());
    }
    // Helper methods

    /**
     * Convert Trip entity to TripDTO
     */
    private TripDTO convertToTripDTO(Trip trip) {
        UUID routeScheduleId = trip.getRouteScheduleId();
        RouteSchedule routeSchedule = routeScheduleRepository.findById(routeScheduleId).get();
        Route route = routeRepository.findById(routeSchedule.getRouteId()).orElse(null);

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId());

        List<TripOrder> tripOrders = tripOrderRepository.findByTripId(trip.getId());
        List<Order> orders = tripOrders.stream().map(tripOrder -> orderRepo.findById(tripOrder.getOrderId()).orElse(null)).toList();

        if(orders.size() != tripOrders.size()) {
            throw new IllegalStateException("Trip order IDs do not match order IDs in the order table");
        }
        // Count delivered orders
        int ordersDelivered = (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED ||
                        order.getStatus() == OrderStatus.COMPLETED)
                .count();

        int packagesCount = orders.stream().mapToInt(order -> orderItemRepo.countAllByOrderId(order.getId())).sum();

        return TripDTO.builder()
                .id(trip.getId())
                .routeScheduleId(routeScheduleId)
                .routeName(route.getRouteName())
                .routeCode(route.getRouteCode())
                .status(trip.getStatus())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .date(trip.getDate())
                .currentStopIndex(trip.getCurrentStopIndex())
                .totalStops(routeStops.size())
                .ordersCount(tripOrders.size())
                .packagesCount(packagesCount)
                .ordersDelivered(ordersDelivered)
                .build();
    }

    @Override
    public Map<String, Object> getTripDetails(UUID tripId, String username) {
        // This existing method from the interface can delegate to the new implementation
        TripDetailsDTO details = getTripDetailsForDriver(tripId, username);
        Map<String, Object> result = new HashMap<>();
        result.put("trip", details);
        return result;
    }

    @Override
    public List<RouteStopDto> getTripStops(UUID tripId, String username) {
        // Convert from TripDetailsDTO
        TripDetailsDTO details = getTripDetailsForDriver(tripId, username);
        return details.getStops().stream()
                .map(stop -> {
                    RouteStopDto dto = new RouteStopDto();
                    dto.setId(stop.getId());
                    dto.setHubId(stop.getHubId());
                    dto.setHubName(stop.getHubName());
                    dto.setHubAddress(stop.getAddress());
                    dto.setHubLatitude(stop.getLatitude());
                    dto.setHubLongitude(stop.getLongitude());
                    dto.setStopSequence(stop.getStopSequence());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> completeTripWithSummary(UUID tripId, String username, String notes) {
        // Delegate to the new implementation
        TripSummaryDTO summary = completeTrip(tripId, username, notes);
        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        return result;
    }

    @Override
    public List<TripDTO> getTripsForHubToday(UUID hubId) {
        // Get all trips for today
        List<Trip> trips = tripRepository.findAllTripsByHubIdAndDate(hubId, LocalDate.now());
        return trips.stream().map(this::convertToTripDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<TripDTO> getTripsForHubTodayStart(UUID hubId) {
        // Get all trips for today start
        List<Trip> trips = tripRepository.findAllTripsByHubIdAndDateStart(hubId, LocalDate.now());
        return trips.stream().map(this::convertToTripDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<TripDTO> getTripsForHubTodayThrough(UUID hubId) {
        // Get all trips for today start
        List<Trip> trips = tripRepository.findAllTripsByHubIdAndDateThrough(hubId, LocalDate.now());
        return trips.stream().map(this::convertToTripDTO)
                .collect(Collectors.toList());
    }
}