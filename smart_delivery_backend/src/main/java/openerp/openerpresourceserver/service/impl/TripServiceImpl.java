package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.RouteStopDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final RouteStopRepository routeStopRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepo driverRepo;
    private final OrderRepo orderRepo;
    private final HubRepo hubRepository;
    private final RouteRepository routeRepository;

    @Override
    @Transactional
    public Trip createTripForToday(UUID routeVehicleId, String username) {
//        // Validate driver
//        Driver driver = driverRepo.findByUsername(username);
//        if (driver == null) {
//            throw new NotFoundException("Driver not found with username: " + username);
//        }
//
//        // Validate route vehicle
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
//                .orElseThrow(() -> new NotFoundException("Route vehicle not found with ID: " + routeVehicleId));
//
//        // Check if there's already an active trip for this route vehicle
//        Optional<Trip> existingTrip = tripRepository.findActiveByRouteVehicleId(routeVehicleId);
//        if (existingTrip.isPresent()) {
//            return existingTrip.get();
//        }
//
//        // Create new trip
//        Trip trip = new Trip();
//        trip.setRouteVehicleId(routeVehicleId);
//        trip.setDriverId(driver.getId());
//        trip.setStartTime(Instant.now());
//        trip.setStatus("IN_PROGRESS");
//        trip.setCurrentStopIndex(0); // Start at first stop
//
//        // Save and return the trip
//        return tripRepository.save(trip);
        return null;
    }

    @Override
    public Trip getActiveTripForRouteVehicle(UUID routeVehicleId, String username) {
        // Validate driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find active trip
        return tripRepository.findActiveByRouteVehicleId(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("No active trip found for route vehicle ID: " + routeVehicleId));
    }

    @Override
    public List<Trip> getActiveTripsForDriver(String username) {
        // Validate driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find all active trips for this driver
        return tripRepository.findActiveByDriverId(driver.getId());
    }

    @Transactional
    @Override
    public Trip advanceToNextStop(UUID tripId, String username) {
//        // Validate driver
//        Driver driver = driverRepo.findByUsername(username);
//        if (driver == null) {
//            throw new NotFoundException("Driver not found with username: " + username);
//        }
//
//        // Get the trip
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));
//
//        // Validate driver is assigned to this trip
//        if (!trip.getDriverId().equals(driver.getId())) {
//            throw new IllegalArgumentException("Driver is not assigned to this trip");
//        }
//
//        // Validate trip is in progress
//        if (!"IN_PROGRESS".equals(trip.getStatus())) {
//            throw new IllegalStateException("Trip is not in progress");
//        }
//
//        // Get all stops for the route
//        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(
//                routeVehicleRepository.findById(trip.getRouteVehicleId())
//                        .orElseThrow(() -> new NotFoundException("Route vehicle not found"))
//                        .getRouteId()
//        );
//
//        // Check if we're at the last stop
//        if (trip.getCurrentStopIndex() >= stops.size() - 1) {
//            throw new IllegalStateException("Already at the last stop");
//        }
//
//        // Advance to next stop
//        trip.setCurrentStopIndex(trip.getCurrentStopIndex() + 1);
//
//        // Record arrival time at new stop
//        trip.setLastStopArrivalTime(Instant.now());
//
//        // Save and return the updated trip
//        return tripRepository.save(trip);
        return null;

    }

    @Override
    public List<RouteStopDto> getTripStops(UUID tripId, String username) {
//        // Validate driver
//        Driver driver = driverRepo.findByUsername(username);
//        if (driver == null) {
//            throw new NotFoundException("Driver not found with username: " + username);
//        }
//
//        // Get the trip
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));
//
//        // Get the route vehicle
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(trip.getRouteVehicleId())
//                .orElseThrow(() -> new NotFoundException("Route vehicle not found"));
//
//        // Get all stops for the route
//        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(routeVehicle.getRouteId());
//
//        // Convert to DTOs with additional information
//        return stops.stream()
//                .map(stop -> {
//                    // Get hub details
//                    Hub hub = hubRepository.findById(stop.getHubId())
//                            .orElse(null);
//
//                    RouteStopDto dto = new RouteStopDto();
//                    dto.setId(stop.getId());
//                    dto.setHubId(stop.getHubId());
//                    dto.setStopSequence(stop.getStopSequence());
//                    dto.setEstimatedWaitTime(stop.getEstimatedWaitTime());
//
//                    if (hub != null) {
//                        dto.setHubName(hub.getName());
//                        dto.setHubAddress(hub.getAddress());
//                        dto.setHubLatitude(hub.getLatitude());
//                        dto.setHubLongitude(hub.getLongitude());
//                    }
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
        return null;

    }

    @Override
    public Map<String, Object> getTripDetails(UUID tripId, String username) {
//        // Validate driver
//        Driver driver = driverRepo.findByUsername(username);
//        if (driver == null) {
//            throw new NotFoundException("Driver not found with username: " + username);
//        }
//
//        // Get the trip
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));
//
//        // Get the route vehicle
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(trip.getRouteVehicleId())
//                .orElseThrow(() -> new NotFoundException("Route vehicle not found"));
//
//        // Get the route
//        Route route = routeRepository.findById(routeVehicle.getRouteId())
//                .orElseThrow(() -> new NotFoundException("Route not found"));
//
//        // Get all stops for the route
//        List<RouteStopDto> stops = getTripStops(tripId, username);
//
//        // Get the current stop
//        RouteStopDto currentStop = null;
//        if (trip.getCurrentStopIndex() >= 0 && trip.getCurrentStopIndex() < stops.size()) {
//            currentStop = stops.get(trip.getCurrentStopIndex());
//        }
//
//        // Get next stop
//        RouteStopDto nextStop = null;
//        if (trip.getCurrentStopIndex() + 1 < stops.size()) {
//            nextStop = stops.get(trip.getCurrentStopIndex() + 1);
//        }
//
//        // Get orders for this trip
//        List<Order> orders = orderRepo.findByRouteVehicleId(trip.getRouteVehicleId());
//
//        // Build response
//        Map<String, Object> result = new HashMap<>();
//        result.put("trip", trip);
//        result.put("routeVehicle", routeVehicle);
//        result.put("route", route);
//        result.put("stops", stops);
//        result.put("currentStop", currentStop);
//        result.put("nextStop", nextStop);
//        result.put("orderCount", orders.size());
//        result.put("deliveredOrderCount", orders.stream()
//                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
//                .count());
//
//        return result;
        return null;

    }

    @Override
    @Transactional
    public Map<String, Object> completeTripWithSummary(UUID tripId, String username, String notes) {
//        // Validate driver
//        Driver driver = driverRepo.findByUsername(username);
//        if (driver == null) {
//            throw new NotFoundException("Driver not found with username: " + username);
//        }
//
//        // Get the trip
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));
//
//        // Validate driver is assigned to this trip
//        if (!trip.getDriverId().equals(driver.getId())) {
//            throw new IllegalArgumentException("Driver is not assigned to this trip");
//        }
//
//        // Mark trip as completed
//        trip.setStatus("COMPLETED");
//        trip.setEndTime(Instant.now());
//        trip.setCompletionNotes(notes);
//
//        // Update route vehicle status
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(trip.getRouteVehicleId())
//                .orElseThrow(() -> new NotFoundException("Route vehicle not found"));
//
//
//        // Update vehicle status
//        Vehicle vehicle = vehicleRepository.findById(routeVehicle.getVehicleId())
//                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
//
//        vehicle.setStatus(VehicleStatus.AVAILABLE);
//        vehicleRepository.save(vehicle);
//
//        // Check any remaining orders in DELIVERING status and mark them as DELIVERED_HUB
//        List<Order> deliveringOrders = orderRepo.findByRouteVehicleIdAndStatus(
//                trip.getRouteVehicleId(), OrderStatus.DELIVERING);
//
//        for (Order order : deliveringOrders) {
//            order.setStatus(OrderStatus.DELIVERED);
//            // We'll just update the order status without creating a tracking event
//            // since OrderTrackingEventRepository is not available
//        }
//
//        orderRepo.saveAll(deliveringOrders);
//
//        // Save trip
//        Trip savedTrip = tripRepository.save(trip);
//
//        // Build summary
//        Map<String, Object> summary = new HashMap<>();
//        summary.put("tripId", savedTrip.getId());
//        summary.put("startTime", savedTrip.getStartTime());
//        summary.put("endTime", savedTrip.getEndTime());
//        summary.put("duration", (savedTrip.getEndTime().toEpochMilli() - savedTrip.getStartTime().toEpochMilli()) / 60000); // in minutes
//        summary.put("routeVehicleId", routeVehicle.getId());
//        summary.put("routeId", routeVehicle.getRouteId());
//        summary.put("vehicleId", vehicle.getVehicleId());
//        summary.put("ordersDelivered", deliveringOrders.size());
//
//        return summary;
        return null;

    }
}