package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.EmployeeStatisticsDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.EmployeeStatisticsService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeStatisticsServiceImpl implements EmployeeStatisticsService {

    private final CollectorRepo collectorRepo;
    private final ShipperRepo shipperRepo;
    private final AssignOrderCollectorRepository assignOrderCollectorRepository;
    private final AssignOrderShipperRepository assignOrderShipperRepository;
    private final OrderRepo orderRepo;

    @Override
    public EmployeeStatisticsDto getCollectorStatistics(UUID collectorId) {
        // For default statistics, use the last 30 days
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return getCollectorStatisticsByDateRange(collectorId, startDate, endDate);
    }

    @Override
    public EmployeeStatisticsDto getCollectorStatisticsByDateRange(UUID collectorId, LocalDate startDate, LocalDate endDate) {
        // Check if collector exists
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new NotFoundException("Collector not found with ID: " + collectorId));

        // Convert dates to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        // Get assignments in date range - using repository method would be more efficient
        List<AssignOrderCollector> assignments = assignOrderCollectorRepository.findAll().stream()
                .filter(assignment -> assignment.getCollectorId().equals(collectorId) &&
                        assignment.getCreatedAt() != null &&
                        assignment.getCreatedAt().after(startTimestamp) &&
                        assignment.getCreatedAt().before(endTimestamp))
                .collect(Collectors.toList());

        if (assignments.isEmpty()) {
            // Return empty statistics if no assignments found
            return createEmptyCollectorStatistics(collector);
        }

        return calculateCollectorStatistics(collector, assignments);
    }

    @Override
    public EmployeeStatisticsDto getCollectorStatisticsByUsername(String username) {
        // Find collector by username
        Collector collector = collectorRepo.findByUsername(username);
        if (collector == null) {
            throw new NotFoundException("Collector not found with username: " + username);
        }

        return getCollectorStatistics(collector.getId());
    }

    @Override
    public EmployeeStatisticsDto getShipperStatistics(UUID shipperId) {
        // For default statistics, use the last 30 days
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return getShipperStatisticsByDateRange(shipperId, startDate, endDate);
    }

    @Override
    public EmployeeStatisticsDto getShipperStatisticsByDateRange(UUID shipperId, LocalDate startDate, LocalDate endDate) {
        // Check if shipper exists
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new NotFoundException("Shipper not found with ID: " + shipperId));

        // Convert dates to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        // Get assignments in date range
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId).stream()
                .filter(assignment -> assignment.getCreatedAt() != null &&
                        assignment.getCreatedAt().after(startTimestamp) &&
                        assignment.getCreatedAt().before(endTimestamp))
                .collect(Collectors.toList());

        if (assignments.isEmpty()) {
            // Return empty statistics if no assignments found
            return createEmptyShipperStatistics(shipper);
        }

        return calculateShipperStatistics(shipper, assignments);
    }

    @Override
    public EmployeeStatisticsDto getShipperStatisticsByUsername(String username) {
        // Find shipper by username
        Shipper shipper = shipperRepo.findByUsername(username);
        if (shipper == null) {
            throw new NotFoundException("Shipper not found with username: " + username);
        }

        return getShipperStatistics(shipper.getId());
    }

    private EmployeeStatisticsDto createEmptyCollectorStatistics(Collector collector) {
        return EmployeeStatisticsDto.builder()
                .employeeId(collector.getId())
                .employeeName(collector.getName())
                .employeeRole("COLLECTOR")
                .totalAssignments(0)
                .completedAssignments(0)
                .failedAssignments(0)
                .pendingAssignments(0)
                .successRate(0.0)
                .averageCompletionTime(0.0)
                .assignmentStatusCounts(new HashMap<>())
                .build();
    }

    private EmployeeStatisticsDto createEmptyShipperStatistics(Shipper shipper) {
        return EmployeeStatisticsDto.builder()
                .employeeId(shipper.getId())
                .employeeName(shipper.getName())
                .employeeRole("SHIPPER")
                .totalAssignments(0)
                .completedAssignments(0)
                .failedAssignments(0)
                .pendingAssignments(0)
                .successRate(0.0)
                .averageCompletionTime(0.0)
                .assignmentStatusCounts(new HashMap<>())
                .build();
    }

    private EmployeeStatisticsDto calculateCollectorStatistics(Collector collector, List<AssignOrderCollector> assignments) {
        // Calculate overall statistics
        int totalAssignments = assignments.size();

        int completedAssignments = (int) assignments.stream()
                .filter(a -> a.getStatus() == CollectorAssignmentStatus.COMPLETED)
                .count();

        int failedAssignments = (int) assignments.stream()
                .filter(a -> a.getStatus() == CollectorAssignmentStatus.FAILED ||
                        a.getStatus() == CollectorAssignmentStatus.CANCELED || a.getStatus() == CollectorAssignmentStatus.FAILED_ONCE)
                .count();

        int pendingAssignments = totalAssignments - completedAssignments - failedAssignments;

        double successRate = totalAssignments > 0 ?
                (double) completedAssignments / totalAssignments * 100 : 0;

        // Calculate average completion time
        double averageCompletionTime = assignments.stream()
                .filter(a -> a.getCreatedAt() != null && a.getUpdatedAt() != null)
                .mapToLong(a -> (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000)) // Convert to minutes
                .average()
                .orElse(0.0);

        // Count assignments by status
        Map<String, Integer> statusCounts = new HashMap<>();
        for (CollectorAssignmentStatus status : CollectorAssignmentStatus.values()) {
            int count = (int) assignments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            statusCounts.put(status.name(), count);
        }

        // Build and return the statistics DTO
        return EmployeeStatisticsDto.builder()
                .employeeId(collector.getId())
                .employeeName(collector.getName())
                .employeeRole("COLLECTOR")
                .totalAssignments(totalAssignments)
                .completedAssignments(completedAssignments)
                .failedAssignments(failedAssignments)
                .pendingAssignments(pendingAssignments)
                .successRate(successRate)
                .averageCompletionTime(averageCompletionTime)
                .assignmentStatusCounts(statusCounts)
                .build();
    }

    private EmployeeStatisticsDto calculateShipperStatistics(Shipper shipper, List<AssignOrderShipper> assignments) {
        // Calculate overall statistics
        int totalAssignments = assignments.size();

        int completedAssignments = (int) assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.COMPLETED ||
                        a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                .count();

        int failedAssignments = (int) assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.FAILED_ATTEMPT ||
                        a.getStatus() == ShipperAssignmentStatus.RETURNED_TO_HUB ||
                        a.getStatus() == ShipperAssignmentStatus.CANCELED)
                .count();

        int pendingAssignments = totalAssignments - completedAssignments - failedAssignments;

        double successRate = totalAssignments > 0 ?
                (double) completedAssignments / totalAssignments * 100 : 0;

        // Calculate average completion time
        double averageCompletionTime = assignments.stream()
                .filter(a -> a.getCreatedAt() != null && a.getUpdatedAt() != null)
                .mapToLong(a -> (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000)) // Convert to minutes
                .average()
                .orElse(0.0);

        // Count assignments by status
        Map<String, Integer> statusCounts = new HashMap<>();
        for (ShipperAssignmentStatus status : ShipperAssignmentStatus.values()) {
            int count = (int) assignments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            statusCounts.put(status.name(), count);
        }

        // Build and return the statistics DTO
        return EmployeeStatisticsDto.builder()
                .employeeId(shipper.getId())
                .employeeName(shipper.getName())
                .employeeRole("SHIPPER")
                .totalAssignments(totalAssignments)
                .completedAssignments(completedAssignments)
                .failedAssignments(failedAssignments)
                .pendingAssignments(pendingAssignments)
                .successRate(successRate)
                .averageCompletionTime(averageCompletionTime)
                .assignmentStatusCounts(statusCounts)
                .build();
    }
}