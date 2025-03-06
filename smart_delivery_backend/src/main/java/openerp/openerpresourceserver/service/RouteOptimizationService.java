package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.DeliveryRouteDTO;
import openerp.openerpresourceserver.entity.AssignOrderShipper;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for optimizing delivery routes for shippers
 */
public interface RouteOptimizationService {

    /**
     * Get an optimized delivery route for a shipper
     */
    DeliveryRouteDTO getOptimizedRoute(UUID shipperId);

    /**
     * Generate an optimized route for a specific set of assignments
     */
    DeliveryRouteDTO generateOptimizedRoute(List<AssignOrderShipper> assignments, UUID hubId);

    /**
     * Calculate the estimated delivery time for an order
     */
    String calculateEstimatedDeliveryTime(UUID orderId);

    /**
     * Resequence assignments for optimal route
     */
    void optimizeAssignmentSequence(UUID shipperId);
}