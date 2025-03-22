package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.ScheduleVehicleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleVehicleAssignmentRepository extends JpaRepository<ScheduleVehicleAssignment, UUID> {

    List<ScheduleVehicleAssignment> findByRouteScheduleId(UUID routeScheduleId);

    List<ScheduleVehicleAssignment> findByVehicleId(UUID vehicleId);

    List<ScheduleVehicleAssignment> findByDriverId(UUID driverId);

    List<ScheduleVehicleAssignment> findByAssignmentDateAndIsActiveTrue(LocalDate date);

    @Query("SELECT sva FROM ScheduleVehicleAssignment sva JOIN RouteSchedule rs ON " +
            "sva.routeScheduleId = rs.id WHERE rs.routeId = :routeId AND sva.isActive = true")
    List<ScheduleVehicleAssignment> findActiveAssignmentsByRouteId(@Param("routeId") UUID routeId);

    Optional<ScheduleVehicleAssignment> findByRouteScheduleIdAndVehicleIdAndIsActiveTrue(
            UUID routeScheduleId, UUID vehicleId);

    @Query("SELECT sva FROM ScheduleVehicleAssignment sva JOIN RouteSchedule rs ON " +
            "sva.routeScheduleId = rs.id WHERE rs.dayOfWeek = :dayOfWeek AND " +
            "sva.assignmentDate = :date AND sva.isActive = true")
    List<ScheduleVehicleAssignment> findActiveAssignmentsByDayAndDate(
            @Param("dayOfWeek") String dayOfWeek,
            @Param("date") LocalDate date);

    // Find assignments for a specific schedule and date
    List<ScheduleVehicleAssignment> findByRouteScheduleIdAndAssignmentDateAndIsActiveTrue(
            UUID routeScheduleId, LocalDate assignmentDate);

    // Find all active assignments with unassignedAt = null
    List<ScheduleVehicleAssignment> findByIsActiveTrueAndUnassignedAtIsNull();

    List<ScheduleVehicleAssignment> findAllByVehicleId(UUID vehicleId);
}