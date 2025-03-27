package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.OrderSummaryDTO;
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
     * Get pending pickup orders for a driver at a specific hub
     */
    List<OrderSummaryDTO> getPendingPickupOrdersForDriver(String username, UUID hubId);

    /**
     * Mark orders as picked up from origin hub
     */
    void pickupOrders(String username, List<UUID> orderIds, UUID tripId);

    /**
     * Mark orders as delivered to destination hub
     */
    void deliverOrders(String username, List<UUID> orderIds);

    /**
     * Update individual order status
     */
    void updateOrderStatus(String username, UUID orderId, OrderStatus status);

    /**
     * Get all orders currently assigned to the driver's vehicle
     */
    List<OrderSummaryDTO> getCurrentOrdersForDriver(String username);



}