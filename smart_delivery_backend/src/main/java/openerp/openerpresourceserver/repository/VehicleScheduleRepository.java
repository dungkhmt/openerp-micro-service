package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.VehicleSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleScheduleRepository extends JpaRepository<VehicleSchedule, UUID> {

    List<VehicleSchedule> findByRouteVehicleId(UUID routeVehicleId);

    List<VehicleSchedule> findByRouteVehicleIdAndIsActiveTrue(UUID routeVehicleId);

    List<VehicleSchedule> findByDayOfWeekAndIsActiveTrue(DayOfWeek dayOfWeek);

    @Query("SELECT vs FROM VehicleSchedule vs JOIN RouteVehicle rv ON vs.routeVehicleId = rv.id WHERE rv.vehicleId = :vehicleId AND vs.dayOfWeek = :dayOfWeek AND vs.isActive = true")
    List<VehicleSchedule> findActiveSchedulesByVehicleAndDay(
            @Param("vehicleId") UUID vehicleId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );

    @Query("SELECT vs FROM VehicleSchedule vs JOIN RouteVehicle rv ON vs.routeVehicleId = rv.id JOIN Vehicle v ON rv.vehicleId = v.vehicleId WHERE v.status = 'AVAILABLE' AND vs.dayOfWeek = :dayOfWeek AND vs.isActive = true")
    List<VehicleSchedule> findActiveSchedulesByDayForAvailableVehicles(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT vs FROM VehicleSchedule vs JOIN RouteVehicle rv ON vs.routeVehicleId = rv.id WHERE rv.vehicleId = :vehicleId")
    List<VehicleSchedule> findByVehicleId(@Param("vehicleId") UUID vehicleId);

    @Query("SELECT vs FROM VehicleSchedule vs JOIN RouteVehicle rv ON vs.routeVehicleId = rv.id WHERE rv.vehicleId = :vehicleId AND vs.isActive = true")
    List<VehicleSchedule> findByVehicleIdAndIsActiveTrue(@Param("vehicleId") UUID vehicleId);

    @Query("SELECT vs FROM VehicleSchedule vs JOIN RouteVehicle rv ON vs.routeVehicleId = rv.id WHERE rv.routeId = :routeId")
    List<VehicleSchedule> findByRouteId(@Param("routeId") UUID routeId);


}