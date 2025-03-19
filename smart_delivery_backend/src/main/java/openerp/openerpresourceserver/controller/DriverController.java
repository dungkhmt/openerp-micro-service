package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.RouteVehicleDetailDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.service.DriverService;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
     * Get all routes assigned to the driver's vehicle
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/routes")
    public ResponseEntity<List<RouteVehicleDetailDto>> getAssignedRoutes(Principal principal) {
        String username = principal.getName();
        System.out.println("DriverController.getAssignedVehicle: " + principal);

        List<RouteVehicleDetailDto> routes = driverService.getDriverRoutesByUsername(username);
        return ResponseEntity.ok(routes);
    }

    /**
     * Get orders assigned to a specific route vehicle
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/routes/{routeVehicleId}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersForRouteVehicle(@PathVariable UUID routeVehicleId) {
        List<OrderResponseDto> orderDtos = driverService.getOrdersForRouteVehicle(routeVehicleId);
        return ResponseEntity.ok(orderDtos);
    }

    /**
     * Get pending collection orders for the driver's vehicle at a specific hub
     */
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/hub/{hubId}/pending-pickups")
    public ResponseEntity<List<OrderSummaryDTO>> getPendingPickupOrders(
            Principal principal,
            @PathVariable UUID hubId) {
        String username = principal.getName();
        System.out.println("DriverController.getAssignedVehicle: " + principal);

        List<OrderSummaryDTO> orders = driverService.getPendingPickupOrdersForDriver(username, hubId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Mark orders as picked up (COLLECTED_HUB -> DELIVERING)
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/pickup-orders")
    public ResponseEntity<Void> pickupOrders(@RequestBody List<UUID> orderIds, Principal principal) {
        String username = principal.getName();
        driverService.pickupOrders(username, orderIds);        System.out.println("DriverController.getAssignedVehicle: " + principal);

        return ResponseEntity.ok().build();

    }

    /**
     * Mark orders as delivered to destination hub (DELIVERING -> DELIVERED)
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/deliver-orders")
    public ResponseEntity<Void> deliverOrders(@RequestBody List<UUID> orderIds, Principal principal) {
        String username = principal.getName();
        driverService.deliverOrders(username, orderIds);
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
    @GetMapping("/current-orders")
    public ResponseEntity<List<OrderSummaryDTO>> getCurrentOrders(Principal principal) {
        String username = principal.getName();
        List<OrderSummaryDTO> orders = driverService.getCurrentOrdersForDriver(username);
        return ResponseEntity.ok(orders);
    }

    /**
     * Complete a trip (mark all orders as delivered and vehicle as available)
     */
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trips/{tripId}/complete")
    public ResponseEntity<Void> completeTrip(
            @PathVariable UUID tripId,
            Principal principal) {
        tripService.completeTrip(tripId);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trips/start")
    public ResponseEntity<Trip> startTrip(
            @RequestBody Map<String, String> request,
            Principal principal) {
        String username = principal.getName();
        UUID routeVehicleId = UUID.fromString(request.get("routeVehicleId"));

        Trip trip = tripService.createTripForToday(routeVehicleId, username);
        return ResponseEntity.ok(trip);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/trips/{tripId}/advance-stop")
    public ResponseEntity<Trip> advanceToNextStop(
            @PathVariable UUID tripId,
            Principal principal) {
        Trip trip = tripService.advanceToNextStop(tripId);
        return ResponseEntity.ok(trip);
    }
}