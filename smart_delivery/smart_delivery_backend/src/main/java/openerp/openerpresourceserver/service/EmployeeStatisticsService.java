package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.EmployeeStatisticsDto;
import java.time.LocalDate;
import java.util.UUID;

public interface EmployeeStatisticsService {

    /**
     * Get statistics for a specific collector
     *
     * @param collectorId The ID of the collector
     * @return Statistics for the collector
     */
    EmployeeStatisticsDto getCollectorStatistics(UUID collectorId);

    /**
     * Get statistics for a specific collector for a date range
     *
     * @param collectorId The ID of the collector
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return Statistics for the collector in the specified period
     */
    EmployeeStatisticsDto getCollectorStatisticsByDateRange(UUID collectorId, LocalDate startDate, LocalDate endDate);

    /**
     * Get statistics for a specific collector by username
     *
     * @param username The username of the collector
     * @return Statistics for the collector
     */
    EmployeeStatisticsDto getCollectorStatisticsByUsername(String username);

    /**
     * Get statistics for a specific shipper
     *
     * @param shipperId The ID of the shipper
     * @return Statistics for the shipper
     */
    EmployeeStatisticsDto getShipperStatistics(UUID shipperId);

    /**
     * Get statistics for a specific shipper for a date range
     *
     * @param shipperId The ID of the shipper
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return Statistics for the shipper in the specified period
     */
    EmployeeStatisticsDto getShipperStatisticsByDateRange(UUID shipperId, LocalDate startDate, LocalDate endDate);

    /**
     * Get statistics for a specific shipper by username
     *
     * @param username The username of the shipper
     * @return Statistics for the shipper
     */
    EmployeeStatisticsDto getShipperStatisticsByUsername(String username);
}