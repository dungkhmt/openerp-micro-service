package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.DeliveryAnalyticsDTO;
import openerp.openerpresourceserver.entity.AssignOrderShipper;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.AssignOrderShipperRepository;
import openerp.openerpresourceserver.repository.HubRepo;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.repository.ShipperRepo;
import openerp.openerpresourceserver.service.DeliveryAnalyticsService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryAnalyticsServiceImpl implements DeliveryAnalyticsService {

    private final OrderRepo orderRepo;
    private final AssignOrderShipperRepository assignOrderShipperRepository;
    private final HubRepo hubRepo;
    private final ShipperRepo shipperRepo;

    @Override
    public DeliveryAnalyticsDTO getDeliveryAnalytics(UUID hubId) {
        // Check if hub exists
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new NotFoundException("Hub not found with ID: " + hubId));

        // Get all assignments related to this hub
        List<AssignOrderShipper> allAssignments = new ArrayList<>();
        List<Order> hubOrders = orderRepo.findAll().stream()
                .filter(order -> order.getFinalHubId() != null && order.getFinalHubId().equals(hubId))
                .collect(Collectors.toList());

        for (Order order : hubOrders) {
            allAssignments.addAll(assignOrderShipperRepository.findByOrderId(order.getId()));
        }

        // Calculate statistics
        int totalAssignments = allAssignments.size();
        int completedDeliveries = (int) allAssignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED ||
                        (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.COMPLETED))
                .count();

        int failedDeliveries = (int) allAssignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.RETURNED_TO_HUB ||
                        (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.CANCELLED))
                .count();

        int pendingDeliveries = totalAssignments - completedDeliveries - failedDeliveries;

        double successRate = totalAssignments > 0 ? (double) completedDeliveries / totalAssignments * 100 : 0;

        // Calculate average delivery time
        double averageDeliveryTime = allAssignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED || a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                .mapToLong(a -> {
                    if (a.getCreatedAt() != null && a.getUpdatedAt() != null) {
                        return (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000); // Convert to minutes
                    }
                    return 0;
                })
                .average()
                .orElse(0);

        // Count by status
        Map<String, Integer> statusCounts = new HashMap<>();
        for (ShipperAssignmentStatus status : ShipperAssignmentStatus.values()) {
            int count = (int) allAssignments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            statusCounts.put(status.name(), count);
        }

        // Get shipper performance
        Map<UUID, List<AssignOrderShipper>> assignmentsByShipper = allAssignments.stream()
                .collect(Collectors.groupingBy(AssignOrderShipper::getShipperId));

        List<DeliveryAnalyticsDTO.ShipperPerformanceDTO> shipperPerformance = new ArrayList<>();

        for (Map.Entry<UUID, List<AssignOrderShipper>> entry : assignmentsByShipper.entrySet()) {
            UUID shipperId = entry.getKey();
            List<AssignOrderShipper> shipperAssignments = entry.getValue();

            String shipperName = shipperAssignments.isEmpty() ? "Unknown" : shipperAssignments.get(0).getShipperName();

            int shipperTotalAssignments = shipperAssignments.size();
            int shipperCompletedDeliveries = (int) shipperAssignments.stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED ||
                            (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                    orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.COMPLETED))
                    .count();

            int shipperFailedDeliveries = (int) shipperAssignments.stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.RETURNED_TO_HUB ||
                            (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                    orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.CANCELLED))
                    .count();

            double shipperSuccessRate = shipperTotalAssignments > 0 ?
                    (double) shipperCompletedDeliveries / shipperTotalAssignments * 100 : 0;

            double shipperAverageDeliveryTime = shipperAssignments.stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED || a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                    .mapToLong(a -> {
                        if (a.getCreatedAt() != null && a.getUpdatedAt() != null) {
                            return (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000);
                        }
                        return 0;
                    })
                    .average()
                    .orElse(0);

            DeliveryAnalyticsDTO.ShipperPerformanceDTO performanceDTO = DeliveryAnalyticsDTO.ShipperPerformanceDTO.builder()
                    .shipperId(shipperId)
                    .shipperName(shipperName)
                    .totalAssignments(shipperTotalAssignments)
                    .completedDeliveries(shipperCompletedDeliveries)
                    .failedDeliveries(shipperFailedDeliveries)
                    .successRate(shipperSuccessRate)
                    .averageDeliveryTime(shipperAverageDeliveryTime)
                    .build();

            shipperPerformance.add(performanceDTO);
        }

        // Build and return the analytics DTO
        return DeliveryAnalyticsDTO.builder()
                .hubId(hubId)
                .hubName(hub.getName())
                .totalDeliveries(totalAssignments)
                .completedDeliveries(completedDeliveries)
                .failedDeliveries(failedDeliveries)
                .pendingDeliveries(pendingDeliveries)
                .successRate(successRate)
                .averageDeliveryTime(averageDeliveryTime)
                .deliveryStatusCounts(statusCounts)
                .shipperPerformance(shipperPerformance)
                .build();
    }

    @Override
    public DeliveryAnalyticsDTO.ShipperPerformanceDTO getShipperPerformance(UUID shipperId) {
        // Check if shipper exists
        if (!shipperRepo.existsById(shipperId)) {
            throw new NotFoundException("Shipper not found with ID: " + shipperId);
        }

        // Get all assignments for this shipper
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId);

        if (assignments.isEmpty()) {
            return DeliveryAnalyticsDTO.ShipperPerformanceDTO.builder()
                    .shipperId(shipperId)
                    .shipperName("Unknown")
                    .totalAssignments(0)
                    .completedDeliveries(0)
                    .failedDeliveries(0)
                    .successRate(0.0)
                    .averageDeliveryTime(0.0)
                    .build();
        }

        // Calculate statistics
        String shipperName = assignments.get(0).getShipperName();
        int totalAssignments = assignments.size();
        int completedDeliveries = (int) assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED ||
                        (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.COMPLETED))
                .count();

        int failedDeliveries = (int) assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.RETURNED_TO_HUB ||
                        (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.CANCELLED))
                .count();

        double successRate = totalAssignments > 0 ? (double) completedDeliveries / totalAssignments * 100 : 0;

        double averageDeliveryTime = assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED || a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                .mapToLong(a -> {
                    if (a.getCreatedAt() != null && a.getUpdatedAt() != null) {
                        return (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000);
                    }
                    return 0;
                })
                .average()
                .orElse(0);

        // Build and return shipper performance DTO
        return DeliveryAnalyticsDTO.ShipperPerformanceDTO.builder()
                .shipperId(shipperId)
                .shipperName(shipperName)
                .totalAssignments(totalAssignments)
                .completedDeliveries(completedDeliveries)
                .failedDeliveries(failedDeliveries)
                .successRate(successRate)
                .averageDeliveryTime(averageDeliveryTime)
                .build();
    }

    @Override
    public DeliveryAnalyticsDTO getDeliveryAnalyticsByDateRange(UUID hubId, LocalDate startDate, LocalDate endDate) {
        // Check if hub exists
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new NotFoundException("Hub not found with ID: " + hubId));

        // Convert dates to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        // Get all assignments related to this hub in the date range
        List<AssignOrderShipper> allAssignments = new ArrayList<>();
        List<Order> hubOrders = orderRepo.findAll().stream()
                .filter(order -> order.getFinalHubId() != null &&
                        order.getFinalHubId().equals(hubId) &&
                        order.getCreatedAt() != null &&
                        order.getCreatedAt().after(startTimestamp) &&
                        order.getCreatedAt().before(endTimestamp))
                .collect(Collectors.toList());

        for (Order order : hubOrders) {
            allAssignments.addAll(assignOrderShipperRepository.findByOrderId(order.getId()));
        }

        // Calculate statistics (similar to getDeliveryAnalytics but filtered by date)
        int totalAssignments = allAssignments.size();
        int completedDeliveries = (int) allAssignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED ||
                        (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.COMPLETED))
                .count();

        int failedDeliveries = (int) allAssignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.RETURNED_TO_HUB ||
                        (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.CANCELLED))
                .count();

        int pendingDeliveries = totalAssignments - completedDeliveries - failedDeliveries;

        double successRate = totalAssignments > 0 ? (double) completedDeliveries / totalAssignments * 100 : 0;

        // Calculate average delivery time
        double averageDeliveryTime = allAssignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED || a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                .mapToLong(a -> {
                    if (a.getCreatedAt() != null && a.getUpdatedAt() != null) {
                        return (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000);
                    }
                    return 0;
                })
                .average()
                .orElse(0);

        // Count by status
        Map<String, Integer> statusCounts = new HashMap<>();
        for (ShipperAssignmentStatus status : ShipperAssignmentStatus.values()) {
            int count = (int) allAssignments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            statusCounts.put(status.name(), count);
        }

        // Get shipper performance (similar to getDeliveryAnalytics)
        Map<UUID, List<AssignOrderShipper>> assignmentsByShipper = allAssignments.stream()
                .collect(Collectors.groupingBy(AssignOrderShipper::getShipperId));

        List<DeliveryAnalyticsDTO.ShipperPerformanceDTO> shipperPerformance = new ArrayList<>();

        for (Map.Entry<UUID, List<AssignOrderShipper>> entry : assignmentsByShipper.entrySet()) {
            UUID shipperId = entry.getKey();
            List<AssignOrderShipper> shipperAssignments = entry.getValue();

            String shipperName = shipperAssignments.isEmpty() ? "Unknown" : shipperAssignments.get(0).getShipperName();

            int shipperTotalAssignments = shipperAssignments.size();
            int shipperCompletedDeliveries = (int) shipperAssignments.stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED ||
                            (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                    orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.COMPLETED))
                    .count();

            int shipperFailedDeliveries = (int) shipperAssignments.stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.RETURNED_TO_HUB ||
                            (a.getStatus() == ShipperAssignmentStatus.COMPLETED &&
                                    orderRepo.findById(a.getOrderId()).map(Order::getStatus).orElse(null) == OrderStatus.CANCELLED))
                    .count();

            double shipperSuccessRate = shipperTotalAssignments > 0 ?
                    (double) shipperCompletedDeliveries / shipperTotalAssignments * 100 : 0;

            double shipperAverageDeliveryTime = shipperAssignments.stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED || a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                    .mapToLong(a -> {
                        if (a.getCreatedAt() != null && a.getUpdatedAt() != null) {
                            return (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000);
                        }
                        return 0;
                    })
                    .average()
                    .orElse(0);

            DeliveryAnalyticsDTO.ShipperPerformanceDTO performanceDTO = DeliveryAnalyticsDTO.ShipperPerformanceDTO.builder()
                    .shipperId(shipperId)
                    .shipperName(shipperName)
                    .totalAssignments(shipperTotalAssignments)
                    .completedDeliveries(shipperCompletedDeliveries)
                    .failedDeliveries(shipperFailedDeliveries)
                    .successRate(shipperSuccessRate)
                    .averageDeliveryTime(shipperAverageDeliveryTime)
                    .build();

            shipperPerformance.add(performanceDTO);
        }

        // Build and return the analytics DTO
        return DeliveryAnalyticsDTO.builder()
                .hubId(hubId)
                .hubName(hub.getName())
                .totalDeliveries(totalAssignments)
                .completedDeliveries(completedDeliveries)
                .failedDeliveries(failedDeliveries)
                .pendingDeliveries(pendingDeliveries)
                .successRate(successRate)
                .averageDeliveryTime(averageDeliveryTime)
                .deliveryStatusCounts(statusCounts)
                .shipperPerformance(shipperPerformance)
                .build();
    }

    @Override
    public DeliveryAnalyticsDTO getAggregatedPerformanceMetrics() {
        // Get all hubs
        List<Hub> hubs = hubRepo.findAll();

        // Initialize aggregated metrics
        int totalDeliveries = 0;
        int completedDeliveries = 0;
        int failedDeliveries = 0;
        int pendingDeliveries = 0;
        double successRateSum = 0;
        double averageDeliveryTimeSum = 0;
        Map<String, Integer> statusCounts = new HashMap<>();
        List<DeliveryAnalyticsDTO.ShipperPerformanceDTO> allShipperPerformance = new ArrayList<>();

        // Get analytics for each hub and aggregate
        int activeHubCount = 0;
        for (Hub hub : hubs) {
            try {
                DeliveryAnalyticsDTO hubAnalytics = getDeliveryAnalytics(hub.getHubId());

                // Skip hubs with no deliveries
                if (hubAnalytics.getTotalDeliveries() == 0) {
                    continue;
                }

                activeHubCount++;
                totalDeliveries += hubAnalytics.getTotalDeliveries();
                completedDeliveries += hubAnalytics.getCompletedDeliveries();
                failedDeliveries += hubAnalytics.getFailedDeliveries();
                pendingDeliveries += hubAnalytics.getPendingDeliveries();
                successRateSum += hubAnalytics.getSuccessRate();
                averageDeliveryTimeSum += hubAnalytics.getAverageDeliveryTime();

                // Aggregate status counts
                for (Map.Entry<String, Integer> entry : hubAnalytics.getDeliveryStatusCounts().entrySet()) {
                    statusCounts.put(entry.getKey(),
                            statusCounts.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }

                // Collect shipper performance
                allShipperPerformance.addAll(hubAnalytics.getShipperPerformance());
            } catch (Exception e) {
                log.error("Error getting analytics for hub " + hub.getHubId(), e);
            }
        }

        // Calculate averages
        double averageSuccessRate = activeHubCount > 0 ? successRateSum / activeHubCount : 0;
        double averageDeliveryTime = activeHubCount > 0 ? averageDeliveryTimeSum / activeHubCount : 0;

        // Build and return aggregated analytics
        return DeliveryAnalyticsDTO.builder()
                .hubId(null) // No specific hub
                .hubName("All Hubs")
                .totalDeliveries(totalDeliveries)
                .completedDeliveries(completedDeliveries)
                .failedDeliveries(failedDeliveries)
                .pendingDeliveries(pendingDeliveries)
                .successRate(averageSuccessRate)
                .averageDeliveryTime(averageDeliveryTime)
                .deliveryStatusCounts(statusCounts)
                .shipperPerformance(allShipperPerformance)
                .build();
    }
}