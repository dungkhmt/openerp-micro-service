package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.RouteVehicleDetailDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Trip;
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
     * Get the routes assigned to a driver's vehicle by username
     */
    List<RouteVehicleDetailDto> getDriverRoutesByUsername(String username);

    /**
     * Get orders for a specific route vehicle
     */
    List<OrderResponseDto> getOrdersForRouteVehicle(UUID routeVehicleId);

    /**
     * Get pending pickup orders for a driver at a specific hub
     */
    List<OrderSummaryDTO> getPendingPickupOrdersForDriver(String username, UUID hubId);

    /**
     * Mark orders as picked up from origin hub
     */
    void pickupOrders(String username, List<UUID> orderIds);

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