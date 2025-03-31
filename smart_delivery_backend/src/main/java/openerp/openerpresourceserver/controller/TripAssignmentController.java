package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.TripDTO;
import openerp.openerpresourceserver.dto.TripDetailsDTO;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.TripOrder;
import openerp.openerpresourceserver.service.TripAssignmentService;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for trip assignment operations
 */
@RestController
@RequestMapping("/smdeli/trip-assignments")
@RequiredArgsConstructor
public class TripAssignmentController {

    private final TripAssignmentService tripAssignmentService;
    private final TripService tripService;

    @PreAuthorize("hasAnyRole('ROUTE_MANAGER')")
    @PostMapping("/weekly/create")
    public ResponseEntity<List<Trip>> createTripThisWeek(){

        return ResponseEntity.ok(tripService.createTripThisWeek());

    }

    @PreAuthorize("hasAnyRole('HUB_MANAGER','HUB_STAFF')")
    @GetMapping("/hub/{hubId}/today")
    public ResponseEntity<List<TripDTO>> getTripsForHubToday(@PathVariable UUID hubId) {
        List<TripDTO> trips = tripService.getTripsForHubToday(hubId);
        return ResponseEntity.ok(trips);
    }

    @PreAuthorize("hasAnyRole('HUB_MANAGER','HUB_STAFF')")
    @GetMapping("/hub/{hubId}/today/start")
    public ResponseEntity<List<TripDTO>> getTripsForHubTodayStart(@PathVariable UUID hubId) {
        List<TripDTO> trips = tripService.getTripsForHubTodayStart(hubId);
        return ResponseEntity.ok(trips);
    }
    @PreAuthorize("hasAnyRole('HUB_MANAGER','HUB_STAFF')")
    @GetMapping("/hub/{hubId}/today/through")
    public ResponseEntity<List<TripDTO>> getTripsForHubTodayThrough(@PathVariable UUID hubId) {
        List<TripDTO> trips = tripService.getTripsForHubTodayThrough(hubId);
        return ResponseEntity.ok(trips);
    }


    @PostMapping("/create-from-to")
    public ResponseEntity<List<Trip>> createTripsFromTo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Trip> createdTrips = tripService.createTripFromTo(startDate, endDate);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdTrips);
    }
    /**
     * Manually trigger morning trip assignments for a hub
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'SCHEDULER')")
    @PostMapping("/hubs/{hubId}/morning-assignments")
    public ResponseEntity<Map<String, Object>> runMorningAssignments(@PathVariable UUID hubId) {
        int assigned = tripAssignmentService.assignMorningTrips(hubId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "assignedOrders", assigned,
                "message", "Successfully assigned " + assigned + " orders to morning trips"
        ));
    }

    /**
     * Manually trigger evening trip assignments for a hub
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'SCHEDULER')")
    @PostMapping("/hubs/{hubId}/evening-assignments")
    public ResponseEntity<Map<String, Object>> runEveningAssignments(@PathVariable UUID hubId) {
        int assigned = tripAssignmentService.assignEveningTrips(hubId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "assignedOrders", assigned,
                "message", "Successfully assigned " + assigned + " orders to evening trips"
        ));
    }

    /**
     * Manually assign specific orders to a trip
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'SCHEDULER')")
    @PostMapping("/trips/{tripId}/orders")
    public ResponseEntity<List<TripOrder>> assignOrdersToTrip(
            @PathVariable UUID tripId,
            @RequestBody List<UUID> orderIds) {
        List<TripOrder> tripOrders = tripAssignmentService.assignOrdersToTrip(tripId, orderIds);
        return ResponseEntity.ok(tripOrders);
    }

    /**
     * Remove an order from a trip
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'SCHEDULER')")
    @DeleteMapping("/trips/{tripId}/orders/{orderId}")
    public ResponseEntity<Void> removeOrderFromTrip(
            @PathVariable UUID tripId,
            @PathVariable UUID orderId) {
        tripAssignmentService.removeOrderFromTrip(tripId, orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all orders assigned to a trip
     */
    @GetMapping("/trips/{tripId}/orders")
    public ResponseEntity<List<OrderSummaryDTO>> getOrdersForTrip(@PathVariable UUID tripId) {
        List<Order> orders = tripAssignmentService.getOrdersForTrip(tripId);
        List<OrderSummaryDTO> orderSummaries = orders.stream()
                .map(OrderSummaryDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderSummaries);
    }

    /**
     * Get all trips an order is assigned to
     */
    @GetMapping("/orders/{orderId}/trips")
    public ResponseEntity<List<Trip>> getTripsForOrder(@PathVariable UUID orderId) {
        List<Trip> trips = tripAssignmentService.getTripsForOrder(orderId);
        return ResponseEntity.ok(trips);
    }

    /**
     * Optimize order sequence within a trip
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'DRIVER')")
    @PostMapping("/trips/{tripId}/optimize")
    public ResponseEntity<List<TripOrder>> optimizeTripSequence(@PathVariable UUID tripId) {
        List<TripOrder> optimizedOrders = tripAssignmentService.optimizeTripSequence(tripId);
        return ResponseEntity.ok(optimizedOrders);
    }
}