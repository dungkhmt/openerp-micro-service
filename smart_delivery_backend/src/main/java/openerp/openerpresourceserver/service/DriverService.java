package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.time.LocalDate;
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



    List<OrderForTripDto> getPendingPickupOrdersForDriver(String username, UUID tripId);

    List<OrderForTripDto> getCurrentOrderItemsForDriver(String username, UUID tripId);

    void deliverOrders(String username, List<UUID> successOrderIds, List<UUID> failOrderIds);

    List<OrderSuggestionDto> getSuggestedOrderItemsForTrip(UUID tripId);
    /**
     * Get driver statistics by username
     */
    DriverStatisticsDto getDriverStatisticsByUsername(String username);

    /**
     * Get driver statistics by username for a specific date range
     */
    DriverStatisticsDto getDriverStatisticsByDateRange(String username, LocalDate startDate, LocalDate endDate);

    /**
     * Get driver statistics by driver ID
     */
    DriverStatisticsDto getDriverStatistics(UUID driverId);

    /**
     * Get driver statistics by driver ID for a specific date range
     */
    DriverStatisticsDto getDriverStatisticsByDateRange(UUID driverId, LocalDate startDate, LocalDate endDate);

    /**
     * Get driver trip history by username
     */
    List<TripHistoryDto> getDriverTripHistory(String username, LocalDate startDate, LocalDate endDate);

    /**
     * Get driver trip history by driver ID
     */
    List<TripHistoryDto> getDriverTripHistoryById(UUID driverId, LocalDate startDate, LocalDate endDate);

    /**
     * Get performance metrics for all drivers
     */
    List<DriverStatisticsDto> getAllDriverPerformanceMetrics(LocalDate startDate, LocalDate endDate);
}