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
import org.locationtech.jts.triangulate.tri.Tri;
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
    private final TripHistoryRepository tripHistoryRepository;
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

    @Override
    public TripHistoryResponseDto getTripHistoryWithDetails(UUID tripId) {
        // Lấy thông tin trip hiện tại
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        // Lấy tất cả lịch sử của trip
        List<TripHistory> tripHistories = tripHistoryRepository.findByTripIdOrderByCreatedAtAsc(tripId);

        // Lấy thông tin route và các điểm dừng
        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                .orElseThrow(() -> new NotFoundException("Route schedule not found"));

        Route route = routeRepository.findById(routeSchedule.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found"));

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId());

        // Map thông tin điểm dừng vào lịch sử
        List<TripHistoryEntryDto> historyEntries = new ArrayList<>();

        // Thêm trạng thái lập kế hoạch từ thời điểm tạo trip
        historyEntries.add(TripHistoryEntryDto.builder()
                .id(UUID.randomUUID())
                .status("PLANNED")
                .timestamp(trip.getCreatedAt())
                .changedBy(trip.getChangedBy())
                .currentStopIndex(0)
                .notes("Chuyến đi được tạo")
                .build());

        // Thêm các mục lịch sử từ bảng TripHistory
        for (TripHistory history : tripHistories) {
            TripHistoryEntryDto entry = TripHistoryEntryDto.builder()
                    .id(history.getId())
                    .status(history.getStatus())
                    .timestamp(history.getCreatedAt())
                    .changedBy(history.getChangedBy())
                    .currentStopIndex(history.getCurrentStopIndex())
                    .notes(history.getNotes())
                    .build();

            // Thêm thông tin về điểm dừng hiện tại nếu có
            if (history.getCurrentStopIndex() != null && history.getCurrentStopIndex() < routeStops.size()) {
                RouteStop stop = routeStops.get(history.getCurrentStopIndex());
                Hub hub = hubRepo.findById(stop.getHubId()).orElse(null);

                if (hub != null) {
                    entry.setCurrentStopName(hub.getName());
                    entry.setCurrentStopAddress(hub.getAddress());
                }
            }

            historyEntries.add(entry);
        }

        // Thêm trạng thái hiện tại nếu khác với trạng thái lịch sử cuối cùng
        if (tripHistories.isEmpty() || !tripHistories.get(tripHistories.size() - 1).getStatus().equals(trip.getStatus().toString())) {
            TripHistoryEntryDto currentEntry = TripHistoryEntryDto.builder()
                    .id(UUID.randomUUID())
                    .status(trip.getStatus().toString())
                    .timestamp(trip.getUpdatedAt())
                    .changedBy(trip.getChangedBy())
                    .currentStopIndex(trip.getCurrentStopIndex())
                    .notes("Trạng thái hiện tại")
                    .build();

            // Thêm thông tin về điểm dừng hiện tại
            if (trip.getCurrentStopIndex() != null && trip.getCurrentStopIndex() < routeStops.size()) {
                RouteStop stop = routeStops.get(trip.getCurrentStopIndex());
                Hub hub = hubRepo.findById(stop.getHubId()).orElse(null);

                if (hub != null) {
                    currentEntry.setCurrentStopName(hub.getName());
                    currentEntry.setCurrentStopAddress(hub.getAddress());
                }
            }

            historyEntries.add(currentEntry);
        }

        // Sắp xếp các mục lịch sử theo thời gian
        historyEntries.sort(Comparator.comparing(TripHistoryEntryDto::getTimestamp));

        // Tạo stop timeline
        List<TripStopTimelineDto> stopTimeline = new ArrayList<>();

        for (int i = 0; i < routeStops.size(); i++) {
            RouteStop stop = routeStops.get(i);
            Hub hub = hubRepo.findById(stop.getHubId()).orElse(null);

            if (hub != null) {
                TripStopTimelineDto stopEntry = TripStopTimelineDto.builder()
                        .stopIndex(i)
                        .stopSequence(stop.getStopSequence())
                        .hubId(hub.getHubId())
                        .hubName(hub.getName())
                        .address(hub.getAddress())
                        .build();

                // Xác định trạng thái điểm dừng
                if (i < trip.getCurrentStopIndex()) {
                    stopEntry.setStatus("COMPLETED");

                    // Tìm thời gian đến điểm dừng này từ lịch sử
                    for (TripHistory history : tripHistories) {
                        if (history.getCurrentStopIndex() != null && history.getCurrentStopIndex() == i + 1) {
                            stopEntry.setArrivalTime(history.getCreatedAt());
                            break;
                        }
                    }
                } else if (i == trip.getCurrentStopIndex()) {
                    stopEntry.setStatus("CURRENT");
                    stopEntry.setArrivalTime(trip.getLastStopArrivalTime());
                } else {
                    stopEntry.setStatus("PENDING");
                }

                stopTimeline.add(stopEntry);
            }
        }

        // Tính toán các thống kê
        long totalDurationMinutes = 0;
        if (trip.getStartTime() != null && trip.getEndTime() != null) {
            totalDurationMinutes = Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
        } else if (trip.getStartTime() != null) {
            totalDurationMinutes = Duration.between(trip.getStartTime(), Instant.now()).toMinutes();
        }

        // Đếm số đơn hàng đã giao và tổng số
        List<TripOrder> tripOrders = tripOrderRepository.findByTripId(tripId);
        int totalOrders = tripOrders.size();
        int deliveredOrders = (int) tripOrders.stream()
                .filter(to -> "DELIVERED".equals(to.getStatus()))
                .count();

        // Tạo response DTO
        return TripHistoryResponseDto.builder()
                .tripId(trip.getId())
                .tripCode(trip.getTripCode())
                .routeId(route.getRouteId())
                .routeName(route.getRouteName())
                .routeCode(route.getRouteCode())
                .currentStatus(trip.getStatus().toString())
                .createdAt(trip.getCreatedAt())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .currentStopIndex(trip.getCurrentStopIndex())
                .totalStops(routeStops.size())
                .totalDurationMinutes(totalDurationMinutes)
                .totalOrders(totalOrders)
                .deliveredOrders(deliveredOrders)
                .completionNotes(trip.getCompletionNotes())
                .historyEntries(historyEntries)
                .stopTimeline(stopTimeline)
                .build();
    }
    // Add this new method to retrieve trip history for frontend visualization
    @Override
    public List<TripHistoryDetailDto> getTripHistoryDetails(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        List<TripHistory> tripHistories = tripHistoryRepository.findByTripIdOrderByCreatedAtAsc(tripId);

        // Retrieve route information
        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                .orElseThrow(() -> new NotFoundException("Route schedule not found"));
        Route route = routeRepository.findById(routeSchedule.getRouteId())
                .orElseThrow(() -> new NotFoundException("Route not found"));

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId());

        // Map trip histories to DTOs with added context
        return tripHistories.stream().map(history -> {
            TripHistoryDetailDto dto = new TripHistoryDetailDto();
            dto.setId(history.getId());
            dto.setTripId(history.getTripId());
            dto.setCreatedAt(history.getCreatedAt());
            dto.setStatus(history.getStatus());
            dto.setChangedBy(history.getChangedBy());
            dto.setCurrentStopIndex(history.getCurrentStopIndex());

            // Add context about the stop if available
            if (history.getCurrentStopIndex() != null && history.getCurrentStopIndex() < routeStops.size()) {
                RouteStop stop = routeStops.get(history.getCurrentStopIndex());
                Hub hub = hubRepo.findById(stop.getHubId()).orElse(null);
                if (hub != null) {
                    dto.setStopHubName(hub.getName());
                    dto.setStopAddress(hub.getAddress());
                }
            }

            // Determine next status (useful for UI visualization)
            if (tripHistories.indexOf(history) < tripHistories.size() - 1) {
                dto.setNextStatus(tripHistories.get(tripHistories.indexOf(history) + 1).getStatus());
            } else {
                dto.setNextStatus(trip.getStatus().toString());
            }

            // Calculate duration to next status
            if (tripHistories.indexOf(history) < tripHistories.size() - 1) {
                TripHistory nextHistory = tripHistories.get(tripHistories.indexOf(history) + 1);
                long durationMinutes = Duration.between(history.getCreatedAt(), nextHistory.getCreatedAt()).toMinutes();
                dto.setDurationToNextStatus(durationMinutes);
            }

            // Add additional context based on status
            switch (history.getStatus()) {
                case "PLANNED":
                    dto.setContextInfo("Trip scheduled");
                    break;
                case "CAME_FIRST_STOP":
                    dto.setContextInfo("Driver arrived at first stop");
                    break;
                case "IN_PROGRESS":
                    dto.setContextInfo("Trip in progress");
                    break;
                case "CAME_LAST_STOP":
                    dto.setContextInfo("Driver arrived at final stop");
                    break;
                case "CONFIRMED_IN":
                    dto.setContextInfo("Trip confirmed at destination hub");
                    break;
                case "COMPLETED":
                    // Calculate total trip duration
                    if (trip.getStartTime() != null && trip.getEndTime() != null) {
                        long tripDuration = Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
                        dto.setContextInfo("Trip completed in " + tripDuration + " minutes");
                    } else {
                        dto.setContextInfo("Trip completed");
                    }
                    break;
                default:
                    dto.setContextInfo("");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // Add this new method to get a timeline-friendly representation of trip status changes
    @Override
    public TripTimelineDto getTripTimeline(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        List<TripHistory> histories = tripHistoryRepository.findByTripIdOrderByCreatedAtAsc(tripId);

        // Get all route stops for this trip
        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                .orElseThrow(() -> new NotFoundException("Route schedule not found"));
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());
        List<Hub> hubs = routeStops.stream()
                .map(stop -> hubRepo.findById(stop.getHubId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Create timeline events
        List<TripTimelineEventDto> events = new ArrayList<>();

        // Add planned event (first event)
        events.add(TripTimelineEventDto.builder()
                .status("PLANNED")
                .timestamp(trip.getCreatedAt())
                .title("Trip Scheduled")
                .description("Trip was scheduled for " + trip.getDate())
                .hubName(null)  // No hub yet
                .completed(true)
                .build());

        // Add start event
        if (trip.getStartTime() != null) {
            events.add(TripTimelineEventDto.builder()
                    .status("CAME_FIRST_STOP")
                    .timestamp(trip.getStartTime())
                    .title("Trip Started")
                    .description("Driver arrived at first hub: " +
                            (hubs.size() > 0 ? hubs.get(0).getName() : "Unknown"))
                    .hubName(hubs.size() > 0 ? hubs.get(0).getName() : null)
                    .completed(true)
                    .build());
        }

        // Add stop events based on history and current trip state
        if (trip.getStatus() == TripStatus.IN_PROGRESS ||
                trip.getStatus() == TripStatus.CAME_LAST_STOP ||
                trip.getStatus() == TripStatus.CONFIRMED_IN ||
                trip.getStatus() == TripStatus.COMPLETED) {

            for (int i = 1; i <= trip.getCurrentStopIndex(); i++) {
                if (i < hubs.size()) {
                    // Find timestamp from history if possible
                    Instant timestamp = null;
                    String notes = "";

                    for (TripHistory history : histories) {
                        if (history.getCurrentStopIndex() != null && history.getCurrentStopIndex() == i) {
                            timestamp = history.getCreatedAt();
                            notes = history.getNotes() != null ? history.getNotes() : "";
                            break;
                        }
                    }

                    if (timestamp == null) {
                        // No exact match found, estimate based on trip start time and current time
                        if (trip.getStartTime() != null) {
                            long totalDuration = Duration.between(trip.getStartTime(), Instant.now()).toMinutes();
                            long estimatedMinutesPerStop = totalDuration / Math.max(1, trip.getCurrentStopIndex());
                            timestamp = trip.getStartTime().plus(Duration.ofMinutes(estimatedMinutesPerStop * i));
                        } else {
                            timestamp = Instant.now().minus(Duration.ofMinutes(30 * (trip.getCurrentStopIndex() - i)));
                        }
                    }

                    events.add(TripTimelineEventDto.builder()
                            .status("STOP_" + i)
                            .timestamp(timestamp)
                            .title("Arrived at Stop " + i)
                            .description("Driver arrived at " + hubs.get(i).getName() + (notes.isEmpty() ? "" : ": " + notes))
                            .hubName(hubs.get(i).getName())
                            .completed(true)
                            .build());
                }
            }
        }

        // Add remaining stops that haven't been visited yet
        for (int i = trip.getCurrentStopIndex() + 1; i < hubs.size(); i++) {
            events.add(TripTimelineEventDto.builder()
                    .status("STOP_" + i)
                    .timestamp(null)  // No timestamp since not reached yet
                    .title("Stop " + i)
                    .description(hubs.get(i).getName())
                    .hubName(hubs.get(i).getName())
                    .completed(false)
                    .build());
        }

        // Add completion event
        if (trip.getStatus() == TripStatus.COMPLETED) {
            events.add(TripTimelineEventDto.builder()
                    .status("COMPLETED")
                    .timestamp(trip.getEndTime())
                    .title("Trip Completed")
                    .description(trip.getCompletionNotes() != null ? trip.getCompletionNotes() : "Trip successfully completed")
                    .hubName(hubs.size() > 0 ? hubs.get(hubs.size() - 1).getName() : null)
                    .completed(true)
                    .build());
        } else if (trip.getStatus() == TripStatus.CANCELLED) {
            // Add cancelled event if applicable
            events.add(TripTimelineEventDto.builder()
                    .status("CANCELLED")
                    .timestamp(trip.getEndTime() != null ? trip.getEndTime() : Instant.now())
                    .title("Trip Cancelled")
                    .description(trip.getCompletionNotes() != null ? trip.getCompletionNotes() : "Trip was cancelled")
                    .hubName(null)
                    .completed(true)
                    .build());
        } else if (trip.getStatus() != TripStatus.PLANNED) {
            // Add future completion event
            events.add(TripTimelineEventDto.builder()
                    .status("COMPLETION")
                    .timestamp(null)  // No timestamp since not completed yet
                    .title("Trip Completion")
                    .description("Pending completion")
                    .hubName(hubs.size() > 0 ? hubs.get(hubs.size() - 1).getName() : null)
                    .completed(false)
                    .build());
        }

        // Sort events by timestamp
        events.sort(Comparator.comparing(
                event -> event.getTimestamp() != null ? event.getTimestamp() : Instant.MAX
        ));

        // Build and return the timeline
        return TripTimelineDto.builder()
                .tripId(trip.getId())
                .tripCode(trip.getTripCode())
                .currentStatus(trip.getStatus().toString())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .currentStopIndex(trip.getCurrentStopIndex())
                .totalStops(routeStops.size())
                .events(events)
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
        if (!TripStatus.PLANNED.equals(trip.getStatus())) {
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
    public TripDetailsDTO arrivedInNextStop(UUID tripId, UUID hubId, String username) {
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
        if (!TripStatus.DONE_STOP.equals(trip.getStatus()) && !TripStatus.CAME_STOP.equals(trip.getStatus()) && !TripStatus.IN_PROGRESS.equals(trip.getStatus()) && !TripStatus.PICKED_UP.equals(trip.getStatus()) && !TripStatus.CAME_FIRST_STOP.equals(trip.getStatus())  && !TripStatus.CONFIRMED_IN.equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not in progress");
        }

        // Get route stops to check if we're at the last stop
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());

        if(TripStatus.PICKED_UP.equals(trip.getStatus())) {
            trip.setStatus(TripStatus.CAME_STOP);
        }
        else if(TripStatus.CONFIRMED_IN.equals(trip.getStatus())) {
            trip.setStatus(TripStatus.CAME_STOP);
        }
        else if(TripStatus.DONE_STOP.equals(trip.getStatus())) {
            trip.setStatus(TripStatus.CAME_STOP);
        }
        //check if the hubId is the next stop

            RouteStop nextStop1 = stops.get(trip.getCurrentStopIndex() + 1);
            if (!nextStop1.getHubId().equals(hubId))
                throw new IllegalArgumentException("Not at the expected next stop");


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

//        // For orders that are being delivered to this stop, update status
//        List<Order> deliveredOrders = orderRepo.findByTripId(trip.getId()).stream()
//                .filter(order -> {
//                    // If this is a delivery stop for the order
//                    return order.getFinalHubId() != null &&
//                            order.getFinalHubId().equals(currentStop.getHubId()) &&
//                            order.getStatus() == OrderStatus.DELIVERING;
//                })
//                .collect(Collectors.toList());
//
//        // Update orders status to DELIVERED
//        for (Order order : deliveredOrders) {
//            order.setStatus(OrderStatus.DELIVERED);
//        }
//
//        // Save all updated orders
//        orderRepo.saveAll(deliveredOrders);
//
//        // Update trip stats
//        trip.setOrdersDelivered((trip.getOrdersDelivered() != null ? trip.getOrdersDelivered() : 0) + deliveredOrders.size());

        // Advance to next stop
        trip.setCurrentStopIndex(trip.getCurrentStopIndex() + 1);
        trip.setLastStopArrivalTime(Instant.now());

        // Save updated trip
        Trip updatedTrip = tripRepository.save(trip);

        // Return updated trip details
        return getTripDetailsForDriver(updatedTrip.getId(), username);
    }


    @Transactional
    @Override
    public TripDetailsDTO doneStop(UUID tripId, String username) {
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
        if (TripStatus.CONFIRMED_IN.equals(trip.getStatus())) {
            // Get route stops to check if we're at the last stop
            List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());



            // done stop
            trip.setStatus(TripStatus.DONE_STOP);
            trip.setChangedBy(username);
            Trip updatedTrip = tripRepository.save(trip);

            // Return updated trip details
            return getTripDetailsForDriver(updatedTrip.getId(), username);
        }
        else if (TripStatus.CAME_STOP.equals(trip.getStatus()) ) {
            // Get route stops to check if we're at the last stop
            List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());


            RouteStop currentStop = stops.get(trip.getCurrentStopIndex());
            // Check to see if there are any orders to be delivered at this stop
            List<TripOrder> tripOrders = tripOrderRepository.findByTripIdAndStatusOrderByCreatedAtDescAndHubId(trip.getId(),currentStop.getHubId(), "DELIVERING");
            if(!tripOrders.isEmpty()) {
                throw new IllegalStateException("Cannot skip stop with orders to be delivered");
            }
            // done stop
            trip.setStatus(TripStatus.DONE_STOP);
            trip.setChangedBy(username);
            Trip updatedTrip = tripRepository.save(trip);
            return getTripDetailsForDriver(updatedTrip.getId(), username);
        }
        else if (TripStatus.PICKED_UP.equals(trip.getStatus()) ) {
            // Get route stops to check if we're at the last stop

            // done stop
            trip.setStatus(TripStatus.DONE_STOP);
            trip.setChangedBy(username);
            Trip updatedTrip = tripRepository.save(trip);
            return getTripDetailsForDriver(updatedTrip.getId(), username);
        }

        // Save updated trip
        throw new IllegalStateException("Cannot done stop");
        // Return updated trip details
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
        if (!TripStatus.CONFIRMED_IN.equals(trip.getStatus())) {
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
                            .plannedStartTime(routeSchedule.getStartTime())
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
                .plannedStartTime(trip.getPlannedStartTime())
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
    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getAllTrips() {
        log.info("Getting all trips");
        List<Trip> trips = tripRepository.findAll();
        return trips.stream()
                .map(this::convertToTripDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TripDetailsDTO getTripById(UUID tripId) {
        log.info("Getting trip details for tripId: {}", tripId);
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));

        return convertToTripDetailsDTO(trip);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<TripDTO> getTripsWithFilter(LocalDate startDate, LocalDate endDate,
//                                            String status, UUID driverId, UUID vehicleId) {
//        log.info("Getting trips with filters - startDate: {}, endDate: {}, status: {}, driverId: {}, vehicleId: {}",
//                startDate, endDate, status, driverId, vehicleId);
//
//        List<Trip> trips = tripRepository.findTripsWithFilters(startDate, endDate, status, driverId, vehicleId);
//        return trips.stream()
//                .map(this::convertToTripDTO)
//                .collect(Collectors.toList());
//    }

    @Override
    public Trip updateTrip(UUID tripId, TripUpdateDto updateDTO, String changedBy) {
        log.info("Updating trip: {} by user: {}", tripId, changedBy);

        Trip existingTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));

        // Validate status transition if status is being updated
        if (updateDTO.getStatus() != null) {
            validateStatusTransition(existingTrip.getStatus(), updateDTO.getStatus());
        }

        // Validate field updates based on current status
        validateFieldUpdates(existingTrip.getStatus(), updateDTO);

        // Apply updates
        applyUpdates(existingTrip, updateDTO, changedBy);

        Trip savedTrip = tripRepository.save(existingTrip);
        log.info("Trip updated successfully: {}", tripId);

        return savedTrip;
    }

    @Override
    public void deleteTrip(UUID tripId) {
        log.info("Deleting trip: {}", tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));

        // Only allow deletion of PLANNED trips
        if (trip.getStatus() != TripStatus.PLANNED) {
            throw new IllegalStateException("Only PLANNED trips can be deleted. Current status: " + trip.getStatus());
        }

        tripRepository.delete(trip);
        log.info("Trip deleted successfully: {}", tripId);
    }
    private TripDetailsDTO convertToTripDetailsDTO(Trip trip) {


        // Find the route for this trip
        UUID routeScheduleId = trip.getRouteScheduleId(); // This now represents the route ID directly
        RouteSchedule routeSchedule = routeScheduleRepository.findById(routeScheduleId)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        Route route = routeRepository.findById(routeSchedule.getRouteId()).orElseThrow(() -> new NotFoundException());
        // Get route stops
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());
        List<TripOrder> tripOrders = tripOrderRepository.findByTripId(trip.getId());

        int orderCount = tripOrders.size();

        // Convert to DTOs and set status based on current stop index
        List<TripStopDTO> stopDTOs = new ArrayList<>();
        for (int i = 0; i < routeStops.size(); i++) {
            RouteStop stop = routeStops.get(i);
            Hub hub = hubRepo.findById(stop.getHubId())
                    .orElseThrow(() -> new NotFoundException("Hub not found"));

            String status;
            if (i < trip.getCurrentStopIndex() || i == trip.getCurrentStopIndex() && ("COMPLETED".equals(trip.getStatus()) || TripStatus.COMPLETED.equals(trip.getStatus()))) {
                status = "COMPLETED";
            } else if (i == trip.getCurrentStopIndex()) {
                status = "CURRENT";
            } else {
                status = "PENDING";
            }

            // Count orders for this stop

            // Calculate estimated arrival time (this would be more complex in a real system)
            LocalTime baseTime = LocalTime.of(8, 0); // Assume 8:00 AM start
            LocalTime estimatedTime = baseTime.plusMinutes(stop.getStopSequence() * 45L); // 45 min between stops

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
        int ordersDelivered = tripOrderRepository.findByTripIdAndDeliveredIsTrue(trip.getId()).size();

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

    // Private helper methods
    private void validateStatusTransition(TripStatus currentStatus, TripStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        boolean isValidTransition = false;

        if (currentStatus == TripStatus.PENDING) {
            isValidTransition = targetStatus == TripStatus.PLANNED || targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.PLANNED) {
            isValidTransition = targetStatus == TripStatus.IN_PROGRESS ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.IN_PROGRESS) {
            isValidTransition = targetStatus == TripStatus.CAME_FIRST_STOP ||
                    targetStatus == TripStatus.CAME_STOP ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.CAME_FIRST_STOP) {
            isValidTransition = targetStatus == TripStatus.PICKED_UP ||
                    targetStatus == TripStatus.CAME_STOP ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.CAME_STOP) {
            isValidTransition = targetStatus == TripStatus.PICKED_UP ||
                    targetStatus == TripStatus.DELIVERED ||
                    targetStatus == TripStatus.DONE_STOP ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.PICKED_UP) {
            isValidTransition = targetStatus == TripStatus.DELIVERED ||
                    targetStatus == TripStatus.CAME_STOP ||
                    targetStatus == TripStatus.CAME_LAST_STOP ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.DELIVERED) {
            isValidTransition = targetStatus == TripStatus.DONE_STOP ||
                    targetStatus == TripStatus.CAME_STOP ||
                    targetStatus == TripStatus.CAME_LAST_STOP ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.DONE_STOP) {
            isValidTransition = targetStatus == TripStatus.CAME_STOP ||
                    targetStatus == TripStatus.CAME_LAST_STOP ||
                    targetStatus == TripStatus.CONFIRMED_OUT ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.CAME_LAST_STOP) {
            isValidTransition = targetStatus == TripStatus.CONFIRMED_OUT ||
                    targetStatus == TripStatus.COMPLETED ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.CONFIRMED_IN) {
            isValidTransition = targetStatus == TripStatus.IN_PROGRESS ||
                    targetStatus == TripStatus.CAME_FIRST_STOP ||
                    targetStatus == TripStatus.DELAYED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.CONFIRMED_OUT) {
            isValidTransition = targetStatus == TripStatus.COMPLETED ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.DELAYED) {
            // From DELAYED, can return to previous appropriate state or cancel
            isValidTransition = targetStatus == TripStatus.IN_PROGRESS ||
                    targetStatus == TripStatus.CAME_FIRST_STOP ||
                    targetStatus == TripStatus.CAME_STOP ||
                    targetStatus == TripStatus.CAME_LAST_STOP ||
                    targetStatus == TripStatus.PICKED_UP ||
                    targetStatus == TripStatus.DELIVERED ||
                    targetStatus == TripStatus.DONE_STOP ||
                    targetStatus == TripStatus.CONFIRMED_OUT ||
                    targetStatus == TripStatus.CANCELLED;
        } else if (currentStatus == TripStatus.COMPLETED || currentStatus == TripStatus.CANCELLED) {
            isValidTransition = false; // Final states
        }

        if (!isValidTransition) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s", currentStatus, targetStatus));
        }
    }


    private void validateFieldUpdates(TripStatus currentStatus, TripUpdateDto updateDTO) {
        // Validate that only appropriate fields are updated based on current status
        switch (currentStatus) {
            case PLANNED -> {
                // Can update: driverId, vehicleId, date, plannedStartTime, status
                if (updateDTO.getEndTime() != null || updateDTO.getCompletionNotes() != null) {
                    throw new IllegalArgumentException("Cannot set completion fields for PLANNED trip");
                }
            }
            case IN_PROGRESS, CONFIRMED_IN -> {
                // Can update: currentStopIndex, lastStopArrivalTime, distanceTraveled,
                // ordersPickedUp, ordersDelivered, delayEvents, status
                if (updateDTO.getDriverId() != null || updateDTO.getVehicleId() != null ||
                        updateDTO.getDate() != null || updateDTO.getPlannedStartTime() != null) {
                    throw new IllegalArgumentException("Cannot update basic trip info while trip is in progress");
                }
            }
            case COMPLETED, CANCELLED -> {
                throw new IllegalArgumentException("Cannot update completed or cancelled trips");
            }
        }

        // Validate completion requirements
        if (updateDTO.getStatus() == TripStatus.COMPLETED) {
            if (updateDTO.getEndTime() == null) {
                throw new IllegalArgumentException("End time is required when completing trip");
            }
            if (updateDTO.getOrdersPickedUp() == null || updateDTO.getOrdersDelivered() == null) {
                throw new IllegalArgumentException("Order counts are required when completing trip");
            }
        }
    }

    private void applyUpdates(Trip existingTrip, TripUpdateDto updateDTO, String changedBy) {
        if (updateDTO.getStatus() != null) {
            existingTrip.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getDriverId() != null) {
            existingTrip.setDriverId(updateDTO.getDriverId());
        }
        if (updateDTO.getVehicleId() != null) {
            existingTrip.setVehicleId(updateDTO.getVehicleId());
        }
        if (updateDTO.getDate() != null) {
            existingTrip.setDate(updateDTO.getDate());
        }
        if (updateDTO.getPlannedStartTime() != null) {
            existingTrip.setPlannedStartTime(updateDTO.getPlannedStartTime());
        }
        if (updateDTO.getStartTime() != null) {
            existingTrip.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            existingTrip.setEndTime(updateDTO.getEndTime());
        }
        if (updateDTO.getCurrentStopIndex() != null) {
            existingTrip.setCurrentStopIndex(updateDTO.getCurrentStopIndex());
        }
        if (updateDTO.getLastStopArrivalTime() != null) {
            existingTrip.setLastStopArrivalTime(updateDTO.getLastStopArrivalTime());
        }
        if (updateDTO.getDistanceTraveled() != null) {
            existingTrip.setDistanceTraveled(updateDTO.getDistanceTraveled());
        }
        if (updateDTO.getCompletionNotes() != null) {
            existingTrip.setCompletionNotes(updateDTO.getCompletionNotes());
        }
        if (updateDTO.getOrdersPickedUp() != null) {
            existingTrip.setOrdersPickedUp(updateDTO.getOrdersPickedUp());
        }
        if (updateDTO.getOrdersDelivered() != null) {
            existingTrip.setOrdersDelivered(updateDTO.getOrdersDelivered());
        }
        if (updateDTO.getDelayEvents() != null) {
            existingTrip.setDelayEvents(updateDTO.getDelayEvents());
        }

        // Set system fields
        existingTrip.setChangedBy(changedBy);
        existingTrip.setUpdatedAt(Instant.now());
    }
}