package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.service.DriverService;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
            @PathVariable UUID tripId,
            Principal principal) {
        String username = principal.getName();
        TripDetailsDTO updatedTrip = tripService.advanceToNextStop(tripId, username);
        return ResponseEntity.ok(updatedTrip);
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
    public ResponseEntity<List<OrderItemForTripDto>> getPendingPickupOrders(
            Principal principal,
            @PathVariable UUID tripId) {
        String username = principal.getName();
        System.out.println("DriverController.getAssignedVehicle: " + principal);

        List<OrderItemForTripDto> orders = driverService.getPendingPickupOrderItemsForDriver(username, tripId);
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
    public ResponseEntity<Void> deliverOrderItems(@RequestBody List<UUID> orderItemIds, Principal principal) {
        String username = principal.getName();
        driverService.deliverOrderItems(username, orderItemIds);
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
    public ResponseEntity<List<OrderItemForTripDto>> getCurrentOrders(Principal principal,@PathVariable UUID tripId) {
        String username = principal.getName();
        List<OrderItemForTripDto> orders = driverService.getCurrentOrderItemsForDriver(username, tripId);
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

}