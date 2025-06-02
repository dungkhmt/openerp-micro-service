package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.service.DriverService;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final TripService tripService;

    /**
     * Get all trips (active, scheduled, and completed) for the logged-in driver
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/trips")
    public ResponseEntity<List<TripDTO>> getAllDriverTrips(Principal principal) {
        String username = principal.getName();
        List<TripDTO> tripsDTO = tripService.getAllTripsForDriver(username);
        return ResponseEntity.ok(tripsDTO);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/trips/today")
    public ResponseEntity<List<TripDTO>> getAllDriverTripsToday(Principal principal) {
        String username = principal.getName();
        List<TripDTO> tripsDTO = tripService.getAllTripsForDriverToday(username);
        return ResponseEntity.ok(tripsDTO);
    }


    /**
     * Get a specific trip with all details (including stops and orders)
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDetailsDTO> getTripDetails(
            @PathVariable UUID tripId,
            Principal principal) {
        String username = principal.getName();
        TripDetailsDTO tripDetails = tripService.getTripDetailsForDriver(tripId, username);
        return ResponseEntity.ok(tripDetails);
    }
    @GetMapping("/trips/{tripId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<TripHistoryResponseDto> getTripHistory(@PathVariable UUID tripId) {
        return ResponseEntity.ok(tripService.getTripHistoryWithDetails(tripId));
    }
    /**
     * Start a scheduled trip
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trips/{tripId}/start")
    public ResponseEntity<TripDetailsDTO> startTrip(
            @PathVariable UUID tripId,
            Principal principal) {
        String username = principal.getName();
        TripDetailsDTO updatedTrip = tripService.startTrip(tripId, username);
        return ResponseEntity.ok(updatedTrip);
    }

    /**
     * Advance to the next stop in a trip
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trips/{tripId}/advance")
    public ResponseEntity<TripDetailsDTO> advanceToNextStop(
            @PathVariable UUID tripId, @RequestParam UUID hubId,
            Principal principal) {
        String username = principal.getName();
        TripDetailsDTO updatedTrip = tripService.arrivedInNextStop(tripId, hubId, username);
        return ResponseEntity.ok(updatedTrip);
    }
    /**
     * Mark a trip as completed
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trips/{tripId}/doneStop")
    public ResponseEntity<TripDetailsDTO> doneStop(
            @PathVariable UUID tripId,
            @RequestBody(required = false) Map<String, String> notes,
            Principal principal) {
        String username = principal.getName();
        String completionNotes = notes != null ? notes.getOrDefault("notes", "") : "";
        TripDetailsDTO summary = tripService.doneStop(tripId, username);
        return ResponseEntity.ok(summary);
    }

    /**
     * Mark a trip as completed
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trips/{tripId}/complete")
    public ResponseEntity<TripSummaryDTO> completeTrip(
            @PathVariable UUID tripId,
            @RequestBody(required = false) Map<String, String> notes,
            Principal principal) {
        String username = principal.getName();
        String completionNotes = notes != null ? notes.getOrDefault("notes", "") : "";
        TripSummaryDTO summary = tripService.completeTrip(tripId, username, completionNotes);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get the driver's assigned vehicle
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/vehicle")
    public ResponseEntity<VehicleDto> getAssignedVehicle(Principal principal) {
        String username = principal.getName();
        System.out.println("DriverController.getAssignedVehicle: " + principal);
        VehicleDto vehicle = driverService.getDriverVehicleByUsername(username);
        return ResponseEntity.ok(vehicle);
    }


    /**
     * Get pending collection orders for the driver's vehicle at a specific hub
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/trip/{tripId}/pending-pickups")
    public ResponseEntity<List<OrderForTripDto>> getPendingPickupOrders(
            Principal principal,
            @PathVariable UUID tripId) {
        String username = principal.getName();
        System.out.println("DriverController.getAssignedVehicle: " + principal);

        List<OrderForTripDto> orders = driverService.getPendingPickupOrdersForDriver(username, tripId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Mark orders as picked up (COLLECTED_HUB -> DELIVERING)
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/pickup-orders")
    public ResponseEntity<Void> pickupOrders(@RequestBody PickupOrdersRequest request, Principal principal) {
        String username = principal.getName();
        driverService.pickupOrders(username, request.getOrderItemIds(), request.getTripId());
        System.out.println("DriverController.getAssignedVehicle: " + principal);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark orders as delivered to destination hub (DELIVERING -> DELIVERED)
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/deliver-orders")
    public ResponseEntity<Void> deliverOrders(@RequestBody OrderIdListsDto orderIdListsDto,  Principal principal) {
        String username = principal.getName();
        driverService.deliverOrders(username, orderIdListsDto.getSuccessOrderIds(), orderIdListsDto.getFailOrderIds());
        return ResponseEntity.ok().build();
    }

    /**
     * Update individual order status
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> request,
            Principal principal) {
        String username = principal.getName();
        OrderStatus status = OrderStatus.valueOf(request.get("status"));
        driverService.updateOrderStatus(username, orderId, status);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all orders currently assigned to the driver's vehicle
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/current-orders/{tripId}")
    public ResponseEntity<List<OrderForTripDto>> getCurrentOrders(Principal principal,@PathVariable UUID tripId) {
        String username = principal.getName();
        List<OrderForTripDto> orders = driverService.getCurrentOrderItemsForDriver(username, tripId);
        return ResponseEntity.ok(orders);
    }


    /**
     * Create a new trip for today
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trip/start")
    public ResponseEntity<Trip> startTrip(
            @RequestBody Map<String, String> request,
            Principal principal) {
        String username = principal.getName();
        UUID routeVehicleId = UUID.fromString(request.get("routeVehicleId"));

        Trip trip = tripService.createTripForToday(routeVehicleId, username);
        return ResponseEntity.ok(trip);
    }


    /**
     * Get all stops for a trip
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/trip/{tripId}/stops")
    public ResponseEntity<List<RouteStopDto>> getTripStops(
            @PathVariable UUID tripId,
            Principal principal) {
        String username = principal.getName();
        List<RouteStopDto> stops = tripService.getTripStops(tripId, username);
        return ResponseEntity.ok(stops);
    }
    /**
     * Get statistics for the currently logged-in driver
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/me")
    public ResponseEntity<DriverStatisticsDto> getMyDriverStatistics(Principal principal) {
        DriverStatisticsDto statistics = driverService.getDriverStatisticsByUsername(principal.getName());
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for the currently logged-in driver within a date range
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/me/range")
    public ResponseEntity<DriverStatisticsDto> getMyDriverStatisticsByDateRange(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        DriverStatisticsDto statistics = driverService.getDriverStatisticsByDateRange(
                principal.getName(), startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for a specific driver (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @GetMapping("/{driverId}")
    public ResponseEntity<DriverStatisticsDto> getDriverStatistics(@PathVariable UUID driverId) {
        DriverStatisticsDto statistics = driverService.getDriverStatistics(driverId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for a specific driver within a date range (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @GetMapping("/{driverId}/range")
    public ResponseEntity<DriverStatisticsDto> getDriverStatisticsByDateRange(
            @PathVariable UUID driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        DriverStatisticsDto statistics = driverService.getDriverStatisticsByDateRange(
                driverId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get detailed trip history for the current driver
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/me/trip-history")
    public ResponseEntity<List<TripHistoryDto>> getMyTripHistory(Principal principal,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // If dates not provided, default to last 30 days
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<TripHistoryDto> tripHistory = driverService.getDriverTripHistory(
                principal.getName(), start, end);
        return ResponseEntity.ok(tripHistory);
    }

    /**
     * Get detailed trip history for a specific driver (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @GetMapping("/{driverId}/trip-history")
    public ResponseEntity<List<TripHistoryDto>> getDriverTripHistory(
            @PathVariable UUID driverId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // If dates not provided, default to last 30 days
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<TripHistoryDto> tripHistory = driverService.getDriverTripHistoryById(
                driverId, start, end);
        return ResponseEntity.ok(tripHistory);
    }

    /**
     * Get performance metrics across all drivers (for management dashboard)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @GetMapping("/performance-metrics")
    public ResponseEntity<List<DriverStatisticsDto>> getDriverPerformanceMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // If dates not provided, default to last 30 days
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<DriverStatisticsDto> metrics = driverService.getAllDriverPerformanceMetrics(start, end);
        return ResponseEntity.ok(metrics);
    }
}