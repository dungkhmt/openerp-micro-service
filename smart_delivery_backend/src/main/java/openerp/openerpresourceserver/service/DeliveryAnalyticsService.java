package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.DeliveryAnalyticsDTO;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service responsible for generating analytics about the delivery process
 */
public interface DeliveryAnalyticsService {

    /**
     * Get delivery analytics for a hub
     */
    DeliveryAnalyticsDTO getDeliveryAnalytics(UUID hubId);

    /**
     * Get delivery analytics for a specific shipper
     */
    DeliveryAnalyticsDTO.ShipperPerformanceDTO getShipperPerformance(UUID shipperId);

    /**
     * Get delivery analytics for a specific time period
     */
    DeliveryAnalyticsDTO getDeliveryAnalyticsByDateRange(UUID hubId, LocalDate startDate, LocalDate endDate);

    /**
     * Get aggregated delivery performance metrics
     */
    DeliveryAnalyticsDTO getAggregatedPerformanceMetrics();
}