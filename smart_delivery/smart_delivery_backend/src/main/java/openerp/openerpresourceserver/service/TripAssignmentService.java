package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.TripOrder;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for assigning orders to trips
 */
public interface TripAssignmentService {

    /**
     * Run the daily morning assignment for collected orders (8 AM)
     * @param hubId The ID of the hub to process orders for
     * @return Number of orders assigned to trips
     */
    int assignMorningTrips(UUID hubId);

    /**
     * Run the daily evening assignment for collected orders (5 PM)
     * @param hubId The ID of the hub to process orders for
     * @return Number of orders assigned to trips
     */
    int assignEveningTrips(UUID hubId);

    /**
     * Manually assign specific orders to a trip
     * @param tripId The ID of the trip
     * @param orderIds List of order IDs to assign
     * @return List of created TripOrder entities
     */
    List<TripOrder> assignOrdersToTrip(UUID tripId, List<UUID> orderIds);

    /**
     * Remove an order from a trip
     * @param tripId The ID of the trip
     * @param orderId The ID of the order
     */
    void removeOrderFromTrip(UUID tripId, UUID orderId);

    /**
     * Get all orders assigned to a trip
     * @param tripId The ID of the trip
     * @return List of orders in the trip
     */
    List<Order> getOrdersForTrip(UUID tripId);

    /**
     * Get all trips for an order
     * @param orderId The ID of the order
     * @return List of trips the order is assigned to
     */
    List<Trip> getTripsForOrder(UUID orderId);

    /**
     * Optimize the order pickup/delivery sequence within a trip
     * @param tripId The ID of the trip
     * @return List of updated TripOrder entities with optimized sequence
     */
    List<TripOrder> optimizeTripSequence(UUID tripId);
}