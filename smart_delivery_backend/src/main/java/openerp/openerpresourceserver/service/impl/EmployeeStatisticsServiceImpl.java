
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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        // Check if collector exists
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new NotFoundException("Collector not found with ID: " + collectorId));

        // Get all assignments for this collector
        List<AssignOrderCollector> allAssignments = assignOrderCollectorRepository.findAll().stream()
                .filter(assignment -> assignment.getCollectorId().equals(collectorId))
                .collect(Collectors.toList());

        if (allAssignments.isEmpty()) {
            // Return empty statistics if no assignments found
            return createEmptyCollectorStatistics(collector);
        }

        return calculateCollectorStatistics(collector, allAssignments);
    }

    @Override
    public EmployeeStatisticsDto getCollectorStatisticsByDateRange(UUID collectorId, LocalDate startDate, LocalDate endDate) {
        // Check if collector exists
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new NotFoundException("Collector not found with ID: " + collectorId));

        // Convert dates to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        // Get assignments in date range
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
        // Check if shipper exists
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new NotFoundException("Shipper not found with ID: " + shipperId));

        // Get all assignments for this shipper
        List<AssignOrderShipper> allAssignments = assignOrderShipperRepository.findByShipperId(shipperId);

        if (allAssignments.isEmpty()) {
            // Return empty statistics if no assignments found
            return createEmptyShipperStatistics(shipper);
        }

        return calculateShipperStatistics(shipper, allAssignments);
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
                .dailyStats(new EmployeeStatisticsDto.PeriodStatistics(0, 0, 0.0, 0.0))
                .weeklyStats(new EmployeeStatisticsDto.PeriodStatistics(0, 0, 0.0, 0.0))
                .monthlyStats(new EmployeeStatisticsDto.PeriodStatistics(0, 0, 0.0, 0.0))
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
                .dailyStats(new EmployeeStatisticsDto.PeriodStatistics(0, 0, 0.0, 0.0))
                .weeklyStats(new EmployeeStatisticsDto.PeriodStatistics(0, 0, 0.0, 0.0))
                .monthlyStats(new EmployeeStatisticsDto.PeriodStatistics(0, 0, 0.0, 0.0))
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
                        a.getStatus() == CollectorAssignmentStatus.CANCELED)
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

        // Calculate daily statistics
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        List<AssignOrderCollector> todayAssignments = assignments.stream()
                .filter(a -> a.getCreatedAt() != null &&
                        a.getCreatedAt().after(startOfDay) &&
                        a.getCreatedAt().before(endOfDay))
                .collect(Collectors.toList());

        EmployeeStatisticsDto.PeriodStatistics dailyStats = calculatePeriodStats(todayAssignments);

        // Calculate weekly statistics
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        Timestamp startOfWeekTimestamp = Timestamp.valueOf(startOfWeek.atStartOfDay());

        List<AssignOrderCollector> weeklyAssignments = assignments.stream()
                .filter(a -> a.getCreatedAt() != null &&
                        a.getCreatedAt().after(startOfWeekTimestamp) &&
                        a.getCreatedAt().before(endOfDay))
                .collect(Collectors.toList());

        EmployeeStatisticsDto.PeriodStatistics weeklyStats = calculatePeriodStats(weeklyAssignments);

        // Calculate monthly statistics
        LocalDate startOfMonth = today.withDayOfMonth(1);
        Timestamp startOfMonthTimestamp = Timestamp.valueOf(startOfMonth.atStartOfDay());

        List<AssignOrderCollector> monthlyAssignments = assignments.stream()
                .filter(a -> a.getCreatedAt() != null &&
                        a.getCreatedAt().after(startOfMonthTimestamp) &&
                        a.getCreatedAt().before(endOfDay))
                .collect(Collectors.toList());

        EmployeeStatisticsDto.PeriodStatistics monthlyStats = calculatePeriodStats(monthlyAssignments);

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
                .dailyStats(dailyStats)
                .weeklyStats(weeklyStats)
                .monthlyStats(monthlyStats)
                .build();
    }

    private EmployeeStatisticsDto.PeriodStatistics calculatePeriodStats(List<AssignOrderCollector> assignments) {
        int total = assignments.size();

        int completed = (int) assignments.stream()
                .filter(a -> a.getStatus() == CollectorAssignmentStatus.COMPLETED)
                .count();

        double successRate = total > 0 ? (double) completed / total * 100 : 0;

        double avgTime = assignments.stream()
                .filter(a -> a.getCreatedAt() != null && a.getUpdatedAt() != null)
                .mapToLong(a -> (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000))
                .average()
                .orElse(0.0);

        return new EmployeeStatisticsDto.PeriodStatistics(total, completed, successRate, avgTime);
    }

    private EmployeeStatisticsDto calculateShipperStatistics(Shipper shipper, List<AssignOrderShipper> assignments) {
        // Calculate overall statistics
        int totalAssignments = assignments.size();

        int completedAssignments = (int) assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.DELIVERED ||
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

        // Calculate daily statistics
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        List<AssignOrderShipper> todayAssignments = assignments.stream()
                .filter(a -> a.getCreatedAt() != null &&
                        a.getCreatedAt().after(startOfDay) &&
                        a.getCreatedAt().before(endOfDay))
                .collect(Collectors.toList());

        EmployeeStatisticsDto.PeriodStatistics dailyStats = calculateShipperPeriodStats(todayAssignments);

        // Calculate weekly statistics
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        Timestamp startOfWeekTimestamp = Timestamp.valueOf(startOfWeek.atStartOfDay());

        List<AssignOrderShipper> weeklyAssignments = assignments.stream()
                .filter(a -> a.getCreatedAt() != null &&
                        a.getCreatedAt().after(startOfWeekTimestamp) &&
                        a.getCreatedAt().before(endOfDay))
                .collect(Collectors.toList());

        EmployeeStatisticsDto.PeriodStatistics weeklyStats = calculateShipperPeriodStats(weeklyAssignments);

        // Calculate monthly statistics
        LocalDate startOfMonth = today.withDayOfMonth(1);
        Timestamp startOfMonthTimestamp = Timestamp.valueOf(startOfMonth.atStartOfDay());

        List<AssignOrderShipper> monthlyAssignments = assignments.stream()
                .filter(a -> a.getCreatedAt() != null &&
                        a.getCreatedAt().after(startOfMonthTimestamp) &&
                        a.getCreatedAt().before(endOfDay))
                .collect(Collectors.toList());

        EmployeeStatisticsDto.PeriodStatistics monthlyStats = calculateShipperPeriodStats(monthlyAssignments);

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
                .dailyStats(dailyStats)
                .weeklyStats(weeklyStats)
                .monthlyStats(monthlyStats)
                .build();
    }

    private EmployeeStatisticsDto.PeriodStatistics calculateShipperPeriodStats(List<AssignOrderShipper> assignments) {
        int total = assignments.size();

        int completed = (int) assignments.stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.DELIVERED ||
                        a.getStatus() == ShipperAssignmentStatus.COMPLETED)
                .count();

        double successRate = total > 0 ? (double) completed / total * 100 : 0;

        double avgTime = assignments.stream()
                .filter(a -> a.getCreatedAt() != null && a.getUpdatedAt() != null)
                .mapToLong(a -> (a.getUpdatedAt().getTime() - a.getCreatedAt().getTime()) / (60 * 1000))
                .average()
                .orElse(0.0);

        return new EmployeeStatisticsDto.PeriodStatistics(total, completed, successRate, avgTime);
    }
}