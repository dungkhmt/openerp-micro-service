package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.RouteVehicleDetailDto;
import openerp.openerpresourceserver.entity.RouteVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface RouteVehicleRepository extends JpaRepository<RouteVehicle, UUID> {
    List<RouteVehicle> findByRouteId(UUID routeId);

    List<RouteVehicle> findByVehicleId(UUID vehicleId);

    @Query("SELECT rv FROM RouteVehicle rv join Vehicle v WHERE v.driverId = :driverId")
    List<RouteVehicle> findByDriverId(@Param("driverId") UUID driverId);

    @Query("SELECT rv FROM RouteVehicle rv WHERE rv.createdAt BETWEEN :startDate AND :endDate")
    List<RouteVehicle> findByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    void deleteByRouteId(UUID routeId);

    @Query("SELECT new openerp.openerpresourceserver.dto.RouteVehicleDetailDto(rv, r.routeName, r.routeCode, r.description, r.status) FROM RouteVehicle rv JOIN Route r on rv.routeId = r.routeId WHERE rv.vehicleId=:vehicleId")
    List<RouteVehicleDetailDto> findDetailByVehicleId(@Param("vehicleId") UUID vehicleId);
}