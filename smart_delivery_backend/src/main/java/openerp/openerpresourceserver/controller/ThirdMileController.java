package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.service.*;
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

@RestController
@RequestMapping("/smdeli/thirdmile")
@RequiredArgsConstructor
public class ThirdMileController {

    private final ShipperAssignmentService shipperAssignmentService;
    private final DeliveryTrackingService deliveryTrackingService;
    private final RouteOptimizationService routeOptimizationService;
    private final DeliveryConfirmationService deliveryConfirmationService;

    /**
     * Get all orders pending delivery at a specific hub
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'HUB_STAFF')")
    @GetMapping("/hub/{hubId}/pending-delivery")
    public ResponseEntity<List<OrderSummaryDTO>> getPendingDeliveryOrders(@PathVariable UUID hubId) {
        return ResponseEntity.ok(shipperAssignmentService.getPendingDeliveryOrders(hubId));
    }

    /**
     * Auto-assign orders to shippers
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @PostMapping("/assign/shipper")
    public ResponseEntity<List<OrderResponseCollectorShipperDto>> assignOrdersToShipper(@RequestBody AssignOrderDto request) {
        return ResponseEntity.ok(shipperAssignmentService.assignOrdersToShippers(
                request.getHubId(),
                request.getOrders(),
                request.getEmployees())); // Note: Using the same DTO structure where "collectors" field contains shippers
    }

    /**
     * Get shipper's current assignments
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'SHIPPER')")
    @GetMapping("/shipper/{shipperId}/assignments")
    public ResponseEntity<List<OrderSummaryDTO>> getShipperAssignments(@PathVariable UUID shipperId) {
        return ResponseEntity.ok(shipperAssignmentService.getShipperAssignments(shipperId));
    }

    /**
     * Update delivery status (shipper picked up, in transit, delivered, failed delivery)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<?> updateDeliveryStatus(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> request) {
        try {
            OrderStatus status = OrderStatus.valueOf(request.get("status"));
            String notes = request.getOrDefault("notes", "");
            deliveryTrackingService.updateOrderStatus(orderId, status, notes);
            return ResponseEntity.ok("Order status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update order status: " + e.getMessage());
        }
    }

    /**
     * Update assignment status
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @PutMapping("/assignment/shipper")
    public ResponseEntity<?> updateShipperAssignment(Principal principal, @Valid @RequestBody UpdateShipperAssignmentRequestDto request) {
        try {
            shipperAssignmentService.updateAssignmentStatus(principal, request.getAssignmentId(), request.getStatus());
            return ResponseEntity.ok("Assignment updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update assignment: " + e.getMessage());
        }
    }
    /**
     * Get delivery route optimization for a shipper
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @GetMapping("/shipper/{shipperId}/route")
    public ResponseEntity<DeliveryRouteDTO> getOptimizedDeliveryRoute(@PathVariable UUID shipperId) {
        return ResponseEntity.ok(routeOptimizationService.getOptimizedRoute(shipperId));
    }

    /**
     * Optimize assignment sequence
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @PostMapping("/shipper/{shipperId}/optimize-route")
    public ResponseEntity<?> optimizeDeliveryRoute(@PathVariable UUID shipperId) {
        try {
            routeOptimizationService.optimizeAssignmentSequence(shipperId);
            return ResponseEntity.ok("Route optimized successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to optimize route: " + e.getMessage());
        }
    }

    /**
     * Get estimated delivery time for an order
     */
    @GetMapping("/order/{orderId}/estimated-delivery")
    public ResponseEntity<String> getEstimatedDeliveryTime(@PathVariable UUID orderId) {
        return ResponseEntity.ok(routeOptimizationService.calculateEstimatedDeliveryTime(orderId));
    }

    /**
     * Generate confirmation code (OTP) for delivery
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @PostMapping("/order/{orderId}/generate-code")
    public ResponseEntity<String> generateConfirmationCode(@PathVariable UUID orderId) {
        try {
            String code = deliveryConfirmationService.generateConfirmationCode(orderId);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate confirmation code: " + e.getMessage());
        }
    }

    /**
     * Confirm delivery with proof (OTP, signature)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @PostMapping("/order/{orderId}/confirm-delivery")
    public ResponseEntity<?> confirmDelivery(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> confirmationData) {
        try {
            String confirmationType = confirmationData.get("type"); // "otp", "signature"
            String confirmationValue = confirmationData.get("value");
            boolean success = deliveryConfirmationService.confirmDelivery(orderId, confirmationType, confirmationValue);
            if (success) {
                return ResponseEntity.ok("Delivery confirmed successfully");
            } else {
                return ResponseEntity.badRequest().body("Delivery confirmation failed: Invalid confirmation");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during delivery confirmation: " + e.getMessage());
        }
    }

    /**
     * Register failed delivery attempt
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @PostMapping("/order/{orderId}/failed-attempt")
    public ResponseEntity<?> registerFailedDelivery(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> failureData) {
        try {
            String reason = failureData.get("reason");
            deliveryConfirmationService.registerFailedDeliveryAttempt(orderId, reason);
            return ResponseEntity.ok("Failed delivery attempt registered");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering failed delivery: " + e.getMessage());
        }
    }
}