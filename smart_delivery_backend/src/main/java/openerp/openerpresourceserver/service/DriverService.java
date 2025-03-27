package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.OrderItemForTripDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for driver operations
 */
public interface DriverService {

    /**
     * Get the vehicle assigned to a driver by username
     */
    VehicleDto getDriverVehicleByUsername(String username);




    /**
     * Mark orders as picked up from origin hub
     */
    void pickupOrders(String username, List<UUID> orderIds, UUID tripId);

    /**
     * Update individual order status
     */
    void updateOrderStatus(String username, UUID orderId, OrderStatus status);



    List<OrderItemForTripDto> getPendingPickupOrderItemsForDriver(String username, UUID tripId);

    List<OrderItemForTripDto> getCurrentOrderItemsForDriver(String username, UUID tripId);

    void deliverOrderItems(String username, List<UUID> orderItemIds);
}