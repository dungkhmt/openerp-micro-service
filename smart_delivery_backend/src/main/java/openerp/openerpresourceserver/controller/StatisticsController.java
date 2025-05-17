package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.EmployeeStatisticsDto;
import openerp.openerpresourceserver.service.EmployeeStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final EmployeeStatisticsService employeeStatisticsService;

    /**
     * Get statistics for the currently logged-in collector
     */
    @PreAuthorize("hasRole('COLLECTOR')")
    @GetMapping("/collector/me")
    public ResponseEntity<EmployeeStatisticsDto> getMyCollectorStatistics(Principal principal) {
        EmployeeStatisticsDto statistics = employeeStatisticsService.getCollectorStatisticsByUsername(principal.getName());
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for the currently logged-in collector within a date range
     */
    @PreAuthorize("hasRole('COLLECTOR')")
    @GetMapping("/collector/me/range")
    public ResponseEntity<EmployeeStatisticsDto> getMyCollectorStatisticsByDateRange(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = principal.getName();
        EmployeeStatisticsDto statistics = employeeStatisticsService.getCollectorStatisticsByUsername(username);
        UUID collectorId = statistics.getEmployeeId();
        return ResponseEntity.ok(employeeStatisticsService.getCollectorStatisticsByDateRange(collectorId, startDate, endDate));
    }

    /**
     * Get statistics for a specific collector (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/collector/{collectorId}")
    public ResponseEntity<EmployeeStatisticsDto> getCollectorStatistics(@PathVariable UUID collectorId) {
        EmployeeStatisticsDto statistics = employeeStatisticsService.getCollectorStatistics(collectorId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for a specific collector within a date range (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/collector/{collectorId}/range")
    public ResponseEntity<EmployeeStatisticsDto> getCollectorStatisticsByDateRange(
            @PathVariable UUID collectorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        EmployeeStatisticsDto statistics = employeeStatisticsService.getCollectorStatisticsByDateRange(collectorId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for the currently logged-in shipper
     */
    @PreAuthorize("hasRole('SHIPPER')")
    @GetMapping("/shipper/me")
    public ResponseEntity<EmployeeStatisticsDto> getMyShipperStatistics(Principal principal) {
        EmployeeStatisticsDto statistics = employeeStatisticsService.getShipperStatisticsByUsername(principal.getName());
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for the currently logged-in shipper within a date range
     */
    @PreAuthorize("hasRole('SHIPPER')")
    @GetMapping("/shipper/me/range")
    public ResponseEntity<EmployeeStatisticsDto> getMyShipperStatisticsByDateRange(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = principal.getName();
        EmployeeStatisticsDto statistics = employeeStatisticsService.getShipperStatisticsByUsername(username);
        UUID shipperId = statistics.getEmployeeId();
        return ResponseEntity.ok(employeeStatisticsService.getShipperStatisticsByDateRange(shipperId, startDate, endDate));
    }

    /**
     * Get statistics for a specific shipper (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/shipper/{shipperId}")
    public ResponseEntity<EmployeeStatisticsDto> getShipperStatistics(@PathVariable UUID shipperId) {
        EmployeeStatisticsDto statistics = employeeStatisticsService.getShipperStatistics(shipperId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for a specific shipper within a date range (admin access)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @GetMapping("/shipper/{shipperId}/range")
    public ResponseEntity<EmployeeStatisticsDto> getShipperStatisticsByDateRange(
            @PathVariable UUID shipperId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        EmployeeStatisticsDto statistics = employeeStatisticsService.getShipperStatisticsByDateRange(shipperId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}